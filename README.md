# AgroCRM - Система управления сельскохозяйственным предприятием

## Описание

AgroCRM - это современная система управления сельскохозяйственным предприятием, разработанная для автоматизации и оптимизации всех ключевых бизнес-процессов в агросекторе. Система предоставляет комплексное решение для управления полями, техникой, материалами, топливом и персоналом.

## Целевая аудитория

- **Сельскохозяйственные предприятия** - от небольших фермерских хозяйств до крупных агрохолдингов
- **Агрономы и технолог** - для планирования и контроля сельскохозяйственных операций
- **Менеджеры и руководители** - для аналитики и принятия управленческих решений
- **Механизаторы и водители** - для ведения путевых листов и учета работы техники
- **Складские работники** - для управления материальными запасами

## Основные возможности

### Управление пользователями и безопасность
- **Ролевая система доступа** (ADMIN, USER)
- **JWT аутентификация** с refresh токенами
- **Управление сессиями** - возможность завершения сессий на других устройствах
- **Аудит действий** - полное логирование всех операций пользователей

### Управление полями
- Создание и редактирование полей с указанием площади и типа почвы
- Планирование севооборота
- Учет истории обработки полей
- Геолокация и картографирование

### Управление техникой и путевыми листами
- Ведение путевых листов (waybills)
- Учет работы техники по полям
- Контроль расхода топлива
- Планирование технического обслуживания

### Управление топливом
- Учет заправок и расхода топлива
- Контроль топливных лимитов
- Аналитика потребления топлива
- Интеграция с путевыми листами

### Техническое обслуживание
- Планирование технического обслуживания
- Создание заявок на ремонт
- Учет выполненных работ
- Контроль состояния техники

### Управление материалами
- Учет семян, удобрений, средств защиты растений
- Контроль остатков на складе
- Выдача материалов по заявкам
- Партионный учет

### Аналитика и отчетность
- Аналитика по полям и урожайности
- Отчеты по работе техники
- Анализ расхода топлива и материалов
- Аудит действий пользователей

## Архитектура системы

### Технологический стек
- **Backend**: Spring Boot 3.x, Java 17
- **База данных**: PostgreSQL 15+
- **Миграции**: Liquibase
- **Безопасность**: Spring Security, JWT
- **Документация**: OpenAPI 3 (Swagger)
- **Контейнеризация**: Docker

### Структура проекта
```
src/main/java/com/agrocrm/
├── AgroCrmApplication.java          # Главный класс приложения
├── analytics/                       # Аналитика и отчеты
│   ├── AnalyticsController.java     # Основная аналитика
│   ├── AuditController.java         # Аудит системы
│   └── ReferenceDataController.java # Справочные данные
├── auth/                           # Аутентификация
│   ├── AuthController.java         # Вход/выход/refresh токены
│   ├── AuthRequest.java            # DTO для входа
│   ├── AuthResponse.java           # DTO ответа аутентификации
│   └── RefreshTokenRequest.java    # DTO для обновления токена
├── config/                         # Конфигурация
│   ├── AuditService.java           # Сервис аудита
│   ├── OpenApiConfig.java          # Конфигурация Swagger
│   └── SecurityConfig.java         # Конфигурация безопасности
├── domain/                         # Доменные модели
│   ├── field/                      # Управление полями
│   ├── fuel/                       # Управление топливом
│   ├── maintenance/                # Техническое обслуживание
│   ├── material/                   # Управление материалами
│   ├── session/                    # Управление сессиями
│   ├── task/                       # Задачи и поручения
│   └── waybill/                    # Путевые листы
├── errors/                         # Обработка ошибок
│   └── GlobalExceptionHandler.java
├── integration/                    # Интеграции
│   └── outbox/                     # Паттерн Outbox
├── security/                       # Безопасность
│   ├── JwtAuthFilter.java          # JWT фильтр
│   ├── JwtService.java             # Сервис JWT
│   └── SecurityUtil.java           # Утилиты безопасности
└── user/                          # Пользователи
    ├── CustomUserDetailsService.java
    └── User.java                   # Модель пользователя
```

## Система безопасности

### JWT токены
- **Access Token**: короткоживущий (30 минут) для API запросов
- **Refresh Token**: долгоживущий (30 дней) для обновления access токенов
- **Типизация токенов**: каждый токен содержит claim "type" для различения

### Управление сессиями
- Каждый пользователь может иметь несколько активных сессий
- Возможность завершения конкретных сессий
- Возможность выхода со всех устройств кроме текущего
- Автоматическое истечение сессий через 30 дней

### Аудит
- Логирование всех важных действий пользователей
- Сохранение IP адресов и User-Agent
- Возможность просмотра аудита через API (только для администраторов)

## API Endpoints

### Аутентификация
- `POST /api/auth/login` - Вход в систему
- `POST /api/auth/refresh` - Обновление токена
- `POST /api/auth/logout` - Выход из системы
- `POST /api/auth/logout-all` - Выход со всех устройств

### Управление сессиями
- `GET /api/sessions` - Получение активных сессий
- `DELETE /api/sessions/{sessionId}` - Завершение конкретной сессии

### Поля
- `GET /api/fields` - Список полей
- `POST /api/fields` - Создание поля
- `PUT /api/fields/{id}` - Обновление поля
- `DELETE /api/fields/{id}` - Удаление поля

