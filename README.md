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

### Вариант 1: Docker (рекомендуется)

#### Автоматический запуск:
**Windows:**
```bash
docker-run.bat
```

**Linux/Mac:**
```bash
chmod +x docker-run.sh
./docker-run.sh
```

#### Ручной запуск:
```bash
# Сборка и запуск
docker-compose up --build -d

# Просмотр логов
docker-compose logs -f app

# Остановка
docker-compose down
```

### Вариант 2: Локальный запуск

1. Поднимите PostgreSQL:
```bash
docker compose up -d postgres
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
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
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

## Docker команды

### Полезные команды:
```bash
# Сборка образа
docker build -t agrocrm .

# Запуск только базы данных
docker-compose up -d postgres

# Запуск приложения с пересборкой
docker-compose up --build

# Просмотр логов
docker-compose logs -f app

# Остановка всех сервисов
docker-compose down

# Остановка с удалением volumes
docker-compose down -v
```

### Переменные окружения для Docker:
- `DB_URL` - URL базы данных
- `DB_USER` - пользователь БД
- `DB_PASSWORD` - пароль БД
- `JWT_SECRET` - секретный ключ для JWT

## Дальше
- Добавить модули: путевые листы, материалы, ТО/ремонт, отчёты и KPI.
- Реализовать матчинг ГСМ ↔ путевые листы ↔ работы, алерты.
- Интеграции с 1С: из outbox в шину/очередь/HTTP, регистрация статусов.
