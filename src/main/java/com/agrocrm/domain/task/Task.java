package com.agrocrm.domain.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Сельскохозяйственная задача")
public class Task {
    @Schema(description = "Уникальный идентификатор задачи")
    private UUID id;
    
    @Schema(description = "ID поля, на котором выполняется задача")
    @NotNull(message = "Field ID is required")
    private UUID fieldId;
    
    @Schema(description = "Название задачи", example = "Посев пшеницы")
    @NotBlank(message = "Title is required")
    private String title;
    
    @Schema(description = "Описание задачи", example = "Посев озимой пшеницы на поле №1")
    private String description;
    
    @Schema(description = "Статус задачи", example = "PLANNED", allowableValues = {"PLANNED", "IN_PROGRESS", "DONE", "CANCELLED"})
    private String status;
    
    @Schema(description = "Приоритет задачи (1-5, где 1 - высший)", example = "3")
    @Min(value = 1, message = "Priority must be between 1 and 5")
    @Max(value = 5, message = "Priority must be between 1 and 5")
    private Integer priority;
    
    @Schema(description = "Планируемое время начала")
    private OffsetDateTime plannedStart;
    
    @Schema(description = "Планируемое время завершения")
    private OffsetDateTime plannedEnd;
    
    @Schema(description = "Фактическое время начала")
    private OffsetDateTime actualStart;
    
    @Schema(description = "Фактическое время завершения")
    private OffsetDateTime actualEnd;
    
    @Schema(description = "ID назначенного пользователя")
    private UUID assignedUser;
    
    @Schema(description = "ID назначенной техники")
    private UUID assignedMachine;
    
    @Schema(description = "Материалы в формате JSON")
    private String materials;
    
    @Schema(description = "Чек-лист в формате JSON")
    private String checklist;

    public Task() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getFieldId() { return fieldId; }
    public void setFieldId(UUID fieldId) { this.fieldId = fieldId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public OffsetDateTime getPlannedStart() { return plannedStart; }
    public void setPlannedStart(OffsetDateTime plannedStart) { this.plannedStart = plannedStart; }
    public OffsetDateTime getPlannedEnd() { return plannedEnd; }
    public void setPlannedEnd(OffsetDateTime plannedEnd) { this.plannedEnd = plannedEnd; }
    public OffsetDateTime getActualStart() { return actualStart; }
    public void setActualStart(OffsetDateTime actualStart) { this.actualStart = actualStart; }
    public OffsetDateTime getActualEnd() { return actualEnd; }
    public void setActualEnd(OffsetDateTime actualEnd) { this.actualEnd = actualEnd; }
    public UUID getAssignedUser() { return assignedUser; }
    public void setAssignedUser(UUID assignedUser) { this.assignedUser = assignedUser; }
    public UUID getAssignedMachine() { return assignedMachine; }
    public void setAssignedMachine(UUID assignedMachine) { this.assignedMachine = assignedMachine; }
    public String getMaterials() { return materials; }
    public void setMaterials(String materials) { this.materials = materials; }
    public String getChecklist() { return checklist; }
    public void setChecklist(String checklist) { this.checklist = checklist; }
}
