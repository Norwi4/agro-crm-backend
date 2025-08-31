package com.agrocrm.domain.role;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Связь пользователя с ролью")
public class UserRole {
    
    @Schema(description = "Уникальный идентификатор связи")
    private Integer id;
    
    @Schema(description = "ID пользователя")
    private UUID userId;
    
    @Schema(description = "ID роли")
    private Integer roleId;
    
    @Schema(description = "ID пользователя, назначившего роль")
    private UUID assignedBy;
    
    @Schema(description = "Дата назначения роли")
    private OffsetDateTime assignedAt;
    
    // Дополнительные поля для удобства
    @Schema(description = "Название роли")
    private String roleName;
    
    @Schema(description = "Имя пользователя")
    private String username;
    
    public UserRole() {}
    
    public UserRole(Integer id, UUID userId, Integer roleId, UUID assignedBy, OffsetDateTime assignedAt) {
        this.id = id;
        this.userId = userId;
        this.roleId = roleId;
        this.assignedBy = assignedBy;
        this.assignedAt = assignedAt;
    }
    
    public UserRole(Integer id, UUID userId, Integer roleId, UUID assignedBy, OffsetDateTime assignedAt, 
                   String roleName, String username) {
        this.id = id;
        this.userId = userId;
        this.roleId = roleId;
        this.assignedBy = assignedBy;
        this.assignedAt = assignedAt;
        this.roleName = roleName;
        this.username = username;
    }
    
    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    
    public Integer getRoleId() { return roleId; }
    public void setRoleId(Integer roleId) { this.roleId = roleId; }
    
    public UUID getAssignedBy() { return assignedBy; }
    public void setAssignedBy(UUID assignedBy) { this.assignedBy = assignedBy; }
    
    public OffsetDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(OffsetDateTime assignedAt) { this.assignedAt = assignedAt; }
    
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}

