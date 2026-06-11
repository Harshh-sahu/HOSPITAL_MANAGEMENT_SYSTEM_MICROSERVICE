# Hospital Management System — MCP Server

A **Model Context Protocol (MCP) Server** built with Spring AI that exposes the Hospital Management System microservices as AI-callable tools. Connect any MCP-compatible AI client (Claude Code, Claude Desktop, custom agents) to interact with the HMS backend using natural language.

---

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Configuration](#configuration)
- [Running the Server](#running-the-server)
- [Connecting to an MCP Client](#connecting-to-an-mcp-client)
  - [Claude Code (CLI)](#1-claude-code-cli)
  - [Claude Desktop](#2-claude-desktop)
  - [MCP Inspector (Testing UI)](#3-mcp-inspector-testing-ui)
  - [Manual curl Test](#4-manual-curl-test)
- [All Tools Reference](#all-tools-reference)
  - [Quick Summary — All 35 Tools](#quick-summary--all-35-tools)
  - [User Tools](#-user-tools)
  - [Doctor Tools](#-doctor-tools)
  - [Patient Tools](#-patient-tools)
  - [Appointment Tools](#-appointment-tools)
  - [Pharmacy Tools](#-pharmacy-tools)
- [Error Handling](#error-handling)
- [Troubleshooting](#troubleshooting)

---

## Overview

| Property | Value |
|----------|-------|
| **Protocol** | Model Context Protocol (MCP) 2024-11-05 |
| **Transport** | HTTP SSE (Server-Sent Events) |
| **Port** | `8090` |
| **SSE Endpoint** | `http://localhost:8090/sse` |
| **Message Endpoint** | `http://localhost:8090/mcp/message` |
| **Total Tools** | 35 |
| **Framework** | Spring Boot 3.x + Spring AI 1.1.7 |

---

## Architecture

```
┌─────────────────────────────────────────────────────┐
│                   MCP Client                        │
│         (Claude Code / Claude Desktop)              │
└───────────────────┬─────────────────────────────────┘
                    │  MCP Protocol (SSE)
                    ▼
┌─────────────────────────────────────────────────────┐
│           HMS MCP Server  :8090                     │
│                                                     │
│  UserTools  DoctorTools  PatientTools               │
│  AppointmentTools  PharmacyTools                    │
└──────┬──────────┬──────────┬──────────┬─────────────┘
       │          │          │          │
       ▼          ▼          ▼          ▼
  UserMS      ProfileMS  AppointmentMS  PharmacyMS
  :8080        :9100        :9200        :9300
```

### Microservices

| Service | Port | Responsibility |
|---------|------|----------------|
| **UserMS** | 8080 | Authentication, user registration, JWT |
| **ProfileMS** | 9100 | Doctor and patient profile management |
| **AppointmentMS** | 9200 | Appointments, medical records, prescriptions |
| **PharmacyMS** | 9300 | Medicine catalog, inventory, sales |
| **GatewayMS** | 9000 | API Gateway (Eureka load balancer) |
| **Eureka Server** | 8761 | Service discovery and registry |

---

## Prerequisites

Before starting the MCP server, ensure the following are installed and running:

| Requirement | Version | Notes |
|-------------|---------|-------|
| **Java (JDK)** | 17+ | Check: `java -version` |
| **Maven** | 3.8+ | Or use included `mvnw` wrapper |
| **MySQL** | 8.0+ | Database `hms_mcp` is auto-created |
| **HMS Microservices** | — | UserMS, ProfileMS, AppointmentMS, PharmacyMS |
| **Eureka Server** | — | Running at `http://localhost:8761` |

### Start order

Start services in this order to avoid connection failures:

1. MySQL database server
2. Eureka Server (`backend/EUREKA-SERVER`)
3. UserMS (`backend/UserMS`)
4. ProfileMS (`backend/profileMS`)
5. AppointmentMS (`backend/Appointment`)
6. PharmacyMS (`backend/PharmacyMS`)
7. **HMS MCP Server** (`backend/Hospital-Management-System-Model-context-Server`) ← this service

---

## Configuration

All configuration is in `src/main/resources/application.yaml`.

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hms_mcp?createDatabaseIfNotExist=true
    username: root          # override with DB_USER env var
    password: root          # override with DB_PASSWORD env var

server:
  port: 8090

hms:
  user-service-url: http://localhost:8080       # override with USER_SERVICE_URL
  profile-service-url: http://localhost:9100    # override with PROFILE_SERVICE_URL
  appointment-service-url: http://localhost:9200 # override with APPOINTMENT_SERVICE_URL
  pharmacy-service-url: http://localhost:9300   # override with PHARMACY_SERVICE_URL
  secret-key: SECRET                            # override with HMS_SECRET_KEY
```

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_USER` | `root` | MySQL username |
| `DB_PASSWORD` | `root` | MySQL password |
| `USER_SERVICE_URL` | `http://localhost:8080` | UserMS base URL |
| `PROFILE_SERVICE_URL` | `http://localhost:9100` | ProfileMS base URL |
| `APPOINTMENT_SERVICE_URL` | `http://localhost:9200` | AppointmentMS base URL |
| `PHARMACY_SERVICE_URL` | `http://localhost:9300` | PharmacyMS base URL |
| `HMS_SECRET_KEY` | `SECRET` | Internal service auth key |
| `AZURE_OPENAI_API_KEY` | — | Azure OpenAI API key |
| `AZURE_OPENAI_ENDPOINT` | — | Azure OpenAI endpoint URL |
| `AZURE_OPENAI_DEPLOYMENT` | `gpt-4o` | Azure OpenAI deployment name |

---

## Running the Server

### Step 1 — Set required environment variables

The server requires Azure OpenAI credentials at startup:

**Linux / macOS:**
```bash
export DB_USER=root
export DB_PASSWORD=yourpassword
export AZURE_OPENAI_API_KEY=your-azure-api-key
export AZURE_OPENAI_ENDPOINT=https://your-azure-endpoint
export AZURE_OPENAI_DEPLOYMENT=gpt-4o
export HMS_SECRET_KEY=SECRET
```

**Windows (PowerShell):**
```powershell
$env:DB_USER = "root"
$env:DB_PASSWORD = "yourpassword"
$env:AZURE_OPENAI_API_KEY = "your-azure-api-key"
$env:AZURE_OPENAI_ENDPOINT = "https://your-azure-endpoint"
$env:AZURE_OPENAI_DEPLOYMENT = "gpt-4o"
$env:HMS_SECRET_KEY = "SECRET"
```

**Windows (Command Prompt):**
```cmd
set DB_USER=root
set DB_PASSWORD=yourpassword
set AZURE_OPENAI_API_KEY=your-azure-api-key
set AZURE_OPENAI_ENDPOINT=https://your-azure-endpoint
```

### Step 2 — Build and run

```bash
# Navigate to MCP server directory
cd backend/Hospital-Management-System-Model-context-Server

# Linux / macOS
./mvnw spring-boot:run

# Windows (PowerShell / CMD)
.\mvnw.cmd spring-boot:run
```

**Or build a JAR and run it:**
```bash
# Build
./mvnw clean package -DskipTests

# Run
java -jar target/Hospital-Management-System-Model-context-Server-0.0.1-SNAPSHOT.jar
```

### Step 3 — Verify startup

Successful startup log output:
```
Started HospitalManagementSystemModelContextServerApplication in X.XXX seconds
```

Verify the SSE endpoint is live:
```bash
# Linux / macOS
curl -N http://localhost:8090/sse

# Windows (PowerShell)
Invoke-WebRequest -Uri "http://localhost:8090/sse" -UseBasicParsing
```

Expected response:
```
event: endpoint
data: /mcp/message?sessionId=<YOUR_SESSION_ID>
```

---

## Connecting to an MCP Client

### 1. Claude Code (CLI)

**Option A — via CLI command:**
```bash
claude mcp add hms-mcp --transport sse http://localhost:8090/sse
```

**Option B — via settings file** (`~/.claude/settings.json` or `.claude/settings.json`):
```json
{
  "mcpServers": {
    "hms-mcp": {
      "type": "sse",
      "url": "http://localhost:8090/sse"
    }
  }
}
```

Restart Claude Code after adding. You can then use natural language:
> *"Get all doctors"*
> *"Schedule an appointment for patient 1 with doctor 1 tomorrow at 10am"*
> *"Show today's appointments"*

---

### 2. Claude Desktop

Add to `claude_desktop_config.json`:

**macOS:** `~/Library/Application Support/Claude/claude_desktop_config.json`
**Windows:** `%APPDATA%\Claude\claude_desktop_config.json`

```json
{
  "mcpServers": {
    "hms-mcp": {
      "type": "sse",
      "url": "http://localhost:8090/sse"
    }
  }
}
```

Restart Claude Desktop after saving.

---

### 3. MCP Inspector (Testing UI)

The fastest way to visually test all tools:

```bash
npx @modelcontextprotocol/inspector
```

1. Open the Inspector UI in your browser
2. Set transport to **SSE**
3. Enter URL: `http://localhost:8090/sse`
4. Click **Connect**
5. Browse and call all 34 tools interactively

---

### 4. Manual curl Test

**Step 1 — Get a session ID:**
```bash
curl -N http://localhost:8090/sse
# Response:
# event: endpoint
# data: /mcp/message?sessionId=<YOUR_SESSION_ID>
```

**Step 2 — Initialize the session:**
```bash
curl -X POST "http://localhost:8090/mcp/message?sessionId=<YOUR_SESSION_ID>" \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "id": 1,
    "method": "initialize",
    "params": {
      "protocolVersion": "2024-11-05",
      "capabilities": {},
      "clientInfo": { "name": "curl-client", "version": "1.0" }
    }
  }'
```

**Step 3 — List all tools:**
```bash
curl -X POST "http://localhost:8090/mcp/message?sessionId=<YOUR_SESSION_ID>" \
  -H "Content-Type: application/json" \
  -d '{"jsonrpc":"2.0","id":2,"method":"tools/list","params":{}}'
```

**Step 4 — Call a tool:**
```bash
curl -X POST "http://localhost:8090/mcp/message?sessionId=<YOUR_SESSION_ID>" \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "id": 3,
    "method": "tools/call",
    "params": {
      "name": "getAllDoctors",
      "arguments": {}
    }
  }'
```

---

## All Tools Reference

> All tools return JSON strings. On error, they return `"Error <action>: <message>"`.

### Quick Summary — All 34 Tools

| # | Tool Name | Category | Description |
|---|-----------|----------|-------------|
| 1 | `registerUser` | 👤 User | Register a new user (ADMIN/DOCTOR/PATIENT) |
| 2 | `loginUser` | 👤 User | Login and get JWT token |
| 3 | `logoutUser` | 👤 User | Invalidate a JWT token |
| 4 | `getUserProfileId` | 👤 User | Get linked profile ID for a user |
| 5 | `getMonthlyRegistrationCounts` | 👤 User | Monthly doctor/patient registration stats |
| 6 | `addDoctor` | 🩺 Doctor | Add a new doctor profile |
| 7 | `getDoctorById` | 🩺 Doctor | Get doctor profile by ID |
| 8 | `getAllDoctors` | 🩺 Doctor | List all doctors |
| 9 | `getDoctorDropdowns` | 🩺 Doctor | Lightweight ID+name list for dropdowns |
| 10 | `updateDoctor` | 🩺 Doctor | Update a doctor's profile |
| 11 | `addPatient` | 🏥 Patient | Add a new patient profile |
| 12 | `getPatientById` | 🏥 Patient | Get patient profile by ID |
| 13 | `getAllPatients` | 🏥 Patient | List all patients |
| 14 | `updatePatient` | 🏥 Patient | Update a patient's profile |
| 15 | `scheduleAppointment` | 📅 Appointment | Schedule a new appointment |
| 16 | `cancelAppointment` | 📅 Appointment | Cancel an appointment |
| 17 | `getAppointmentDetails` | 📅 Appointment | Get enriched appointment details |
| 18 | `getAppointmentsByPatient` | 📅 Appointment | All appointments for a patient |
| 19 | `getAppointmentsByDoctor` | 📅 Appointment | All appointments for a doctor |
| 20 | `getTodaysAppointments` | 📅 Appointment | Today's full schedule |
| 21 | `getMonthlyVisitCounts` | 📅 Appointment | Month-wise visit statistics |
| 22 | `createAppointmentReport` | 📅 Appointment | Create post-consultation medical record |
| 23 | `getReportByAppointmentId` | 📅 Appointment | Get report for an appointment |
| 24 | `getPatientMedicalHistory` | 📅 Appointment | Full medical history for a patient |
| 25 | `getPatientPrescriptions` | 📅 Appointment | All prescriptions for a patient |
| 26 | `getAppointmentReasonCounts` | 📅 Appointment | Most common visit reasons |
| 27 | `getMedicinesByPatient` | 📅 Appointment | All medicines ever prescribed to a patient |
| 28 | `addMedicine` | 💊 Pharmacy | Add a medicine to catalog |
| 29 | `getMedicineById` | 💊 Pharmacy | Get medicine details by ID |
| 30 | `getAllMedicines` | 💊 Pharmacy | List all medicines in catalog |
| 31 | `updateMedicine` | 💊 Pharmacy | Update a medicine in the catalog |
| 32 | `createSale` | 💊 Pharmacy | Process a pharmacy sale |
| 33 | `getSaleById` | 💊 Pharmacy | Get a sale record by ID |
| 34 | `getAllSales` | 💊 Pharmacy | List all pharmacy sales |
| 35 | `getSaleItems` | 💊 Pharmacy | Get line items for a specific sale |

> **Note:** The tool count is 35. The `getSaleItems` tool was previously undercounted in the overview table.

---

### 👤 User Tools

#### `registerUser`
Register a new user account in the HMS system.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `name` | String | ✅ | Full name |
| `email` | String | ✅ | Unique email address |
| `password` | String | ✅ | Account password |
| `role` | String | ✅ | `ADMIN`, `DOCTOR`, or `PATIENT` |

**Example:**
```json
{ "name": "Dr. John", "email": "john@hms.com", "password": "pass123", "role": "DOCTOR" }
```
**Returns:** `{"message": "Account created."}`

---

#### `loginUser`
Authenticate a user and receive a JWT token.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `email` | String | ✅ | Registered email |
| `password` | String | ✅ | Account password |

**Returns:** JWT token string (e.g. `eyJhbGciOiJIUzI1NiJ9...`)

---

#### `logoutUser`
Logout the current session. Invalidates the JWT on the client side.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `jwtToken` | String | ✅ | The JWT token to invalidate |

**Returns:** `{"status":"success","message":"Logged out successfully. Please discard your JWT token."}`

---

#### `getUserProfileId`
Get the linked doctor/patient profile ID for a user account.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `userId` | Long | ✅ | User account ID |

**Returns:** Profile ID (Long)

---

#### `getMonthlyRegistrationCounts`
Get month-wise registration trend for doctors and patients.

**Returns:**
```json
{
  "doctorCounts": [{"month": "March", "count": 1}],
  "patientCounts": [{"month": "March", "count": 1}]
}
```

---

### 🩺 Doctor Tools

#### `addDoctor`
Create a new doctor profile in the system.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `name` | String | ✅ | Full name |
| `email` | String | ✅ | Unique email |
| `dob` | String | ✅ | Date of birth (`YYYY-MM-DD`) |
| `phone` | String | ✅ | Contact number |
| `address` | String | ✅ | Address |
| `licenseNo` | String | ✅ | Unique medical license number |
| `specialization` | String | ✅ | e.g. `Cardiology`, `Neurology` |
| `department` | String | ✅ | e.g. `ICU`, `OPD`, `Emergency` |
| `totalExp` | Integer | ✅ | Years of experience |

**Returns:** Generated doctor ID (Long)

---

#### `getDoctorById`
Get a doctor's full profile by their ID.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `doctorId` | Long | ✅ | Doctor ID |

**Returns:** Doctor profile object

---

#### `getAllDoctors`
Get all doctors registered in the hospital.

**Returns:** Array of doctor profile objects

---

#### `getDoctorDropdowns`
Get a lightweight list of doctors (ID + name) for dropdowns.

**Returns:**
```json
[{"id": 1, "name": "Dr. Harsh Sahu"}, ...]
```

---

#### `updateDoctor`
Update an existing doctor's profile.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `doctorId` | Long | ✅ | Doctor ID to update |
| `name` | String | ✅ | Updated name |
| `email` | String | ✅ | Updated email |
| `dob` | String | ✅ | Date of birth (`YYYY-MM-DD`) |
| `phone` | String | ✅ | Updated phone |
| `address` | String | ✅ | Updated address |
| `licenseNo` | String | ✅ | License number |
| `specialization` | String | ✅ | Specialization |
| `department` | String | ✅ | Department |
| `totalExp` | Integer | ✅ | Years of experience |

**Returns:** Updated doctor profile object

---

### 🏥 Patient Tools

#### `addPatient`
Create a new patient profile in the system.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `name` | String | ✅ | Full name |
| `email` | String | ✅ | Unique email |
| `dob` | String | ✅ | Date of birth (`YYYY-MM-DD`) |
| `phone` | String | ✅ | Contact number |
| `address` | String | ✅ | Address |
| `aadharNo` | String | ✅ | Unique Aadhaar number |
| `bloodGroup` | String | ✅ | `A_POSITIVE`, `A_NEGATIVE`, `B_POSITIVE`, `B_NEGATIVE`, `O_POSITIVE`, `O_NEGATIVE`, `AB_POSITIVE`, `AB_NEGATIVE` |
| `allergies` | String | ✅ | Comma-separated or `None` |
| `chronicDiseases` | String | ✅ | Comma-separated or `None` |

**Returns:** Generated patient ID (Long)

---

#### `getPatientById`
Get a patient's full profile by their ID.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `patientId` | Long | ✅ | Patient ID |

**Returns:** Patient profile object

---

#### `getAllPatients`
Get all patients registered in the hospital.

**Returns:** Array of patient profile objects

---

#### `updatePatient`
Update an existing patient's profile.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `patientId` | Long | ✅ | Patient ID to update |
| `name` | String | ✅ | Updated name |
| `email` | String | ✅ | Updated email |
| `dob` | String | ✅ | Date of birth (`YYYY-MM-DD`) |
| `phone` | String | ✅ | Updated phone |
| `address` | String | ✅ | Updated address |
| `aadharNo` | String | ✅ | Aadhaar number |
| `bloodGroup` | String | ✅ | Blood group |
| `allergies` | String | ✅ | Allergies |
| `chronicDiseases` | String | ✅ | Chronic diseases |

**Returns:** Updated patient profile object

---

### 📅 Appointment Tools

#### `scheduleAppointment`
Book a new appointment for a patient with a doctor.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `patientId` | Long | ✅ | Patient ID |
| `doctorId` | Long | ✅ | Doctor ID |
| `appointmentTime` | String | ✅ | Format: `YYYY-MM-DDTHH:MM:SS` (e.g. `2025-07-15T10:30:00`) |
| `reason` | String | ✅ | Visit reason (e.g. `Chest pain`) |
| `notes` | String | ❌ | Additional notes |

**Returns:** Generated appointment ID (Long)

---

#### `cancelAppointment`
Cancel an existing appointment.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `appointmentId` | Long | ✅ | Appointment ID |

**Returns:** `"Appointment Cancelled"`

---

#### `getAppointmentDetails`
Get enriched appointment details including patient and doctor names.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `appointmentId` | Long | ✅ | Appointment ID |

**Returns:** Appointment object with patient name, doctor name, time, reason, status

---

#### `getAppointmentsByPatient`
Get all appointments (past and upcoming) for a patient.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `patientId` | Long | ✅ | Patient ID |

**Returns:** Array of appointment objects

---

#### `getAppointmentsByDoctor`
Get all appointments assigned to a doctor.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `doctorId` | Long | ✅ | Doctor ID |

**Returns:** Array of appointment objects

---

#### `getTodaysAppointments`
Get all appointments scheduled for today with patient and doctor names.

**Returns:** Array of today's appointment objects

---

#### `getMonthlyVisitCounts`
Get month-wise appointment statistics for the hospital.

**Returns:**
```json
[{"month": "April", "count": 5}, {"month": "May", "count": 8}]
```

---

#### `createAppointmentReport`
Create a post-consultation medical record.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `appointmentId` | Long | ✅ | Appointment ID |
| `diagnosis` | String | ✅ | Doctor's diagnosis |
| `notes` | String | ✅ | Additional clinical notes |

**Returns:** Generated record ID (Long)

---

#### `getReportByAppointmentId`
Get the medical record for a specific appointment.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `appointmentId` | Long | ✅ | Appointment ID |

**Returns:** Appointment report object

---

#### `getPatientMedicalHistory`
Get a patient's complete medical history (all records).

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `patientId` | Long | ✅ | Patient ID |

**Returns:** Array of medical record objects with diagnosis and notes

---

#### `getPatientPrescriptions`
Get all prescriptions issued to a patient across all visits.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `patientId` | Long | ✅ | Patient ID |

**Returns:** Array of prescription objects with medicine details

---

#### `getAppointmentReasonCounts`
Get the most common visit reasons across all appointments.

**Returns:**
```json
[{"reason": "Fever", "count": 12}, {"reason": "Chest pain", "count": 7}]
```

---

#### `getMedicinesByPatient`
Get all medicines ever prescribed to a patient.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `patientId` | Long | ✅ | Patient ID |

**Returns:** Array of medicine objects

---

### 💊 Pharmacy Tools

#### `addMedicine`
Add a new medicine to the pharmacy catalog.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `name` | String | ✅ | Medicine name |
| `dosage` | String | ✅ | Dosage (e.g. `500mg`) |
| `category` | String | ✅ | `ANTIBIOTIC`, `ANALGESIC`, `ANTIPYRETIC`, `ANTIVIRAL`, `ANTIFUNGAL`, `VITAMIN`, `SUPPLEMENT`, `OTHER` |
| `type` | String | ✅ | `TABLET`, `CAPSULE`, `SYRUP`, `INJECTION`, `CREAM`, `DROPS` |
| `manufacturer` | String | ✅ | Manufacturer name |
| `unitPrice` | Integer | ✅ | Price per unit |
| `stock` | Integer | ✅ | Initial stock count |

**Returns:** Generated medicine ID (Long)

---

#### `getMedicineById`
Get a medicine's full details by ID.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `medicineId` | Long | ✅ | Medicine ID |

**Returns:** Medicine object with name, dosage, category, type, price, stock

---

#### `getAllMedicines`
Get the complete pharmacy medicine catalog.

**Returns:** Array of medicine objects

---

#### `updateMedicine`
Update an existing medicine in the catalog.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `medicineId` | Long | ✅ | Medicine ID to update |
| `name` | String | ✅ | Medicine name |
| `dosage` | String | ✅ | Dosage |
| `category` | String | ✅ | Category |
| `type` | String | ✅ | Type |
| `manufacturer` | String | ✅ | Manufacturer |
| `unitPrice` | Integer | ✅ | Unit price |
| `stock` | Integer | ✅ | Stock count |

**Returns:** `"Medicine Updated!"`

---

#### `createSale`
Process a pharmacy sale for medicine purchases.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `prescriptionId` | Long | ❌ | Linked prescription ID (pass `0` if none) |
| `buyerName` | String | ✅ | Buyer's name |
| `buyerContact` | String | ✅ | Buyer's contact number |
| `saleItemsJson` | String | ✅ | JSON array: `[{"medicineId":1,"quantity":2}]` |

**Returns:** Generated sale ID (Long)

---

#### `getSaleById`
Get a pharmacy sale record by sale ID.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `saleId` | Long | ✅ | Sale ID |

**Returns:** Sale object with buyer info, total amount, prescription link

---

#### `getAllSales`
Get the complete pharmacy sales history.

**Returns:** Array of sale objects

---

#### `getSaleItems`
Get all line items (medicines + quantities) for a specific sale.

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `saleId` | Long | ✅ | Sale ID |

**Returns:** Array of sale item objects with medicine details and quantities

---

## Error Handling

All tools return a plain error string on failure:

```
"Error fetching doctor: 404 Not Found: ..."
"Error scheduling appointment: Connection refused"
```

Common causes:
| Error | Cause |
|-------|-------|
| `403 Forbidden` | `X-Secret-Key` header missing or wrong (check `hms.secret-key` in config) |
| `Connection refused` | Target microservice is not running |
| `404 Not Found` | Resource with given ID does not exist |
| `500 Internal Server Error` | Validation failed or business logic error |

---

## Troubleshooting

**MCP server starts but Claude Code can't connect**
- Ensure the server is running on port 8090
- Check firewall isn't blocking port 8090
- Verify SSE endpoint: `curl -N http://localhost:8090/sse`

**All tool calls return `403`**
- The `X-Secret-Key` header value is wrong
- Set `HMS_SECRET_KEY` env var to match the value in each microservice's `SecurityConfig`

**Tool calls return `Connection refused`**
- The target microservice is not running
- Start all microservices before starting the MCP server

**`MethodToolCallbackProvider` class not found**
- Wrong Spring AI version — this server requires Spring AI `1.1.7`
- Run `./mvnw dependency:resolve` to ensure dependencies are downloaded

**Azure OpenAI errors on startup**
- Set `AZURE_OPENAI_API_KEY` and `AZURE_OPENAI_ENDPOINT` environment variables
- These are required even if you only use MCP tools (not the AI chat features)

**Windows: `./mvnw` command not recognized**
- Use `.\mvnw.cmd spring-boot:run` instead of `./mvnw spring-boot:run`

**MySQL connection refused**
- Ensure MySQL is running on port 3306
- Verify `DB_USER` and `DB_PASSWORD` environment variables match your MySQL credentials
- The database `hms_mcp` is auto-created on first startup if it doesn't exist

**`Failed to configure a DataSource` error on startup**
- MySQL is not reachable — start MySQL before running the MCP server
- Double-check the datasource URL in `application.yaml`
