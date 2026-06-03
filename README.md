# Recipes

Monorepo for the Recipes project.

## Projects

- `web/` - Angular recipe web app.
- `recipes-api/` - Spring Boot recipe API.

## Web

```bash
cd web
npm start
```

## API

```bash
cd recipes-api
docker compose up -d postgres
mvn spring-boot:run
```
