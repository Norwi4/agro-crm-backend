package com.agrocrm.domain.role;

import com.agrocrm.config.AuditService;
import com.agrocrm.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@Tag(name = "Роли", description = "API для управления ролями")
public class RoleController {
    private static final Logger log = LoggerFactory.getLogger(RoleController.class);
    
    private final RoleService service;
    private final SecurityUtil sec;
    private final AuditService auditService;

    public RoleController(RoleService service, SecurityUtil sec, AuditService auditService) {
        this.service = service;
        this.sec = sec;
        this.auditService = auditService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить список всех ролей", description = "Возвращает список всех ролей в системе")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список ролей получен успешно"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<List<Role>> list() {
        try {
            List<Role> roles = service.list();
            log.info("Retrieved {} roles", roles.size());
            return ResponseEntity.ok(roles);
        } catch (Exception e) {
            log.error("Failed to retrieve roles", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить роль по ID", description = "Возвращает роль по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Роль найдена"),
        @ApiResponse(responseCode = "404", description = "Роль не найдена"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<Role> get(
            @Parameter(description = "ID роли") @PathVariable Integer id) {
        try {
            Role role = service.get(id);
            log.info("Retrieved role: id={}, name={}", id, role.getName());
            return ResponseEntity.ok(role);
        } catch (Exception e) {
            log.error("Failed to retrieve role: id={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/name/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить роль по названию", description = "Возвращает роль по указанному названию")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Роль найдена"),
        @ApiResponse(responseCode = "404", description = "Роль не найдена"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<Role> getByName(
            @Parameter(description = "Название роли") @PathVariable String name) {
        try {
            Role role = service.getByName(name);
            log.info("Retrieved role: name={}", name);
            return ResponseEntity.ok(role);
        } catch (Exception e) {
            log.error("Failed to retrieve role: name={}", name, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Создать новую роль", description = "Создает новую роль в системе")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Роль создана успешно"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные"),
        @ApiResponse(responseCode = "409", description = "Роль с таким названием уже существует"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<Role> create(
            @Parameter(description = "Данные роли") @Valid @RequestBody Role role) {
        try {
            UUID userId = sec.currentUserIdOrNull();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            Integer id = service.create(role);
            Role createdRole = service.get(id);
            
            auditService.log(userId, "ROLE_CREATED", "role", id.toString(), 
                           "Created role: " + role.getName());
            
            log.info("Created role: id={}, name={}", id, role.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
        } catch (Exception e) {
            log.error("Failed to create role: name={}", role.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Обновить роль", description = "Обновляет существующую роль")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Роль обновлена успешно"),
        @ApiResponse(responseCode = "404", description = "Роль не найдена"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные"),
        @ApiResponse(responseCode = "409", description = "Роль с таким названием уже существует"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<Role> update(
            @Parameter(description = "ID роли") @PathVariable Integer id,
            @Parameter(description = "Обновленные данные роли") @Valid @RequestBody Role role) {
        try {
            UUID userId = sec.currentUserIdOrNull();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            service.update(id, role);
            Role updatedRole = service.get(id);
            
            auditService.log(userId, "ROLE_UPDATED", "role", id.toString(), 
                           "Updated role: " + role.getName());
            
            log.info("Updated role: id={}, name={}", id, role.getName());
            return ResponseEntity.ok(updatedRole);
        } catch (Exception e) {
            log.error("Failed to update role: id={}, name={}", id, role.getName(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить роль", description = "Удаляет роль из системы")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Роль удалена успешно"),
        @ApiResponse(responseCode = "404", description = "Роль не найдена"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID роли") @PathVariable Integer id) {
        try {
            UUID userId = sec.currentUserIdOrNull();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Получаем информацию о роли перед удалением для аудита
            Role role = service.get(id);
            
            service.delete(id);
            
            auditService.log(userId, "ROLE_DELETED", "role", id.toString(), 
                           "Deleted role: " + role.getName());
            
            log.info("Deleted role: id={}, name={}", id, role.getName());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to delete role: id={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

