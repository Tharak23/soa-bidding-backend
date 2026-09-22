# BidVelocity backend

Spring Cloud services: Eureka, Gateway, Auth, Auction, Bidding, Payment.

## Env

Copy `.env.example` to `.env` in this folder (or export the same names before each `java -jar`).

```bash
set -a && source .env && set +a
```

## Run

```bash
./mvnw -DskipTests package
```

Separate terminals, from this folder:

```bash
java -jar eureka-server/target/eureka-server-0.0.1-SNAPSHOT.jar

set -a && source .env && set +a
java -jar api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar

set -a && source .env && set +a
java -jar auth-service/target/auth-service-0.0.1-SNAPSHOT.jar

set -a && source .env && set +a
java -jar auction-service/target/auction-service-0.0.1-SNAPSHOT.jar

set -a && source .env && set +a
java -jar bidding-service/target/bidding-service-0.0.1-SNAPSHOT.jar

set -a && source .env && set +a
SERVER_PORT=8085 java -jar bidding-service/target/bidding-service-0.0.1-SNAPSHOT.jar

set -a && source .env && set +a
java -jar payment-service/target/payment-service-0.0.1-SNAPSHOT.jar
```

Gateway: `http://localhost:8080`. Schema: `schema.sql`.
