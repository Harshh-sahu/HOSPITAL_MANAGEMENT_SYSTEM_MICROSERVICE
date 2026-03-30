# Gateway Service API Documentation

## Base Information

- **Service Name:** `GatewayMS`
- **Default Port:** `9000`
- **Base URL:** `http://localhost:9000`
- **Swagger UI (primary):** `http://localhost:9000/swagger-ui/index.html`
- **Swagger UI (configured path):** `http://localhost:9000/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:9000/v3/api-docs`
- **OpenAPI YAML:** `http://localhost:9000/v3/api-docs.yaml`

## Access and Authentication

- Gateway Swagger endpoints are publicly accessible.
- Routed business APIs are protected by `TokenFilter` and expect JWT except selected public paths.
- `TokenFilter` now bypasses Swagger/OpenAPI paths so docs can be accessed through gateway routes.

## Swagger Usage

1. Start `GatewayMS`.
2. Open Swagger UI hub: `http://localhost:9000/swagger-ui/index.html`.
3. Use the **Select a definition** dropdown to switch between all services.
4. Use OpenAPI JSON/YAML for API tooling and verification.

## One URL for All Docs

Use this single URL:

- `http://localhost:9000/swagger-ui/index.html`

It now aggregates these OpenAPI specs:

- `GatewayMS`
- `UserMS`
- `ProfileMS`
- `AppointmentMS`
- `PharmacyMS`
- `MediaMS`
- `EurekaServer`

## Gateway Route Prefixes

- `/user/**` -> `UserMS`
- `/profile/**` -> `ProfileMS`
- `/appointment/**` -> `AppointmentMS`
- `/pharmacy/**` -> `PharmacyMS`
- `/media/**` -> `MediaMS`

## Routed Swagger Endpoints (via Gateway)

If downstream services are running and registered in Eureka, you can access docs through gateway prefixes:

- `http://localhost:9000/user/v3/api-docs`
- `http://localhost:9000/profile/v3/api-docs`
- `http://localhost:9000/appointment/v3/api-docs`
- `http://localhost:9000/pharmacy/v3/api-docs`
- `http://localhost:9000/media/v3/api-docs`
- `http://localhost:9000/eureka-docs/v3/api-docs`

> Note: Availability depends on route registration and downstream service status.

