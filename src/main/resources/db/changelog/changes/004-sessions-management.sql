--liquibase formatted sql

--changeset agrocrm:031-create-user-sessions-table
CREATE TABLE IF NOT EXISTS user_session (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
  session_token TEXT NOT NULL UNIQUE,
  device_info JSONB,
  ip_address INET,
  user_agent TEXT,
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  last_activity TIMESTAMPTZ NOT NULL DEFAULT now(),
  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
  expires_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_user_session_user_id ON user_session(user_id);
CREATE INDEX IF NOT EXISTS idx_user_session_token ON user_session(session_token);
CREATE INDEX IF NOT EXISTS idx_user_session_active ON user_session(is_active);
CREATE INDEX IF NOT EXISTS idx_user_session_expires ON user_session(expires_at);

--changeset agrocrm:032-create-session-audit-table
CREATE TABLE IF NOT EXISTS session_audit (
  id BIGSERIAL PRIMARY KEY,
  user_id UUID NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
  session_id UUID REFERENCES user_session(id) ON DELETE SET NULL,
  action TEXT NOT NULL CHECK (action IN ('LOGIN', 'LOGOUT', 'SESSION_EXPIRED', 'SESSION_TERMINATED', 'SESSION_CREATED')),
  device_info JSONB,
  ip_address INET,
  user_agent TEXT,
  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_session_audit_user_id ON session_audit(user_id);
CREATE INDEX IF NOT EXISTS idx_session_audit_created_at ON session_audit(created_at);
