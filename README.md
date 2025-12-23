# Shadow Ledger System – API Endpoints

This document lists all REST endpoints exposed by the Shadow Ledger System with sample inputs for testing.

---

## API Gateway (Port 8080)

All external requests should go through the API Gateway.

### Create Event (USER role)

**POST** `/events`

**Headers**
- Authorization: Bearer <JWT_TOKEN>
- X-Trace-Id: TRACE-12345
- Content-Type: application/json

**Body**
```json
{
  "eventId": "E1001",
  "accountId": "A10",
  "type": "credit",
  "amount": 500,
  "timestamp": 1735561800000
}
```

---

### Drift Check (AUDITOR role)

**POST** `/drift-check`

**Headers**
- Authorization: Bearer <JWT_TOKEN>
- X-Trace-Id: TRACE-12345
- Content-Type: application/json

**Body**
```json
[
  {
    "accountId": "A10",
    "reportedBalance": 800.0
  }
]
```

---

### Manual Correction (ADMIN role)

**POST** `/correct/{accountId}`

**Headers**
- Authorization: Bearer <JWT_TOKEN>
- X-Trace-Id: TRACE-12345

**Query Parameters**
- type=credit
- amount=50

---

## Event Service (Port 8081)

### Create Event

**POST** `/events`

**Headers**
- X-Trace-Id: TRACE-12345
- Content-Type: application/json

**Body**
```json
{
  "eventId": "E1002",
  "accountId": "A10",
  "type": "debit",
  "amount": 50,
  "timestamp": 1735561900000
}
```

### Health Check

**GET** `/actuator/health`

---

## Shadow Ledger Service (Port 8082)

### Get Shadow Balance

**GET** `/accounts/{accountId}/shadow-balance`

**Example**

```
GET /accounts/A10/shadow-balance
```

**Response**
```json
{
  "accountId": "A10",
  "balance": 750.0,
  "lastEvent": "E1002"
}
```

### Health Check

**GET** `/actuator/health`

---

## Drift Correction Service (Port 8083)

### Drift Check

**POST** `/drift-check`

**Headers**
- X-Trace-Id: TRACE-12345
- Content-Type: application/json

**Body**
```json
[
  {
    "accountId": "A10",
    "reportedBalance": 800.0
  }
]
```

### Manual Correction

**POST** `/correct/{accountId}`

**Example**
```
POST /correct/A10?type=credit&amount=100
```

**Response**
```json
{
  "eventId": "MANUAL-A10-abc123",
  "accountId": "A10",
  "type": "credit",
  "amount": 100.0
}
```

### Health Check

**GET** `/actuator/health`

---

## Kafka Topics

| Topic                    | Purpose                  |
|--------------------------|--------------------------|
| transactions.raw         | Raw debit/credit events  |
| transactions.corrections | Correction events        |

