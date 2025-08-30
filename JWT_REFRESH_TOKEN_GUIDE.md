# Система JWT токенов с Refresh токенами

## Обзор

Система аутентификации была обновлена для поддержки refresh токенов, что обеспечивает лучший пользовательский опыт для мобильных приложений.

## Архитектура токенов

### Access Token
- **Время жизни**: 30 минут
- **Назначение**: Доступ к API
- **Содержит**: username, role, sessionId, type="access"
- **Использование**: В заголовке `Authorization: Bearer <access_token>`

### Refresh Token
- **Время жизни**: 30 дней
- **Назначение**: Обновление access токена
- **Содержит**: username, sessionId, type="refresh"
- **Использование**: Для получения нового access токена

## API Endpoints

### 1. Вход в систему
```http
POST /auth/login
Content-Type: application/json

{
  "username": "user",
  "password": "password"
}
```

**Ответ:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "role": "ADMIN",
  "username": "user"
}
```

### 2. Обновление токена
```http
POST /auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Ответ:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "role": "ADMIN",
  "username": "user"
}
```

### 3. Выход из системы
```http
POST /auth/logout
Authorization: Bearer <access_token>
```

### 4. Выход со всех устройств
```http
POST /auth/logout-all
Authorization: Bearer <access_token>
```

### 5. Управление сессиями

#### Получение активных сессий
```http
GET /api/sessions
Authorization: Bearer <access_token>
```

#### Завершение конкретной сессии
```http
DELETE /api/sessions/{sessionId}
Authorization: Bearer <access_token>
```

## Логика работы

### Вход в систему
1. Пользователь отправляет логин/пароль
2. Система создает сессию в базе данных
3. Генерируются access и refresh токены
4. Refresh токен сохраняется в сессии

### Использование API
1. Клиент использует access токен для API запросов
2. Если access токен истек (401 ошибка), клиент использует refresh токен
3. Система проверяет refresh токен и активность сессии
4. Генерируются новые access и refresh токены

### Обновление токенов
1. Клиент отправляет refresh токен
2. Система проверяет:
   - Валидность refresh токена
   - Не истек ли токен
   - Активна ли сессия в базе данных
3. Если все проверки пройдены, генерируются новые токены

### Управление сессиями
- Пользователь может видеть все свои активные сессии
- Может завершить конкретную сессию
- Может выйти со всех устройств одновременно
- При выходе со всех устройств все refresh токены становятся недействительными

## Безопасность

### Защита от кражи токенов
- Access токены имеют короткое время жизни (30 минут)
- Refresh токены привязаны к сессиям в базе данных
- Возможность отозвать refresh токены в любой момент

### Управление сессиями
- Каждая сессия имеет уникальный ID
- Сессии можно деактивировать удаленно
- Ведется аудит всех действий с сессиями

## Конфигурация

В `application.yml`:
```yaml
app:
  jwt:
    secret: ${JWT_SECRET:your-secret-key}
    accessTokenExpirationMinutes: 30
    refreshTokenExpirationDays: 30
```

## Рекомендации для клиентов

### Мобильные приложения
1. Сохраняйте refresh токен в безопасном хранилище
2. При получении 401 ошибки автоматически обновляйте токен
3. Реализуйте механизм повторных попыток для обновления токена

### Web приложения
1. Используйте refresh токен для автоматического обновления
2. Реализуйте logout для очистки токенов
3. Предоставьте пользователю возможность управления сессиями

## Миграция

Если у вас есть существующие клиенты:
1. Обновите клиенты для работы с новой системой токенов
2. Добавьте обработку refresh токенов
3. Реализуйте автоматическое обновление токенов при 401 ошибках
