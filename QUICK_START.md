# Quick Start Guide - Distributed Tracing with Zipkin

## ✅ What's Been Implemented

### 1. All 6 Microservices Updated
- ✅ GatewayMS
- ✅ UserMS
- ✅ ProfileMS
- ✅ AppointmentMS
- ✅ PharmacyMS
- ✅ MediaMS

### 2. Each Service Now Has:
- ✅ Sleuth & Zipkin dependencies in pom.xml
- ✅ Zipkin endpoint configuration
- ✅ 100% sampling (development mode)
- ✅ Automatic trace propagation

### 3. Infrastructure Files Created
- ✅ docker-compose.yml (Zipkin server)
- ✅ START_SERVICES.bat (startup helper)
- ✅ Documentation files

---

## 🚀 Getting Started - 3 Simple Steps

### STEP 1: Start Zipkin Server
```bash
docker-compose up -d zipkin
```
Zipkin UI will be available at: **http://localhost:9411**

### STEP 2: Start Eureka Server
```bash
cd backend/EUREKA-SERVER
mvnw spring-boot:run
```

### STEP 3: Start All Microservices (each in separate terminal)

**Terminal 1 - API Gateway:**
```bash
cd backend/GatewayMS
mvnw spring-boot:run
```

**Terminal 2 - User Service:**
```bash
cd backend/UserMS
mvnw spring-boot:run
```

**Terminal 3 - Profile Service:**
```bash
cd backend/profileMS
mvnw spring-boot:run
```

**Terminal 4 - Appointment Service:**
```bash
cd backend/Appointment
mvnw spring-boot:run
```

**Terminal 5 - Pharmacy Service:**
```bash
cd backend/PharmacyMS
mvnw spring-boot:run
```

**Terminal 6 - Media Service:**
```bash
cd backend/MediaMS
mvnw spring-boot:run
```

---

## 🧪 Testing the Tracing

### 1. Make a Request Through API Gateway
```bash
curl -X GET http://localhost:9000/user/list \
  -H "Authorization: Bearer <your-token>"
```

### 2. Check Zipkin UI
Navigate to: **http://localhost:9411**

### 3. View the Trace
- Select service from dropdown (e.g., "GatewayMS")
- Click "Find Traces"
- Click on any trace to see:
  - Complete request flow
  - Service dependency chain
  - Latency per service
  - Error details (if any)

---

## 📊 Understanding the Trace Visualization

### Trace Example
```
Request to API Gateway (TraceID: abc123def456)
├── GatewayMS (10ms)
│   ├── Route to UserMS
│   ├── Propagate TraceID
│   └── Receive Response
├── UserMS (25ms)
│   ├── Process Request
│   ├── DB Query
│   └── Return Data
└── HTTP Response (35ms total)
```

### Key Information in Traces
- **Span Name**: Operation being performed
- **Duration**: Time taken
- **Timestamp**: When operation started
- **Tags**: Custom metadata
- **Status**: Success or failure

---

## 🔧 Configuration Reference

### Zipkin Configuration (in all services)
```properties
# Active in all microservices
spring.sleuth.tracing.export.zipkin.endpoint=http://localhost:9411/api/v2/spans
management.tracing.sampling.probability=1.0
spring.sleuth.sampler.probability=1.0
```

### To Change Sampling (adjust in application.properties)
```properties
# Development: 100% (all traces)
spring.sleuth.sampler.probability=1.0

# Staging: 50%
spring.sleuth.sampler.probability=0.5

# Production: 10% (recommended)
spring.sleuth.sampler.probability=0.1
```

---

## 📍 Service Endpoints

| Service | URL | Port |
|---------|-----|------|
| Zipkin UI | http://localhost:9411 | 9411 |
| Eureka UI | http://localhost:8761 | 8761 |
| API Gateway | http://localhost:9000 | 9000 |
| UserMS | http://localhost:8080 | 8080 |
| ProfileMS | http://localhost:9100 | 9100 |
| AppointmentMS | http://localhost:9200 | 9200 |
| PharmacyMS | http://localhost:9300 | 9300 |
| MediaMS | http://localhost:9400 | 9400 |

---