### Путевые листы
- `GET /api/waybills` - Список путевых листов
- `POST /api/waybills` - Создание путевого листа
- `PUT /api/waybills/{id}` - Обновление путевого листа

### Топливо
- `GET /api/fuel/transactions` - Транзакции с топливом
- `POST /api/fuel/transactions` - Создание транзакции
- `GET /api/fuel/policies` - Топливные лимиты

### Материалы
- `GET /api/materials` - Список материалов
- `POST /api/materials` - Создание материала
- `GET /api/materials/{id}/batches` - Партии материала
- `POST /api/materials/{id}/issues` - Выдача материала

### Техническое обслуживание
- `GET /api/maintenance/orders` - Заказы на обслуживание
- `POST /api/maintenance/orders` - Создание заказа
- `GET /api/maintenance/plans` - Планы обслуживания

### Аудит
- `GET /api/audit` - Журнал аудита (только ADMIN)
- `GET /api/audit/users/{userId}` - Аудит пользователя
- `GET /api/audit/actions/{action}` - Аудит по типу действия

## Установка и запуск

### Предварительные требования
- Java 17 или выше
- PostgreSQL 15 или выше
- Maven 3.6 или выше
- Docker (опционально)

### Локальная установка

1. **Клонирование репозитория**
```bash
git clone <repository-url>
cd agro-crm-backend
```

2. **Настройка базы данных**
```sql
CREATE DATABASE agrocrm;
CREATE USER agrocrm_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE agrocrm TO agrocrm_user;
```

3. **Настройка конфигурации**
Создайте файл `application-local.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/agrocrm
    username: agrocrm_user
    password: your_password
  jpa:
    hibernate:
      ddl-auto: validate

app:
  jwt:
    secret: your-super-secret-jwt-key-here
    accessTokenExpirationMinutes: 30
    refreshTokenExpirationDays: 30
```

4. **Запуск приложения**
```bash
mvn spring-boot:run -Dspring.profiles.active=local
```

### Запуск через Docker

1. **Сборка образа**
```bash
docker build -t agrocrm-backend .
```

2. **Запуск с Docker Compose**
```bash
docker-compose up -d
```

## Демо данные

Система поставляется с демонстрационными данными:
- Пользователи: `admin` (пароль: `admin`), `user` (пароль: `user`)
- Примеры полей, техники, материалов
- Демонстрационные путевые листы и транзакции

## Конфигурация

### Основные настройки (application.yml)
```yaml
server:
  port: 8080

spring:
  application:
    name: agro-crm-backend
  datasource:
    url: jdbc:postgresql://localhost:5432/agrocrm
    username: ${DB_USERNAME:agrocrm_user}
    password: ${DB_PASSWORD:password}
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  liquibase:
    change-log: classpath:db/changelog/db.changelog-master.xml

app:
  jwt:
    secret: ${JWT_SECRET:change-me-super-secret-please-change-this-is-a-very-long-secret-key-for-hs512-algorithm-that-must-be-at-least-512-bits-long}
    accessTokenExpirationMinutes: 30
    refreshTokenExpirationDays: 30
```

### Переменные окружения
- `DB_USERNAME` - имя пользователя базы данных
- `DB_PASSWORD` - пароль базы данных
- `JWT_SECRET` - секретный ключ для JWT (обязательно изменить в продакшене)

## Документация API

После запуска приложения документация доступна по адресу:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## Мониторинг и логирование

### Логирование
- Все действия пользователей логируются в таблицу `audit_log`
- Сессии пользователей отслеживаются в таблице `user_session`
- Подробные логи приложения в стандартном формате Spring Boot

### Метрики
- Встроенные метрики Spring Boot Actuator
- Мониторинг состояния базы данных
- Отслеживание производительности API

## Развертывание в продакшене

### Рекомендации по безопасности
1. Измените JWT_SECRET на уникальный секретный ключ
2. Настройте HTTPS/TLS
3. Ограничьте доступ к базе данных
4. Настройте файрвол
5. Регулярно обновляйте зависимости

### Масштабирование
- Используйте внешнюю базу данных (например, AWS RDS)
- Настройте балансировщик нагрузки для нескольких экземпляров
- Используйте Redis для кэширования сессий (опционально)

## Разработка

### Структура базы данных
Миграции базы данных управляются через Liquibase:
- `001-initial-schema.sql` - Основная схема
- `002-domain-extensions.sql` - Расширения домена
- `003-demo-data.sql` - Демо данные
- `004-sessions-management.sql` - Управление сессиями
- `005-audit-log-user-id-migration.sql` - Миграция аудита

### Добавление новых функций
1. Создайте миграцию в `src/main/resources/db/changelog/changes/`
2. Добавьте модель в соответствующий пакет `domain/`
3. Создайте контроллер с аннотациями Swagger
4. Добавьте аудит для важных операций
5. Обновите документацию

## Поддержка

### Контакты
- **Email**: support@agrocrm.com
- **Документация**: https://docs.agrocrm.com
- **Issues**: GitHub Issues

### Сообщество
- Форум разработчиков
- Telegram канал
- Ежемесячные вебинары

## Лицензия

MIT License - см. файл [LICENSE](LICENSE) для подробностей.

---

**AgroCRM** - современное решение для цифровизации сельского хозяйства
