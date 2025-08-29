--liquibase formatted sql

--changeset agrocrm:009-create-material-tables
CREATE TABLE IF NOT EXISTS material (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  name TEXT NOT NULL,
  unit TEXT NOT NULL,
  category TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS material_batch (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  material_id UUID NOT NULL REFERENCES material(id) ON DELETE CASCADE,
  batch_number TEXT NOT NULL,
  qty NUMERIC(12,3) NOT NULL CHECK (qty >= 0),
  unit_price NUMERIC(12,4),
  supplier TEXT,
  expiry_date DATE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS material_issue (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  task_id UUID REFERENCES task(id),
  material_batch_id UUID NOT NULL REFERENCES material_batch(id) ON DELETE CASCADE,
  qty NUMERIC(12,3) NOT NULL CHECK (qty > 0),
  created_by UUID REFERENCES app_user(id),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

--changeset agrocrm:010-create-waybill-table
CREATE TABLE IF NOT EXISTS waybill (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  task_id UUID REFERENCES task(id) ON DELETE SET NULL,
  driver_id UUID REFERENCES app_user(id),
  machine_id UUID REFERENCES machine(id),
  route JSONB,
  start_ts TIMESTAMPTZ,
  end_ts TIMESTAMPTZ,
  odometer_start NUMERIC(12,1),
  odometer_end NUMERIC(12,1),
  engine_hours_start NUMERIC(12,1),
  engine_hours_end NUMERIC(12,1),
  fuel_start NUMERIC(12,2),
  fuel_end NUMERIC(12,2),
  pdf_path TEXT,
  status TEXT NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT','ISSUED','SIGNED','ARCHIVED')),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

--changeset agrocrm:011-create-maintenance-tables
CREATE TABLE IF NOT EXISTS maintenance_plan (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  machine_id UUID NOT NULL REFERENCES machine(id) ON DELETE CASCADE,
  type TEXT NOT NULL,
  interval_hours INT,
  interval_km INT,
  interval_days INT
);

CREATE TABLE IF NOT EXISTS maintenance_order (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  machine_id UUID NOT NULL REFERENCES machine(id) ON DELETE CASCADE,
  type TEXT NOT NULL,
  planned_ts TIMESTAMPTZ,
  status TEXT NOT NULL DEFAULT 'PLANNED' CHECK (status IN ('PLANNED','IN_PROGRESS','DONE','CANCELLED')),
  parts JSONB,
  cost NUMERIC(14,2) DEFAULT 0,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

--changeset agrocrm:012-create-fuel-limit-table
CREATE TABLE IF NOT EXISTS fuel_limit (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  card_number TEXT NOT NULL UNIQUE,
  daily_limit_liters NUMERIC(12,2) NOT NULL DEFAULT 0,
  enabled BOOLEAN NOT NULL DEFAULT TRUE
);

--changeset agrocrm:013-create-audit-log-table
CREATE TABLE IF NOT EXISTS audit_log (
  id BIGSERIAL PRIMARY KEY,
  username TEXT,
  action TEXT NOT NULL,
  entity TEXT,
  entity_id TEXT,
  ts TIMESTAMPTZ NOT NULL DEFAULT now(),
  details JSONB
);
