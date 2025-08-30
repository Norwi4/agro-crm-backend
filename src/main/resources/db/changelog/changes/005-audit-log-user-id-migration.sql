--liquibase formatted sql

--changeset agrocrm:034-audit-log-user-id-migration
-- Добавляем новый столбец user_id
ALTER TABLE audit_log ADD COLUMN user_id UUID;

-- Обновляем user_id на основе username (если есть данные)
UPDATE audit_log SET user_id = app_user.id 
FROM app_user 
WHERE audit_log.username = app_user.username;

-- Удаляем старый столбец username
ALTER TABLE audit_log DROP COLUMN username;

-- Добавляем внешний ключ
ALTER TABLE audit_log ADD CONSTRAINT fk_audit_log_user_id 
    FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE SET NULL;

-- Добавляем индекс для быстрого поиска по user_id
CREATE INDEX IF NOT EXISTS idx_audit_log_user_id ON audit_log(user_id);
CREATE INDEX IF NOT EXISTS idx_audit_log_ts ON audit_log(ts);
