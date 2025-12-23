# Shadow Ledger Service

## Overview

Shadow Ledger Service is a Spring Boot microservice designed to manage and process ledger events in a distributed system. It provides RESTful APIs for interacting with ledger data and integrates with Kafka to consume and process event streams. The service is suitable for financial applications or any system requiring reliable event-driven ledger management.

## Main Features

- **Event Consumption:** Listens to Kafka topics for ledger-related events, including raw and correction events.
- **Ledger Management:** Processes incoming events and updates the ledger state accordingly.
- **REST API:** Exposes endpoints for querying ledger balances and event histories.
- **Traceability:** Includes tracing and filtering mechanisms for request tracking and debugging.

## Architecture

- **Spring Boot Application:** Entry point is `ShadowLedgerServiceApplication.java`.
- **Kafka Consumers:**
  - `RawEventConsumer.java`: Handles raw ledger events from Kafka.
  - `CorrectionEventConsumer.java`: Handles correction events for ledger entries.
- **Controllers:**
  - `LedgerController.java`: Provides REST endpoints for ledger operations.
- **Services:**
  - `LedgerService.java`: Core business logic for processing events and managing ledger state.
- **Repositories:**
  - `LedgerEventRepository.java`: Data access layer for ledger events.
- **DTOs and Models:**
  - `EventDto.java`, `ShadowBalance.java`, `LedgerEvent.java`, `EventType.java`: Data transfer and domain models.
- **Config:**
  - `KafkaTraceConsumerInterceptor.java`, `TraceIdFilter.java`: Tracing and request filtering utilities.

## How It Works

1. **Event Ingestion:** Kafka consumers listen for ledger events and corrections, passing them to the service layer for processing.
2. **Ledger Update:** The service updates the ledger state based on incoming events, ensuring consistency and traceability.
3. **API Access:** Clients interact with the service via REST endpoints to query balances, event histories, and other ledger-related data.
4. **Tracing:** Requests and event processing are traced for debugging and monitoring purposes.

## Getting Started

### Prerequisites
- Java 11 or higher
- Maven
- Kafka broker (for event streaming)

### Running the Service
1. Clone the repository.
2. Configure Kafka and database settings in `src/main/resources/application.properties` or `application.yml`.
3. Build the project:
   ```bash
   ./mvnw clean install
   ```
4. Start the service:
   ```bash
   ./mvnw spring-boot:run
   ```

### API Endpoints
- `/api/ledger/balance` - Get current ledger balance.
- `/api/ledger/events` - Get ledger event history.

## Configuration
- Kafka and database connection details are managed in the `application.properties` or `application.yml` files.
- Logging is configured via `logback-spring.xml`.

## License
This project is for demonstration purposes. Please review and update licensing as needed.

