# Eureka Server API Documentation

## Base Information

- **Service Name:** `EUREKA-SERVER`
- **Default Port:** `8761`
- **Base URL:** `http://localhost:8761`
- **Swagger UI (primary):** `http://localhost:8761/swagger-ui/index.html`
- **Swagger UI (configured path):** `http://localhost:8761/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8761/v3/api-docs`
- **OpenAPI YAML:** `http://localhost:8761/v3/api-docs.yaml`
- **Eureka Dashboard:** `http://localhost:8761/`

## Access

- Eureka server has no custom business REST controllers in this project.
- Swagger primarily documents service metadata and runtime-exposed endpoints.
- No `X-Secret-Key` is required for Eureka Swagger endpoints.

## Swagger Usage

1. Start `EUREKA-SERVER`.
2. Open Swagger UI: `http://localhost:8761/swagger-ui/index.html`.
3. Use OpenAPI JSON/YAML for integrations and tool-based inspection.

## Notes

- Eureka registry UI remains available at `/`.
- If you secure Eureka in future, add matching auth schemes in `OpenApiConfig`.

