package com.agrocrm.domain.maintenance;

import java.time.OffsetDateTime;
import java.util.UUID;

public class MaintenanceOrder {
    private UUID id;
    private UUID machineId;
    private String type;
    private OffsetDateTime plannedTs;
    private String status;
    private String parts; // JSON
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
