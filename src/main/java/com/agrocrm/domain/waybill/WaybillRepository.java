package com.agrocrm.domain.waybill;

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
public class WaybillRepository {
    private static final Logger log = LoggerFactory.getLogger(WaybillRepository.class);
    
    private final JdbcTemplate jdbc;

    public WaybillRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private RowMapper<Waybill> mapper = new RowMapper<Waybill>() {
        @Override
        public Waybill mapRow(ResultSet rs, int rowNum) throws SQLException {
            Waybill w = new Waybill();
            w.setId((UUID) rs.getObject("id"));
            w.setTaskId((UUID) rs.getObject("task_id"));
            w.setDriverId((UUID) rs.getObject("driver_id"));
            w.setMachineId((UUID) rs.getObject("machine_id"));
            w.setRoute(rs.getString("route"));
            w.setStartTs(rs.getObject("start_ts", java.time.OffsetDateTime.class));
            w.setEndTs(rs.getObject("end_ts", java.time.OffsetDateTime.class));
            w.setOdometerStart(rs.getObject("odometer_start", Double.class));
            w.setOdometerEnd(rs.getObject("odometer_end", Double.class));
            w.setEngineHoursStart(rs.getObject("engine_hours_start", Double.class));
            w.setEngineHoursEnd(rs.getObject("engine_hours_end", Double.class));
            w.setFuelStart(rs.getObject("fuel_start", Double.class));
            w.setFuelEnd(rs.getObject("fuel_end", Double.class));
            w.setPdfPath(rs.getString("pdf_path"));
            w.setStatus(rs.getString("status"));
            return w;
        }
    };

    public UUID create(Waybill w) {
        try {
            UUID id = UUID.randomUUID();
            String sql = "INSERT INTO waybill(id, task_id, driver_id, machine_id, route, start_ts, status) VALUES (?,?,?,?, to_jsonb(?::json), ?, 'ISSUED')";
            jdbc.update(sql, id, w.getTaskId(), w.getDriverId(), w.getMachineId(), w.getRoute(), w.getStartTs());
            log.debug("Created waybill: id={}, taskId={}, driverId={}, machineId={}", id, w.getTaskId(), w.getDriverId(), w.getMachineId());
            return id;
        } catch (Exception e) {
            log.error("Failed to create waybill: taskId={}, driverId={}, machineId={}", w.getTaskId(), w.getDriverId(), w.getMachineId(), e);
            throw e;
        }
    }

    public void updateStatus(UUID id, String status) {
        try {
            jdbc.update("UPDATE waybill SET status=? WHERE id=?", status, id);
            log.debug("Updated waybill status: id={}, status={}", id, status);
        } catch (Exception e) {
            log.error("Failed to update waybill status: id={}, status={}", id, status, e);
            throw e;
        }
    }

    public void close(UUID id, Waybill w) {
        try {
            String sql = "UPDATE waybill SET end_ts=?, odometer_end=?, engine_hours_end=?, fuel_end=?, status='SIGNED' WHERE id=?";
            jdbc.update(sql, w.getEndTs(), w.getOdometerEnd(), w.getEngineHoursEnd(), w.getFuelEnd(), id);
            log.debug("Closed waybill: id={}, endTs={}", id, w.getEndTs());
        } catch (Exception e) {
            log.error("Failed to close waybill: id={}", id, e);
            throw e;
        }
    }

    public List<Waybill> list(String status) {
        try {
            String sql = "SELECT *, route::text as route FROM waybill " + (status != null ? "WHERE status=?" : "") + " ORDER BY created_at DESC";
            List<Waybill> waybills = status != null ? jdbc.query(sql, mapper, status) : jdbc.query(sql, mapper);
            log.debug("Found {} waybills: status={}", waybills.size(), status);
            return waybills;
        } catch (Exception e) {
            log.error("Failed to list waybills: status={}", status, e);
            throw e;
        }
    }
}
