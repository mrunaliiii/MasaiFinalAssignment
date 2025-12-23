# Shadow Ledger API Gateway

This project is an **API Gateway** built with Spring Boot, designed to sit in front of microservices in the Shadow Ledger System. It handles authentication, request routing, and tracing for incoming HTTP requests.

## Key Features

- **JWT Authentication**: Validates JSON Web Tokens for secure access to backend services.
- **Request Routing**: Forwards requests to appropriate microservices based on configuration.
- **Global Filters**: Adds trace IDs to requests for distributed tracing and debugging.
- **Centralized Entry Point**: Acts as the single entry for all client requests, enforcing security and logging.

## Main Components

- `controller/AuthController.java`: Handles authentication endpoints (e.g., login, token refresh).
- `filter/JwtAuthFilter.java`: Intercepts requests to validate JWT tokens.
- `filter/TraceIdGlobalFilter.java`: Adds a unique trace ID to each request for tracking across services.
- `service/JwtService.java`: Provides JWT token generation and validation logic.
- `config/GatewayConfig.java`: Configures routing rules and gateway behavior.
- `resources/application.yml` & `application.properties`: Configuration files for customizing gateway settings.

## How It Works

1. **Client requests** arrive at the API Gateway.
2. The **JWTAuthFilter** checks for a valid JWT token. Unauthorized requests are blocked.
3. The **TraceIdGlobalFilter** attaches a trace ID to each request for observability.
4. Requests are **routed** to the correct backend service as defined in the gateway configuration.
5. The **AuthController** provides endpoints for authentication and token management.

## Running the Service

### Prerequisites
- Java 17+
- Maven
- Docker (optional)

### Build and Run

```bash
./mvnw clean package
java -jar target/api-gateway-*.jar
```

### Using Docker

```bash
docker build -t shadow-ledger-api-gateway .
docker run -p 8080:8080 shadow-ledger-api-gateway
```

### Configuration
- Edit `src/main/resources/application.yml` or `application.properties` to customize routes, authentication, and other settings.


## Customization & Extensibility
- Add new filters for logging, rate limiting, etc.
- Update routing rules in `GatewayConfig.java`.
- Integrate with additional authentication providers as needed.

## License
This project is for demonstration and educational purposes.

