package com.agrocrm.integration.outbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class OutboxRepository {
    private static final Logger log = LoggerFactory.getLogger(OutboxRepository.class);
    
    private final JdbcTemplate jdbc;

    public OutboxRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<Map<String, Object>> pullBatch(int size) {
        try {
            String sql = "SELECT id, event_type, aggregate_type, aggregate_id, payload FROM outbox_event WHERE status = 'NEW' ORDER BY id ASC LIMIT ?";
            return jdbc.queryForList(sql, size);
        } catch (Exception e) {
            log.error("Failed to pull outbox batch: size={}", size, e);
            throw e;
        }
    }

    public void markSent(long id) {
        try {
            jdbc.update("UPDATE outbox_event SET status='SENT' WHERE id=?", id);
        } catch (Exception e) {
            log.error("Failed to mark outbox event as sent: id={}", id, e);
            throw e;
        }
    }

    public void markFailed(long id, String error) {
        try {
            jdbc.update("UPDATE outbox_event SET status='FAILED', last_error=? WHERE id=?", error, id);
        } catch (Exception e) {
            log.error("Failed to mark outbox event as failed: id={}, error={}", id, error, e);
            throw e;
        }
    }
}
