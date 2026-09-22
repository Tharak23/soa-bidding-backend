#!/usr/bin/env python3
import os
import sys
import time
import subprocess
import signal

BACKEND_DIR = os.path.dirname(os.path.abspath(__file__))
LOGS_DIR = os.path.join(BACKEND_DIR, "logs")
PID_FILE = os.path.join(BACKEND_DIR, ".pids")
ENV_FILE = os.path.join(BACKEND_DIR, ".env")

os.makedirs(LOGS_DIR, exist_ok=True)

# Load .env from this directory
env = os.environ.copy()
if not os.path.exists(ENV_FILE):
    print("Error: .env not found.")
    print("Copy .env.example to .env and fill in Clerk + Supabase values.")
    sys.exit(1)

with open(ENV_FILE, "r") as f:
    for line in f:
        line = line.strip()
        if line and not line.startswith("#") and "=" in line:
            key, val = line.split("=", 1)
            env[key.strip()] = val.strip().strip("'\"")

required = ["SUPABASE_DB_URL", "SUPABASE_DB_USERNAME", "SUPABASE_DB_PASSWORD"]
missing_env = [k for k in required if not env.get(k)]
if missing_env:
    print("Error: .env is missing required keys: " + ", ".join(missing_env))
    print("Copy .env.example to .env and fill those values.")
    sys.exit(1)

def stop_all():
    print("Stopping existing backend services...")
    if os.path.exists(PID_FILE):
        try:
            with open(PID_FILE, "r") as f:
                for line in f:
                    pid = line.strip()
                    if pid and pid.isdigit():
                        try:
                            os.kill(int(pid), signal.SIGTERM)
                        except ProcessLookupError:
                            pass
                        except Exception:
                            pass
            os.remove(PID_FILE)
        except Exception:
            pass

    # Clean ports 8761, 8080, 8081, 8082, 8083, 8084
    ports = [8761, 8080, 8081, 8082, 8083, 8084]
    for port in ports:
        try:
            out = subprocess.check_output(["lsof", "-ti", f"tcp:{port}"], text=True).strip()
            if out:
                for p in out.split():
                    try:
                        os.kill(int(p), signal.SIGKILL)
                    except Exception:
                        pass
        except Exception:
            pass
    print("Ports cleared.")

def start_service(name, jar_path, port):
    full_jar = os.path.join(BACKEND_DIR, jar_path)
    log_path = os.path.join(LOGS_DIR, f"{name}.log")
    log_file = open(log_path, "w")
    proc = subprocess.Popen(
        ["java", "-jar", full_jar],
        cwd=BACKEND_DIR,
        env=env,
        stdout=log_file,
        stderr=subprocess.STDOUT,
        start_new_session=True,
    )
    print(f"[{name}] started on port {port} (PID {proc.pid}) -> logs/{name}.log")
    return proc.pid

def main():
    if len(sys.argv) > 1 and sys.argv[1] == "stop":
        stop_all()
        return

    stop_all()

    # Verify or build jars
    jars = [
        ("eureka-server", "eureka-server/target/eureka-server-0.0.1-SNAPSHOT.jar", 8761),
        ("auth-service", "auth-service/target/auth-service-0.0.1-SNAPSHOT.jar", 8081),
        ("auction-service", "auction-service/target/auction-service-0.0.1-SNAPSHOT.jar", 8082),
        ("bidding-service", "bidding-service/target/bidding-service-0.0.1-SNAPSHOT.jar", 8083),
        ("payment-service", "payment-service/target/payment-service-0.0.1-SNAPSHOT.jar", 8084),
        ("api-gateway", "api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar", 8080),
    ]

    missing = [j for _, j, _ in jars if not os.path.exists(os.path.join(BACKEND_DIR, j))]
    if missing:
        print("Building microservices (package)...")
        subprocess.check_call(["./mvnw", "-DskipTests", "package"], cwd=BACKEND_DIR, env=env)

    pids = []

    # 1. Start Eureka
    pids.append(start_service("eureka-server", "eureka-server/target/eureka-server-0.0.1-SNAPSHOT.jar", 8761))
    print("Waiting 4 seconds for Eureka Server to initialize...")
    time.sleep(4)

    # 2. Start Domain Services
    pids.append(start_service("auth-service", "auth-service/target/auth-service-0.0.1-SNAPSHOT.jar", 8081))
    pids.append(start_service("auction-service", "auction-service/target/auction-service-0.0.1-SNAPSHOT.jar", 8082))
    pids.append(start_service("bidding-service", "bidding-service/target/bidding-service-0.0.1-SNAPSHOT.jar", 8083))
    pids.append(start_service("payment-service", "payment-service/target/payment-service-0.0.1-SNAPSHOT.jar", 8084))

    print("Waiting 8 seconds for microservices to register with Eureka...")
    time.sleep(8)

    # 3. Start API Gateway
    pids.append(start_service("api-gateway", "api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar", 8080))

    with open(PID_FILE, "w") as f:
        for p in pids:
            f.write(f"{p}\n")

    print("\n--------------------------------------------------------")
    print("All 6 backend services running in background!")
    print("API Gateway:      http://localhost:8080")
    print("Eureka Registry:  http://localhost:8761")
    print("Logs directory:   logs/*.log")
    print("Stop services:    ./stop-all.sh")
    print("--------------------------------------------------------")

if __name__ == "__main__":
    main()
