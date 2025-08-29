package com.agrocrm.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с JWT токеном после успешной аутентификации")
public class AuthResponse {
    @Schema(description = "JWT токен для доступа к API", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Schema(description = "Роль пользователя", example = "ADMIN")
    private String role;
    
    @Schema(description = "Имя пользователя", example = "admin")
    private String username;

    public AuthResponse() {}

    public AuthResponse(String token, String role, String username) {
        this.token = token;
        this.role = role;
        this.username = username;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
