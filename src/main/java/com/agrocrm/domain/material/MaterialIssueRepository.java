package com.agrocrm.domain.material;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public class MaterialIssueRepository {
    private static final Logger log = LoggerFactory.getLogger(MaterialIssueRepository.class);
    
    private final JdbcTemplate jdbc;

    public MaterialIssueRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @Transactional
    public UUID issue(MaterialIssue mi, UUID userId) {
        try {
            UUID id = UUID.randomUUID();
            jdbc.update("INSERT INTO material_issue(id, task_id, material_batch_id, qty, created_by) VALUES (?,?,?,?,?)",
                    id, mi.getTaskId(), mi.getMaterialBatchId(), mi.getQty(), userId);
            // списываем со склада
            jdbc.update("UPDATE material_batch SET qty = qty - ? WHERE id=?", mi.getQty(), mi.getMaterialBatchId());
            return id;
        } catch (Exception e) {
            log.error("Failed to issue material: taskId={}, materialBatchId={}, qty={}, userId={}", 
                     mi.getTaskId(), mi.getMaterialBatchId(), mi.getQty(), userId, e);
            throw e;
        }
    }
}
