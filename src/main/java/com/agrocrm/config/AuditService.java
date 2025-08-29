package com.agrocrm.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
    private static final Logger log = LoggerFactory.getLogger(AuditService.class);
    
    private final JdbcTemplate jdbc;

    public AuditService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void log(String username, String action, String entity, String entityId, String detailsJson) {
        try {
            String safeDetailsJson = detailsJson != null ? detailsJson : "{}";
            jdbc.update("INSERT INTO audit_log(username, action, entity, entity_id, details) VALUES (?,?,?,?, to_jsonb(?::json))",
                    username, action, entity, entityId, safeDetailsJson);
        } catch (Exception e) {
            log.error("Failed to log audit event: username={}, action={}, entity={}, entityId={}", 
                     username, action, entity, entityId, e);
        }
    }
}
