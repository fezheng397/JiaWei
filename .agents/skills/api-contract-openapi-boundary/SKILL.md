---
name: api-contract-openapi-boundary
description: Maintain the Recipes project's Spring Boot-to-Angular API contract through controller DTOs, springdoc OpenAPI, and the generated Angular client. Use whenever working on recipes-api controllers, HTTP request or response DTOs, validation, error responses, OpenAPI generation, Angular OpenAPI client generation, frontend API consumption, or anything that changes HTTP request or response shapes.
---

# API Contract OpenAPI Boundary

Keep the contract flow unidirectional:

`Spring controller DTOs -> /v3/api-docs -> generated Angular client/types`

Never generate TypeScript directly from Java source. Never expose persistence entities through the API. Never duplicate generated API DTOs by hand or edit generated Angular API files manually.

## Inspect Before Changing

1. Inspect the relevant controllers, API DTOs, services, contract tests, and frontend API usage.
2. Decide explicitly whether the task changes an HTTP request or response shape.
3. If the contract changes, plan the backend DTO/controller change and Angular client regeneration together.
4. Identify existing contract and serialization tests that need updates.

## Backend Boundary

- Use named API DTOs for controller request and response types; never use JPA entities or ambiguous maps/objects.
- Do not accept JPA entities as request bodies or return them from controllers.
- Use `publicId` in public URLs and responses; keep internal UUIDs and persistence details private.
- Use the project's `ApiError` shape where applicable.
- Add Jakarta Bean Validation annotations to request DTOs where applicable.
- Keep endpoint paths, response statuses, and DTO names clear and stable.
- Update or add contract tests when a response shape changes.
- Do not add recipe write DTOs unless create/edit endpoints are part of the task.
- Do not introduce frontend domain models into the backend.
- Do not expose OpenAPI schemas for internal entities, including `Recipe`, `Ingredient`, `Author`, `RecipeStep`, `RecipeIngredient`, `StepIngredient`, `StepIngredientId`, or `AuditedEntity`.

## Frontend Boundary

- Treat `web/src/app/api/generated` as generated-only. Never edit its files manually.
- Regenerate with `npm run generate:api` from the Angular root.
- Do not hand-write TypeScript interfaces that duplicate generated request or response DTOs.
- Use generated DTOs and services at the API boundary.
- Map generated DTOs explicitly into feature view models when UI state needs a different shape.
- Keep mappings in feature services and test them where useful.
- Centralize API configuration; do not scatter raw API URLs through components.

## OpenAPI Generation

- Use `/v3/api-docs` as the backend OpenAPI endpoint.
- Ensure springdoc describes controller DTOs only.
- Keep generated Angular output isolated under `web/src/app/api/generated`.
- If generated files change unexpectedly, inspect the OpenAPI diff and controller DTO changes before accepting them.

## Verify After Changing

Run the checks relevant to the task:

```bash
cd recipes-api
mvn clean verify
curl http://localhost:8080/v3/api-docs
```

```bash
cd web
npm run generate:api
npm run build
```

Then confirm:

- Generated changes exist only under `web/src/app/api/generated`.
- OpenAPI exposes no JPA entities or internal IDs.
- Frontend code does not duplicate generated DTOs.
- Contract tests cover changed request and response shapes.

If the backend is not running or regeneration would require unavailable services, state which checks were not run and recommend the exact follow-up commands.

## Review Checklist

Flag:

- JPA entity leaks through controllers or OpenAPI.
- Hand-written frontend DTOs duplicating generated types.
- Manual edits under `web/src/app/api/generated`.
- Controller methods returning ambiguous maps or objects instead of named DTOs.
- Missing request DTO validation.
- Accidental internal UUID use in API paths or responses.
- Contract changes without regenerated Angular types or corresponding contract-test updates.
