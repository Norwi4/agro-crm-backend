package com.agrocrm.domain.material;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Партия материала")
public class MaterialBatch {
    @Schema(description = "Уникальный идентификатор партии")
    private UUID id;
    
    @Schema(description = "ID материала")
    private UUID materialId;
    
    @Schema(description = "Номер партии", example = "BATCH-2024-001")
    private String batchNo;
    
    @Schema(description = "Количество", example = "1000.0")
    private BigDecimal qty;
    
    @Schema(description = "Цена за единицу", example = "45.50")
    private BigDecimal unitPrice;
    
    @Schema(description = "Поставщик", example = "ООО 'Семена Плюс'")
    private String supplier;
    
    @Schema(description = "Дата истечения срока годности", example = "2025-12-31")
    private LocalDate expiresAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getMaterialId() { return materialId; }
    public void setMaterialId(UUID materialId) { this.materialId = materialId; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public BigDecimal getQty() { return qty; }
    public void setQty(BigDecimal qty) { this.qty = qty; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }
    public LocalDate getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDate expiresAt) { this.expiresAt = expiresAt; }
}
