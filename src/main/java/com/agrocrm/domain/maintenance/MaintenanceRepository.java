package com.agrocrm.domain.maintenance;

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
public class MaintenanceRepository {
    private static final Logger log = LoggerFactory.getLogger(MaintenanceRepository.class);
    
    private final JdbcTemplate jdbc;

    public MaintenanceRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private RowMapper<MaintenanceOrder> mapper = new RowMapper<MaintenanceOrder>() {
        @Override
        public MaintenanceOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
            MaintenanceOrder o = new MaintenanceOrder();
            o.setId((UUID) rs.getObject("id"));
            o.setMachineId((UUID) rs.getObject("machine_id"));
            o.setType(rs.getString("type"));
            o.setPlannedTs(rs.getObject("planned_ts", java.time.OffsetDateTime.class));
            o.setStatus(rs.getString("status"));
            o.setParts(rs.getString("parts"));
            o.setCost(rs.getObject("cost", Double.class));
            return o;
        }
    };

    public UUID create(MaintenanceOrder o) {
        try {
            UUID id = UUID.randomUUID();
            String sql = "INSERT INTO maintenance_order(id, machine_id, type, planned_ts, status, parts, cost) VALUES (?,?,?,?,'PLANNED', to_jsonb(?::json), ?)";
            jdbc.update(sql, id, o.getMachineId(), o.getType(), o.getPlannedTs(), o.getParts(), o.getCost());
            return id;
        } catch (Exception e) {
            log.error("Failed to create maintenance order", e);
            throw e;
        }
    }

    public void setStatus(UUID id, String status) {
        try {
            jdbc.update("UPDATE maintenance_order SET status=? WHERE id=?", status, id);
        } catch (Exception e) {
            log.error("Failed to update maintenance order status: id={}, status={}", id, status, e);
            throw e;
        }
    }

    public List<MaintenanceOrder> list(String status) {
        try {
            String sql = "SELECT *, parts::text as parts FROM maintenance_order " + (status != null ? "WHERE status=?" : "") + " ORDER BY planned_ts DESC";
            return status != null ? jdbc.query(sql, mapper, status) : jdbc.query(sql, mapper);
        } catch (Exception e) {
            log.error("Failed to list maintenance orders: status={}", status, e);
            throw e;
        }
    }
}
