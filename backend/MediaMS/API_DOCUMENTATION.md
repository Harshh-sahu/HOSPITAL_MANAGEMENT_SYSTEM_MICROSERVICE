# Media Service API Documentation

## Base Information

- **Service Name:** `MediaMS`
- **Default Port:** `9400`
- **Base URL:** `http://localhost:9400`
- **Swagger UI (primary):** `http://localhost:9400/swagger-ui/index.html`
- **Swagger UI (configured path):** `http://localhost:9400/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:9400/v3/api-docs`
- **OpenAPI YAML:** `http://localhost:9400/v3/api-docs.yaml`

## Authentication / Access

All business endpoints are protected and require this header:

```http
X-Secret-Key: SECRET
```

Swagger endpoints are publicly accessible:
- `/swagger-ui/**`
- `/swagger-ui.html`
- `/v3/api-docs/**`

## Swagger Setup and Usage (Deep)

1. Start the `MediaMS` service.
2. Open Swagger UI: `http://localhost:9400/swagger-ui/index.html`.
3. Click **Authorize**.
4. Provide API key value `SECRET` for header `X-Secret-Key`.
5. Use APIs under tag `Media APIs`.

Swagger includes:
- operation ids for integrations and client generation
- request/response schemas with examples
- multipart upload request documentation
- shared standard error schema (`ErrorInfo`) for `400`, `404`, and `500`

---

## Media APIs

**Controller Base Path:** `/media`

| Method | Endpoint | Description | Request | Success Response |
|---|---|---|---|---|
| POST | `/upload` | Upload a media file | `multipart/form-data` with field `file` | `200 OK` + `MediaFileDTO` |
| GET | `/{id}` | Download media file by id | Path param `id` | `200 OK` + file bytes (`application/octet-stream`) |

### Upload Request Example (cURL)

```bash
curl -X POST "http://localhost:9400/media/upload" \
  -H "X-Secret-Key: SECRET" \
  -F "file=@C:/path/to/report.pdf"
```

### Upload Response Example

```json
{
  "id": 101,
  "name": "report.pdf",
  "type": "application/pdf",
  "size": 582341,
  "storage": "DB"
}
```

### Download Request Example (cURL)

```bash
curl -X GET "http://localhost:9400/media/101" \
  -H "X-Secret-Key: SECRET" \
  --output report.pdf
```

---

## Error Response

```json
{
  "errorMessage": "File not found",
  "errorCode": 404,
  "timeStamp": "2026-03-30T17:00:00"
}
```

---

## DTO Reference

### MediaFileDTO

```json
{
  "id": 101,
  "name": "xray-report.pdf",
  "type": "application/pdf",
  "size": 582341,
  "storage": "DB"
}
```

