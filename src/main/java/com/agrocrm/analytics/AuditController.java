package com.agrocrm.analytics;

import com.agrocrm.config.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/audit")
@Tag(name = "Аудит", description = "API для просмотра аудита системы")
@SecurityRequirement(name = "Bearer Authentication")
public class AuditController {
    private static final Logger log = LoggerFactory.getLogger(AuditController.class);

    private final JdbcTemplate jdbc;

    public AuditController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Получить журнал аудита",
        description = "Возвращает журнал аудита системы с возможностью фильтрации"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Журнал аудита получен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public List<Map<String, Object>> getAuditLog(
            @Parameter(description = "ID пользователя для фильтрации") 
            @RequestParam(required = false) String userId,
            
            @Parameter(description = "Действие для фильтрации") 
            @RequestParam(required = false) String action,
            
            @Parameter(description = "Сущность для фильтрации") 
            @RequestParam(required = false) String entity,
            
            @Parameter(description = "Начальная дата (ISO 8601)") 
            @RequestParam(required = false) String fromDate,
            
            @Parameter(description = "Конечная дата (ISO 8601)") 
            @RequestParam(required = false) String toDate,
            
            @Parameter(description = "Количество записей (по умолчанию 100)") 
            @RequestParam(defaultValue = "100") int limit) {
        
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT id, user_id, action, entity, entity_id, ts, details ");
            sql.append("FROM audit_log WHERE 1=1");
            
            List<Object> params = new ArrayList<>();
            
            if (userId != null && !userId.trim().isEmpty()) {
                sql.append(" AND user_id = ?");
                params.add(UUID.fromString(userId));
            }
            if (action != null && !action.trim().isEmpty()) {
                sql.append(" AND action ILIKE ?");
                params.add("%" + action + "%");
            }
            if (entity != null && !entity.trim().isEmpty()) {
                sql.append(" AND entity ILIKE ?");
                params.add("%" + entity + "%");
            }
            if (fromDate != null && !fromDate.trim().isEmpty()) {
                sql.append(" AND ts >= ?::timestamptz");
                params.add(fromDate);
            }
            if (toDate != null && !toDate.trim().isEmpty()) {
                sql.append(" AND ts <= ?::timestamptz");
                params.add(toDate);
            }
            
            sql.append(" ORDER BY ts DESC LIMIT ?");
            params.add(limit);
            
            List<Map<String, Object>> result = jdbc.queryForList(sql.toString(), params.toArray());
            
            log.info("Retrieved audit log: count={}, filters={userId={}, action={}, entity={}, fromDate={}, toDate={}}", 
                    result.size(), userId, action, entity, fromDate, toDate);
            
            return result;
        } catch (Exception e) {
            log.error("Failed to retrieve audit log", e);
            throw e;
        }
    }

    @GetMapping("/users/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Получить аудит конкретного пользователя",
        description = "Возвращает журнал аудита для конкретного пользователя"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Аудит пользователя получен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public List<Map<String, Object>> getUserAudit(
            @Parameter(description = "ID пользователя") 
            @PathVariable String userId,
            
            @Parameter(description = "Количество записей (по умолчанию 50)") 
            @RequestParam(defaultValue = "50") int limit) {
        
        try {
            String sql = "SELECT id, user_id, action, entity, entity_id, ts, details " +
                        "FROM audit_log WHERE user_id = ? ORDER BY ts DESC LIMIT ?";
            
            List<Map<String, Object>> result = jdbc.queryForList(sql, UUID.fromString(userId), limit);
            
            log.info("Retrieved user audit: userId={}, count={}", userId, result.size());
            
            return result;
        } catch (Exception e) {
            log.error("Failed to retrieve user audit: userId={}", userId, e);
            throw e;
        }
    }

    @GetMapping("/actions/{action}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Получить аудит по типу действия",
        description = "Возвращает журнал аудита для конкретного типа действия"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Аудит действия получен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public List<Map<String, Object>> getActionAudit(
            @Parameter(description = "Тип действия") 
            @PathVariable String action,
            
            @Parameter(description = "Количество записей (по умолчанию 50)") 
            @RequestParam(defaultValue = "50") int limit) {
        
        try {
            String sql = "SELECT id, user_id, action, entity, entity_id, ts, details " +
                        "FROM audit_log WHERE action = ? ORDER BY ts DESC LIMIT ?";
            
            List<Map<String, Object>> result = jdbc.queryForList(sql, action, limit);
            
            log.info("Retrieved action audit: action={}, count={}", action, result.size());
            
            return result;
        } catch (Exception e) {
            log.error("Failed to retrieve action audit: action={}", action, e);
            throw e;
        }
    }
}
