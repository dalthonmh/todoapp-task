# todoapp-task

Java Spring Boot microservice that provides the `/api/tasks` REST API.

**Important:**
- `GET /api/tasks` is public (no auth needed).
- `POST`, `PUT`, `DELETE` require a valid JWT Bearer token containing a `username` claim.
- This service must use the **same `JWT_SECRET`** as the `todoapp-auth` service.

## Quick Start (Docker Compose)

```bash
git clone https://github.com/dalthonmh/todoapp-task.git
cd todoapp-task

export JWT_SECRET="a-long-secret-of-at-least-32-characters"

docker compose up --build
```

API runs on `http://localhost:8085`.

Check health:
```bash
curl http://localhost:8085/actuator/health
```

Stop:
```bash
docker compose down
docker compose down -v   # also removes database data
```

## Authentication

| Method | Endpoint            | Auth Required |
|--------|---------------------|---------------|
| GET    | `/api/tasks`        | No            |
| POST   | `/api/tasks`        | Yes           |
| PUT    | `/api/tasks/{id}`   | Yes           |
| DELETE | `/api/tasks/{id}`   | Yes           |

### Get a Token

**Option 1 (recommended):** Start `todoapp-auth` and login:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"dalthonmh","password":"123456"}'
```

Copy the token and export it:
```bash
export TOKEN="eyJ..."
```

**Option 2 (quick, this service only):**

```bash
pip install pyjwt
python3 -c '
import jwt, time, os
secret = os.getenv("JWT_SECRET", "todoapp-my-secret-dalthonmh-jwtkey")
print(jwt.encode({"username": "dalthonmh", "exp": int(time.time())+86400}, secret, algorithm="HS256"))
'
```

## API Examples

Base URL: `http://localhost:8085`

```bash
# List all tasks (public)
curl http://localhost:8085/api/tasks

# Create task (requires token)
curl -X POST http://localhost:8085/api/tasks \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title": "Prepare Kubernetes demo", "completed": false}'

# Get one task
curl http://localhost:8085/api/tasks/1

# Update task
curl -X PUT http://localhost:8085/api/tasks/1 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title": "Prepare Kubernetes demo", "completed": true}'

# Delete task
curl -X DELETE http://localhost:8085/api/tasks/1 \
  -H "Authorization: Bearer $TOKEN"
```

## Run Locally (Development)

```bash
./mvnw spring-boot:run
```

- Uses in-memory H2 database.
- H2 Console: http://localhost:8085/h2-console (JDBC: `jdbc:h2:mem:taskdb`, user `sa`, empty password).

## Other Commands

```bash
./mvnw test                 # Run tests
docker build -t todoapp-task:latest .   # Build Docker image
```

## Kubernetes

This service is part of the full ToDo App. See the deployment instructions in the root `README.md` and `infra/k8s/README.md`.

## Troubleshooting

- **401 "Token required"**: Missing or invalid `Authorization: Bearer ...` header.
- **403 "Invalid or expired token"**: `JWT_SECRET` does not match the one used to sign the token. Use the same secret as `todoapp-auth`.
- Secret must be at least 32 characters long.

For full config see `src/main/resources/application.yml`.