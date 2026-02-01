# PayPal Provider Service API (Demo)

This repository contains a **demo PayPal Provider Service API** that I created after my internship.  
The purpose of this project is purely educational — to improve my knowledge and gain hands-on experience with backend development and API integration.

---

paypal_provider_api/
│── src/
│   ├── main/
│   │   ├── java/com/example/paypal/
│   │   │   ├── controller/   # REST controllers
│   │   │   ├── service/      # Business logic
│   │   │   ├── config/       # PayPal configuration
│   │   │   └── model/        # Request/Response models
│   │   └── resources/
│   │       └── application.properties
│   └── test/                 # Unit tests
│
└── README.md


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

![Successfully Create Order](succesully_CreateOrder.jpg)


This structure makes it crystal clear:
- **Setup steps** (client ID/secret replacement)  
- **Postman request/response example**  
- **Project folder layout**  






 
