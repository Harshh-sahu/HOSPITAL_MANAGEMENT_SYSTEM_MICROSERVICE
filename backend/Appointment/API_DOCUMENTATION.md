# Appointment Service API Documentation

## Base Information

- **Service Name:** `AppointmentMS`
- **Default Port:** `9200`
- **Base URL:** `http://localhost:9200`
- **Swagger UI (primary):** `http://localhost:9200/swagger-ui/index.html`
- **Swagger UI (configured path):** `http://localhost:9200/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:9200/v3/api-docs`
- **OpenAPI YAML:** `http://localhost:9200/v3/api-docs.yaml`

## Swagger Usage (Deep)

1. Start `AppointmentMS`.
2. Open Swagger UI: `http://localhost:9200/swagger-ui/index.html`.
3. Click **Authorize**.
4. Enter API key value `SECRET` for header `X-Secret-Key`.
5. Execute APIs from these tags:
   - `Appointment APIs`
   - `Appointment Report APIs`

Swagger now includes:
- request/response examples
- schema-level field constraints (`required`, max length, nested models)
- global standardized `400` and `500` error response models (`ErrorInfo`)
- operation-level ids for easier integration and client generation

## Authentication / Access

All business endpoints are protected by `SecurityConfig` and require this header:

```http
X-Secret-Key: SECRET
```

Swagger endpoints are publicly accessible:
- `/swagger-ui/**`
- `/swagger-ui.html`
- `/v3/api-docs/**`

## Common Error Response

On handled exceptions, APIs return this structure:

```json
{
  "errorMessage": "Appointment not found",
  "errorCode": 500,
  "timeStamp": "2026-03-30T17:00:00"
}
```

Validation errors return HTTP `400` with the same structure and joined validation messages.

---

## Appointment APIs

**Controller Base Path:** `/appointment`

| Method | Endpoint | Description | Request Body | Success Response |
|---|---|---|---|---|
| POST | `/schedule` | Schedule a new appointment | `AppointmentDTO` | `201 Created` + `Long` (appointment id) |
| PUT | `/cancel/{appointmentId}` | Cancel appointment by id | - | `204 No Content` + message body |
| GET | `/get/{appointmentId}` | Get appointment details | - | `200 OK` + `AppointmentDTO` |
| GET | `/get/details/{appointmentId}` | Get appointment with patient/doctor names | - | `200 OK` + `AppointmentDetails` |
| GET | `/getAllByPatient/{patientId}` | Get all appointments by patient id | - | `200 OK` + list |
| GET | `/getAllByDoctor/{doctorId}` | Get all appointments by doctor id | - | `200 OK` + list |
| GET | `/countByPatient/{patientId}` | Monthly appointment counts for one patient | - | `200 OK` + `List<MonthlyVisitDTO>` |
| GET | `/countByDoctor/{doctorId}` | Monthly appointment counts for one doctor | - | `200 OK` + `List<MonthlyVisitDTO>` |
| GET | `/visitCount` | Overall monthly appointment counts | - | `200 OK` + `List<MonthlyVisitDTO>` |
| GET | `/countReasonsByPatient/{patientId}` | Reason counts for one patient | - | `200 OK` + `List<ReasonCountDTO>` |
| GET | `/countReasonsByDoctor/{doctorId}` | Reason counts for one doctor | - | `200 OK` + `List<ReasonCountDTO>` |
| GET | `/countReasons` | Overall reason counts | - | `200 OK` + `List<ReasonCountDTO>` |
| GET | `/getMedicinesByPatient/{patientId}` | Get medicines prescribed to a patient | - | `200 OK` + `List<MedicineDTO>` |
| GET | `/today` | Get today's appointments with names | - | `200 OK` + `List<AppointmentDetails>` |

---

## Appointment Report APIs

**Controller Base Path:** `/appointment/report`

| Method | Endpoint | Description | Request Body | Success Response |
|---|---|---|---|---|
| POST | `/create` | Create appointment report | `ApRecordDTO` | `201 Created` + `Long` (record id) |
| PUT | `/update` | Update appointment report | `ApRecordDTO` | `200 OK` + message |
| GET | `/getByAppointmentId/{appointmentId}` | Get report by appointment id | - | `200 OK` + `ApRecordDTO` |
| GET | `/getById/{recordId}` | Get report by record id | - | `200 OK` + `ApRecordDTO` |
| GET | `/getDetailsByAppointmentId/{recordId}` | Get enriched report details by appointment id (path variable is named `recordId` in code) | - | `200 OK` + `ApRecordDTO` |
| GET | `/getRecordsByPatientId/{patientId}` | Get all records by patient id | - | `200 OK` + `List<RecordDetails>` |
| GET | `/isRecordExists/{appointmentId}` | Check whether report exists for appointment | - | `200 OK` + `Boolean` |
| GET | `/getPrescriptionsByPatientId/{patientId}` | Get prescriptions by patient | - | `200 OK` + `List<PrescriptionDetails>` |
| GET | `/getAllPrescriptions` | Get all prescriptions | - | `200 OK` + `List<PrescriptionDetails>` |
| GET | `/getMedicinesByPrescriptionId/{prescriptionId}` | Get medicines by prescription id | - | `200 OK` + `List<MedicineDTO>` |

---

## DTO Reference

### AppointmentDTO

```json
{
  "id": 1,
  "patientId": 101,
  "doctorId": 501,
  "appointmentTime": "2026-03-31T10:30:00",
  "status": "SCHEDULED",
  "reason": "Fever",
  "notes": "First visit"
}
```

- `status` enum values: `SCHEDULED`, `CANCELLED`, `COMPLETED`

### ApRecordDTO

```json
{
  "id": 1,
  "patientId": 101,
  "doctorId": 501,
  "appointmentId": 7001,
  "symptoms": ["fever", "headache"],
  "diagnosis": "Viral infection",
  "tests": ["CBC"],
  "notes": "Hydration advised",
  "referral": "None",
  "prescription": {
    "id": 11,
    "patientId": 101,
    "doctorId": 501,
    "appointmentId": 7001,
    "prescriptionDate": "2026-03-30",
    "notes": "Take medicines after food",
    "medicines": [
      {
        "id": 1,
        "name": "Paracetamol",
        "medicineId": 10001,
        "dosage": "500mg",
        "frequency": "TID",
        "duration": 5,
        "route": "Oral",
        "type": "Tablet",
        "instructions": "After meals",
        "prescriptionId": 11
      }
    ]
  },
  "followUpDate": "2026-04-05",
  "createdAt": "2026-03-30T15:30:00"
}
```

### Aggregation DTOs

- `MonthlyVisitDTO`
  - `month` (`String`)
  - `count` (`Long`)
- `ReasonCountDTO`
  - `reason` (`String`)
  - `count` (`Long`)

---

## Error Message Keys Used by Service

Configured in `application.properties`:

- `APPOINTMENT_NOT_FOUND`
- `APPOINTMENT_ALREADY_CANCELLED`
- `DOCTOR_NOT_FOUND`
- `PATIENT_NOT_FOUND`
- `APPOINTMENT_RECORD_NOT_FOUND`

These keys are resolved by the global exception handler to human-readable messages.

