package com.agrocrm.domain.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
public class TaskRepository {
    private static final Logger log = LoggerFactory.getLogger(TaskRepository.class);
    
    private final JdbcTemplate jdbc;

    public TaskRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private RowMapper<Task> mapper = new RowMapper<Task>() {
        @Override
        public Task mapRow(ResultSet rs, int rowNum) throws SQLException {
            Task t = new Task();
            t.setId((UUID) rs.getObject("id"));
            t.setFieldId((UUID) rs.getObject("field_id"));
            t.setTitle(rs.getString("title"));
            t.setDescription(rs.getString("description"));
            t.setStatus(rs.getString("status"));
            t.setPriority(rs.getInt("priority"));
            t.setPlannedStart(rs.getObject("planned_start", java.time.OffsetDateTime.class));
            t.setPlannedEnd(rs.getObject("planned_end", java.time.OffsetDateTime.class));
            t.setActualStart(rs.getObject("actual_start", java.time.OffsetDateTime.class));
            t.setActualEnd(rs.getObject("actual_end", java.time.OffsetDateTime.class));
            t.setAssignedUser((UUID) rs.getObject("assigned_user"));
            t.setAssignedMachine((UUID) rs.getObject("assigned_machine"));
            t.setMaterials(rs.getString("materials"));
            t.setChecklist(rs.getString("checklist"));
            return t;
        }
    };

    public UUID create(Task t, UUID creator) {
        try {
            UUID id = java.util.UUID.randomUUID();
            String sql = "INSERT INTO task(id, field_id, title, description, status, priority, planned_start, planned_end, " +
                         "assigned_user, assigned_machine, materials, checklist, created_by) " +
                         "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
            jdbc.update(sql, id, t.getFieldId(), t.getTitle(), t.getDescription(), "PLANNED",
                    t.getPriority() == null ? 3 : t.getPriority(), t.getPlannedStart(), t.getPlannedEnd(),
                    t.getAssignedUser(), t.getAssignedMachine(), t.getMaterials(), t.getChecklist(), creator);
            log.debug("Created task: id={}, title={}, creator={}", id, t.getTitle(), creator);
            return id;
        } catch (Exception e) {
            log.error("Failed to create task: title={}, creator={}", t.getTitle(), creator, e);
            throw e;
        }
    }

    public List<Task> find(String status, UUID fieldId) {
        try {
            String base = "SELECT * FROM task WHERE 1=1";
            java.util.List<Object> args = new java.util.ArrayList<>();
            if (status != null) { base += " AND status = ?"; args.add(status); }
            if (fieldId != null) { base += " AND field_id = ?"; args.add(fieldId); }
            base += " ORDER BY planned_start NULLS LAST, created_at DESC";
            List<Task> tasks = jdbc.query(base, mapper, args.toArray());
            log.debug("Found {} tasks: status={}, fieldId={}", tasks.size(), status, fieldId);
            return tasks;
        } catch (Exception e) {
            log.error("Failed to find tasks: status={}, fieldId={}", status, fieldId, e);
            throw e;
        }
    }

    public void setStatus(UUID id, String newStatus) {
        try {
            String colStart = null, colEnd = null;
            if ("IN_PROGRESS".equals(newStatus)) colStart = "actual_start";
            if ("DONE".equals(newStatus) || "CANCELLED".equals(newStatus)) colEnd = "actual_end";
            String sql = "UPDATE task SET status = ?" +
                         (colStart != null ? ", " + colStart + " = now()" : "") +
                         (colEnd != null ? ", " + colEnd + " = now()" : "") +
                         ", updated_at = now() WHERE id = ?";
            jdbc.update(sql, newStatus, id);
            log.debug("Updated task status: id={}, status={}", id, newStatus);
        } catch (Exception e) {
            log.error("Failed to update task status: id={}, status={}", id, newStatus, e);
            throw e;
        }
    }
}
