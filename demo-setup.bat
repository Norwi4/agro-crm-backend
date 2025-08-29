@echo off
echo 🚀 Быстрый запуск Agro CRM с демо-данными...

REM Проверяем, установлен ли Docker
docker --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker не установлен. Пожалуйста, установите Docker.
    pause
    exit /b 1
)

REM Проверяем, установлен ли Docker Compose
docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker Compose не установлен. Пожалуйста, установите Docker Compose.
    pause
    exit /b 1
)

REM Останавливаем существующие контейнеры
echo 🛑 Останавливаем существующие контейнеры...
docker-compose down -v

REM Собираем и запускаем приложение
echo 🔨 Собираем и запускаем приложение...
docker-compose up --build -d

REM Ждем запуска приложения
echo ⏳ Ждем запуска приложения...
timeout /t 45 /nobreak >nul

REM Проверяем статус
echo 📊 Статус контейнеров:
docker-compose ps

echo.
echo ✅ Agro CRM запущен с демо-данными!
echo.
echo 🌐 Swagger UI: http://localhost:8080/swagger-ui/index.html
echo 🔗 API Docs: http://localhost:8080/v3/api-docs
echo 💚 Health Check: http://localhost:8080/actuator/health
echo.
echo 👥 Демо-пользователи (пароль для всех: password):
echo    - admin (Администратор)
echo    - agronom (Агроном)
echo    - mechanic (Механик)
echo    - driver1 (Водитель 1)
echo    - driver2 (Водитель 2)
echo    - accountant (Бухгалтер)
echo    - manager (Менеджер)
echo.
echo 📋 Что можно показать заказчику:
echo    1. Авторизация через Swagger UI
echo    2. Просмотр полей: GET /api/fields
echo    3. Просмотр задач: GET /api/tasks
echo    4. Просмотр материалов: GET /api/materials
echo    5. Просмотр техники и путевых листов
echo    6. Аналитика: GET /api/analytics/kpi/cost-per-ha
echo    7. Топливные транзакции и алерты
echo    8. Заявки на техобслуживание
echo.
echo 📝 Логи приложения:
echo docker-compose logs -f app
echo.
echo 🛑 Остановка:
echo docker-compose down
echo.
echo 🗑️  Полная очистка:
echo docker-compose down -v
echo.
pause
