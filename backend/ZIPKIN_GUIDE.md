# Zipkin Documentation (HMS Microservices)

This guide explains how to run, configure, and verify distributed tracing with Zipkin for this project.

## Scope

Services covered:
- `GatewayMS` (port `9000`)
- `UserMS` (port `8080`)
- `profileMS` (port `9100`)
- `AppointmentMS` (port `9200`)
- `PharmacyMS` (port `9300`)
- `MediaMS` (port `9400`)
- `EUREKA-SERVER` (port `8761`)

Zipkin:
- UI/API port: `9411`

---

## 1) Start Zipkin

### Docker (recommended)

```powershell
docker pull openzipkin/zipkin:latest
docker run -d --name zipkin -p 9411:9411 openzipkin/zipkin:latest
```

### Verify Zipkin is up

```powershell
Invoke-WebRequest -Uri "http://localhost:9411/zipkin/" -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:9411/api/v2/services" -UseBasicParsing
```

Open UI:
- `http://localhost:9411/zipkin/`

---

## 2) Tracing Configuration Used in This Project

The services are configured with Micrometer + Brave + Zipkin reporter.

### Required properties (`application.properties`)

```properties
management.zipkin.tracing.endpoint=http://localhost:9411/api/v2/spans
management.tracing.sampling.probability=1.0
management.tracing.brave.trace-id128=true
management.tracing.enabled=true
```

### Logging pattern used (trace/span in logs)

```properties
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} [%-5level] [%thread] [%-36.36logger{36}] [TraceId:%replace(%X{traceId}){'^$','-'}, SpanId:%replace(%X{spanId}){'^$','-'}] - %msg%n
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss.SSS} [%-5level] [%thread] [%-36.36logger{36}] [TraceId:%replace(%X{traceId}){'^$','-'}, SpanId:%replace(%X{spanId}){'^$','-'}] - %msg%n
```

### Eureka local-dev registration (important)

```properties
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
eureka.instance.hostname=localhost
eureka.instance.prefer-ip-address=false
eureka.instance.non-secure-port=${server.port}
eureka.instance.instance-id=${spring.application.name}:${server.port}
```

Why this matters:
- Prevents Gateway from routing to unreachable adapter IPs.
- Keeps local discovery stable (`localhost`).

---

## 3) Startup Order

Use this order in local development:

1. `Zipkin`
2. `EUREKA-SERVER`
3. Core services: `profileMS`, `UserMS`, `AppointmentMS`, `PharmacyMS`, `MediaMS`
4. `GatewayMS`

If startup order is wrong, traces may be partial or route calls may fail.

---

## 4) Verify Tracing End-to-End

### Step A: Confirm each service is healthy

```powershell
Invoke-WebRequest -Uri "http://localhost:8761/" -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:9000/actuator/health" -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:9100/actuator/health" -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:9200/actuator/health" -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:9300/actuator/health" -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:9400/actuator/health" -UseBasicParsing
```

### Step B: Trigger a request through Gateway

```powershell
Invoke-WebRequest -Uri "http://localhost:9000/user/register" -Method Post -ContentType "application/json" -Body '{"name":"trace-test","email":"trace@test.com","password":"test123","role":"PATIENT"}' -UseBasicParsing
```

### Step C: Check logs for same TraceId

- `GatewayMS/logs/gateway.log`
- `UserMS/logs/usersvc.log`
- `profileMS/logs/profile.log`

Expected:
- Same `TraceId` appears in all services for one request.
- `SpanId` changes per service/operation.

### Step D: Validate in Zipkin UI

In `http://localhost:9411/zipkin/`:
1. Choose service (for example `GatewayMS`)
2. Click **Run Query**
3. Open a trace
4. Confirm cross-service spans exist (`GatewayMS` -> downstream service)

---

## 5) Swagger Through Gateway and Docs Routes

Gateway is configured with docs routes such as:
- `/user-docs/**`
- `/profile-docs/**`
- `/appointment-docs/**`
- `/pharmacy-docs/**`
- `/media-docs/**`

If `http://localhost:9000/swagger-ui/index.html` shows upstream timeout errors, check:
1. Target service is running on expected port.
2. Eureka registration host is `localhost` (not machine adapter IP).
3. Gateway can resolve `lb://<ServiceId>` to a reachable instance.

---

## 6) Common Issues and Fixes

### Issue: Logs show `[TraceId:-, SpanId:-]`

Meaning:
- No active request span in that log line, or log emitted before/after request scope.

Fix/check:
- Trigger a real API request through Gateway.
- Check business logs (controller/service/JPA logs), not only framework startup logs.
- Ensure properties exist:
  - `management.tracing.enabled=true`
  - `management.tracing.sampling.probability=1.0`

### Issue: Gateway logs `Connection timed out ... /<service-docs>/v3/api-docs`

Fix/check:
- Confirm direct service endpoint works, e.g. `http://localhost:9200/v3/api-docs`.
- Confirm Eureka app entry host is `localhost`.

```powershell
(Invoke-WebRequest -Uri "http://localhost:8761/eureka/apps/APPOINTMENTMS" -UseBasicParsing).Content
```

If host is old adapter IP:
- Restart service after applying Eureka local-dev registration properties.
- Restart Gateway so route cache refreshes.

### Issue: Bean cycle around `WebClient.Builder`

Fix:
- Do not return `WebClient.Builder` bean that depends on `WebClient.Builder`.
- Use `WebClientCustomizer` bean instead.

### Issue: SQL appears but without trace context

Fix:
- Avoid `spring.jpa.show-sql=true`.
- Use logger-based SQL:

```properties
spring.jpa.show-sql=false
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.orm.jdbc.bind=TRACE
```

---

## 7) Minimal Production Notes

- Set `management.tracing.sampling.probability` lower than `1.0` (e.g. `0.1`) to reduce overhead.
- Use non-local Zipkin endpoint (internal DNS/service name).
- Keep Eureka hostname/IP settings environment-specific (do not force `localhost` outside local dev).

---

## 8) Quick Command Set (PowerShell)

```powershell
# Zipkin
Invoke-WebRequest -Uri "http://localhost:9411/zipkin/" -UseBasicParsing

# Eureka registration check
(Invoke-WebRequest -Uri "http://localhost:8761/eureka/apps/APPOINTMENTMS" -UseBasicParsing).Content

# Direct docs endpoint
Invoke-WebRequest -Uri "http://localhost:9200/v3/api-docs" -UseBasicParsing

# Gateway docs endpoint
Invoke-WebRequest -Uri "http://localhost:9000/appointment-docs/v3/api-docs" -UseBasicParsing
```

---

## 9) Expected Trace Behavior

For one incoming request:
- `TraceId` stays same across all services in that chain.
- `SpanId` changes at each service hop and internal operation.
- Zipkin groups all related spans under one trace.

If this behavior is visible in logs and Zipkin UI, tracing is correctly configured.

