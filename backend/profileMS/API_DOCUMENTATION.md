# Profile Service API Documentation

## Base Information

- **Service Name:** `profileMS`
- **Default Port:** `9100`
- **Base URL:** `http://localhost:9100`
- **Swagger UI (primary):** `http://localhost:9100/swagger-ui/index.html`
- **Swagger UI (configured path):** `http://localhost:9100/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:9100/v3/api-docs`
- **OpenAPI YAML:** `http://localhost:9100/v3/api-docs.yaml`

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

1. Start `profileMS`.
2. Open Swagger UI: `http://localhost:9100/swagger-ui/index.html`.
3. Click **Authorize** and enter `SECRET` for `X-Secret-Key`.
4. Use APIs under these tags:
   - `Profile Doctor APIs`
   - `Profile Patient APIs`

Swagger includes:
- operation IDs for integration/client generation
- request and response schemas with examples
- global `400` and `500` standardized error responses (`ErrorInfo`)
- field-level validation constraints in DTO schemas

---

## Doctor APIs

**Controller Base Path:** `profile/doctor`

| Method | Endpoint | Description | Response |
|---|---|---|---|
| POST | `/add` | Create doctor profile | `201` + doctor id |
| GET | `/get/{id}` | Get doctor by id | `200` + `DoctorDTO` |
| GET | `/getProfileId/{id}` | Get doctor profile picture media id | `200` + `Long` |
| PUT | `/update` | Update doctor profile | `200` + `DoctorDTO` |
| GET | `/exists/{id}` | Check doctor exists | `200` + `Boolean` |
| GET | `/dropdowns` | Get doctor dropdown list | `200` + `List<DoctorDropdown>` |
| GET | `/getDoctorsById?ids=1001,1002` | Get doctors by ids | `200` + `List<DoctorDropdown>` |
| GET | `/getAll` | Get all doctors | `200` + `List<DoctorDTO>` |

## Patient APIs

**Controller Base Path:** `profile/patient`

| Method | Endpoint | Description | Response |
|---|---|---|---|
| POST | `/add` | Create patient profile | `201` + patient id |
| GET | `/get/{id}` | Get patient by id | `200` + `PatientDTO` |
| GET | `/getProfileId/{id}` | Get patient profile picture media id | `200` + `Long` |
| PUT | `/update` | Update patient profile | `200` + `PatientDTO` |
| GET | `/exists/{id}` | Check patient exists | `200` + `Boolean` |
| GET | `/getPatientsById?ids=2001,2002` | Get patients by ids | `200` + `List<DoctorDropdown>` |
| GET | `/getAll` | Get all patients | `200` + `List<PatientDTO>` |

---

## Error Response

```json
{
  "errorMessage": "Doctor not found",
  "errorCode": 500,
  "timeStamp": "2026-03-30T17:00:00"
}
```

