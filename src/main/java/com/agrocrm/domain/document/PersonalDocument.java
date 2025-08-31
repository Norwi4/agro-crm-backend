package com.agrocrm.domain.document;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Персональный документ сотрудника")
public class PersonalDocument {
    
    @Schema(description = "Уникальный идентификатор документа")
    private Integer id;
    
    @Schema(description = "ID профиля сотрудника")
    @NotNull(message = "ID профиля обязателен")
    private UUID profileId;
    
    @Schema(description = "Тип документа", example = "паспорт")
    @NotBlank(message = "Тип документа обязателен")
    @Size(max = 50, message = "Тип документа не может быть длиннее 50 символов")
    private String documentType;
    
    @Schema(description = "Номер документа", example = "1234 567890")
    @NotBlank(message = "Номер документа обязателен")
    @Size(max = 100, message = "Номер документа не может быть длиннее 100 символов")
    private String documentNumber;
    
    @Schema(description = "Дата выдачи", example = "2010-01-15")
    @NotNull(message = "Дата выдачи обязательна")
    private LocalDate issueDate;
    
    @Schema(description = "Дата окончания действия", example = "2030-01-15")
    private LocalDate expiryDate;
    
    @Schema(description = "Орган, выдавший документ", example = "УФМС России по Московской области")
    @NotBlank(message = "Орган выдачи обязателен")
    @Size(max = 255, message = "Орган выдачи не может быть длиннее 255 символов")
    private String issuingAuthority;
    
    @Schema(description = "Скан документа (Base64)")
    private String documentScan;
    
    @Schema(description = "Дата создания")
    private OffsetDateTime createdAt;
    
    @Schema(description = "Дата последнего обновления")
    private OffsetDateTime updatedAt;
    
    public PersonalDocument() {}
    
    public PersonalDocument(Integer id, UUID profileId, String documentType, String documentNumber,
                          LocalDate issueDate, LocalDate expiryDate, String issuingAuthority,
                          String documentScan, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.profileId = profileId;
        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.issuingAuthority = issuingAuthority;
        this.documentScan = documentScan;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public UUID getProfileId() { return profileId; }
    public void setProfileId(UUID profileId) { this.profileId = profileId; }
    
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
    
    public String getDocumentNumber() { return documentNumber; }
    public void setDocumentNumber(String documentNumber) { this.documentNumber = documentNumber; }
    
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    
    public String getIssuingAuthority() { return issuingAuthority; }
    public void setIssuingAuthority(String issuingAuthority) { this.issuingAuthority = issuingAuthority; }
    
    public String getDocumentScan() { return documentScan; }
    public void setDocumentScan(String documentScan) { this.documentScan = documentScan; }
    
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    /**
     * Проверяет, истек ли срок действия документа
     */
    public boolean isExpired() {
        if (expiryDate == null) {
            return false; // Документы без срока действия не истекают
        }
        return LocalDate.now().isAfter(expiryDate);
    }
    
    /**
     * Проверяет, истекает ли документ в ближайшие 30 дней
     */
    public boolean isExpiringSoon() {
        if (expiryDate == null) {
            return false;
        }
        LocalDate thirtyDaysFromNow = LocalDate.now().plusDays(30);
        return !LocalDate.now().isAfter(expiryDate) && !expiryDate.isAfter(thirtyDaysFromNow);
    }
}

