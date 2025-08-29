package com.agrocrm.domain.maintenance;

import java.time.OffsetDateTime;
import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Заявка на техническое обслуживание")
public class MaintenanceOrder {
    @Schema(description = "Уникальный идентификатор заявки")
    private UUID id;
    
    @Schema(description = "ID техники")
    private UUID machineId;
    
    @Schema(description = "Тип обслуживания", example = "ТО", allowableValues = {"ТО", "РЕМОНТ", "ЗАМЕНА_МАСЛА", "ПРОЧЕЕ"})
    private String type;
    
    @Schema(description = "Планируемое время обслуживания")
    private OffsetDateTime plannedTs;
    
    @Schema(description = "Статус заявки", example = "NEW", allowableValues = {"NEW", "IN_PROGRESS", "DONE", "CANCELLED"})
    private String status;
    
    @Schema(description = "Запчасти в формате JSON")
    private String parts; // JSON
    
    @Schema(description = "Стоимость обслуживания", example = "15000.0")
    private Double cost;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getMachineId() { return machineId; }
    public void setMachineId(UUID machineId) { this.machineId = machineId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public OffsetDateTime getPlannedTs() { return plannedTs; }
    public void setPlannedTs(OffsetDateTime plannedTs) { this.plannedTs = plannedTs; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getParts() { return parts; }
    public void setParts(String parts) { this.parts = parts; }
    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }
}
