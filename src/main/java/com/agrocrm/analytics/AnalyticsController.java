package com.agrocrm.analytics;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    private final JdbcTemplate jdbc;

    public AnalyticsController(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @GetMapping("/kpi/cost-per-ha")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','ACCOUNTANT','AGRONOMIST')")
    public List<Map<String,Object>> costPerHa(@RequestParam(required = false) String season) {
        String sql = "SELECT f.id as field_id, f.name, COALESCE(SUM(mt.qty * m.price_per_unit),0) + " +
                     "COALESCE((SELECT SUM(amount) FROM fuel_transaction ft WHERE ft.matched_task = t.id),0) AS cost, " +
                     "COALESCE(SUM(wl.area_ha),0) AS area " +
                     "FROM field f " +
                     "LEFT JOIN task t ON t.field_id = f.id " +
                     "LEFT JOIN material_issue mt ON mt.task_id = t.id " +
                     "LEFT JOIN material_batch mb ON mb.id = mt.material_batch_id " +
                     "LEFT JOIN material m ON m.id = mb.material_id " +
                     (season != null ? "WHERE f.season = ? " : "") +
                     "GROUP BY f.id, f.name";
        return season != null ? jdbc.queryForList(sql, season) : jdbc.queryForList(sql);
    }

    @GetMapping("/kpi/machine-uptime")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','MECHANIC')")
    public List<Map<String,Object>> machineUptime() {
        String sql = "SELECT m.id, m.reg_number, " +
                     "COALESCE((SELECT COUNT(*) FROM maintenance_order mo WHERE mo.machine_id = m.id AND mo.status <> 'DONE'),0) as pending_orders " +
                     "FROM machine m ORDER BY pending_orders DESC";
        return jdbc.queryForList(sql);
    }
}
