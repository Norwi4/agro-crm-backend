package com.agrocrm.domain.material;

import java.math.BigDecimal;
import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Выдача материала")
public class MaterialIssue {
    @Schema(description = "Уникальный идентификатор выдачи")
    private UUID id;
    
    @Schema(description = "ID задачи, для которой выдается материал")
    private UUID taskId;
    
    @Schema(description = "ID партии материала")
    private UUID materialBatchId;
    
    @Schema(description = "Количество выданного материала", example = "50.0")
    private BigDecimal qty;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getTaskId() { return taskId; }
    public void setTaskId(UUID taskId) { this.taskId = taskId; }
    public UUID getMaterialBatchId() { return materialBatchId; }
    public void setMaterialBatchId(UUID materialBatchId) { this.materialBatchId = materialBatchId; }
    public BigDecimal getQty() { return qty; }
    public void setQty(BigDecimal qty) { this.qty = qty; }
}
