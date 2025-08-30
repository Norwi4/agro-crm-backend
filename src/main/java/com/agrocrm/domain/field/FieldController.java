package com.agrocrm.domain.field;

import com.agrocrm.config.AuditService;
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
@RequestMapping("/api/fields")
@Tag(name = "Поля", description = "API для управления сельскохозяйственными полями")
@SecurityRequirement(name = "Bearer Authentication")
public class FieldController {
    private static final Logger log = LoggerFactory.getLogger(FieldController.class);

    private final FieldService service;
    private final SecurityUtil sec;
    private final AuditService auditService;

    public FieldController(FieldService service, SecurityUtil sec, AuditService auditService) {
        this.service = service;
        this.sec = sec;
        this.auditService = auditService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','AGRONOMIST','MANAGER')")
    @Operation(
        summary = "Получить список всех полей",
        description = "Возвращает список всех сельскохозяйственных полей в системе"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список полей успешно получен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public List<Field> list() { 
        try {
            return service.list();
        } catch (Exception e) {
            log.error("Failed to list fields", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','AGRONOMIST','MANAGER')")
    @Operation(
        summary = "Получить поле по ID",
        description = "Возвращает информацию о конкретном поле по его идентификатору"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Поле найдено"),
        @ApiResponse(responseCode = "404", description = "Поле не найдено"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public Field get(@Parameter(description = "ID поля") @PathVariable UUID id) { 
        try {
            return service.get(id);
        } catch (Exception e) {
            log.error("Failed to get field: id={}", id, e);
            throw e;
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','AGRONOMIST')")
    @Operation(
        summary = "Создать новое поле",
        description = "Создает новое сельскохозяйственное поле в системе"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Поле успешно создано"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные поля"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для создания")
    })
    public Map<String, Object> create(@Valid @RequestBody Field f) {
        try {
            UUID userId = sec.currentUserIdOrNull(); // TODO: resolve from auth principal
            UUID id = service.create(f, userId);
            
            // Логируем создание поля
            auditService.log(userId, "CREATE", "FIELD", id.toString());
            
            log.info("Created field: id={}, name={}, creator={}", id, f.getName(), userId);
            return Map.of("id", id);
        } catch (Exception e) {
            log.error("Failed to create field: name={}", f.getName(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','AGRONOMIST')")
    @Operation(
        summary = "Обновить поле",
        description = "Обновляет информацию о существующем поле"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Поле успешно обновлено"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные поля"),
        @ApiResponse(responseCode = "404", description = "Поле не найдено"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для обновления")
    })
    public void update(@Parameter(description = "ID поля") @PathVariable UUID id, @Valid @RequestBody Field f) {
        try {
            service.update(id, f);
            
            // Логируем обновление поля
            UUID currentUserId = sec.currentUserIdOrNull();
            auditService.log(currentUserId, "UPDATE", "FIELD", id.toString());
            
            log.info("Updated field: id={}, name={}", id, f.getName());
        } catch (Exception e) {
            log.error("Failed to update field: id={}, name={}", id, f.getName(), e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
        summary = "Удалить поле",
        description = "Удаляет поле из системы (только для администраторов)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Поле успешно удалено"),
        @ApiResponse(responseCode = "404", description = "Поле не найдено"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для удаления")
    })
    public void delete(@Parameter(description = "ID поля") @PathVariable UUID id) {
        try {
            service.delete(id);
            
            // Логируем удаление поля
            UUID currentUserId = sec.currentUserIdOrNull();
            auditService.log(currentUserId, "DELETE", "FIELD", id.toString());
            
            log.info("Deleted field: id={}", id);
        } catch (Exception e) {
            log.error("Failed to delete field: id={}", id, e);
            throw e;
        }
    }
}
