package com.agrocrm.domain.department;

import com.agrocrm.config.AuditService;
import com.agrocrm.security.SecurityUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping("/api/departments")
@Tag(name = "Департаменты", description = "API для управления департаментами/отделами")
@SecurityRequirement(name = "Bearer Authentication")
public class DepartmentController {
    private static final Logger log = LoggerFactory.getLogger(DepartmentController.class);

    private final DepartmentService service;
    private final SecurityUtil sec;
    private final AuditService auditService;

    public DepartmentController(DepartmentService service, SecurityUtil sec, AuditService auditService) {
        this.service = service;
        this.sec = sec;
        this.auditService = auditService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(
        summary = "Получить список всех департаментов",
        description = "Возвращает список всех департаментов в системе"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список департаментов успешно получен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public List<Department> list() { 
        try {
            return service.list();
        } catch (Exception e) {
            log.error("Failed to list departments", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(
        summary = "Получить департамент по ID",
        description = "Возвращает информацию о конкретном департаменте по его идентификатору"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Департамент найден"),
        @ApiResponse(responseCode = "404", description = "Департамент не найден"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public Department get(@Parameter(description = "ID департамента") @PathVariable Integer id) { 
        try {
            return service.get(id);
        } catch (Exception e) {
            log.error("Failed to get department: id={}", id, e);
            throw e;
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Создать новый департамент",
        description = "Создает новый департамент в системе"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Департамент успешно создан"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные департамента"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для создания")
    })
    public Map<String, Object> create(@Valid @RequestBody Department department) {
        try {
            UUID userId = sec.currentUserIdOrNull();
            Integer id = service.create(department);
            
            // Логируем создание департамента
            auditService.log(userId, "CREATE", "DEPARTMENT", id.toString());
            
            log.info("Created department: id={}, name={}, creator={}", id, department.getName(), userId);
            return Map.of("id", id);
        } catch (Exception e) {
            log.error("Failed to create department: name={}", department.getName(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Обновить департамент",
        description = "Обновляет информацию о существующем департаменте"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Департамент успешно обновлен"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные департамента"),
        @ApiResponse(responseCode = "404", description = "Департамент не найден"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для обновления")
    })
    public void update(@Parameter(description = "ID департамента") @PathVariable Integer id, 
                      @Valid @RequestBody Department department) {
        try {
            UUID userId = sec.currentUserIdOrNull();
            service.update(id, department);
            
            // Логируем обновление департамента
            auditService.log(userId, "UPDATE", "DEPARTMENT", id.toString());
            
            log.info("Updated department: id={}, name={}", id, department.getName());
        } catch (Exception e) {
            log.error("Failed to update department: id={}, name={}", id, department.getName(), e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Удалить департамент",
        description = "Удаляет департамент из системы"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Департамент успешно удален"),
        @ApiResponse(responseCode = "404", description = "Департамент не найден"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для удаления")
    })
    public void delete(@Parameter(description = "ID департамента") @PathVariable Integer id) {
        try {
            UUID userId = sec.currentUserIdOrNull();
            service.delete(id);
            
            // Логируем удаление департамента
            auditService.log(userId, "DELETE", "DEPARTMENT", id.toString());
            
            log.info("Deleted department: id={}", id);
        } catch (Exception e) {
            log.error("Failed to delete department: id={}", id, e);
            throw e;
        }
    }
}
