# Transactions Microservice API

REST API for transactions with Spring Boot and PostgreSQL.

## Requirements

- Docker Desktop
- Java 21
- Maven 3.9+
- Docker & Docker Compose

## Installation

### 1. Clone the repository

```bash
git clone https://github.com/dalthonmh/java-transactions.git
cd java-transactions
```

### 2. Start the service

```bash
docker-compose up -d
```

### 3. Verify

```bash
curl http://localhost:8080/api/tasks/health
```

## Check endpoints

### Create transaction

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Deploy java app",
    "completed": true,
    "username": "dalthon"
  }'
```

### Get all tasks

```bash
curl http://localhost:8080/api/tasks
```

### Transactions by user

```bash
curl http://localhost:8080/api/v1/transactions/user/user001
```

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

## Local development

### 1. Start PostgreSQL

```bash
docker run -d \
  --name postgres \
  -e POSTGRES_DB=transactionsdb \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:16-alpine
```

### 2. Start application

```bash
./mvnw spring-boot:run
```

## Environment

- `DB_HOST`: PostgreSQL Host (default: localhost)
- `DB_PORT`: PostgreSQL port (default: 5432)
- `DB_NAME`: Database name (default: transactionsdb)
- `DB_USER`: PostgreSQL user (default: postgres)
- `DB_PASSWORD`: PostgreSQL password
- `SERVER_PORT`: Default port application (default: 8080)

### 3. Build the package

```bash
./mvnw clean package
```

### 4. Upload the package to registry

```bash
./mvnw clean deploy
```

> Note: To avoid the test, add `-DskipTests` to the ./mvnw sript as the last argument.
