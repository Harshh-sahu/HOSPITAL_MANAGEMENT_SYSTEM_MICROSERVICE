# Pharmacy Service API Documentation

## Base Information

- **Service Name:** `PharmacyMS`
- **Default Port:** `9300`
- **Base URL:** `http://localhost:9300`
- **Swagger UI (primary):** `http://localhost:9300/swagger-ui/index.html`
- **Swagger UI (configured path):** `http://localhost:9300/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:9300/v3/api-docs`
- **OpenAPI YAML:** `http://localhost:9300/v3/api-docs.yaml`

## Authentication / Access

All business endpoints are protected and require this header:

```http
X-Secret-Key: SECRET
```

Swagger endpoints are publicly accessible:
- `/swagger-ui/**`
- `/swagger-ui.html`
- `/v3/api-docs/**`

## Swagger Setup and Usage

1. Start `PharmacyMS`.
2. Open Swagger UI at `http://localhost:9300/swagger-ui/index.html`.
3. Click **Authorize** and enter `SECRET` for header `X-Secret-Key`.
4. Explore these tags:
   - `Pharmacy Medicine APIs`
   - `Pharmacy Inventory APIs`
   - `Pharmacy Sale APIs`

Swagger includes:
- operation ids for easier client generation and integration mapping
- request/response contracts with examples
- shared standard error model (`ErrorInfo`) with global `400` and `500` responses

---

## API Groups

### Medicine APIs

- Base path: `/pharmacy/medicines`
- Endpoints:
  - `POST /add`
  - `GET /get/{id}`
  - `PUT /update`
  - `GET /getAll`

### Inventory APIs

- Base path: `/pharmacy/inventory`
- Endpoints:
  - `POST /add`
  - `PUT /update`
  - `GET /get/{id}`
  - `GET /getAll`

### Sale APIs

- Base path: `/pharmacy/sales`
- Endpoints:
  - `POST /create`
  - `PUT /update`
  - `GET /getSaleItem/{saleId}`
  - `GET /get/{id}`
  - `GET /getAll`

---

## Standard Error Response

```json
{
  "errorMessage": "Medicine not found",
  "errorCode": 500,
  "timeStamp": "2026-03-30T17:00:00"
}
```

