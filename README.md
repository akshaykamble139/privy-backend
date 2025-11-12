# Privy Backend

Privy is a secure chat application designed with end-to-end encryption.  
The backend provides APIs, authentication, and real-time messaging transport.  
It does not handle plaintext messages — those remain encrypted on the client.

## Tech Stack
- Java 21+
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security with JWT
- H2 Database (for development)

## Goals / Features

### ✅ Phase 1 — Core Authentication
- Project setup and server bootstrapping  
- User registration and login APIs  
- JWT-based authentication and authorization  
- Password hashing using `PasswordEncoder`  
- Global exception handling for consistent JSON error responses  
- In-memory H2 database configuration for development  

### ✅ Phase 2 — Device & Public Key Management
- Multi-device support:  
  -- Each user can register multiple devices  
  -- Each device stores its own public key (private key stays on the client)  
- APIs implemented:  
  - `POST /api/devices/register` – add new device for logged-in user  
  - `GET /api/users/{username}/devices` – fetch all public keys for that user  
- Device tracking (`deviceName`, `createdAt`, `lastSeenAt`)  
- Validation and error handling integrated with existing auth flow  

Later phases will add real-time messaging, encryption workflows, device keys, and secure message storage.

## How to Run (Development)
### Prerequisites
- Java 21+
- Maven

### Steps
```
mvn clean install
mvn spring-boot:run
```
