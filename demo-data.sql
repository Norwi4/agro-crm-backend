-- Скрипт для заполнения базы данных демонстрационными данными
-- Используйте этот скрипт для быстрого заполнения данных при показе системы заказчику

-- Очистка существующих данных (опционально)
-- DELETE FROM outbox_event;
-- DELETE FROM fuel_transaction;
-- DELETE FROM waybill;
-- DELETE FROM material_issue;
-- DELETE FROM task;
-- DELETE FROM material_batch;
-- DELETE FROM material;
-- DELETE FROM machine;
-- DELETE FROM field;
-- DELETE FROM app_user WHERE username != 'admin';

-- Пользователи системы
INSERT INTO app_user (username, password, full_name, role, department) VALUES
('agronom', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Петров Иван Сергеевич', 'AGRONOMIST', 'Агрономия'),
('mechanic', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Сидоров Алексей Петрович', 'MECHANIC', 'Механика'),
('driver1', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Козлов Михаил Иванович', 'DRIVER', 'Транспорт'),
('driver2', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Новиков Дмитрий Александрович', 'DRIVER', 'Транспорт'),
('accountant', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Иванова Елена Владимировна', 'ACCOUNTANT', 'Бухгалтерия'),
('manager', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Смирнов Андрей Николаевич', 'MANAGER', 'Управление')
ON CONFLICT (username) DO NOTHING;

-- Сельскохозяйственные поля
INSERT INTO field (id, name, area_ha, crop, season, soil_type, geojson) VALUES
('550e8400-e29b-41d4-a716-446655440001', 'Поле №1 - Северное', 150.5, 'Пшеница озимая', '2024', 'Чернозем', '{"type":"Polygon","coordinates":[[[37.123,55.456],[37.124,55.456],[37.124,55.457],[37.123,55.457],[37.123,55.456]]]}'),
('550e8400-e29b-41d4-a716-446655440002', 'Поле №2 - Восточное', 200.0, 'Ячмень', '2024', 'Чернозем', '{"type":"Polygon","coordinates":[[[37.125,55.458],[37.126,55.458],[37.126,55.459],[37.125,55.459],[37.125,55.458]]]}'),
('550e8400-e29b-41d4-a716-446655440003', 'Поле №3 - Западное', 120.3, 'Подсолнечник', '2024', 'Суглинок', '{"type":"Polygon","coordinates":[[[37.120,55.460],[37.121,55.460],[37.121,55.461],[37.120,55.461],[37.120,55.460]]]}'),
('550e8400-e29b-41d4-a716-446655440004', 'Поле №4 - Южное', 180.7, 'Кукуруза', '2024', 'Чернозем', '{"type":"Polygon","coordinates":[[[37.122,55.462],[37.123,55.462],[37.123,55.463],[37.122,55.463],[37.122,55.462]]]}'),
('550e8400-e29b-41d4-a716-446655440005', 'Поле №5 - Центральное', 95.2, 'Рапс', '2024', 'Суглинок', '{"type":"Polygon","coordinates":[[[37.124,55.464],[37.125,55.464],[37.125,55.465],[37.124,55.465],[37.124,55.464]]]}')
ON CONFLICT (id) DO NOTHING;

-- Техника
INSERT INTO machine (id, reg_number, type, fuel_norm_lph, fuel_norm_lpha, service_plan) VALUES
('550e8400-e29b-41d4-a716-446655440010', 'А123БВ77', 'ТРАКТОР', 15.5, 8.2, '{"intervals": {"oil_change": 250, "air_filter": 500, "fuel_filter": 1000}}'),
('550e8400-e29b-41d4-a716-446655440011', 'В456ГД78', 'ТРАКТОР', 18.0, 9.5, '{"intervals": {"oil_change": 300, "air_filter": 600, "fuel_filter": 1200}}'),
('550e8400-e29b-41d4-a716-446655440012', 'Е789ЖЗ79', 'КОМБАЙН', 25.0, 12.0, '{"intervals": {"oil_change": 200, "air_filter": 400, "fuel_filter": 800}}'),
('550e8400-e29b-41d4-a716-446655440013', 'И012КЛ80', 'ПОСЕВНОЙ_КОМПЛЕКС', 12.0, 6.5, '{"intervals": {"oil_change": 150, "air_filter": 300, "fuel_filter": 600}}'),
('550e8400-e29b-41d4-a716-446655440014', 'М345НО81', 'ОПРЫСКИВАТЕЛЬ', 10.5, 5.8, '{"intervals": {"oil_change": 100, "air_filter": 200, "fuel_filter": 400}}')
ON CONFLICT (id) DO NOTHING;

-- Материалы
INSERT INTO material (id, name, unit, category) VALUES
('550e8400-e29b-41d4-a716-446655440020', 'Семена пшеницы озимой', 'кг', 'СЕМЕНА'),
('550e8400-e29b-41d4-a716-446655440021', 'Семена ячменя', 'кг', 'СЕМЕНА'),
('550e8400-e29b-41d4-a716-446655440022', 'Семена подсолнечника', 'кг', 'СЕМЕНА'),
('550e8400-e29b-41d4-a716-446655440023', 'Азотные удобрения', 'кг', 'УДОБРЕНИЯ'),
('550e8400-e29b-41d4-a716-446655440024', 'Фосфорные удобрения', 'кг', 'УДОБРЕНИЯ'),
('550e8400-e29b-41d4-a716-446655440025', 'Гербицид Раундап', 'л', 'СРЕДСТВА_ЗАЩИТЫ'),
('550e8400-e29b-41d4-a716-446655440026', 'Инсектицид Децис', 'л', 'СРЕДСТВА_ЗАЩИТЫ')
ON CONFLICT (id) DO NOTHING;

-- Партии материалов
INSERT INTO material_batch (id, material_id, batch_number, qty, unit_price, supplier, expiry_date) VALUES
('550e8400-e29b-41d4-a716-446655440030', '550e8400-e29b-41d4-a716-446655440020', 'BATCH-2024-001', 5000.0, 45.50, 'ООО "Семена Плюс"', '2025-12-31'),
('550e8400-e29b-41d4-a716-446655440031', '550e8400-e29b-41d4-a716-446655440021', 'BATCH-2024-002', 3000.0, 38.75, 'ООО "Семена Плюс"', '2025-12-31'),
('550e8400-e29b-41d4-a716-446655440032', '550e8400-e29b-41d4-a716-446655440022', 'BATCH-2024-003', 2000.0, 125.00, 'ООО "Семена Плюс"', '2025-12-31'),
('550e8400-e29b-41d4-a716-446655440033', '550e8400-e29b-41d4-a716-446655440023', 'BATCH-2024-004', 10000.0, 25.80, 'ООО "Агрохим"', '2026-06-30'),
('550e8400-e29b-41d4-a716-446655440034', '550e8400-e29b-41d4-a716-446655440025', 'BATCH-2024-005', 500.0, 850.00, 'ООО "Агрохим"', '2025-08-31'),
('550e8400-e29b-41d4-a716-446655440035', '550e8400-e29b-41d4-a716-446655440026', 'BATCH-2024-006', 200.0, 1200.00, 'ООО "Агрохим"', '2025-10-31')
ON CONFLICT (id) DO NOTHING;

-- Задачи
INSERT INTO task (id, field_id, title, description, status, priority, planned_start, planned_end, assigned_user, assigned_machine) VALUES
('550e8400-e29b-41d4-a716-446655440040', '550e8400-e29b-41d4-a716-446655440001', 'Посев пшеницы озимой', 'Посев пшеницы озимой на поле №1', 'DONE', 1, '2024-09-15 08:00:00+03', '2024-09-15 18:00:00+03', (SELECT id FROM app_user WHERE username = 'agronom'), '550e8400-e29b-41d4-a716-446655440013'),
('550e8400-e29b-41d4-a716-446655440041', '550e8400-e29b-41d4-a716-446655440002', 'Посев ячменя', 'Посев ячменя на поле №2', 'IN_PROGRESS', 2, '2024-09-20 08:00:00+03', '2024-09-20 18:00:00+03', (SELECT id FROM app_user WHERE username = 'agronom'), '550e8400-e29b-41d4-a716-446655440013'),
('550e8400-e29b-41d4-a716-446655440042', '550e8400-e29b-41d4-a716-446655440003', 'Обработка гербицидом', 'Обработка подсолнечника гербицидом', 'PLANNED', 3, '2024-09-25 08:00:00+03', '2024-09-25 16:00:00+03', (SELECT id FROM app_user WHERE username = 'agronom'), '550e8400-e29b-41d4-a716-446655440014'),
('550e8400-e29b-41d4-a716-446655440043', '550e8400-e29b-41d4-a716-446655440004', 'Уборка кукурузы', 'Уборка кукурузы на поле №4', 'PLANNED', 1, '2024-10-15 08:00:00+03', '2024-10-15 20:00:00+03', (SELECT id FROM app_user WHERE username = 'driver1'), '550e8400-e29b-41d4-a716-446655440012'),
('550e8400-e29b-41d4-a716-446655440044', '550e8400-e29b-41d4-a716-446655440005', 'Внесение удобрений', 'Внесение азотных удобрений на рапс', 'PLANNED', 2, '2024-09-30 08:00:00+03', '2024-09-30 14:00:00+03', (SELECT id FROM app_user WHERE username = 'driver2'), '550e8400-e29b-41d4-a716-446655440010')
ON CONFLICT (id) DO NOTHING;

-- Выдача материалов
INSERT INTO material_issue (id, task_id, material_batch_id, qty) VALUES
('550e8400-e29b-41d4-a716-446655440050', '550e8400-e29b-41d4-a716-446655440040', '550e8400-e29b-41d4-a716-446655440030', 2500.0),
('550e8400-e29b-41d4-a716-446655440051', '550e8400-e29b-41d4-a716-446655440041', '550e8400-e29b-41d4-a716-446655440031', 1500.0),
('550e8400-e29b-41d4-a716-446655440052', '550e8400-e29b-41d4-a716-446655440042', '550e8400-e29b-41d4-a716-446655440034', 100.0)
ON CONFLICT (id) DO NOTHING;

-- Путевые листы
INSERT INTO waybill (id, task_id, driver_id, machine_id, route, start_ts, end_ts, odometer_start, odometer_end, engine_hours_start, engine_hours_end, fuel_start, fuel_end, status) VALUES
('550e8400-e29b-41d4-a716-446655440060', '550e8400-e29b-41d4-a716-446655440040', (SELECT id FROM app_user WHERE username = 'driver1'), '550e8400-e29b-41d4-a716-446655440013', '{"route": "База - Поле №1 - База"}', '2024-09-15 08:00:00+03', '2024-09-15 18:30:00+03', 1250.5, 1280.2, 950.7, 975.3, 50.0, 15.0, 'SIGNED'),
('550e8400-e29b-41d4-a716-446655440061', '550e8400-e29b-41d4-a716-446655440041', (SELECT id FROM app_user WHERE username = 'driver2'), '550e8400-e29b-41d4-a716-446655440013', '{"route": "База - Поле №2 - База"}', '2024-09-20 08:00:00+03', NULL, 1280.2, NULL, 975.3, NULL, 50.0, NULL, 'ISSUED')
ON CONFLICT (id) DO NOTHING;

-- Топливные транзакции
INSERT INTO fuel_transaction (id, card_number, vehicle_reg, liters, price, amount, ts, location, source, matched_task, anomalies) VALUES
('550e8400-e29b-41d4-a716-446655440070', 'CARD-001', 'А123БВ77', 50.0, 45.50, 2275.0, '2024-09-15 07:30:00+03', '{"name": "АЗС №1", "address": "ул. Ленина, 15"}', '1C', '550e8400-e29b-41d4-a716-446655440040', NULL),
('550e8400-e29b-41d4-a716-446655440071', 'CARD-002', 'В456ГД78', 45.0, 45.50, 2047.5, '2024-09-15 07:45:00+03', '{"name": "АЗС №2", "address": "ул. Мира, 8"}', '1C', NULL, NULL),
('550e8400-e29b-41d4-a716-446655440072', 'CARD-003', 'Е789ЖЗ79', 80.0, 45.50, 3640.0, '2024-09-15 08:00:00+03', '{"name": "АЗС №1", "address": "ул. Ленина, 15"}', '1C', NULL, NULL),
('550e8400-e29b-41d4-a716-446655440073', 'CARD-001', 'А123БВ77', 30.0, 45.50, 1365.0, '2024-09-15 18:45:00+03', '{"name": "АЗС №1", "address": "ул. Ленина, 15"}', '1C', '550e8400-e29b-41d4-a716-446655440040', NULL),
('550e8400-e29b-41d4-a716-446655440074', 'CARD-004', 'И012КЛ80', 25.0, 45.50, 1137.5, '2024-09-20 07:30:00+03', '{"name": "АЗС №1", "address": "ул. Ленина, 15"}', '1C', '550e8400-e29b-41d4-a716-446655440041', NULL),
('550e8400-e29b-41d4-a716-446655440075', 'CARD-005', 'М345НО81', 40.0, 45.50, 1820.0, '2024-09-20 23:30:00+03', '{"name": "АЗС №3", "address": "ул. Гагарина, 25"}', '1C', NULL, '{"type": "night_fueling", "description": "Ночная заправка"}')
ON CONFLICT (id) DO NOTHING;

-- Заявки на техобслуживание
INSERT INTO maintenance_order (id, machine_id, type, planned_ts, status, parts, cost) VALUES
('550e8400-e29b-41d4-a716-446655440080', '550e8400-e29b-41d4-a716-446655440010', 'ТО', '2024-09-25 09:00:00+03', 'NEW', '{"parts": ["Масляный фильтр", "Воздушный фильтр", "Топливный фильтр"]}', 15000.0),
('550e8400-e29b-41d4-a716-446655440081', '550e8400-e29b-41d4-a716-446655440012', 'РЕМОНТ', '2024-09-22 10:00:00+03', 'IN_PROGRESS', '{"parts": ["Ремень ГРМ", "Натяжитель"]}', 45000.0),
('550e8400-e29b-41d4-a716-446655440082', '550e8400-e29b-41d4-a716-446655440011', 'ЗАМЕНА_МАСЛА', '2024-09-28 08:00:00+03', 'PLANNED', '{"parts": ["Моторное масло 15W-40", "Масляный фильтр"]}', 8000.0)
ON CONFLICT (id) DO NOTHING;

-- События для интеграции
INSERT INTO outbox_event (event_type, aggregate_type, aggregate_id, payload, status, created_at) VALUES
('TASK_COMPLETED', 'TASK', '550e8400-e29b-41d4-a716-446655440040', '{"taskId": "550e8400-e29b-41d4-a716-446655440040", "fieldId": "550e8400-e29b-41d4-a716-446655440001", "completedAt": "2024-09-15T18:30:00+03:00"}', 'NEW', '2024-09-15 18:30:00+03'),
('FUEL_TRANSACTION', 'FUEL_TRANSACTION', '550e8400-e29b-41d4-a716-446655440070', '{"transactionId": "550e8400-e29b-41d4-a716-446655440070", "amount": 2275.0, "liters": 50.0}', 'NEW', '2024-09-15 07:30:00+03'),
('MATERIAL_ISSUED', 'MATERIAL_ISSUE', '550e8400-e29b-41d4-a716-446655440050', '{"issueId": "550e8400-e29b-41d4-a716-446655440050", "materialId": "550e8400-e29b-41d4-a716-446655440020", "qty": 2500.0}', 'NEW', '2024-09-15 08:15:00+03');

-- Вывод статистики
SELECT 'Демо-данные успешно загружены!' as status;
SELECT 'Пользователи: ' || COUNT(*) as users_count FROM app_user;
SELECT 'Поля: ' || COUNT(*) as fields_count FROM field;
SELECT 'Техника: ' || COUNT(*) as machines_count FROM machine;
SELECT 'Материалы: ' || COUNT(*) as materials_count FROM material;
SELECT 'Задачи: ' || COUNT(*) as tasks_count FROM task;
SELECT 'Путевые листы: ' || COUNT(*) as waybills_count FROM waybill;
SELECT 'Топливные транзакции: ' || COUNT(*) as fuel_transactions_count FROM fuel_transaction;
SELECT 'Заявки на ТО: ' || COUNT(*) as maintenance_orders_count FROM maintenance_order;
