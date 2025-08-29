package com.agrocrm.domain.session;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Schema(description = "Пользовательская сессия")
public class UserSession {
    @Schema(description = "Уникальный идентификатор сессии")
    private UUID id;
    
    @Schema(description = "ID пользователя")
    private UUID userId;
    
    @Schema(description = "Токен сессии")
    private String sessionToken;
    
    @Schema(description = "Информация об устройстве")
    private Map<String, Object> deviceInfo;
    
    @Schema(description = "IP адрес")
    private String ipAddress;
    
    @Schema(description = "User Agent")
    private String userAgent;
    
    @Schema(description = "Активна ли сессия")
    private Boolean isActive;
    
    @Schema(description = "Время последней активности")
    private OffsetDateTime lastActivity;
    
    @Schema(description = "Время создания сессии")
    private OffsetDateTime createdAt;
    
    @Schema(description = "Время истечения сессии")
    private OffsetDateTime expiresAt;

    // Конструкторы
    public UserSession() {}

    public UserSession(UUID userId, String sessionToken, Map<String, Object> deviceInfo, 
                      String ipAddress, String userAgent, OffsetDateTime expiresAt) {
        this.userId = userId;
        this.sessionToken = sessionToken;
        this.deviceInfo = deviceInfo;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.isActive = true;
        this.lastActivity = OffsetDateTime.now();
        this.createdAt = OffsetDateTime.now();
        this.expiresAt = expiresAt;
    }

    // Геттеры и сеттеры
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    
    public String getSessionToken() { return sessionToken; }
    public void setSessionToken(String sessionToken) { this.sessionToken = sessionToken; }
    
    public Map<String, Object> getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(Map<String, Object> deviceInfo) { this.deviceInfo = deviceInfo; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public OffsetDateTime getLastActivity() { return lastActivity; }
    public void setLastActivity(OffsetDateTime lastActivity) { this.lastActivity = lastActivity; }
    
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    
    public OffsetDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(OffsetDateTime expiresAt) { this.expiresAt = expiresAt; }
}
