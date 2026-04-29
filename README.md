# PayPal Provider Service API 

# PayPal Provider Service

Spring Boot microservice handling PayPal payment initiation — part of a two-service payment microservices system built for a food delivery platform.

## Architecture

This service acts as the **payment provider layer**:
- Accepts payment requests from the order system
- Initiates PayPal Standard Checkout (Create Order API)
- Captures payments via PayPal Capture API
- Communicates with `paypal_processing_service` for downstream processing

## Tech Stack

- Java 17, Spring Boot
- OAuth2.0 + JWT (token-based security)
- Redis (caching — reduced API response time by 40%)
- Docker (containerised deployment)
- AWS EC2 (production deployment)
- REST API — JSON request/response
- Eureka Service Registry *(in progress)*
- API Gateway routing *(in progress)*
- Circuit Breaker — Resilience4j *(in progress)*

## Project Structure

---

src/main/java/com/example/paypal/
├── controller/     # REST endpoints
├── service/        # Business logic + PayPal API calls
├── config/         # PayPal OAuth2 configuration
└── model/          # Request/Response DTOs


##  How to Use

### 1. Prerequisites
- Java 17+  
- Maven/Gradle  
- PayPal Developer Account (to generate credentials)  
- Postman (for testing)

### 2. Setup PayPal Credentials
1. Log in to [PayPal Developer Dashboard](https://developer.paypal.com/).  
2. Create an **App** under **Sandbox**.  
3. Copy the **Client ID** and **Client Secret**.  
4. Open the project’s `application.properties` (or `application.yml`) file.  
5. Replace the placeholders with your credentials:

```properties
paypal.client.id=YOUR_CLIENT_ID
paypal.client.secret=YOUR_CLIENT_SECRET
```

 Follow Request structure to test the api : 
POST http://localhost:8083/payments 
Content-Type: application/json

 Body*(json) 
 {
  "intent": "CAPTURE",
  "currencyCode": "USD",
  "amount": 100.00,
  "returnUrl": "https://example.com/return",
  "cancelUrl": "https://example.com/cancel"
}

 Response* (json) 
 {
  "status": "success",
  "paymentId": "PAY-123456789",
  "redirectUrl": "https://www.sandbox.paypal.com/checkoutnow?token=EC-123456"
}

## 🖼️ Example Screenshot

Here’s a sample screenshot showing a successful order creation:



<img src="z(required-images,testing,system)/succesully_CreateOrder.jpg" alt="Successfully Create Order" width="600"/>

<img src="z(required-images,testing,system)/successfully.png" alt="Successfully Create Order" width="600"/>

This structure makes it crystal clear:
- **Setup steps** (client ID/secret replacement)  
- **Postman request/response example**  
- **Project folder layout**  






 
