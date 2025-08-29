package com.agrocrm.analytics;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reference")
@Tag(name = "Справочные данные", description = "API для получения справочной информации")
@SecurityRequirement(name = "Bearer Authentication")
public class ReferenceDataController {
    private final JdbcTemplate jdbc;

    public ReferenceDataController(JdbcTemplate jdbc) { this.jdbc = jdbc; }

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

    @GetMapping("/task-statuses")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','ACCOUNTANT','AGRONOMIST')")
    @Operation(
        summary = "Получить список статусов задач",
        description = "Возвращает список всех возможных статусов задач"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список статусов успешно получен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public List<String> getTaskStatuses() {
        return List.of("PLANNED", "IN_PROGRESS", "DONE", "CANCELLED");
    }

    @GetMapping("/waybill-statuses")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','ACCOUNTANT','DRIVER')")
    @Operation(
        summary = "Получить список статусов путевых листов",
        description = "Возвращает список всех возможных статусов путевых листов"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список статусов успешно получен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public List<String> getWaybillStatuses() {
        return List.of("DRAFT", "ISSUED", "SIGNED", "ARCHIVED");
    }

    @GetMapping("/maintenance-statuses")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','MECHANIC')")
    @Operation(
        summary = "Получить список статусов заявок на обслуживание",
        description = "Возвращает список всех возможных статусов заявок на обслуживание"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список статусов успешно получен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public List<String> getMaintenanceStatuses() {
        return List.of("PLANNED", "IN_PROGRESS", "DONE", "CANCELLED");
    }

    @GetMapping("/crops")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','ACCOUNTANT','AGRONOMIST')")
    @Operation(
        summary = "Получить список культур",
        description = "Возвращает список всех культур, выращиваемых в хозяйстве"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список культур успешно получен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public List<String> getCrops() {
        String sql = "SELECT DISTINCT crop FROM field WHERE crop IS NOT NULL ORDER BY crop";
        return jdbc.queryForList(sql, String.class);
    }

    @GetMapping("/machine-types")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','MECHANIC')")
    @Operation(
        summary = "Получить список типов техники",
        description = "Возвращает список всех типов техники в хозяйстве"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список типов техники успешно получен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public List<String> getMachineTypes() {
        String sql = "SELECT DISTINCT type FROM machine ORDER BY type";
        return jdbc.queryForList(sql, String.class);
    }

    @GetMapping("/material-categories")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','ACCOUNTANT','AGRONOMIST')")
    @Operation(
        summary = "Получить список категорий материалов",
        description = "Возвращает список всех категорий материалов"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список категорий успешно получен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public List<String> getMaterialCategories() {
        String sql = "SELECT DISTINCT category FROM material WHERE category IS NOT NULL ORDER BY category";
        return jdbc.queryForList(sql, String.class);
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(
        summary = "Получить все справочные данные",
        description = "Возвращает все справочные данные в одном запросе"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Справочные данные успешно получены"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public Map<String, Object> getAllReferenceData() {
        Map<String, Object> result = new HashMap<>();
        
        // Сезоны
        String seasonsSql = "SELECT DISTINCT season FROM field WHERE season IS NOT NULL ORDER BY season DESC";
        result.put("seasons", jdbc.queryForList(seasonsSql, String.class));
        
        // Культуры
        String cropsSql = "SELECT DISTINCT crop FROM field WHERE crop IS NOT NULL ORDER BY crop";
        result.put("crops", jdbc.queryForList(cropsSql, String.class));
        
        // Типы техники
        String machineTypesSql = "SELECT DISTINCT type FROM machine ORDER BY type";
        result.put("machineTypes", jdbc.queryForList(machineTypesSql, String.class));
        
        // Категории материалов
        String materialCategoriesSql = "SELECT DISTINCT category FROM material WHERE category IS NOT NULL ORDER BY category";
        result.put("materialCategories", jdbc.queryForList(materialCategoriesSql, String.class));
        
        // Статусы
        result.put("taskStatuses", List.of("PLANNED", "IN_PROGRESS", "DONE", "CANCELLED"));
        result.put("waybillStatuses", List.of("DRAFT", "ISSUED", "SIGNED", "ARCHIVED"));
        result.put("maintenanceStatuses", List.of("PLANNED", "IN_PROGRESS", "DONE", "CANCELLED"));
        
        return result;
    }
}
