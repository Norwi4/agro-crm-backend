package com.agrocrm.domain.task;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import com.agrocrm.security.SecurityUtil;
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
@RequestMapping("/api/tasks")
@Tag(name = "Задачи", description = "API для управления сельскохозяйственными задачами")
@SecurityRequirement(name = "Bearer Authentication")
public class TaskController {
    private static final Logger log = LoggerFactory.getLogger(TaskController.class);
    
    private final TaskService service;
    private final SecurityUtil sec;

    public TaskController(TaskService service, SecurityUtil sec) { this.service = service; this.sec = sec; }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','AGRONOMIST')")
    @Operation(
        summary = "Создать новую задачу",
        description = "Создает новую сельскохозяйственную задачу в системе"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Задача успешно создана"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные задачи"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для создания")
    })
    public Map<String, Object> create(@Valid @RequestBody Task t) {
        try {
            UUID creator = sec.currentUserIdOrNull(); // TODO: resolve from auth principal
            UUID id = service.create(t, creator);
            log.info("Created task: id={}, title={}, creator={}", id, t.getTitle(), creator);
            return Map.of("id", id);
        } catch (Exception e) {
            log.error("Failed to create task: title={}", t.getTitle(), e);
            throw e;
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','AGRONOMIST','MANAGER','MECHANIC','DRIVER')")
    @Operation(
        summary = "Получить список задач",
        description = "Возвращает список задач с возможностью фильтрации по статусу и полю"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список задач успешно получен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public List<Task> list(
        @Parameter(description = "Статус задачи для фильтрации") @RequestParam(value = "status", required = false) String status,
        @Parameter(description = "ID поля для фильтрации") @RequestParam(value = "fieldId", required = false) UUID fieldId) {
        try {
            return service.find(status, fieldId);
        } catch (Exception e) {
            log.error("Failed to list tasks: status={}, fieldId={}", status, fieldId, e);
            throw e;
        }
    }

    @PostMapping("/{id}/start")
    @PreAuthorize("hasAnyRole('ADMIN','AGRONOMIST','DRIVER','MECHANIC')")
    @Operation(
        summary = "Начать выполнение задачи",
        description = "Переводит задачу в статус 'В работе'"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Задача успешно запущена"),
        @ApiResponse(responseCode = "404", description = "Задача не найдена"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для запуска")
    })
    public void start(@Parameter(description = "ID задачи") @PathVariable UUID id) { 
        try {
            service.setStatus(id, "IN_PROGRESS");
            log.info("Started task: id={}", id);
        } catch (Exception e) {
            log.error("Failed to start task: id={}", id, e);
            throw e;
        }
    }

    @PostMapping("/{id}/finish")
    @PreAuthorize("hasAnyRole('ADMIN','AGRONOMIST','DRIVER','MECHANIC')")
    @Operation(
        summary = "Завершить задачу",
        description = "Переводит задачу в статус 'Завершено'"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Задача успешно завершена"),
        @ApiResponse(responseCode = "404", description = "Задача не найдена"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для завершения")
    })
    public void finish(@Parameter(description = "ID задачи") @PathVariable UUID id) { 
        try {
            service.setStatus(id, "DONE");
            log.info("Finished task: id={}", id);
        } catch (Exception e) {
            log.error("Failed to finish task: id={}", id, e);
            throw e;
        }
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN','AGRONOMIST')")
    @Operation(
        summary = "Отменить задачу",
        description = "Переводит задачу в статус 'Отменено'"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Задача успешно отменена"),
        @ApiResponse(responseCode = "404", description = "Задача не найдена"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для отмены")
    })
    public void cancel(@Parameter(description = "ID задачи") @PathVariable UUID id) { 
        try {
            service.setStatus(id, "CANCELLED");
            log.info("Cancelled task: id={}", id);
        } catch (Exception e) {
            log.error("Failed to cancel task: id={}", id, e);
            throw e;
        }
    }
}
