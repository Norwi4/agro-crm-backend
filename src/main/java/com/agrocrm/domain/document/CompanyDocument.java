package com.agrocrm.domain.document;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Общий документ предприятия")
public class CompanyDocument {
    
    @Schema(description = "Уникальный идентификатор документа")
    private UUID id;
    
    @Schema(description = "Название документа", example = "Политика безопасности труда")
    @NotBlank(message = "Название документа обязательно")
    @Size(max = 255, message = "Название документа не может быть длиннее 255 символов")
    private String title;
    
    @Schema(description = "Описание документа", example = "Основные правила безопасности при работе на предприятии")
    private String description;
    
    @Schema(description = "Тип документа", example = "policy")
    @NotBlank(message = "Тип документа обязателен")
    @Size(max = 100, message = "Тип документа не может быть длиннее 100 символов")
    private String documentType;
    
    @Schema(description = "Путь к файлу", example = "/documents/policy_2025.pdf")
    @Size(max = 500, message = "Путь к файлу не может быть длиннее 500 символов")
    private String filePath;
    
    @Schema(description = "Размер файла в байтах", example = "1024000")
    private Long fileSize;
    
    @Schema(description = "MIME тип файла", example = "application/pdf")
    @Size(max = 100, message = "MIME тип не может быть длиннее 100 символов")
    private String mimeType;
    
    @Schema(description = "Статус документа", example = "active")
    @NotBlank(message = "Статус документа обязателен")
    @Size(max = 50, message = "Статус документа не может быть длиннее 50 символов")
    private String status;
    
    @Schema(description = "ID создателя документа")
    @NotNull(message = "ID создателя обязателен")
    private UUID createdBy;
    
    @Schema(description = "ID назначенного пользователя")
    private UUID assignedTo;
    
    @Schema(description = "ID департамента")
    private Integer departmentId;
    
    @Schema(description = "Дата создания")
    private OffsetDateTime createdAt;
    
    @Schema(description = "Дата последнего обновления")
    private OffsetDateTime updatedAt;
    
    @Schema(description = "Дата истечения срока действия", example = "2025-12-31")
    private LocalDate expiresAt;
    
    @Schema(description = "Версия документа", example = "1")
    private Integer version;
    
    public CompanyDocument() {}
    
    public CompanyDocument(UUID id, String title, String description, String documentType, String filePath,
                          Long fileSize, String mimeType, String status, UUID createdBy, UUID assignedTo,
                          Integer departmentId, OffsetDateTime createdAt, OffsetDateTime updatedAt,
                          LocalDate expiresAt, Integer version) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.documentType = documentType;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
        this.status = status;
        this.createdBy = createdBy;
        this.assignedTo = assignedTo;
        this.departmentId = departmentId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.expiresAt = expiresAt;
        this.version = version;
    }
    
    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public UUID getCreatedBy() { return createdBy; }
    public void setCreatedBy(UUID createdBy) { this.createdBy = createdBy; }
    
    public UUID getAssignedTo() { return assignedTo; }
    public void setAssignedTo(UUID assignedTo) { this.assignedTo = assignedTo; }
    
    public Integer getDepartmentId() { return departmentId; }
    public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }
    
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDate getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDate expiresAt) { this.expiresAt = expiresAt; }
    
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    
    /**
     * Проверяет, истек ли срок действия документа
     */
    public boolean isExpired() {
        if (expiresAt == null) {
            return false; // Документы без срока действия не истекают
        }
        return LocalDate.now().isAfter(expiresAt);
    }
    
    /**
     * Проверяет, истекает ли документ в ближайшие 30 дней
     */
    public boolean isExpiringSoon() {
        if (expiresAt == null) {
            return false;
        }
        LocalDate thirtyDaysFromNow = LocalDate.now().plusDays(30);
        return !LocalDate.now().isAfter(expiresAt) && !expiresAt.isAfter(thirtyDaysFromNow);
    }
}

