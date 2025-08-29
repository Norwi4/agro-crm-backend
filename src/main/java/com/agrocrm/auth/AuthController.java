package com.agrocrm.auth;

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

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, 
                         SessionService sessionService, CustomUserDetailsService userDetailsService,
                         JdbcTemplate jdbc) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.sessionService = sessionService;
        this.userDetailsService = userDetailsService;
        this.jdbc = jdbc;
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
            
            String role = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst().orElse("ROLE_USER");
            if (role.startsWith("ROLE_")) role = role.substring(5);
            
            // Получаем ID пользователя из базы данных
            UUID userId = getUserIdByUsername(request.getUsername());
            
            // Генерируем временный токен для создания сессии
            String tempToken = UUID.randomUUID().toString();
            
            // Создаем сессию с временным токеном
            String userAgent = httpRequest.getHeader("User-Agent");
            String ipAddress = getClientIpAddress(httpRequest);
            UserSession session = sessionService.createSession(userId, tempToken, userAgent, ipAddress);
            
            // Генерируем JWT токен с правильным ID сессии
            String token = jwtService.generateToken(request.getUsername(), role, session.getId().toString());
            
            // Обновляем сессию с JWT токеном
            sessionService.updateSessionToken(session.getId(), token);
            
            log.info("User logged in successfully: username={}, role={}, sessionId={}", 
                    request.getUsername(), role, session.getId());
            
            return ResponseEntity.ok(new AuthResponse(token, role, request.getUsername()));
        } catch (Exception e) {
            log.warn("Login failed: username={}", request.getUsername(), e);
            throw e;
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
