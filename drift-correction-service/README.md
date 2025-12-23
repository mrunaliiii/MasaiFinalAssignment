# Drift Correction Service

## Overview
The Drift Correction Service is a Spring Boot microservice designed to detect and correct balance drifts in a shadow ledger system. It monitors discrepancies between the core banking system (CBS) and the shadow ledger, generates correction events, and communicates these events via REST APIs and Kafka messaging.

## Features
- **Drift Detection:** Identifies mismatches between CBS and shadow ledger balances.
- **Correction Event Generation:** Creates events to reconcile detected drifts.
- **REST API:** Exposes endpoints for drift detection and correction event management.
- **Kafka Integration:** Publishes correction events to Kafka topics for downstream processing.
- **Configurable:** Uses external configuration files for environment-specific settings.

## Main Components
- **Controller (`controller/DriftCorrectionController.java`):** Handles HTTP requests for drift detection and correction events.
- **Service (`service/DriftCorrectionService.java`):** Contains business logic for drift detection and event generation.
- **DTOs (`dto/`):** Data Transfer Objects for CBS balances and correction events.
- **Kafka Producer (`kafka/KafkaProducerConfig.java`):** Configures Kafka producer for event publishing.
- **Config (`config/`):** Includes REST template and trace ID filter for distributed tracing and HTTP communication.

## How It Works
1. The service receives balance data from CBS and the shadow ledger.
2. It compares the balances to detect any drift (discrepancy).
3. Upon detecting a drift, it generates a correction event.
4. The correction event is published to a Kafka topic and/or returned via REST API.
5. Downstream services consume these events to reconcile the shadow ledger.

## Running the Service
### Prerequisites
- Java 17+
- Maven
- Kafka (for event publishing)

### Build and Run
```bash
./mvnw clean package
java -jar target/drift-correction-service-*.jar
```

### Docker
A `Dockerfile` is provided for containerization:
```bash
docker build -t drift-correction-service .
docker run -p 8080:8080 drift-correction-service
```

### Configuration
- Application properties are set in `src/main/resources/application.properties` and `application.yml`.
- Kafka and other environment variables can be configured as needed.

## API Endpoints
- `POST /drift-correction/detect` - Detects drift between CBS and shadow ledger balances.
- `POST /drift-correction/correct` - Generates and publishes correction events.

## Testing
Unit and integration tests are located in `src/test/java/com/shadowledger/drift_correction_service/`.
Run tests with:
```bash
./mvnw test
```

## Logging & Tracing
- Logging is configured via `logback-spring.xml`.
- Distributed tracing is supported via the `TraceIdFilter`.

## License
This project is proprietary to Shadow Ledger System.

