# ToDo-APP Task

Microservice for task management built with Java 21 and Spring Boot.

## Requirements

- Java 21
- Maven 3.9+
- Docker & Docker Compose

---

## Option 1 — Run locally with Maven

Runs the API using an in-memory H2 database (development profile).

### 1. Clone the repository

```bash
git clone https://github.com/dalthonmh/todoapp-task.git
cd todoapp-task
```

### 2. Start the application

```bash
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8085`.

---

## Option 2 — Run with Docker Compose (recommended)

Starts the API and a PostgreSQL database together in containers.

### 1. (Optional) Set a custom JWT secret

The `docker-compose.yml` includes a default secret for convenience. For production, replace the `JWT_SECRET` value before starting:

```yaml
environment:
  JWT_SECRET: your-secure-secret-at-least-32-characters
```

### 2. Build and start all services

```bash
docker compose up --build
```

This will automatically:

1. Build the API image from the `Dockerfile`.
2. Start PostgreSQL and wait until it is healthy.
3. Start the API on port **8085**.

### 3. Verify the services are running

```bash
docker compose ps
```

Both services should appear as `running` / `healthy`.

### 4. Stop the services

```bash
docker compose down
```

To also remove the persisted database data:

```bash
docker compose down -v
```

---

## API Usage

Replace `localhost:8085` with your host and port if needed.

### Health check

```bash
curl http://localhost:8085/actuator/health
```

### Create a task

```bash
curl -X POST http://localhost:8085/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My first task",
    "completed": false,
    "username": "dalthonmh"
  }'
```

### Get all tasks

```bash
curl http://localhost:8085/api/tasks
```

### Get a task by ID

```bash
curl http://localhost:8085/api/tasks/1
```

---
