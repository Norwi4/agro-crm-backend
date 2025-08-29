package com.agrocrm.domain.material;

import java.math.BigDecimal;
import java.util.UUID;

public class MaterialIssue {
    private UUID id;
    private UUID taskId;
    private UUID materialBatchId;
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
