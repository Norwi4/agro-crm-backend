#!/bin/bash

echo "🚀 Запуск Agro CRM в Docker..."

# Проверяем, установлен ли Docker
if ! command -v docker &> /dev/null; then
    echo "❌ Docker не установлен. Пожалуйста, установите Docker."
    exit 1
fi

# Проверяем, установлен ли Docker Compose
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose не установлен. Пожалуйста, установите Docker Compose."
    exit 1
fi

# Останавливаем существующие контейнеры
echo "🛑 Останавливаем существующие контейнеры..."
docker-compose down

# Собираем и запускаем приложение
echo "🔨 Собираем и запускаем приложение..."
docker-compose up --build -d

# Ждем запуска приложения
echo "⏳ Ждем запуска приложения..."
sleep 30

# Проверяем статус
echo "📊 Статус контейнеров:"
docker-compose ps

echo ""
echo "✅ Agro CRM запущен!"
echo "🌐 Swagger UI: http://localhost:8080/swagger-ui/index.html"
echo "🔗 API Docs: http://localhost:8080/v3/api-docs"
echo "💚 Health Check: http://localhost:8080/actuator/health"
echo ""
echo "📝 Логи приложения:"
echo "docker-compose logs -f app"
echo ""
echo "🛑 Остановка:"
echo "docker-compose down"
