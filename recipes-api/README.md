# Recipes API

Spring Boot API for the Recipes project.

## Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Flyway migrations
- PostgreSQL

## Local Development

Start Postgres:

```bash
docker compose up -d postgres
```

If you ran an earlier version of this API before the UUID/public ID migration, reset the local
Postgres volume once:

```bash
docker compose down -v
docker compose up -d postgres
```

Run the API:

```bash
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
```

The API runs on `http://localhost:8080`.

Useful endpoints:

- `GET /actuator/health`
- `GET /api/recipes`
- `GET /api/recipes?kind=dish`
- `GET /api/recipes/{publicId}`
- `GET /api/recipes/{publicId}/ingredient-redirect`
- `GET /api/ingredients`
- `GET /api/ingredients?hasRecipe=true`
- `GET /api/ingredients/{publicId}`

## Database

Flyway owns the schema:

- `src/main/resources/db/migration/V1__create_recipe_schema.sql`

Development seed data lives in:

- `src/main/resources/db/dev-migration/V2__seed_mock_recipe_data.sql`

The default Flyway location only runs schema migrations. The `dev` Spring profile adds the
development migration location, so mock seed data is loaded locally but not in production.

The schema is normalized around the current web entities:

- authors
- ingredients
- recipes
- recipe tags
- recipe ingredient rows
- recipe steps
- step ingredient links

Internal database IDs are UUIDs. API URLs and responses use `publicId` values, which map to
database `public_id` columns and are 26-character ULID-style strings. Java is the single source
of truth for generated public IDs; the database enforces uniqueness, non-nullability, and valid
ULID format.

## Neon / Render Notes

For Neon, use the JDBC URL form:

```text
jdbc:postgresql://<host>/<database>?sslmode=require
```

Set these environment variables on Render, a VPS, or wherever the service runs:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://<host>/<database>?sslmode=require
SPRING_DATASOURCE_USERNAME=<user>
SPRING_DATASOURCE_PASSWORD=<password>
APP_CORS_ALLOWED_ORIGINS=https://your-web-domain.example
PORT=8080
```

Do not set `SPRING_PROFILES_ACTIVE=dev` in production unless you intentionally want development
seed data loaded into that database.

The included `Dockerfile` builds the app with Maven and runs it on Java 21.
