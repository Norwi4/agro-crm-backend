package com.agrocrm.domain.user;

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
@RequestMapping("/api/user-profiles")
@Tag(name = "Профили пользователей", description = "API для управления профилями пользователей/сотрудников")
@SecurityRequirement(name = "Bearer Authentication")
public class UserProfileController {
    private static final Logger log = LoggerFactory.getLogger(UserProfileController.class);

    private final UserProfileService service;
    private final SecurityUtil sec;
    private final AuditService auditService;

    public UserProfileController(UserProfileService service, SecurityUtil sec, AuditService auditService) {
        this.service = service;
        this.sec = sec;
        this.auditService = auditService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(
        summary = "Получить список всех профилей пользователей",
        description = "Возвращает список всех профилей пользователей в системе"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список профилей успешно получен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    public List<UserProfile> list() { 
        try {
            return service.list();
        } catch (Exception e) {
            log.error("Failed to list user profiles", e);
            throw e;
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(
        summary = "Получить профиль пользователя по ID",
        description = "Возвращает информацию о конкретном профиле пользователя по его идентификатору"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Профиль найден"),
        @ApiResponse(responseCode = "404", description = "Профиль не найден"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public UserProfile get(@Parameter(description = "ID профиля") @PathVariable UUID id) { 
        try {
            return service.get(id);
        } catch (Exception e) {
            log.error("Failed to get user profile: id={}", id, e);
            throw e;
        }
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or #userId == authentication.principal.id")
    @Operation(
        summary = "Получить профиль пользователя по ID пользователя",
        description = "Возвращает профиль пользователя по ID пользователя"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Профиль найден"),
        @ApiResponse(responseCode = "404", description = "Профиль не найден"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public UserProfile getByUserId(@Parameter(description = "ID пользователя") @PathVariable UUID userId) { 
        try {
            return service.getByUserId(userId);
        } catch (Exception e) {
            log.error("Failed to get user profile by userId: userId={}", userId, e);
            throw e;
        }
    }

    @GetMapping("/employee/{employeeNumber}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @Operation(
        summary = "Получить профиль пользователя по табельному номеру",
        description = "Возвращает профиль пользователя по табельному номеру"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Профиль найден"),
        @ApiResponse(responseCode = "404", description = "Профиль не найден"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public UserProfile getByEmployeeNumber(@Parameter(description = "Табельный номер") @PathVariable String employeeNumber) { 
        try {
            return service.getByEmployeeNumber(employeeNumber);
        } catch (Exception e) {
            log.error("Failed to get user profile by employee number: employeeNumber={}", employeeNumber, e);
            throw e;
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Создать новый профиль пользователя",
        description = "Создает новый профиль пользователя в системе"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Профиль успешно создан"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные профиля"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для создания")
    })
    public Map<String, Object> create(@Valid @RequestBody UserProfile profile) {
        try {
            UUID userId = sec.currentUserIdOrNull();
            UUID id = service.create(profile);
            
            // Логируем создание профиля
            auditService.log(userId, "CREATE", "USER_PROFILE", id.toString());
            
            log.info("Created user profile: id={}, name={}, creator={}", id, profile.getFullName(), userId);
            return Map.of("id", id);
        } catch (Exception e) {
            log.error("Failed to create user profile: name={}", profile.getFullName(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.profileId")
    @Operation(
        summary = "Обновить профиль пользователя",
        description = "Обновляет информацию о существующем профиле пользователя"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Профиль успешно обновлен"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные профиля"),
        @ApiResponse(responseCode = "404", description = "Профиль не найден"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для обновления")
    })
    public void update(@Parameter(description = "ID профиля") @PathVariable UUID id, 
                      @Valid @RequestBody UserProfile profile) {
        try {
            UUID userId = sec.currentUserIdOrNull();
            service.update(id, profile);
            
            // Логируем обновление профиля
            auditService.log(userId, "UPDATE", "USER_PROFILE", id.toString());
            
            log.info("Updated user profile: id={}, name={}", id, profile.getFullName());
        } catch (Exception e) {
            log.error("Failed to update user profile: id={}, name={}", id, profile.getFullName(), e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Удалить профиль пользователя",
        description = "Удаляет профиль пользователя из системы"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Профиль успешно удален"),
        @ApiResponse(responseCode = "404", description = "Профиль не найден"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для удаления")
    })
    public void delete(@Parameter(description = "ID профиля") @PathVariable UUID id) {
        try {
            UUID userId = sec.currentUserIdOrNull();
            service.delete(id);
            
            // Логируем удаление профиля
            auditService.log(userId, "DELETE", "USER_PROFILE", id.toString());
            
            log.info("Deleted user profile: id={}", id);
        } catch (Exception e) {
            log.error("Failed to delete user profile: id={}", id, e);
            throw e;
        }
    }
}
