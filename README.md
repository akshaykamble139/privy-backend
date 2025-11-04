# Privy Backend

Privy is a secure chat application designed with end-to-end encryption.  
The backend provides APIs, authentication, and real-time messaging transport.  
It does not handle plaintext messages — those remain encrypted on the client.

## Tech Stack
- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security (planned)
- WebSockets (planned)
- H2 / PostgreSQL (H2 for development)

## Goals / Features (Phase-1)
- Project setup and server bootstrapping
- Basic health endpoint
- Prepare structure for:
  - Authentication
  - WebSockets for chat delivery
  - Message and user storage

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
