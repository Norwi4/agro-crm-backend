package com.agrocrm.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с JWT токенами после успешной аутентификации")
public class AuthResponse {
    @Schema(description = "Access JWT токен для доступа к API", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;
    
    @Schema(description = "Refresh JWT токен для обновления access токена", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;
    
    @Schema(description = "Роль пользователя", example = "ADMIN")
    private String role;
    
    @Schema(description = "Имя пользователя", example = "admin")
    private String username;

    public AuthResponse() {}

    public AuthResponse(String accessToken, String refreshToken, String role, String username) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.role = role;
        this.username = username;
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
