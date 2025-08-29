package com.agrocrm.domain.session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class UserSessionRepository {
    private static final Logger log = LoggerFactory.getLogger(UserSessionRepository.class);
    
    private final JdbcTemplate jdbc;
    private final ObjectMapper objectMapper;

    public UserSessionRepository(JdbcTemplate jdbc) { 
        this.jdbc = jdbc; 
        this.objectMapper = new ObjectMapper();
    }

    private RowMapper<UserSession> mapper = new RowMapper<UserSession>() {
        @Override
        public UserSession mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserSession session = new UserSession();
            session.setId((UUID) rs.getObject("id"));
            session.setUserId((UUID) rs.getObject("user_id"));
            session.setSessionToken(rs.getString("session_token"));
            
            // Обработка JSONB поля
            String deviceInfoJson = rs.getString("device_info");
            if (deviceInfoJson != null && !deviceInfoJson.isEmpty()) {
                try {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> deviceInfo = objectMapper.readValue(deviceInfoJson, Map.class);
                    session.setDeviceInfo(deviceInfo);
                } catch (Exception e) {
                    log.warn("Failed to parse device_info JSON: {}", deviceInfoJson, e);
                    session.setDeviceInfo(new HashMap<>());
                }
            } else {
                session.setDeviceInfo(new HashMap<>());
            }
            
            session.setIpAddress(rs.getString("ip_address"));
            session.setUserAgent(rs.getString("user_agent"));
            session.setIsActive(rs.getBoolean("is_active"));
            session.setLastActivity(rs.getObject("last_activity", OffsetDateTime.class));
            session.setCreatedAt(rs.getObject("created_at", OffsetDateTime.class));
            session.setExpiresAt(rs.getObject("expires_at", OffsetDateTime.class));
            return session;
        }
    };

    @Transactional
    public UUID createSession(UserSession session) {
        try {
            UUID id = UUID.randomUUID();
            String sql = "INSERT INTO user_session(id, user_id, session_token, device_info, ip_address, user_agent, expires_at) " +
                         "VALUES (?, ?, ?, ?::jsonb, ?::inet, ?, ?)";
            jdbc.update(sql, id, session.getUserId(), session.getSessionToken(), 
                       mapToJson(session.getDeviceInfo()), session.getIpAddress(), session.getUserAgent(), session.getExpiresAt());
            log.debug("Created session: id={}, userId={}", id, session.getUserId());
            return id;
        } catch (Exception e) {
            log.error("Failed to create session: userId={}", session.getUserId(), e);
            throw e;
        }
    }

    public UserSession findByToken(String sessionToken) {
        try {
            String sql = "SELECT * FROM user_session WHERE session_token = ? AND is_active = true AND expires_at > now()";
            List<UserSession> sessions = jdbc.query(sql, mapper, sessionToken);
            return sessions.isEmpty() ? null : sessions.get(0);
        } catch (Exception e) {
            log.error("Failed to find session by token: {}", sessionToken, e);
            throw e;
        }
    }

    public UserSession findById(UUID sessionId) {
        try {
            String sql = "SELECT * FROM user_session WHERE id = ?";
            List<UserSession> sessions = jdbc.query(sql, mapper, sessionId);
            return sessions.isEmpty() ? null : sessions.get(0);
        } catch (Exception e) {
            log.error("Failed to find session by ID: {}", sessionId, e);
            throw e;
        }
    }

    public List<UserSession> findByUserId(UUID userId) {
        try {
            String sql = "SELECT * FROM user_session WHERE user_id = ? ORDER BY created_at DESC";
            return jdbc.query(sql, mapper, userId);
        } catch (Exception e) {
            log.error("Failed to find sessions by userId: {}", userId, e);
            throw e;
        }
    }

    public List<UserSession> findActiveByUserId(UUID userId) {
        try {
            String sql = "SELECT * FROM user_session WHERE user_id = ? AND is_active = true AND expires_at > now() ORDER BY created_at DESC";
            return jdbc.query(sql, mapper, userId);
        } catch (Exception e) {
            log.error("Failed to find active sessions by userId: {}", userId, e);
            throw e;
        }
    }

    @Transactional
    public void deactivateSession(UUID sessionId) {
        try {
            jdbc.update("UPDATE user_session SET is_active = false WHERE id = ?", sessionId);
            log.debug("Deactivated session: id={}", sessionId);
        } catch (Exception e) {
            log.error("Failed to deactivate session: id={}", sessionId, e);
            throw e;
        }
    }

    @Transactional
    public void deactivateAllUserSessions(UUID userId) {
        try {
            jdbc.update("UPDATE user_session SET is_active = false WHERE user_id = ?", userId);
            log.debug("Deactivated all sessions for user: id={}", userId);
        } catch (Exception e) {
            log.error("Failed to deactivate all sessions for user: id={}", userId, e);
            throw e;
        }
    }

    @Transactional
    public void updateLastActivity(UUID sessionId) {
        try {
            jdbc.update("UPDATE user_session SET last_activity = now() WHERE id = ?", sessionId);
        } catch (Exception e) {
            log.error("Failed to update last activity for session: id={}", sessionId, e);
            throw e;
        }
    }

    @Transactional
    public void updateSessionToken(UUID sessionId, String sessionToken) {
        try {
            jdbc.update("UPDATE user_session SET session_token = ? WHERE id = ?", sessionToken, sessionId);
        } catch (Exception e) {
            log.error("Failed to update session token for session: id={}", sessionId, e);
            throw e;
        }
    }

    @Transactional
    public void cleanupExpiredSessions() {
        try {
            int deleted = jdbc.update("DELETE FROM user_session WHERE expires_at < now()");
            log.debug("Cleaned up {} expired sessions", deleted);
        } catch (Exception e) {
            log.error("Failed to cleanup expired sessions", e);
            throw e;
        }
    }

    public void logSessionAudit(UUID userId, UUID sessionId, String action, Map<String, Object> deviceInfo, 
                               String ipAddress, String userAgent) {
        try {
            String sql = "INSERT INTO session_audit(user_id, session_id, action, device_info, ip_address, user_agent) " +
                         "VALUES (?, ?, ?, ?::jsonb, ?::inet, ?)";
            jdbc.update(sql, userId, sessionId, action, deviceInfo != null ? mapToJson(deviceInfo) : null, ipAddress, userAgent);
        } catch (Exception e) {
            log.error("Failed to log session audit: userId={}, action={}", userId, action, e);
            // Не бросаем исключение, так как это не критично
        }
    }

    private String mapToJson(Map<String, Object> map) {
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize map to JSON", e);
            return "{}";
        }
    }
}
