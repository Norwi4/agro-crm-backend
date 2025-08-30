package com.agrocrm.auth;

import com.agrocrm.config.AuditService;
import com.agrocrm.domain.session.SessionService;
import com.agrocrm.domain.session.UserSession;
import com.agrocrm.security.JwtService;
import com.agrocrm.user.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@Tag(name = "Аутентификация", description = "API для входа в систему и получения JWT токенов")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final SessionService sessionService;
    private final CustomUserDetailsService userDetailsService;
    private final JdbcTemplate jdbc;
    private final AuditService auditService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, 
                         SessionService sessionService, CustomUserDetailsService userDetailsService,
                         JdbcTemplate jdbc, AuditService auditService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.sessionService = sessionService;
        this.userDetailsService = userDetailsService;
        this.jdbc = jdbc;
        this.auditService = auditService;
    }

    @PostMapping("/login")
    @Operation(
        summary = "Вход в систему",
        description = "Аутентификация пользователя и получение JWT токена для доступа к API"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Успешная аутентификация",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Неверные учетные данные"
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Некорректные данные запроса"
        )
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request, HttpServletRequest httpRequest) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
            
            // Получаем все роли пользователя
            java.util.List<String> roles = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(authority -> authority.startsWith("ROLE_") ? authority.substring(5) : authority)
                    .toList();
            
            // Для обратной совместимости берем первую роль как основную
            String primaryRole = roles.isEmpty() ? "USER" : roles.get(0);
            
            // Получаем ID пользователя из базы данных
            UUID userId = getUserIdByUsername(request.getUsername());
            
            // Генерируем временный токен для создания сессии
            String tempToken = UUID.randomUUID().toString();
            
            // Создаем сессию с временным токеном
            String userAgent = httpRequest.getHeader("User-Agent");
            String ipAddress = getClientIpAddress(httpRequest);
            UserSession session = sessionService.createSession(userId, tempToken, userAgent, ipAddress);
            
            // Генерируем access и refresh токены с множественными ролями
            String accessToken = jwtService.generateAccessToken(request.getUsername(), roles, session.getId().toString());
            String refreshToken = jwtService.generateRefreshToken(request.getUsername(), session.getId().toString());
            
            // Обновляем сессию с refresh токеном
            sessionService.updateSessionToken(session.getId(), refreshToken);
            
            // Логируем успешный вход
            auditService.logUserAction(userId, "LOGIN", "USER", userId.toString(), ipAddress, userAgent);
            
            log.info("User logged in successfully: username={}, roles={}, sessionId={}", 
                    request.getUsername(), roles, session.getId());
            
            return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken, primaryRole, request.getUsername(), roles));
        } catch (Exception e) {
            log.warn("Login failed: username={}", request.getUsername(), e);
            throw e;
        }
    }

    @PostMapping("/refresh")
    @Operation(
        summary = "Обновление access токена",
        description = "Обновление access токена с помощью refresh токена"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Токен успешно обновлен",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Недействительный refresh токен"
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Некорректные данные запроса"
        )
    })
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            // Проверяем, что это refresh токен
            if (!jwtService.isRefreshToken(request.getRefreshToken())) {
                log.warn("Invalid token type for refresh: not a refresh token");
                return ResponseEntity.status(401).build();
            }

            // Проверяем, что токен не истек
            if (jwtService.isTokenExpired(request.getRefreshToken())) {
                log.warn("Refresh token expired");
                return ResponseEntity.status(401).build();
            }

            // Парсим токен
            var claims = jwtService.parse(request.getRefreshToken());
            String username = claims.getSubject();
            String sessionId = claims.get("sessionId", String.class);

            // Проверяем, что сессия активна
            if (!sessionService.isSessionActive(UUID.fromString(sessionId))) {
                log.warn("Session is not active: sessionId={}", sessionId);
                return ResponseEntity.status(401).build();
            }

            // Получаем информацию о пользователе
            var userDetails = userDetailsService.loadUserByUsername(username);
            java.util.List<String> roles = userDetails.getAuthorities().stream()
                    .map(authority -> authority.getAuthority())
                    .map(authority -> authority.startsWith("ROLE_") ? authority.substring(5) : authority)
                    .toList();
            
            // Для обратной совместимости берем первую роль как основную
            String primaryRole = roles.isEmpty() ? "USER" : roles.get(0);

            // Генерируем новые токены с множественными ролями
            String newAccessToken = jwtService.generateAccessToken(username, roles, sessionId);
            String newRefreshToken = jwtService.generateRefreshToken(username, sessionId);

            // Обновляем сессию с новым refresh токеном
            sessionService.updateSessionToken(UUID.fromString(sessionId), newRefreshToken);

            log.info("Token refreshed successfully: username={}, sessionId={}", username, sessionId);

            return ResponseEntity.ok(new AuthResponse(newAccessToken, newRefreshToken, primaryRole, username, roles));
        } catch (Exception e) {
            log.warn("Token refresh failed", e);
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/logout")
    @Operation(
        summary = "Выход из системы",
        description = "Завершение сессии пользователя"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Успешный выход из системы"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Недействительный токен"
        )
    })
    public ResponseEntity<Void> logout(HttpServletRequest request) {
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
            String sessionId = claims.get("sessionId", String.class);
            
            // Получаем информацию о пользователе
            String username = claims.getSubject();
            UUID userId = getUserIdByUsername(username);
            
            // Завершаем сессию
            String userAgent = request.getHeader("User-Agent");
            String ipAddress = getClientIpAddress(request);
            sessionService.logout(UUID.fromString(sessionId), userId, ipAddress, userAgent);
            
            // Логируем выход
            auditService.logUserAction(userId, "LOGOUT", "SESSION", sessionId, ipAddress, userAgent);
            
            log.info("User logged out: username={}, sessionId={}", username, sessionId);
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.warn("Logout failed", e);
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/logout-all")
    @Operation(
        summary = "Выход со всех устройств",
        description = "Завершение всех активных сессий пользователя"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Успешный выход со всех устройств"
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Недействительный токен"
        )
    })
    public ResponseEntity<Void> logoutAll(HttpServletRequest request) {
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
            String sessionId = claims.get("sessionId", String.class);
            
            // Получаем информацию о пользователе
            String username = claims.getSubject();
            UUID userId = getUserIdByUsername(username);
            
            // Завершаем все сессии кроме текущей
            String userAgent = request.getHeader("User-Agent");
            String ipAddress = getClientIpAddress(request);
            sessionService.terminateAllOtherSessions(UUID.fromString(sessionId), userId, ipAddress, userAgent);
            
            // Логируем выход со всех устройств
            auditService.logUserAction(userId, "LOGOUT_ALL", "USER", userId.toString(), ipAddress, userAgent);
            
            log.info("User logged out from all devices: username={}, currentSessionId={}", username, sessionId);
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.warn("Logout all failed", e);
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
