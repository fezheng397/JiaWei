---
name: spring-backend-package-structure
description: Maintain the Recipes Spring Boot backend's package-by-feature architecture. Use whenever adding or moving recipes-api Java files, creating backend features, reviewing package placement, or refactoring controllers, services, mappers, DTOs, JPA entities, repositories, common infrastructure, configuration, or corresponding tests.
---

# Spring Backend Package Structure

Prefer package-by-feature under `com.recipes.api`. Make persistence models, API contracts, repositories, and application workflows visually distinct.

Use this shape where applicable:

```text
feature/
  FeatureController.java
  FeatureService.java
  FeatureMapper.java
  dto/
  entity/
  repository/
```

Do not create application-root technical layers such as global `controller`, `service`, `repository`, `dto`, or `model` packages. Never create a global `model` package.

## Place Backend Code

- Keep `RecipesApiApplication` at the application root.
- Keep each business area in a feature package such as `author`, `ingredient`, or `recipe`.
- Keep controllers, services, mappers, and a small number of feature-specific helpers at the feature root.
- Put API request and response DTOs in feature-local `dto` packages.
- Put JPA entities and persistence enums in feature-local `entity` packages.
- Put Spring Data interfaces in feature-local `repository` packages.
- Put reusable cross-feature errors, exception handling, auditing bases, ID generation, and infrastructure helpers in `common`.
- Put Spring/application configuration in `config`.
- Do not use `common` as a dumping ground or place feature-specific logic there.
- Avoid subpackages for tiny one-off concerns unless they improve clarity or follow the established structure.
- Keep package declarations aligned exactly with folder paths.

## Maintain Boundaries

### DTOs

- Treat DTOs as API request and response shapes.
- Name their roles clearly where useful: `Request`, `Response`, `SummaryResponse`, `DetailResponse`, or `RedirectResponse`.
- Keep JPA annotations and internal persistence concerns out of DTOs.
- Use public identifiers at API boundaries where stable external references are needed.
- Never place DTOs in `entity`.

### Entities

- Treat entities as persistence state and database relationships.
- Keep JPA annotations, persistence relationships, internal IDs, audit/version fields, and persistence enums in `entity`.
- Keep internal database IDs internal unless explicitly required.
- Never place entities in `dto`, expose them from controllers, or serialize them directly as API responses.

### Repositories

- Add repositories only for a clear query or persistence use case.
- Do not create one automatically for every entity, especially child entities and join tables.
- Keep business workflows out of repositories.

### Controllers, Services, And Mappers

- Keep controllers thin HTTP adapters.
- Keep business workflows and transaction boundaries in services.
- Keep non-trivial entity-to-DTO conversion in mappers.
- Do not duplicate mapping logic across controllers.

## Add A Feature

1. Create a feature package if needed.
2. Add controller, service, mapper, or helper classes at the feature root only when required.
3. Add `dto`, `entity`, and `repository` subpackages only for relevant file types.
4. Keep API DTOs separate from JPA entities and repositories separate from services.
5. Add behavior-focused tests under the corresponding test package.

## Refactor Packages Safely

For a structure-only refactor:

1. Inventory affected main and test packages before moving files.
2. Move files without changing class names or behavior.
3. Update package declarations and imports.
4. Keep API field names, endpoint behavior, JPA annotations, and database mappings unchanged.
5. Do not create or edit Flyway migrations.
6. Do not change OpenAPI behavior except required package/import changes.
7. Move tests only when their package must follow a moved behavior boundary.

If the task changes HTTP shapes, also follow `api-contract-openapi-boundary`. If it changes entities or schema behavior, also follow `flyway-database-migration-safety`.

## Organize Tests

- Generally mirror main packages.
- Organize around behavior boundaries, not one test per production class.
- Keep package-private behavior tests in the matching package.
- Keep feature, common utility, config/OpenAPI, and application-level tests in their corresponding packages.

## Verify Structure

Run:

```bash
cd recipes-api
mvn clean verify
```

Then confirm:

- The app starts and existing tests pass.
- OpenAPI generation still works.
- Controllers expose DTOs, not JPA entities.
- DTOs, entities, and repositories live only in their matching feature-local packages.
- Package declarations match folder paths.
- No global `model` or application-root technical-layer package was introduced.
- No generated files, build output, Flyway migrations, or schema behavior changed during a structure-only refactor.

## Review Checklist

Flag:

- Global `model` or application-root technical-layer packages.
- DTOs mixed with entities or entities returned from controllers.
- Feature-specific logic in `common` or global utilities inside feature packages.
- Repositories without a clear persistence use case.
- Business logic in controllers or repositories.
- Duplicated mapping logic.
- Tests far from the behavior they cover.
- Package declarations that do not match folder paths.
- Structure-only refactors that change API or database behavior.
