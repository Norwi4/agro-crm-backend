package com.agrocrm.analytics;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Аналитика", description = "API для получения аналитических данных и KPI")
@SecurityRequirement(name = "Bearer Authentication")
public class AnalyticsController {
    private final JdbcTemplate jdbc;

    public AnalyticsController(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @GetMapping("/seasons")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','ACCOUNTANT','AGRONOMIST')")
    @Operation(
        summary = "Получить список доступных сезонов",
        description = "Возвращает список всех сезонов, по которым есть данные в системе"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список сезонов успешно получен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public List<String> getAvailableSeasons() {
        String sql = "SELECT DISTINCT season FROM field WHERE season IS NOT NULL ORDER BY season DESC";
        return jdbc.queryForList(sql, String.class);
    }

    @GetMapping("/kpi/cost-per-ha")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','ACCOUNTANT','AGRONOMIST')")
    @Operation(
        summary = "Стоимость на гектар",
        description = "Рассчитывает стоимость материалов и топлива на гектар для каждого поля"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Данные по стоимости на гектар получены"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public List<Map<String,Object>> costPerHa(
        @Parameter(description = "Сезон для фильтрации (опционально)") @RequestParam(value = "season", required = false) String season) {
        String sql = "SELECT f.id as field_id, f.name, " +
                     "COALESCE(SUM(mt.qty * mb.unit_price),0) + " +
                     "COALESCE(SUM(ft.amount),0) AS cost, " +
                     "f.area_ha AS area " +
                     "FROM field f " +
                     "LEFT JOIN task t ON t.field_id = f.id " +
                     "LEFT JOIN material_issue mt ON mt.task_id = t.id " +
                     "LEFT JOIN material_batch mb ON mb.id = mt.material_batch_id " +
                     "LEFT JOIN material m ON m.id = mb.material_id " +
                     "LEFT JOIN fuel_transaction ft ON ft.matched_task = t.id " +
                     (season != null ? "WHERE f.season = ? " : "") +
                     "GROUP BY f.id, f.name, f.area_ha";
        return season != null ? jdbc.queryForList(sql, season) : jdbc.queryForList(sql);
    }

    @GetMapping("/kpi/machine-uptime")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','MECHANIC')")
    @Operation(
        summary = "Время работы техники",
        description = "Показывает количество незавершенных заявок на обслуживание для каждой единицы техники"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Данные по времени работы техники получены"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public List<Map<String,Object>> machineUptime() {
        String sql = "SELECT m.id, m.reg_number, " +
                     "COALESCE((SELECT COUNT(*) FROM maintenance_order mo WHERE mo.machine_id = m.id AND mo.status <> 'DONE'),0) as pending_orders " +
                     "FROM machine m ORDER BY pending_orders DESC";
        return jdbc.queryForList(sql);
    }
}
