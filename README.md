🍔 Food Delivery Payment Provider (PayPal Integration)
 Overview
This repository contains the backend microservice for handling payment operations in a food delivery web application.
The service integrates with PayPal APIs to securely process transactions and is designed to be deployed on AWS for scalability and reliability.

🔥 Features 🔥
. PayPal payment gateway integration
. RESTful API endpoints for payment operations
. Secure transaction handling
. Microservice architecture (independent, deployable unit)
. AWS-ready deployment setup 

📂 Project Structure
PAYPAL_PROVIDER_API
| -- SRC/
| -- DOCS/
| -- IMAGES/
| -- README.MD

⚖️ Copyright Notice
This project is originally built by me for educational and practical purposes.
All designs, notes, and implementations are my own.
There is no copyright infringement — external references are used only for learning inspiration.

📝 System Design & Decisions
All design decisions are documented in the docs/ folder.
I maintain handwritten notes and diagrams (uploaded as images inside - z(required-images,testing,system) folder) 
to showcase the thought process behind the architecture.

![image alt](https://github.com/CoderRushikesh/paypal_provider_api/blob/90f37678033f9188059b937b546ec2d99ee4c1d9/z(required-images%2Ctesting%2Csystem)/descision.jpeg)
![image alt](https://github.com/CoderRushikesh/paypal_provider_api/blob/408647e845d334556036133d89d7bbb956cdb91c/z(required-images%2Ctesting%2Csystem)/design.jpeg)

📦 Deployment
1 Steps to deploy on AWS:
2 Build Docker image of the service
3 Push to AWS ECR
4 Deploy on AWS ECS / EC2
5 Configure API Gateway for external access
6 Use AWS Secret Manager for secure PayPal credentials

🔑 Environment Variables
PAYPAL_CLIENT_ID=your-client-id
PAYPAL_SECRET=your-secret
DB_URL=jdbc:mysql://...
DB_USER=...
DB_PASSWORD=...





