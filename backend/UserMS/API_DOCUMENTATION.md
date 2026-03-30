# User Service API Documentation

## Base Information

- **Service Name:** `UserMS`
- **Default Port:** `8080`
- **Base URL:** `http://localhost:8080`
- **Swagger UI (primary):** `http://localhost:8080/swagger-ui/index.html`
- **Swagger UI (configured path):** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`
- **OpenAPI YAML:** `http://localhost:8080/v3/api-docs.yaml`

## Authentication / Access

All business endpoints are protected and require this header:

```http
X-Secret-Key: SECRET
```

Swagger endpoints are publicly accessible:
- `/swagger-ui/**`
- `/swagger-ui.html`
- `/v3/api-docs/**`

## Swagger Usage

1. Start `UserMS`.
2. Open Swagger UI: `http://localhost:8080/swagger-ui/index.html`.
3. Click **Authorize** and enter `SECRET` for `X-Secret-Key`.
4. Explore APIs under tag `User APIs`.

Swagger includes:
- operation ids for client generation and integration mapping
- detailed request/response schemas with examples
- standardized global `400` and `500` error responses (`ErrorInfo`)
- validation constraints reflected in DTO schemas

---

## User APIs

**Controller Base Path:** `/user`

| Method | Endpoint | Description | Success Response |
|---|---|---|---|
| POST | `/register` | Register new user account | `201` + `ResponseDTO` |
| POST | `/login` | Authenticate and get JWT token | `200` + token string |
| GET | `/test` | Test endpoint | `200` + `"Test"` |
| GET | `/getProfile/{id}` | Get linked profile id by user id | `200` + `Long` |
| GET | `/getRegistrationCounts` | Monthly doctor/patient registration trends | `200` + `RegistrationCountDTO` |

### Register Request Example

```json
{
  "name": "Aman Sharma",
  "email": "aman@hms.com",
  "password": "Pass@1234",
  "role": "PATIENT"
}
```

### Login Request Example

```json
{
  "email": "aman@hms.com",
  "password": "Pass@1234"
}
```

### Login Response Example

```text
eyJhbGciOiJIUzI1NiJ9...
```

### Registration Counts Response Example

```json
{
  "doctorCounts": [
    {
      "month": "MARCH",
      "count": 8
    }
  ],
  "patientCounts": [
    {
      "month": "MARCH",
      "count": 21
    }
  ]
}
```

---

## Error Response

```json
{
  "errorMessage": "Invalid Credentials.",
  "errorCode": 500,
  "timeStamp": "2026-03-31T10:00:00"
}
```

