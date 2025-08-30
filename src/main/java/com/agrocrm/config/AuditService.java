package com.agrocrm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class AuditService {
    private static final Logger log = LoggerFactory.getLogger(AuditService.class);
    
    private final JdbcTemplate jdbc;
    private final ObjectMapper objectMapper;

    public AuditService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        this.objectMapper = new ObjectMapper();
    }

    public void log(UUID userId, String action, String entity, String entityId, String detailsText) {
        try {
            // Если detailsText - это обычная строка, создаем JSON объект с полем "message"
            String detailsJson;
            if (detailsText != null && !detailsText.trim().isEmpty()) {
                // Проверяем, является ли строка валидным JSON
                try {
                    objectMapper.readTree(detailsText);
                    detailsJson = detailsText; // Уже валидный JSON
                } catch (Exception e) {
                    // Не валидный JSON, создаем объект с полем "message"
                    detailsJson = objectMapper.writeValueAsString(Map.of("message", detailsText));
                }
            } else {
                detailsJson = "{}";
            }
            
            jdbc.update("INSERT INTO audit_log(user_id, action, entity, entity_id, details) VALUES (?,?,?,?, to_jsonb(?::json))",
                    userId, action, entity, entityId, detailsJson);
        } catch (Exception e) {
            log.error("Failed to log audit event: userId={}, action={}, entity={}, entityId={}", 
                     userId, action, entity, entityId, e);
        }
    }

    public void log(UUID userId, String action, String entity, String entityId, Map<String, Object> details) {
        try {
            String detailsJson = objectMapper.writeValueAsString(details);
            log(userId, action, entity, entityId, detailsJson);
        } catch (Exception e) {
            log.error("Failed to serialize audit details: userId={}, action={}, entity={}", 
                     userId, action, entity, e);
        }
    }

    public void log(UUID userId, String action, String entity, String entityId) {
        log(userId, action, entity, entityId, (String) null);
    }

    public void logUserAction(UUID userId, String action, String entity, String entityId, String ipAddress, String userAgent) {
        Map<String, Object> details = Map.of(
            "ipAddress", ipAddress != null ? ipAddress : "unknown",
            "userAgent", userAgent != null ? userAgent : "unknown",
            "timestamp", System.currentTimeMillis()
        );
        log(userId, action, entity, entityId, details);
    }
}
