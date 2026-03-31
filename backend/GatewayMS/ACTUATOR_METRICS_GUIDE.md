# API Gateway - Actuator Metrics Guide

## Overview
The API Gateway now exposes comprehensive metrics and monitoring endpoints through Spring Boot Actuator. You can access gateway metrics directly and also proxy metrics from other microservices.

---

## 🔍 Gateway Metrics Endpoints

### Base Actuator Endpoint
```
http://localhost:9000/actuator
```

### Core Metrics Endpoints

| Endpoint | URL | Description |
|----------|-----|-------------|
| **Health Check** | `http://localhost:9000/actuator/health-check` | Overall application health status |
| **Health Components** | `http://localhost:9000/actuator/health/liveness` | Liveness probe for Kubernetes |
| **Health Readiness** | `http://localhost:9000/actuator/health/readiness` | Readiness probe for Kubernetes |
| **Info** | `http://localhost:9000/actuator/info` | Application information and version |
| **Metrics** | `http://localhost:9000/actuator/metrics` | List all available metrics |
| **Prometheus** | `http://localhost:9000/actuator/prometheus` | Prometheus-compatible metrics format |

### Advanced Monitoring Endpoints

| Endpoint | URL | Description |
|----------|-----|-------------|
| **Gateway Routes** | `http://localhost:9000/actuator/gateway/routes` | View all configured gateway routes |
| **Gateway Route Filters** | `http://localhost:9000/actuator/gateway/routefilters` | View all route filters |
| **Gateway Refresh** | `POST http://localhost:9000/actuator/gateway/refresh` | Refresh gateway routes |
| **Thread Dump** | `http://localhost:9000/actuator/threaddump` | Current thread status and stack traces |
| **Heap Dump** | `http://localhost:9000/actuator/heapdump` | Download heap dump for memory analysis |
| **Environment** | `http://localhost:9000/actuator/env` | Application environment properties |
| **Loggers** | `http://localhost:9000/actuator/loggers` | View and manage logger configurations |
| **Caches** | `http://localhost:9000/actuator/caches` | Cache statistics and management |
| **Conditions** | `http://localhost:9000/actuator/conditions` | Spring Boot auto-configuration conditions |
| **Configuration Properties** | `http://localhost:9000/actuator/configprops` | All configuration properties |
| **Beans** | `http://localhost:9000/actuator/beans` | All Spring beans in application context |

---

## 📊 Specific Metrics Examples

### JVM Metrics
```
http://localhost:9000/actuator/metrics/jvm.memory.used
http://localhost:9000/actuator/metrics/jvm.threads.live
http://localhost:9000/actuator/metrics/jvm.gc.memory.allocated
```

### HTTP Metrics
```
http://localhost:9000/actuator/metrics/http.server.requests
http://localhost:9000/actuator/metrics/http.client.requests
```

### Gateway Specific Metrics
```
http://localhost:9000/actuator/metrics/gateway.requests
```

### Query Metrics with Tags
```
http://localhost:9000/actuator/metrics/http.server.requests?tag=method:GET&tag=uri:/user/**
```

---

## 🔗 Access Metrics from Other Microservices

The gateway now provides proxy routes to access metrics from all microservices without needing direct access to each service.

### UserMS Metrics
```
http://localhost:9000/user-metrics/health
http://localhost:9000/user-metrics/metrics
http://localhost:9000/user-metrics/prometheus
http://localhost:9000/user-metrics/threaddump
```

### ProfileMS Metrics
```
http://localhost:9000/profile-metrics/health
http://localhost:9000/profile-metrics/metrics
http://localhost:9000/profile-metrics/prometheus
http://localhost:9000/profile-metrics/threaddump
```

### AppointmentMS Metrics
```
http://localhost:9000/appointment-metrics/health
http://localhost:9000/appointment-metrics/metrics
http://localhost:9000/appointment-metrics/prometheus
http://localhost:9000/appointment-metrics/threaddump
```

### PharmacyMS Metrics
```
http://localhost:9000/pharmacy-metrics/health
http://localhost:9000/pharmacy-metrics/metrics
http://localhost:9000/pharmacy-metrics/prometheus
http://localhost:9000/pharmacy-metrics/threaddump
```

### MediaMS Metrics
```
http://localhost:9000/media-metrics/health
http://localhost:9000/media-metrics/metrics
http://localhost:9000/media-metrics/prometheus
http://localhost:9000/media-metrics/threaddump
```

