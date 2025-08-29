package com.agrocrm.domain.maintenance;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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
@RequestMapping("/api/maintenance")
@Tag(name = "Техобслуживание", description = "API для управления заявками на техническое обслуживание")
@SecurityRequirement(name = "Bearer Authentication")
public class MaintenanceController {

    private final MaintenanceRepository repo;

    public MaintenanceController(MaintenanceRepository repo) { this.repo = repo; }

    @PostMapping("/orders")
    @PreAuthorize("hasAnyRole('ADMIN','MECHANIC')")
    @Operation(
        summary = "Создать заявку на ТО",
        description = "Создает новую заявку на техническое обслуживание техники"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Заявка успешно создана"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные заявки"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для создания")
    })
    public Map<String, Object> create(@RequestBody MaintenanceOrder o) {
        UUID id = repo.create(o);
        return Map.of("id", id);
    }

    @PostMapping("/orders/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','MECHANIC')")
    @Operation(
        summary = "Изменить статус заявки",
        description = "Обновляет статус заявки на техническое обслуживание"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Статус успешно обновлен"),
        @ApiResponse(responseCode = "404", description = "Заявка не найдена"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для изменения статуса")
    })
    public void setStatus(
        @Parameter(description = "ID заявки") @PathVariable UUID id, 
        @Parameter(description = "Новый статус заявки") @RequestParam(value = "status") String status) {
        repo.setStatus(id, status);
    }

    @GetMapping("/orders")
    @PreAuthorize("hasAnyRole('ADMIN','MECHANIC','MANAGER')")
    @Operation(
        summary = "Получить список заявок",
        description = "Возвращает список заявок на техническое обслуживание с возможностью фильтрации по статусу"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список заявок успешно получен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public List<MaintenanceOrder> list(
        @Parameter(description = "Статус для фильтрации (опционально)") @RequestParam(value = "status", required = false) String status) {
        return repo.list(status);
    }
}
