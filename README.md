# BidVelocity backend

```bash
./mvnw -DskipTests package

set -a && source .env && set +a
java -jar eureka-server/target/eureka-server-0.0.1-SNAPSHOT.jar          # http://localhost:8761

set -a && source .env && set +a
java -jar api-gateway/target/api-gateway-0.0.1-SNAPSHOT.jar              # http://localhost:8080

set -a && source .env && set +a
java -jar auth-service/target/auth-service-0.0.1-SNAPSHOT.jar            # http://localhost:8081

set -a && source .env && set +a
java -jar auction-service/target/auction-service-0.0.1-SNAPSHOT.jar      # http://localhost:8082

set -a && source .env && set +a
java -jar bidding-service/target/bidding-service-0.0.1-SNAPSHOT.jar      # http://localhost:8083

set -a && source .env && set +a
SERVER_PORT=8085 java -jar bidding-service/target/bidding-service-0.0.1-SNAPSHOT.jar  # http://localhost:8085

set -a && source .env && set +a
java -jar payment-service/target/payment-service-0.0.1-SNAPSHOT.jar      # http://localhost:8084
```

## Postman

Base URL: `http://localhost:8080`

Get a token: sign in at `http://localhost:3000`, console:

```js
await window.Clerk.session.getToken()
```

Postman → Authorization → Bearer Token → paste it. Use this token on every request except step 1 and 2.

### 1. List lots (no token)

`GET` `http://localhost:8080/api/auctions`

Expected: `200` and a JSON array of open lots (seeded items like Leica, Rolex, Strat). Copy one `id`.

### 2. Open one lot (no token)

`GET` `http://localhost:8080/api/auctions/PASTE_ID`

Expected: `200` with that lot’s `title`, `currentPriceCents`, `status: "OPEN"`.

### 3. Who am I

`GET` `http://localhost:8080/api/me`

Expected: `200` with `clerkUserId`, `email`, `onboarded`. No token → `401`.

### 4. Wallet

`GET` `http://localhost:8080/api/wallet`

Expected: `200` with `availableBalanceCents`, `heldBalanceCents`, `currency`.

### 5. Bid

`POST` `http://localhost:8080/api/bids`

```json
{
  "auctionId": "PASTE_ID",
  "amountCents": 85000
}
```

Expected: `200` with `status: "accepted"` and your `amountCents`. Then `GET` wallet again: available goes down, held goes up.

Too-low bid or not enough wallet → `409`.

### 6. Create a lot

`POST` `http://localhost:8080/api/auctions`

```json
{
  "title": "Test lot",
  "description": "Postman",
  "startPriceCents": 1000,
  "minIncrementCents": 100,
  "endsAt": "2026-09-23T12:00:00Z"
}
```

Expected: `200` with a new `id`, `status: "OPEN"`, `currentPriceCents: 1000`.
