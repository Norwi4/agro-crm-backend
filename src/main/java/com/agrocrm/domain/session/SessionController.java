package com.agrocrm.domain.session;

import com.agrocrm.security.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sessions")
@Tag(name = "Управление сессиями", description = "API для управления пользовательскими сессиями")
@SecurityRequirement(name = "Bearer Authentication")
public class SessionController {
    private static final Logger log = LoggerFactory.getLogger(SessionController.class);
    
    private final SessionService sessionService;
    private final SecurityUtil securityUtil;

    public SessionController(SessionService sessionService, SecurityUtil securityUtil) {
        this.sessionService = sessionService;
        this.securityUtil = securityUtil;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','ACCOUNTANT','AGRONOMIST','MECHANIC','DRIVER')")
    @Operation(
        summary = "Получить список активных сессий пользователя",
        description = "Возвращает список всех активных сессий текущего пользователя"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список сессий успешно получен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<List<UserSession>> getMySessions() {
        try {
            UUID userId = securityUtil.currentUserIdOrNull();
            if (userId == null) {
                return ResponseEntity.status(401).build();
            }
            
            List<UserSession> sessions = sessionService.getActiveUserSessions(userId);
            log.debug("Retrieved {} active sessions for user {}", sessions.size(), userId);
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            log.error("Failed to get user sessions", e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','ACCOUNTANT','AGRONOMIST','MECHANIC','DRIVER')")
    @Operation(
        summary = "Получить все сессии пользователя",
        description = "Возвращает список всех сессий текущего пользователя (включая неактивные)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список сессий успешно получен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<List<UserSession>> getAllMySessions() {
        try {
            UUID userId = securityUtil.currentUserIdOrNull();
            if (userId == null) {
                return ResponseEntity.status(401).build();
            }
            
            List<UserSession> sessions = sessionService.getUserSessions(userId);
            log.debug("Retrieved {} total sessions for user {}", sessions.size(), userId);
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            log.error("Failed to get all user sessions", e);
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/{sessionId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','ACCOUNTANT','AGRONOMIST','MECHANIC','DRIVER')")
    @Operation(
        summary = "Завершить сессию",
        description = "Завершает указанную сессию пользователя"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Сессия успешно завершена"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа"),
        @ApiResponse(responseCode = "404", description = "Сессия не найдена")
    })
    public ResponseEntity<Void> terminateSession(@PathVariable("sessionId") UUID sessionId, HttpServletRequest request) {
        try {
            UUID userId = securityUtil.currentUserIdOrNull();
            if (userId == null) {
                return ResponseEntity.status(401).build();
            }
            
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            sessionService.terminateSession(sessionId, userId, ipAddress, userAgent);
            log.info("User {} terminated session {}", userId, sessionId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to terminate session: {}", sessionId, e);
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/terminate-others")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','ACCOUNTANT','AGRONOMIST','MECHANIC','DRIVER')")
    @Operation(
        summary = "Завершить все другие сессии",
        description = "Завершает все активные сессии пользователя, кроме текущей"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Все другие сессии успешно завершены"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<Void> terminateAllOtherSessions(HttpServletRequest request) {
        try {
            UUID userId = securityUtil.currentUserIdOrNull();
            if (userId == null) {
                return ResponseEntity.status(401).build();
            }
            
            // Получаем текущий токен сессии из заголовка Authorization
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).build();
            }
            
            String currentToken = authHeader.substring(7);
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            sessionService.terminateAllOtherSessions(UUID.randomUUID(), userId, ipAddress, userAgent);
            log.info("User {} terminated all other sessions", userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to terminate all other sessions", e);
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','ACCOUNTANT','AGRONOMIST','MECHANIC','DRIVER')")
    @Operation(
        summary = "Выйти из системы",
        description = "Завершает текущую сессию пользователя"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Выход выполнен успешно"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        try {
            UUID userId = securityUtil.currentUserIdOrNull();
            if (userId == null) {
                return ResponseEntity.status(401).build();
            }
            
            // Получаем текущий токен сессии из заголовка Authorization
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).build();
            }
            
            String currentToken = authHeader.substring(7);
            String ipAddress = getClientIpAddress(request);
            String userAgent = request.getHeader("User-Agent");
            
            sessionService.logout(UUID.randomUUID(), userId, ipAddress, userAgent);
            log.info("User {} logged out", userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to logout", e);
            return ResponseEntity.status(500).build();
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
