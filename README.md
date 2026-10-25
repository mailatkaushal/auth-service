# Auth Service

A Spring Boot authentication service built with Java 27, Spring Security, JWT, PostgreSQL, and Kafka. It supports user signup, login, refresh-token rotation, logout, and role-based protection for secured endpoints.

## Features

- User signup and login
- JWT access token generation
- Refresh token generation and rotation
- Secure refresh-token cookie handling
- Token reuse detection and session invalidation
- Role-based authorization using Spring Security
- Kafka event publishing for newly created users
- PostgreSQL persistence with JPA
- Docker Compose setup for local Kafka

## Tech Stack

- Java 27
- Spring Boot 4.1.1
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (jjwt)
- Kafka
- Maven
- Docker Compose

## Project Structure

```text
src/
├── main/
│   ├── java/com/mailatkaushal/authservice/
│   │   ├── config/
│   │   ├── constant/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── enums/
│   │   ├── exception/
│   │   ├── kafka/
│   │   ├── repository/
│   │   ├── security/
│   │   └── service/
│   └── resources/
│       └── application.yaml
└── test/java/com/mailatkaushal/authservice/
```

## Prerequisites

Before running the service, ensure you have:

- Java 27 or newer
- Docker with Docker Compose
- PostgreSQL running and accessible to the application (the Compose file does not start PostgreSQL)
- Required application environment variables configured

## Environment Variables

The application requires the following environment variables. Configure them in the environment used to launch the app. Spring Boot does not automatically load values from a `.env` file. Configure `DB_HOST` and the other database values for a PostgreSQL instance that is already running.

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=authservice
DB_USERNAME=postgres
DB_PASSWORD=your_password
JWT_SECRET=your_jwt_secret
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

The configured auth settings are in `src/main/resources/application.yaml`.

## Running the Application

The Docker Compose file starts Kafka only. Start it first:

```bash
docker compose up -d
```

Then, from the project root, run the Spring Boot application with Maven:

```bash
./mvnw spring-boot:run
```

On Windows, open PowerShell or Command Prompt in the project root (the folder containing `mvnw.cmd`). In PowerShell, run:

```powershell
.\mvnw.cmd spring-boot:run
```

In Command Prompt, run:

```cmd
mvnw.cmd spring-boot:run
```

To stop Kafka when you are finished, run `docker compose down`.

## Authentication Flow

### 1. Sign up

```http
POST /auth/signup
Content-Type: application/json
```

Request body:

```json
{
  "username": "john",
  "password": "secret123"
}
```

Response:

```json
{
  "accessToken": "<jwt_access_token>"
}
```

The refresh token is not included in the JSON response. The server sets it in the `refresh_token` HTTP-only cookie using the `Set-Cookie` response header.

### 2. Login

```http
POST /auth/login
Content-Type: application/json
```

Request body:

```json
{
  "username": "john",
  "password": "secret123"
}
```

### 3. Refresh token

```http
POST /auth/refresh
Cookie: refresh_token=<refresh_token>
```

The server validates the refresh token, rotates it, and issues a new access token and refresh token pair.
The response contains the new access token in its JSON body and sets the rotated refresh token in the HTTP-only cookie.

### 4. Logout

```http
POST /auth/logout
Cookie: refresh_token=<refresh_token>
```

This revokes the refresh token and clears the cookie.

## Protected Endpoints

These routes are secured by JWT authentication:

- `/test/authenticated`
- `/test/user`
- `/test/admin`

Public endpoints:

- `/auth/signup`
- `/auth/login`
- `/auth/refresh`
- `/auth/logout`
- `/test/public`

## Role Model

The application creates default roles in the database and assigns the `USER` role to newly registered users.

Available roles:

- `USER`
- `ADMIN`

## Security Notes

- Access tokens are stateless JWTs with short lifetime.
- Refresh tokens are stored in the database and rotated on use.
- Reuse of a refresh token triggers invalidation of all sessions for that user.
- Cookies use `HttpOnly`, configurable path, and same-site policy settings.

## Contributing

1. Fork the repo
2. Create a feature branch
3. Commit your changes
4. Push to your fork
5. Open a pull request
