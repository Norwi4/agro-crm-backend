# Agro CRM Backend (Spring Boot + JdbcTemplate + PostgreSQL)

Минимально рабочий бэкенд под описание CRM для агробизнеса.

## Стек
- Java 21, Spring Boot 3.3
- Spring Web, Security (JWT), Validation
- Spring JDBC (JdbcTemplate)
- PostgreSQL 16, Liquibase (миграции)
- Swagger/OpenAPI 3 (документация API)
- Outbox-шина для интеграций (1С, телематика)

## Быстрый старт
1. Поднимите PostgreSQL:
```bash
docker compose up -d
```
2. Установите переменные окружения (или правьте `application.yml`):
```bash
export DB_URL=jdbc:postgresql://localhost:5432/agrocrm
export DB_USER=postgres
export DB_PASSWORD=postgres
export JWT_SECRET=change-me-super-secret-please-change
```
3. Запуск:
```bash
mvn spring-boot:run
```

## Документация API
После запуска приложения документация API доступна по адресу:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## Логины по умолчанию
- admin / admin123 (ROLE_ADMIN)
- agronom / agronom123 (ROLE_AGRONOMIST)
- driver / password (ROLE_DRIVER)

## Основные эндпоинты (MVP)
- `POST /auth/login` → JWT
- `GET/POST/PUT/DELETE /api/fields`
- `POST /api/tasks` (создание)
- `POST /api/tasks/{id}/start|finish|cancel`
- `GET /api/tasks?status=IN_PROGRESS&fieldId=...`
- `POST /api/fuel/import` (загрузка транзакций ГСМ, черновик)
- `GET /api/fuel/alerts/night`
- `GET /actuator/health`

## Дальше
- Добавить модули: путевые листы, материалы, ТО/ремонт, отчёты и KPI.
- Реализовать матчинг ГСМ ↔ путевые листы ↔ работы, алерты.
- Интеграции с 1С: из outbox в шину/очередь/HTTP, регистрация статусов.