## 🐛 Troubleshooting

### Traces Not Appearing?

1. **Check Zipkin is running:**
   ```bash
   curl http://localhost:9411/zipkin/
   ```

2. **Verify service started with Sleuth:**
   Look for "Zipkin" in startup logs

3. **Check trace configuration:**
   ```bash
   # Should be in application.properties
   spring.sleuth.tracing.export.zipkin.endpoint=http://localhost:9411/api/v2/spans
   ```

### No Traces Between Services?

1. Ensure OpenFeign/RestTemplate is used for inter-service calls
2. Check if TraceID headers are being propagated
3. Verify all services can reach Zipkin server

### High Latency in Traces?

1. This indicates slow service operations, not tracing overhead
2. Reduce sampling to 0.1 (10%) in production
3. Check database queries and external API calls

---

## 📝 Files Modified

### pom.xml Files (Added Dependencies)
- backend/GatewayMS/pom.xml
- backend/UserMS/pom.xml
- backend/profileMS/pom.xml
- backend/Appointment/pom.xml
- backend/PharmacyMS/pom.xml
- backend/MediaMS/pom.xml

### application.properties Files (Added Configuration)
- backend/GatewayMS/src/main/resources/application.properties
- backend/UserMS/src/main/resources/application.properties
- backend/profileMS/src/main/resources/application.properties
- backend/Appointment/src/main/resources/application.properties
- backend/PharmacyMS/src/main/resources/application.properties
- backend/MediaMS/src/main/resources/application.properties

### New Files Created
- docker-compose.yml
- START_SERVICES.bat
- TRACING_README.md

---

## 🎯 How It Works

1. **Request Arrives** → API Gateway (Port 9000)
2. **TraceID Created** → Spring Cloud Sleuth generates unique ID
3. **Propagation** → TraceID sent via B3 headers to downstream services
4. **Span Creation** → Each service creates spans for its operations
5. **Async Export** → Spans sent to Zipkin (non-blocking)
6. **Visualization** → Zipkin correlates and displays all spans
7. **Analysis** → View complete request flow and bottlenecks

---

## ✨ Features Enabled

✅ Distributed tracing across all services
✅ Automatic TraceID generation and propagation
✅ HTTP client instrumentation (Feign, RestTemplate, WebClient)
✅ Service dependency mapping
✅ Latency analysis per service
✅ Error tracking and visualization
✅ Correlation IDs across logs
✅ 100% sampling in development
✅ Configurable sampling for production
✅ Zero-configuration setup (auto-instrumentation)

---

## 📚 Additional Resources

- Spring Cloud Sleuth: https://spring.io/projects/spring-cloud-sleuth
- OpenZipkin: https://zipkin.io/
- B3 Propagation: https://github.com/openzipkin/b3-propagation
- Brave Framework: https://github.com/openzipkin/brave

---

## ✔️ Verification Checklist

- [ ] Docker installed and running
- [ ] All 6 microservices compiled successfully
- [ ] docker-compose.yml in root directory
- [ ] Zipkin started via docker-compose
- [ ] All services started without errors
- [ ] Zipkin UI accessible at http://localhost:9411
- [ ] Made at least one request through API Gateway
- [ ] Traces visible in Zipkin UI
- [ ] Can see complete request flow across services

---

## 🚀 Next Steps (Optional)

### 1. Add Custom Spans
```java
@Service
public class MyService {
    private final Tracer tracer;
    
    public MyService(Tracer tracer) {
        this.tracer = tracer;
    }
    
    public void myMethod() {
        tracer.currentSpan()
            .tag("userId", 123)
            .event("processing_started");
        // Business logic
    }
}
```

### 2. Configure for Production
- Reduce sampling to 0.1 (10%)
- Set up Elasticsearch for persistent storage
- Configure log aggregation with TraceID

### 3. Monitor Performance
- View service latencies in Zipkin
- Identify bottlenecks
- Optimize slow services

---

## 📞 Support

If traces aren't appearing:
1. Check docker: `docker ps | grep zipkin`
2. Check logs for "Sleuth" or "Zipkin" errors
3. Verify endpoint configuration
4. Restart services after config changes

