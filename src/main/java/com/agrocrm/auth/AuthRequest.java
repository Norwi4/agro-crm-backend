package com.agrocrm.auth;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на аутентификацию пользователя")
public class AuthRequest {
    @Schema(description = "Имя пользователя", example = "admin")
    @NotBlank(message = "Username is required")
    private String username;
    
    @Schema(description = "Пароль пользователя", example = "admin123")
    @NotBlank(message = "Password is required")
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
