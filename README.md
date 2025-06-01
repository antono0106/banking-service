
# Banking Service

A simple Spring Boot application that provides RESTful APIs for basic banking operations, including account creation, deposits, withdrawals, and transfers.

---

## Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Liquibase
- Docker & Docker Compose
- Testcontainers
- JUnit 5 & Mockito
- JaCoCo

---

## Requirements

- JRE / JDK 17 or later
- Docker (for PostgreSQL only or Docker Compose)

---

##  Getting Started

###  Build & Run Locally With PostgreSQL Container

1. Start Postgres docker container from docker-compose.yaml:

```bash
docker-compose up postgres -d --build
```

2. Start the application:

```bash
# Build the project without running tests
./mvnw clean package -DskipTests

# Run the application
java -jar target/banking-service.jar
```
or

```bash
./mvnw spring-boot:run
```

To exit, close terminal with the application, or just press `Ctrl + C`, and after that run:

```bash
docker-compose down
```

### Run with Docker Compose

In project directory, run:

```bash
docker-compose up --build
```

To exit, run:

```bash
docker-compose down
```
##

In both cases, database table will be prefilled, and the application will be available at `http://localhost:8080`.

---

##  Configuration

Database and app settings are managed via `application.properties`.

---

## API Endpoints

### Account Management

- `POST /v0/accounts/management` – Create new account
- `GET /v0/accounts/management/{id}` – Get account by ID
- `GET /v0/accounts/management?page={page}&size={size}` – List all accounts (with pagination). Default pagination params are page = 0 and size = 10.

### Transactions

- `PATCH /v0/accounts/transactions/deposit/{id}` – Deposit funds
- `PATCH /v0/accounts/transactions/withdraw/{id}` – Withdraw funds
- `PATCH /v0/accounts/transactions/transfer` – Transfer funds

---

## Testing

**Note: in order to launch repository test correctly, Docker must be launched!**

Run:
```bash
./mvnw clean test
```

- Unit tests with coverage
- Testcontainers for isolated PostgreSQL testing

Test coverage report will be available at `target/site/index.html`.

## API Documentation

This project includes interactive API documentation using **Swagger UI**.

After starting the application, you can access the documentation at:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

The documentation is based on an [OpenAPI specification](https://swagger.io/specification/) defined in the file: `src/main/resources/openapi.yaml`