### Eureka Server Metrics
```
http://localhost:9000/eureka-metrics/health
http://localhost:9000/eureka-metrics/metrics
http://localhost:9000/eureka-metrics/prometheus
http://localhost:9000/eureka-metrics/env
http://localhost:9000/eureka-metrics/loggers
```

#### Direct Access to Eureka Server (Optional):
```
http://localhost:8761/actuator/health
http://localhost:8761/actuator/metrics
http://localhost:8761/actuator/prometheus
```

---

## 📈 Using Prometheus Format

The Prometheus endpoint exports metrics in a format compatible with Prometheus monitoring:

```
http://localhost:9000/actuator/prometheus
```

Example metrics:
```
http_server_requests_seconds_count{method="GET",outcome="SUCCESS",status="200",uri="/actuator/metrics"} 42.0
jvm_memory_used_bytes{area="heap",id="PS Scavenge"} 123456789
jvm_threads_live_threads 42
```

---

## 🔧 Common Metric Queries

### Memory Usage
```
GET http://localhost:9000/actuator/metrics/jvm.memory.used
```

### CPU Usage
```
GET http://localhost:9000/actuator/metrics/process.cpu.usage
```

### Active Threads
```
GET http://localhost:9000/actuator/metrics/jvm.threads.live
```

### HTTP Request Count
```
GET http://localhost:9000/actuator/metrics/http.server.requests
```

### Gateway Route Performance
```
GET http://localhost:9000/actuator/metrics/gateway.requests
```

---

## 🔐 Security Considerations

- Currently, actuator endpoints are exposed without authentication
- In production, consider:
  - Restricting actuator endpoints to internal networks only
  - Adding authentication/authorization via Spring Security
  - Using `management.endpoints.web.exposure.exclude` to hide sensitive endpoints
  - Enabling HTTPS for actuator endpoints

### Add Authentication (Optional)
```properties
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.probes.enabled=true
# Only expose basic endpoints
management.endpoint.threaddump.enabled=false
management.endpoint.heapdump.enabled=false
```

---

## 📋 Exposed Endpoints Summary

The following 20+ actuator endpoints are currently enabled:

✅ `health` - Health status  
✅ `info` - Application info  
✅ `metrics` - Metrics listing  
✅ `prometheus` - Prometheus format  
✅ `gateway` - Gateway routes & filters  
✅ `threaddump` - Thread diagnostics  
✅ `heapdump` - Heap dump download  
✅ `env` - Environment variables  
✅ `loggers` - Logger configuration  
✅ `caches` - Cache statistics  
✅ `conditions` - Auto-config conditions  
✅ `configprops` - Configuration properties  
✅ `beans` - Spring beans  
✅ `httptrace` - HTTP trace logs  

---

## 🚀 Integration Examples

### Curl Examples

**Get Gateway Health:**
```bash
curl http://localhost:9000/actuator/health
```

**Get All Metrics:**
```bash
curl http://localhost:9000/actuator/metrics
```

**Get Specific Metric (JVM Memory):**
```bash
curl http://localhost:9000/actuator/metrics/jvm.memory.used
```

**View Gateway Routes:**
```bash
curl http://localhost:9000/actuator/gateway/routes
```

**Get UserMS Metrics via Gateway:**
```bash
curl http://localhost:9000/user-metrics/metrics
```

### Prometheus Scrape Config
```yaml
scrape_configs:
  - job_name: 'gateway'
    static_configs:
      - targets: ['localhost:9000']
    metrics_path: '/actuator/prometheus'
    
  - job_name: 'user-service'
    static_configs:
      - targets: ['localhost:9000']
    metrics_path: '/user-metrics/prometheus'
    
  - job_name: 'profile-service'
    static_configs:
      - targets: ['localhost:9000']
    metrics_path: '/profile-metrics/prometheus'
    
  - job_name: 'appointment-service'
    static_configs:
      - targets: ['localhost:9000']
    metrics_path: '/appointment-metrics/prometheus'
    
  - job_name: 'pharmacy-service'
    static_configs:
      - targets: ['localhost:9000']
    metrics_path: '/pharmacy-metrics/prometheus'
    
  - job_name: 'media-service'
    static_configs:
      - targets: ['localhost:9000']
    metrics_path: '/media-metrics/prometheus'
    
  - job_name: 'eureka-server'
    static_configs:
      - targets: ['localhost:9000']
    metrics_path: '/eureka-metrics/prometheus'
```

---

## 📝 Notes

- All microservices should have actuator dependency and similar configuration
- Metrics are cumulative from application startup
- Gateway buffers requests/responses for monitoring
- Use Prometheus + Grafana for visualization of metrics over time


