package com.agrocrm.domain.session;

import com.agrocrm.security.JwtService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sessions")
@Tag(name = "Управление сессиями", description = "API для управления пользовательскими сессиями")
public class SessionController {
    private static final Logger log = LoggerFactory.getLogger(SessionController.class);

    private final SessionService sessionService;
    private final JwtService jwtService;
    private final JdbcTemplate jdbc;

    public SessionController(SessionService sessionService, JwtService jwtService, JdbcTemplate jdbc) {
        this.sessionService = sessionService;
        this.jwtService = jwtService;
        this.jdbc = jdbc;
    }

    @GetMapping
    @Operation(
        summary = "Получение активных сессий",
        description = "Получение списка всех активных сессий текущего пользователя"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Список сессий получен",
            content = @Content(schema = @Schema(implementation = UserSession.class))
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Неавторизованный доступ"
        )
    })
    public ResponseEntity<List<UserSession>> getActiveSessions(HttpServletRequest request) {
        try {
            // Получаем токен из заголовка
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).build();
            }
            
            String token = authHeader.substring(7);
            
            // Проверяем, что это access токен
            if (!jwtService.isAccessToken(token)) {
                return ResponseEntity.status(401).build();
            }
            
            // Парсим токен
            var claims = jwtService.parse(token);
            String username = claims.getSubject();
            UUID userId = getUserIdByUsername(username);
            
            List<UserSession> sessions = sessionService.getActiveUserSessions(userId);
            
            log.info("Retrieved active sessions for user: username={}, count={}", username, sessions.size());
            
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            log.warn("Failed to get active sessions", e);
            return ResponseEntity.status(401).build();
        }
    }

    @DeleteMapping("/{sessionId}")
    @Operation(
        summary = "Завершение сессии",
        description = "Завершение конкретной сессии пользователя"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Сессия успешно завершена"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Неавторизованный доступ"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Доступ запрещен"
        )
    })
    public ResponseEntity<Void> terminateSession(@PathVariable("sessionId") String sessionId, HttpServletRequest request) {
        try {
            // Получаем токен из заголовка
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).build();
            }
            
            String token = authHeader.substring(7);
            
            // Проверяем, что это access токен
            if (!jwtService.isAccessToken(token)) {
                return ResponseEntity.status(401).build();
            }
            
            // Парсим токен
            var claims = jwtService.parse(token);
            String username = claims.getSubject();
            UUID userId = getUserIdByUsername(username);
            
            // Проверяем, что сессия принадлежит пользователю
            UserSession session = sessionService.validateSessionById(sessionId);
            if (session == null || !session.getUserId().equals(userId)) {
                return ResponseEntity.status(403).build();
            }
            
            // Завершаем сессию
            String userAgent = request.getHeader("User-Agent");
            String ipAddress = getClientIpAddress(request);
            sessionService.terminateSession(UUID.fromString(sessionId), userId, ipAddress, userAgent);
            
            log.info("Session terminated: username={}, sessionId={}", username, sessionId);
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.warn("Failed to terminate session", e);
            return ResponseEntity.status(401).build();
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

    private UUID getUserIdByUsername(String username) {
        try {
            String sql = "SELECT id FROM app_user WHERE username = ?";
            return jdbc.queryForObject(sql, UUID.class, username);
        } catch (Exception e) {
            log.error("Failed to get user ID for username: {}", username, e);
            throw new RuntimeException("Failed to get user ID", e);
        }
    }
}
