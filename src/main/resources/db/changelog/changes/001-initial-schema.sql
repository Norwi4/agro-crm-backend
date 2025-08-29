--liquibase formatted sql

--changeset agrocrm:001-create-uuid-extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

--changeset agrocrm:002-create-app-user-table
CREATE TABLE IF NOT EXISTS app_user (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  username TEXT NOT NULL UNIQUE,
  password TEXT NOT NULL,
  full_name TEXT NOT NULL,
  role TEXT NOT NULL CHECK (role IN ('ADMIN','AGRONOMIST','MECHANIC','DRIVER','ACCOUNTANT','MANAGER')),
  department TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_app_user_role ON app_user(role);

--changeset agrocrm:003-create-field-table
CREATE TABLE IF NOT EXISTS field (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  name TEXT NOT NULL,
  area_ha NUMERIC(12,2) NOT NULL CHECK (area_ha >= 0),
  crop TEXT,
  season TEXT,
  soil_type TEXT,
  geojson JSONB,
  photos JSONB,
  created_by UUID REFERENCES app_user(id),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_field_season ON field(season);
CREATE INDEX IF NOT EXISTS idx_field_crop ON field(crop);

--changeset agrocrm:004-create-machine-table
CREATE TABLE IF NOT EXISTS machine (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  type TEXT NOT NULL,
  reg_number TEXT NOT NULL UNIQUE,
  fuel_norm_lph NUMERIC(10,2),
  fuel_norm_lpha NUMERIC(10,2),
  service_plan JSONB,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

--changeset agrocrm:005-create-task-table
CREATE TABLE IF NOT EXISTS task (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  field_id UUID NOT NULL REFERENCES field(id) ON DELETE CASCADE,
  title TEXT NOT NULL,
  description TEXT,
  status TEXT NOT NULL DEFAULT 'PLANNED' CHECK (status IN ('PLANNED','IN_PROGRESS','DONE','CANCELLED')),
  priority INT NOT NULL DEFAULT 3 CHECK (priority BETWEEN 1 AND 5),
  planned_start TIMESTAMPTZ,
  planned_end TIMESTAMPTZ,
  actual_start TIMESTAMPTZ,
  actual_end TIMESTAMPTZ,
  assigned_user UUID REFERENCES app_user(id),
  assigned_machine UUID REFERENCES machine(id),
  materials JSONB,
  checklist JSONB,
  sla_hours INT,
  created_by UUID REFERENCES app_user(id),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_task_field_status ON task(field_id, status);
CREATE INDEX IF NOT EXISTS idx_task_assigned_user ON task(assigned_user);

--changeset agrocrm:006-create-fuel-transaction-table
CREATE TABLE IF NOT EXISTS fuel_transaction (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  card_number TEXT NOT NULL,
  vehicle_reg TEXT,
  liters NUMERIC(12,2) NOT NULL CHECK (liters >= 0),
  price NUMERIC(12,4),
  amount NUMERIC(12,2),
  ts TIMESTAMPTZ NOT NULL,
  location JSONB,
  source TEXT NOT NULL,
  matched_task UUID REFERENCES task(id),
  anomalies JSONB,
  raw JSONB,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_fuel_card_ts ON fuel_transaction(card_number, ts);

--changeset agrocrm:007-create-outbox-event-table
CREATE TABLE IF NOT EXISTS outbox_event (
  id BIGSERIAL PRIMARY KEY,
  event_type TEXT NOT NULL,
  aggregate_type TEXT NOT NULL,
  aggregate_id UUID,
  payload JSONB NOT NULL,
  status TEXT NOT NULL DEFAULT 'NEW' CHECK (status IN ('NEW','SENT','FAILED')),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  last_error TEXT
);
CREATE INDEX IF NOT EXISTS idx_outbox_status ON outbox_event(status);

--changeset agrocrm:008-insert-default-users
INSERT INTO app_user (username, password, full_name, role) VALUES
 ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'System Admin', 'ADMIN'),
 ('agronom', '$2a$10$8K1p/a0dL1LXMIgoEDFrwOfgqwAGcwZQe9.8T3HmDlF1KjqH3qKqG', 'Chief Agronomist', 'AGRONOMIST'),
 ('driver', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Fleet Driver', 'DRIVER')
ON CONFLICT (username) DO NOTHING;
