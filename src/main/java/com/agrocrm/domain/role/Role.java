package com.agrocrm.domain.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

@Schema(description = "Роль пользователя")
public class Role {
    
    @Schema(description = "Уникальный идентификатор роли")
    private Integer id;
    
    @Schema(description = "Название роли", example = "ADMIN")
    @NotBlank(message = "Название роли обязательно")
    @Size(max = 50, message = "Название роли не может быть длиннее 50 символов")
    private String name;
    
    @Schema(description = "Описание роли", example = "Системный администратор - полный доступ ко всем функциям")
    private String description;
    
    @Schema(description = "Дата создания")
    private OffsetDateTime createdAt;
    
    @Schema(description = "Дата последнего обновления")
    private OffsetDateTime updatedAt;
    
    public Role() {}
    
    public Role(Integer id, String name, String description, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
