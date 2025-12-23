# Event Service

This is a Spring Boot microservice responsible for receiving, processing, and persisting event data in the Shadow Ledger System.

## Overview

The Event Service exposes a REST API endpoint to accept event data, processes the events, and stores them in a database. It is designed to be part of a distributed system, with support for tracing and integration with Kafka for event streaming.

## Features

- **REST API**: Accepts event data via HTTP POST requests at `/events`.
- **Event Processing**: Handles incoming events using business logic defined in the service layer.
- **Persistence**: Stores received events in a database using JPA repositories.
- **Kafka Integration**: Configured to interact with Kafka for event streaming (see `KafkaConfig`).
- **Tracing**: Includes a filter to attach trace IDs to requests for distributed tracing.
- **Global Exception Handling**: Provides consistent error responses.

## Main Components

- `EventController`: REST controller exposing the `/events` endpoint.
- `EventService`: Contains business logic for processing events.
- `ReceivedEvent`: Entity representing an event stored in the database.
- `ReceivedEventRepository`: JPA repository for event persistence.
- `KafkaConfig`: Kafka configuration for event streaming.
- `TraceIdFilter`: Adds trace IDs to requests for observability.
- `GlobalExceptionHandler`: Handles exceptions globally.

## API Usage

### Create Event

- **Endpoint**: `POST /events`
- **Request Body**: JSON representation of `EventDto`.
- **Response**: HTTP 202 Accepted if the event is processed successfully.

Example:
```json
{
  "eventType": "USER_REGISTERED",
  "payload": { "userId": "123", "timestamp": "2025-12-23T10:00:00Z" }
}
```

## Running the Service

1. **Build the project**:
   ```sh
   ./mvnw clean package
   ```
2. **Run the application**:
   ```sh
   java -jar target/event-service-*.jar
   ```
3. **Configuration**: Adjust `application.properties` or `application.yml` for database and Kafka settings.

## Requirements
- Java 17 or higher
- Maven
- (Optional) Kafka and a database (e.g., PostgreSQL) for full functionality

## License
This project is part of the Shadow Ledger System. See the main repository for license details.

