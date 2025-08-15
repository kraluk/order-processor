# Repository Guidelines

## Project Structure & Module Organization
- `src/main/java`: Spring Boot app code (`io.kraluk.orderprocessor`), entrypoint, adapters, domain.
- `src/main/resources`: `application.yaml`, `application-local.yaml`, `logback-spring.xml`, Liquibase (`liquibase/`).
- `src/test/java`: Unit tests.
- `src/testIntegration/java`: Integration/acceptance tests (JUnit 5, Testcontainers) under `…/test` and `…/acceptance`.
- Tooling/infra: `build.gradle.kts`, `settings.gradle.kts`, `compose.yml` (PostgreSQL, LocalStack), `.scripts/localstack/*` (S3/SQS seed), `docs/`.

## Build, Test, and Development Commands
- Run locally: `docker compose up` then `./gradlew bootRun --args='--spring.profiles.active=local'`.
- Unit tests: `./gradlew test`.
- Integration tests: `./gradlew testIntegration` (separate source set; uses profile `integration`).
- Full verification + coverage: `./gradlew check` (finalizes `jacocoTestReport`).
- Coverage report: `./gradlew jacocoTestReport` → `build/reports/jacoco/index.html`.
- Format: `./gradlew spotlessApply`.
- Dependency review: `./gradlew dependencyUpdates`.
- jOOQ after Liquibase changes: `./gradlew jooqGenerate`.

## Coding Style & Naming Conventions
- Java 24; 2‑space indentation (see `.editorconfig`).
- Formatting: Spotless with Palantir/Google style; no wildcard imports.
- Static analysis: Error Prone + NullAway for `io.kraluk.*`. Prefer non‑null APIs; use `Optional`/JSR‑305/JSpecify annotations when needed.
- Packages: `io.kraluk.orderprocessor…`; Classes `UpperCamelCase`, methods/fields `lowerCamelCase`.
- Tests: class names end with `Test`; integration tests extend `IntegrationTest` when feasible.

## Testing Guidelines
- Frameworks: JUnit 5, AssertJ, Awaitility; Testcontainers for infra.
- Data/infra: Postgres via `compose.yml`; LocalStack seeded by `.scripts/localstack/setup.sh`.
- Aim for fast unit tests; mock adapters and focus on domain. Place end‑to‑end flows under `…/acceptance`.

## Commit & Pull Request Guidelines
- Commits: Conventional Commits (e.g., `feat: …`, `fix(scope): …`, `chore(deps): …`).
- PRs: clear description, linked issues, test plan/commands, and logs/screenshots if relevant. Update docs and Liquibase/jOOQ when schema changes.
- CI: must pass `./gradlew check` (CI uses Temurin 24).

## Security & Configuration Tips
- Do not commit secrets. Prefer `application-local.yaml` for local overrides.
- Colima/Testcontainers users: export `DOCKER_HOST` and `TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE` as shown in `README.md`.
