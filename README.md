# ToDo-APP Task

Microservice for tasks app using Java and Spring Boot.

## Requirements

- Java 21
- Maven 3.9+
- Docker & Docker Compose

## Installation

### 1. Clone the repository

```bash
git clone https://github.com/dalthonmh/todoapp-task.git
cd todoapp-task
```

### 2. Start application

```bash
./mvnw spring-boot:run
```

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

### Create tasks

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My task is here",
    "completed": true,
    "username": "dalthonmh"
  }'
```

### Get all tasks

```bash
curl http://localhost:8080/api/tasks
```

### Transactions by user

```bash
curl http://localhost:8080/api/tasks/1
```

---
