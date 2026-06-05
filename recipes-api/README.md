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
- `GET /recipes`
- `GET /recipes?kind=dish`
- `GET /recipes/{publicId}`
- `GET /recipes/{publicId}/ingredient-redirect`
- `GET /ingredients`
- `GET /ingredients?hasRecipe=true`
- `GET /ingredients/{publicId}`

## Database

Flyway owns the schema:

- `src/main/resources/db/migration/V1__create_recipe_schema.sql`
- `src/main/resources/db/migration/V2__add_ingredient_name_unique_index.sql`
- `src/main/resources/db/migration/V3__add_author_name_unique_index.sql`
- `src/main/resources/db/migration/V5__simplify_tag_and_step_link_tables.sql`

Development seed data lives in:

- `src/main/resources/db/dev-migration/R__seed_development_recipe_data.sql`

The default Flyway location only runs schema migrations. The `dev` Spring profile adds the
development migration location, so repeatable seed data is loaded locally but not in production.
The missing schema migration version `V4` is intentional because that version was previously
used only for development seed data. The next schema migration should use `V6`.

The `prod` Spring profile disables Flyway during application startup. Production migrations run
from GitHub Actions before Render is asked to deploy the application. The production app still
uses Hibernate `ddl-auto=validate` and will fail startup if its entity mappings do not match the
migrated schema. The deployment workflow runs Maven `process-resources` before `flyway:migrate`
so the configured `classpath:db/migration` location is populated; missing migration locations fail
the build.

### Local Flyway Reset

If local development startup fails with a Flyway checksum mismatch after migration edits, reset
the disposable local development database instead of editing `flyway_schema_history` manually or
using `flyway repair`:

```bash
dropdb recipes
createdb recipes
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
```

When using the included Docker Postgres service, remove and recreate its local database volume:

```bash
docker compose down -v
docker compose up -d postgres
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
```

Versioned `V__` migrations must not be edited after they have been applied to a shared or permanent
database. Add a new `V__` migration for schema changes. Use idempotent `R__` repeatable migrations
for development seed data because Flyway reruns them whenever their checksum changes.

Prefer backward-compatible production migrations: add schema before application code uses it,
deploy the compatible code, backfill separately when needed, and constrain or remove old schema in
a later migration.

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
jdbc:postgresql://<direct-host>/<database>?sslmode=verify-full
```

Set these environment variables on Render, a VPS, or wherever the service runs:

```bash
SPRING_PROFILES_ACTIVE=prod
SPRING_FLYWAY_ENABLED=false
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
SPRING_DATASOURCE_URL=jdbc:postgresql://<host>/<database>?sslmode=verify-full
SPRING_DATASOURCE_USERNAME=<user>
SPRING_DATASOURCE_PASSWORD=<password>
APP_CORS_ALLOWED_ORIGINS=https://your-web-domain.example
PORT=8080
```

Render auto-deploy is disabled in `render.yaml`. Configure the GitHub `production` environment
with these secrets so `.github/workflows/deploy-backend.yml` can migrate first and deploy only
afterward:

```text
PROD_DATABASE_JDBC_URL
PROD_DATABASE_USERNAME
PROD_DATABASE_PASSWORD
RENDER_DEPLOY_HOOK_URL
```

Use Neon's direct, non-`-pooler` endpoint for `PROD_DATABASE_JDBC_URL`; schema migration tools
should not use transaction-pooled connections. The deployment and manual inspection workflows
require `sslmode=verify-full`. The application runtime may use a pooled endpoint if needed for
connection concurrency.

Run the manual `Inspect Production Migrations` GitHub Actions workflow to execute `flyway:info`
and `flyway:validate` against production without applying migrations.

In the Neon Console, protect the production branch against accidental reset or deletion. Keep the
Render service in a region near the Neon production project (`aws-us-east-1`) to minimize database
latency.

Never set `SPRING_PROFILES_ACTIVE=dev` in production; the development profile includes
`db/dev-migration/R__seed_development_recipe_data.sql`.

The included `Dockerfile` builds the app with Maven and runs it on Java 21.
