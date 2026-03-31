# Distributed Tracing Setup

## All Services Updated with Zipkin Integration

Services configured: GatewayMS, UserMS, ProfileMS, AppointmentMS, PharmacyMS, MediaMS

### Configuration Added to Each Service:

**pom.xml:**
- spring-cloud-starter-sleuth
- spring-cloud-sleuth-zipkin

**application.properties:**
```
spring.sleuth.tracing.export.zipkin.endpoint=http://localhost:9411/api/v2/spans
management.tracing.sampling.probability=1.0
spring.sleuth.sampler.probability=1.0
```

### Start Zipkin:
```bash
docker-compose up -d zipkin
```

### Access UI:
http://localhost:9411

### How It Works:
1. API Gateway receives request with TraceID
2. TraceID propagates through all services via B3 headers
3. Spans are sent to Zipkin for visualization
4. Complete request flow is visible in Zipkin UI

### Sampling Settings:
- Development: 1.0 (100%)
- Production: 0.1 (10%)

