# Hospital Management System - Backend

## Eureka Server Configuration Guide

This section documents `EUREKA-SERVER/src/main/resources/application.yml`.

### What this config does
- Runs Eureka Server on `8761`
- Enables Actuator and Prometheus metrics endpoint
- Exports traces to Zipkin
- Configures Swagger/OpenAPI
- Uses trace-aware logging format with file rotation

---

## 1) Application and Port

```yaml
spring:
  application:
    name: EurekaServer

server:
  port: 8761
```

- Service name is `EurekaServer`
- Eureka dashboard is available on `http://localhost:8761/`

---

## 2) Eureka Server Mode

```yaml
eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
  server:
    wait-time-in-ms-when-sync-empty: 0
    enable-self-preservation: false
  instance:
    hostname: localhost
```

- `register-with-eureka: false`: server does not register itself
- `fetch-registry: false`: server does not pull registry from peers
- `enable-self-preservation: false`: okay for local/dev only
- `hostname: localhost`: local development friendly

---

## 3) Actuator, Prometheus, and Tracing

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
    prometheus:
      enabled: true
  zipkin:
    tracing:
      endpoint: http://localhost:9411/api/v2/spans
  tracing:
    sampling:
      probability: 1.0
    brave:
      trace-id128: true
    enabled: true
  metrics:
    export:
      prometheus:
        enabled: true
    tags:
      application: ${spring.application.name}
```

### Meaning
- Exposes:
  - `/actuator/health`
  - `/actuator/info`
  - `/actuator/metrics`
  - `/actuator/prometheus`
- Sends spans to Zipkin at `http://localhost:9411/api/v2/spans`
- Samples 100% traces in dev (`probability: 1.0`)
- Adds metrics tag `application=EurekaServer`

---

## 4) OpenAPI / Swagger

```yaml
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: method
    tags-sorter: alpha
```

- API docs JSON: `http://localhost:8761/v3/api-docs`
- Swagger UI: `http://localhost:8761/swagger-ui.html`

---

## 5) Logging

```yaml
logging:
  pattern:
    level: "%5p [${spring.application.name},%replace(%X{traceId}){'^$','-'},%replace(%X{spanId}){'^$','-'}]"
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%-5level] [%thread] [%-36.36logger{36}] [TraceId:%replace(%X{traceId}){'^$','-'}, SpanId:%replace(%X{spanId}){'^$','-'}] - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%-5level] [%thread] [%-36.36logger{36}] [TraceId:%replace(%X{traceId}){'^$','-'}, SpanId:%replace(%X{spanId}){'^$','-'}] - %msg%n"
  level:
    root: INFO
    com.netflix.eureka: WARN
    com.netflix.discovery: WARN
    org.springframework.boot: INFO
    org.springframework.web: INFO
  file:
    name: logs/eureka-server.log
    max-size: 10MB
    max-history: 10
    total-size-cap: 1GB
```

### Meaning
- Includes TraceId/SpanId in logs (fallback `-` if absent)
- Reduces noisy Eureka internals to `WARN`
- Writes logs to `logs/eureka-server.log` with rotation

---

## 6) Useful URLs

- Eureka dashboard: `http://localhost:8761/`
- Health: `http://localhost:8761/actuator/health`
- Prometheus metrics: `http://localhost:8761/actuator/prometheus`
- OpenAPI JSON: `http://localhost:8761/v3/api-docs`
- Swagger UI: `http://localhost:8761/swagger-ui.html`
- Zipkin UI: `http://localhost:9411/zipkin/`

---

## 7) Quick verification (PowerShell)

```powershell
Invoke-WebRequest -Uri "http://localhost:8761/" -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:8761/actuator/health" -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:8761/actuator/prometheus" -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:8761/v3/api-docs" -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:9411/api/v2/services" -UseBasicParsing
```

---

## 8) Production notes

For production, consider:
- Set `eureka.server.enable-self-preservation: true`
- Lower trace sampling (`management.tracing.sampling.probability`, e.g. `0.1`)
- Restrict actuator endpoint exposure and secure access
- Externalize Zipkin endpoint and credentials with environment config

---

## 9) Complete Endpoints Reference

### Core service base URLs
- GatewayMS: `http://localhost:9000`
- UserMS: `http://localhost:8080`
- profileMS: `http://localhost:9100`
- AppointmentMS: `http://localhost:9200`
- PharmacyMS: `http://localhost:9300`
- MediaMS: `http://localhost:9400`
- Eureka Server: `http://localhost:8761`

### Actuator + Prometheus endpoints
- GatewayMS health: `http://localhost:9000/actuator/health-check`
- GatewayMS prometheus: `http://localhost:9000/actuator/prometheus`

- UserMS health: `http://localhost:8080/actuator/health`
- UserMS prometheus: `http://localhost:8080/actuator/prometheus`

- profileMS health: `http://localhost:9100/actuator/health`
- profileMS prometheus: `http://localhost:9100/actuator/prometheus`

- AppointmentMS health: `http://localhost:9200/actuator/health`
- AppointmentMS prometheus: `http://localhost:9200/actuator/prometheus`

- PharmacyMS health: `http://localhost:9300/actuator/health`
- PharmacyMS prometheus: `http://localhost:9300/actuator/prometheus`

- MediaMS health: `http://localhost:9400/actuator/health`
- MediaMS prometheus: `http://localhost:9400/actuator/prometheus`

- Eureka health: `http://localhost:8761/actuator/health`
- Eureka prometheus: `http://localhost:8761/actuator/prometheus`

### Direct OpenAPI endpoints
- GatewayMS docs JSON: `http://localhost:9000/v3/api-docs`
- UserMS docs JSON: `http://localhost:8080/v3/api-docs`
- profileMS docs JSON: `http://localhost:9100/v3/api-docs`
- AppointmentMS docs JSON: `http://localhost:9200/v3/api-docs`
- PharmacyMS docs JSON: `http://localhost:9300/v3/api-docs`
- MediaMS docs JSON: `http://localhost:9400/v3/api-docs`
- Eureka docs JSON: `http://localhost:8761/v3/api-docs`

### Gateway-routed docs endpoints
- Gateway Swagger UI: `http://localhost:9000/swagger-ui/index.html`
- UserMS via Gateway: `http://localhost:9000/user-docs/v3/api-docs`
- profileMS via Gateway: `http://localhost:9000/profile-docs/v3/api-docs`
- AppointmentMS via Gateway: `http://localhost:9000/appointment-docs/v3/api-docs`
- PharmacyMS via Gateway: `http://localhost:9000/pharmacy-docs/v3/api-docs`
- MediaMS via Gateway: `http://localhost:9000/media-docs/v3/api-docs`
- Eureka via Gateway: `http://localhost:9000/eureka-docs/v3/api-docs`

### Observability tools
- Zipkin UI: `http://localhost:9411/zipkin/`
- Prometheus UI: `http://localhost:9090`
- Prometheus targets: `http://localhost:9090/targets`
- Grafana UI: `http://localhost:3000`

### Quick endpoint smoke test (PowerShell)

```powershell
Invoke-WebRequest -Uri "http://localhost:9000/actuator/health-check" -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:8080/actuator/prometheus" -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:9100/actuator/prometheus" -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:9200/actuator/prometheus" -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:9300/actuator/prometheus" -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:9400/actuator/prometheus" -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:8761/actuator/prometheus" -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:9090/targets" -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:3000/api/health" -UseBasicParsing
Invoke-WebRequest -Uri "http://localhost:9411/api/v2/services" -UseBasicParsing
```
