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

### Вариант 1: Docker с демо-данными (рекомендуется для демонстрации)

#### Автоматический запуск:
**Windows:**
```bash
demo-setup.bat
```

**Linux/Mac:**
```bash
chmod +x demo-setup.sh
./demo-setup.sh
```

#### Ручной запуск:
```bash
# Сборка и запуск с демо-данными
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

4. Заполните демо-данными (опционально):
```bash
psql -h localhost -U postgres -d agrocrm -f demo-data.sql
```

## Документация API
После запуска приложения документация API доступна по адресу:
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## Демо-данные

### Пользователи (пароль для всех: `password`)
- **admin** - Администратор Системы (IT)
- **agronom** - Петров Иван Сергеевич (Агрономия)
- **mechanic** - Сидоров Алексей Петрович (Механика)
- **driver1** - Козлов Михаил Иванович (Транспорт)
- **driver2** - Новиков Дмитрий Александрович (Транспорт)
- **accountant** - Иванова Елена Владимировна (Бухгалтерия)
- **manager** - Смирнов Андрей Николаевич (Управление)

### Сельскохозяйственные поля
- **Поле №1 - Северное** (150.5 га) - Пшеница озимая
- **Поле №2 - Восточное** (200.0 га) - Ячмень
- **Поле №3 - Западное** (120.3 га) - Подсолнечник
- **Поле №4 - Южное** (180.7 га) - Кукуруза
- **Поле №5 - Центральное** (95.2 га) - Рапс

### Техника
- **А123БВ77** - John Deere 8R 410 (Трактор)
- **В456ГД78** - Case IH Magnum 400 (Трактор)
- **Е789ЖЗ79** - Claas Lexion 760 (Комбайн)
- **И012КЛ80** - Horsch Pronto 6DC (Посевной комплекс)
- **М345НО81** - Amazone UX 5200 (Опрыскиватель)

### Материалы
- Семена пшеницы, ячменя, подсолнечника
- Азотные и фосфорные удобрения
- Гербицид Раундап, Инсектицид Децис

### Задачи
- ✅ Посев пшеницы озимой (завершена)
- 🔄 Посев ячменя (в работе)
- 📅 Обработка гербицидом (запланирована)
- 📅 Уборка кукурузы (запланирована)
- 📅 Внесение удобрений (запланирована)

## Основные эндпоинты (MVP)
- `POST /auth/login` → JWT
- `GET/POST/PUT/DELETE /api/fields`
- `POST /api/tasks` (создание)
- `POST /api/tasks/{id}/start|finish|cancel`
- `GET /api/tasks?status=IN_PROGRESS&fieldId=...`
- `POST /api/fuel/import` (загрузка транзакций ГСМ, черновик)
- `GET /api/fuel/alerts/night`
- `GET /actuator/health`

## Демонстрация заказчику

### Что показать:
1. **Авторизация** - Вход в систему через Swagger UI
2. **Поля** - `GET /api/fields` - Просмотр сельскохозяйственных полей
3. **Задачи** - `GET /api/tasks` - Управление сельскохозяйственными задачами
4. **Материалы** - `GET /api/materials` - Учет материалов и их выдача
5. **Путевые листы** - `GET /api/waybills` - Документооборот техники
6. **Топливо** - `GET /api/fuel/alerts/night` - Контроль топливных транзакций
7. **Техобслуживание** - `GET /api/maintenance/orders` - Заявки на ТО
8. **Аналитика** - `GET /api/analytics/kpi/cost-per-ha` - Стоимость на гектар

### Сценарий демонстрации:
1. Запустите `demo-setup.bat` (Windows) или `./demo-setup.sh` (Linux/Mac)
2. Откройте http://localhost:8080/swagger-ui/index.html
3. Авторизуйтесь как `admin` с паролем `password`
4. Покажите основные функции системы

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
