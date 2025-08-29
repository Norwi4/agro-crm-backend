@echo off
echo 🚀 Запуск Agro CRM в Docker...

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
docker-compose down

REM Собираем и запускаем приложение
echo 🔨 Собираем и запускаем приложение...
docker-compose up --build -d

REM Ждем запуска приложения
echo ⏳ Ждем запуска приложения...
timeout /t 30 /nobreak >nul

REM Проверяем статус
echo 📊 Статус контейнеров:
docker-compose ps

echo.
echo ✅ Agro CRM запущен!
echo 🌐 Swagger UI: http://localhost:8080/swagger-ui/index.html
echo 🔗 API Docs: http://localhost:8080/v3/api-docs
echo 💚 Health Check: http://localhost:8080/actuator/health
echo.
echo 📝 Логи приложения:
echo docker-compose logs -f app
echo.
echo 🛑 Остановка:
echo docker-compose down
echo.
pause
