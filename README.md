# PayPal Provider Service API

> **Microservice 1 of 2** in the PayPal Payment Microservices System.
> Handles PayPal order creation, approval redirect, and payment capture — communicating directly with the PayPal REST API via OAuth2.0.

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.2-brightgreen?style=flat-square&logo=springboot)
![PayPal](https://img.shields.io/badge/PayPal-Sandbox%20%2F%20Live-blue?style=flat-square&logo=paypal)
![Lombok](https://img.shields.io/badge/Lombok-Enabled-red?style=flat-square)
![Maven](https://img.shields.io/badge/Maven-Build-red?style=flat-square&logo=apachemaven)
![Port](https://img.shields.io/badge/Port-8083-lightgrey?style=flat-square)

---

## System Architecture

```
Order System
     │
     ▼
┌─────────────────────────┐
│  paypal-provider-service │  ──── OAuth2.0 + REST ────►  PayPal REST API
│  (this service)          │                               (Sandbox / Live)
│  Port: 8083              │
└─────────────────────────┘
     │
     ▼
paypal-processing-service
(Port: 8084)
     │
     ▼
MySQL Database + Redis Cache
```

This service is responsible for the **PayPal-facing** side of the payment flow:
- Authenticating with PayPal via OAuth2.0 (Client Credentials)
- Creating PayPal orders
- Returning the PayPal approval redirect URL to the client
- Capturing the payment after user approval

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.4.2 |
| JSON Parsing | Gson |
| Object Mapping | ModelMapper 3.2.1 |
| Boilerplate Reduction | Lombok |
| Payment Gateway | PayPal Orders REST API v2 |
| Auth | PayPal OAuth2.0 (Client Credentials Grant) |
| Build Tool | Maven |
| Profiles | local / dev / prod |

---

## Project Structure

```
paypal_provider_api/
├── src/
│   └── main/
│       ├── java/com/payments/
│       │   ├── controller/         # PaymentController — exposes REST endpoints
│       │   ├── service/            # PaymentService — PayPal API integration logic
│       │   ├── config/             # PayPal OAuth2.0 client configuration
│       │   ├── model/              # Request/Response DTOs (Lombok + ModelMapper)
│       │   └── PaypalProviderServiceApplication.java
│       └── resources/
│           ├── application.properties
│           ├── application-local.properties
│           ├── application-dev.properties
│           └── application-prod.properties
├── z(required-images,testing,system)/   # Postman screenshots & test evidence
├── .gitignore
├── pom.xml
└── README.md
```

---

## Prerequisites

- Java 17+
- Maven 3.8+
- [PayPal Developer Account](https://developer.paypal.com) with a Sandbox App
- Postman (for API testing)

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/CoderRushikesh/paypal_provider_api.git
cd paypal_provider_api
```

### 2. Create your PayPal Sandbox App

1. Log in to [PayPal Developer Dashboard](https://developer.paypal.com/dashboard/applications/sandbox)
2. Go to **Apps & Credentials** → **Create App**
3. Copy your **Client ID** and **Client Secret**

### 3. Configure `application-local.properties`

```properties
# Server
server.port=8083
spring.application.name=paypal-provider-service

# PayPal Sandbox Credentials
paypal.client.id=YOUR_SANDBOX_CLIENT_ID
paypal.client.secret=YOUR_SANDBOX_CLIENT_SECRET
paypal.base-url=https://api-m.sandbox.paypal.com
paypal.mode=sandbox
```

### 4. Run the application

```bash
# Run with local profile (default)
./mvnw spring-boot:run

# Or specify a profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Server starts at: `http://localhost:8083`

---

## API Reference & Testing Guide

The complete payment flow has **2 steps**:

```
POST /payments         →  Create PayPal Order  →  Get approval URL
                                                        │
                                              User approves on PayPal
                                                        │
POST /{orderId}/capture  →  Capture Payment  →  Payment Complete
```

---

### Step 1 — Create PayPal Order

**POST** `http://localhost:8083/payments`

**Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "intent": "CAPTURE",
  "amount": 10.00,
  "currencyCode": "USD",
  "returnUrl": "http://localhost:8083/payments/success",
  "cancelUrl": "http://localhost:8083/payments/cancel"
}
```

| Field | Type | Description |
|---|---|---|
| `intent` | String | Always `"CAPTURE"` for immediate payment |
| `amount` | Double | Payment amount |
| `currencyCode` | String | ISO currency code e.g. `"USD"` |
| `returnUrl` | String | PayPal redirects here after user approves |
| `cancelUrl` | String | PayPal redirects here if user cancels |

**Response:**
```json
{
  "orderId": "89S163096V242562S",
  "status": "CREATED",
  "approvalUrl": "https://www.sandbox.paypal.com/checkoutnow?token=89S163096V242562S"
}
```

> 📝 **Copy the `orderId`** — you'll need it for the capture step.
> 📝 **Copy the `approvalUrl`** — paste it in your browser for Step 2.

---

### Step 2 — Approve Payment on PayPal Sandbox

1. Paste the `approvalUrl` from Step 1 into your browser
2. You'll see the PayPal sandbox login screen
3. Go to [PayPal Developer Dashboard](https://developer.paypal.com/dashboard/accounts) → **Sandbox Accounts**
4. Select any **Personal** account → copy the email and password
5. Log in with those credentials on the PayPal page
6. Choose a payment method and click **Pay**

> ✅ Once PayPal confirms the payment, come back to Postman for Step 3.

---

### Step 3 — Capture the Payment

**POST** `http://localhost:8083/{orderId}/capture`

Replace `{orderId}` with the ID from Step 1 (e.g. `89S163096V242562S`).

**Example URL:**
```
POST http://localhost:8083/89S163096V242562S/capture
```

**Headers:**
```
Content-Type: application/json
```

No request body required.

**Response:**
```json
{
  "orderId": "89S163096V242562S",
  "captureId": "5O190127TN364715T",
  "status": "COMPLETED",
  "amount": 10.00,
  "currencyCode": "USD"
}
```

---

## Quick Postman Testing Sequence

| # | Method | URL | Body |
|---|--------|-----|------|
| 1 | POST | `localhost:8083/payments` | Order details JSON |
| 2 | Browser | Paste `approvalUrl`, log in with sandbox account, approve | — |
| 3 | POST | `localhost:8083/{orderId}/capture` | None |

---

## Multi-Profile Configuration

The service ships with 3 Spring profiles:

| Profile | Use Case | Activated By |
|---|---|---|
| `local` | Local development (default) | `./mvnw spring-boot:run` |
| `dev` | Dev server deployment | `-Dspring-boot.run.profiles=dev` |
| `prod` | Production deployment | `-Dspring-boot.run.profiles=prod` |

Log paths are configured per profile in `pom.xml`.

---

## Screenshots

Postman test evidence and sandbox screenshots are available in the [`z(required-images,testing,system)/`](./z(required-images,testing,system)) folder.

---

## Related Microservice

This service is **Part 1** of a two-service architecture:

| Service | Port | Role |
|---|---|---|
| **paypal-provider-service** (this) | 8083 | Communicates with PayPal REST API |
| [paypal-processing-service](https://github.com/CoderRushikesh) | 8084 | Handles downstream payment logic, MySQL + Redis |

---

## Author

**Rushikesh Sahadev Kamble**
Java Backend Developer | Spring Boot | Microservices | AWS

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-blue?style=flat-square&logo=linkedin)](https://linkedin.com/in/rushikesh-kamble)
[![GitHub](https://img.shields.io/badge/GitHub-CoderRushikesh-black?style=flat-square&logo=github)](https://github.com/CoderRushikesh)

---

## License

This project is open source and available under the [MIT License](LICENSE).
