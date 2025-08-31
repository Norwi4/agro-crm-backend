package com.agrocrm.domain.department;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Департамент/отдел предприятия")
public class Department {
    
    @Schema(description = "Уникальный идентификатор департамента")
    private Integer id;
    
    @Schema(description = "Название департамента", example = "Агрономия")
    @NotBlank(message = "Название департамента обязательно")
    @Size(max = 100, message = "Название департамента не может быть длиннее 100 символов")
    private String name;
    
    @Schema(description = "Описание департамента", example = "Агрономическая служба")
    private String description;
    
    @Schema(description = "ID руководителя департамента")
    private UUID managerId;
    
    @Schema(description = "ID родительского департамента")
    private Integer parentDepartmentId;
    
    @Schema(description = "Дата создания")
    private OffsetDateTime createdAt;
    
    @Schema(description = "Дата последнего обновления")
    private OffsetDateTime updatedAt;
    
    public Department() {}
    
    public Department(Integer id, String name, String description, UUID managerId, 
                     Integer parentDepartmentId, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.managerId = managerId;
        this.parentDepartmentId = parentDepartmentId;
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
    
    public UUID getManagerId() { return managerId; }
    public void setManagerId(UUID managerId) { this.managerId = managerId; }
    
    public Integer getParentDepartmentId() { return parentDepartmentId; }
    public void setParentDepartmentId(Integer parentDepartmentId) { this.parentDepartmentId = parentDepartmentId; }
    
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}

