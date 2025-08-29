package com.agrocrm.auth;

import com.agrocrm.security.JwtService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/auth")
@Tag(name = "Аутентификация", description = "API для входа в систему и получения JWT токенов")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
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
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
            String role = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst().orElse("ROLE_USER");
            if (role.startsWith("ROLE_")) role = role.substring(5);
            String token = jwtService.generateToken(request.getUsername(), role);
            log.info("User logged in successfully: username={}, role={}", request.getUsername(), role);
            return ResponseEntity.ok(new AuthResponse(token, role, request.getUsername()));
        } catch (Exception e) {
            log.warn("Login failed: username={}", request.getUsername(), e);
            throw e;
        }
    }
}
