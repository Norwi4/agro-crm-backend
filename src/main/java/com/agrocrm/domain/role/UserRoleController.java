package com.agrocrm.domain.role;

import com.agrocrm.config.AuditService;
import com.agrocrm.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-roles")
@Tag(name = "Роли пользователей", description = "API для управления ролями пользователей")
public class UserRoleController {
    private static final Logger log = LoggerFactory.getLogger(UserRoleController.class);
    
    private final UserRoleService service;
    private final SecurityUtil sec;
    private final AuditService auditService;

    public UserRoleController(UserRoleService service, SecurityUtil sec, AuditService auditService) {
        this.service = service;
        this.sec = sec;
        this.auditService = auditService;
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Получить роли пользователя", description = "Возвращает все роли, назначенные пользователю")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Роли пользователя получены успешно"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<List<UserRole>> getUserRoles(
            @Parameter(description = "ID пользователя") @PathVariable UUID userId) {
        try {
            List<UserRole> userRoles = service.getUserRoles(userId);
            log.info("Retrieved {} roles for user: {}", userRoles.size(), userId);
            return ResponseEntity.ok(userRoles);
        } catch (Exception e) {
            log.error("Failed to retrieve user roles: userId={}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/role/{roleId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Получить пользователей с определенной ролью", description = "Возвращает всех пользователей, имеющих указанную роль")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пользователи с ролью получены успешно"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<List<UserRole>> getUsersByRole(
            @Parameter(description = "ID роли") @PathVariable Integer roleId) {
        try {
            List<UserRole> userRoles = service.getUsersByRole(roleId);
            log.info("Retrieved {} users with role: {}", userRoles.size(), roleId);
            return ResponseEntity.ok(userRoles);
        } catch (Exception e) {
            log.error("Failed to retrieve users by role: roleId={}", roleId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}/role-names")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Получить названия ролей пользователя", description = "Возвращает список названий ролей пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Названия ролей получены успешно"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<List<String>> getUserRoleNames(
            @Parameter(description = "ID пользователя") @PathVariable UUID userId) {
        try {
            List<String> roleNames = service.getUserRoleNames(userId);
            log.info("Retrieved {} role names for user: {}", roleNames.size(), userId);
            return ResponseEntity.ok(roleNames);
        } catch (Exception e) {
            log.error("Failed to retrieve user role names: userId={}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/assign")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Назначить роль пользователю", description = "Назначает указанную роль пользователю")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Роль назначена успешно"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные"),
        @ApiResponse(responseCode = "409", description = "Роль уже назначена пользователю"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<UserRole> assignRole(
            @Parameter(description = "ID пользователя") @RequestParam UUID userId,
            @Parameter(description = "ID роли") @RequestParam Integer roleId) {
        try {
            UUID assignedBy = sec.currentUserIdOrNull();
            if (assignedBy == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            Integer userRoleId = service.assignRole(userId, roleId, assignedBy);
            UserRole userRole = service.getUserRole(userRoleId);
            
            auditService.log(assignedBy, "USER_ROLE_ASSIGNED", "user_role", userRoleId.toString(), 
                           "Assigned role " + roleId + " to user " + userId);
            
            log.info("Assigned role {} to user: {}", roleId, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(userRole);
        } catch (Exception e) {
            log.error("Failed to assign role: userId={}, roleId={}", userId, roleId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить роль пользователя по ID связи", description = "Удаляет связь пользователь-роль по ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Роль удалена успешно"),
        @ApiResponse(responseCode = "404", description = "Связь пользователь-роль не найдена"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<Void> removeRole(
            @Parameter(description = "ID связи пользователь-роль") @PathVariable Integer id) {
        try {
            UUID userId = sec.currentUserIdOrNull();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Получаем информацию о связи перед удалением для аудита
            UserRole userRole = service.getUserRole(id);
            
            service.removeRole(id);
            
            auditService.log(userId, "USER_ROLE_REMOVED", "user_role", id.toString(), 
                           "Removed role " + userRole.getRoleId() + " from user " + userRole.getUserId());
            
            log.info("Removed user role: id={}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to remove user role: id={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/user/{userId}/role/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить роль пользователя", description = "Удаляет указанную роль у пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Роль удалена успешно"),
        @ApiResponse(responseCode = "404", description = "Связь пользователь-роль не найдена"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<Void> removeUserRole(
            @Parameter(description = "ID пользователя") @PathVariable UUID userId,
            @Parameter(description = "ID роли") @PathVariable Integer roleId) {
        try {
            UUID removedBy = sec.currentUserIdOrNull();
            if (removedBy == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            service.removeRole(userId, roleId);
            
            auditService.log(removedBy, "USER_ROLE_REMOVED", "user_role", userId.toString(), 
                           "Removed role " + roleId + " from user " + userId);
            
            log.info("Removed role {} from user: {}", roleId, userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to remove role: userId={}, roleId={}", userId, roleId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/user/{userId}/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить все роли пользователя", description = "Удаляет все роли у указанного пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Все роли удалены успешно"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<Void> removeAllUserRoles(
            @Parameter(description = "ID пользователя") @PathVariable UUID userId) {
        try {
            UUID removedBy = sec.currentUserIdOrNull();
            if (removedBy == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            service.removeAllUserRoles(userId);
            
            auditService.log(removedBy, "USER_ROLES_CLEARED", "user_role", userId.toString(), 
                           "Removed all roles from user " + userId);
            
            log.info("Removed all roles from user: {}", userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to remove all roles: userId={}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
