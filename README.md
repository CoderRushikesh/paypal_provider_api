# PayPal Provider Service

Thin REST service that talks to the PayPal Orders API (v2) using OAuth2 client credentials.
It is called by [paypal_processing_service-](https://github.com/CoderRushikesh/paypal_processing_service-),
which owns transactions and statuses.

> Training project built with the PayPal sandbox. Not used with real money.

## Architecture

```
paypal-processing-service :8082 ──REST──► paypal-provider-service :8083 ──OAuth2──► PayPal REST API
```

## Tech stack

Java 17, Spring Boot 3.4, springdoc-openapi (Swagger UI), ModelMapper, Gson, Lombok,
Eureka client, Actuator, JUnit 5, Maven, Docker. Spring profiles: local / dev / qa / uat / prod.

## API (paths are case-sensitive)

| Method | Path | Description |
|--------|------|-------------|
| POST | `/Payments` | Create a PayPal order, returns `id`, `status`, `redirectUrl` |
| POST | `/{orderId}/capture` | Capture an order after the buyer approved it |

Swagger UI: `http://localhost:8083/swagger-ui.html`

### Flow

1. `POST /Payments` with `{"currencyCode":"USD","amount":10.00,"returnUrl":"...","cancelUrl":"..."}`
2. Open `redirectUrl`, log in with a PayPal sandbox buyer account and approve.
3. `POST /{orderId}/capture`.

Validation errors return 400 (invalid amount, missing currency / return URL / cancel URL).
PayPal rejections return 422, PayPal unavailable returns 503.

## Configuration (environment variables)

| Variable | Purpose |
|----------|---------|
| `PAYPAL_CLIENT_ID`, `PAYPAL_CLIENT_SECRET` | PayPal sandbox app credentials (never commit them) |
| `EUREKA_ENABLED`, `EUREKA_URL` | Optional service discovery (`false` by default) |

## Run

```bash
export PAYPAL_CLIENT_ID=...
export PAYPAL_CLIENT_SECRET=...
./mvnw spring-boot:run      # http://localhost:8083
./mvnw test                 # unit tests
docker build -t paypal-provider .
```

Postman screenshots and an EC2 deployment screenshot are in `z(required-images,testing,system)/`.

## Author

Rushikesh Kamble | [GitHub](https://github.com/CoderRushikesh)
