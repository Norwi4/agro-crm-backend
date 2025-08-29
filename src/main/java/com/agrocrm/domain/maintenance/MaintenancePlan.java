package com.agrocrm.domain.maintenance;

import java.util.UUID;

public class MaintenancePlan {
    private UUID id;
    private UUID machineId;
    private String type;
    private Integer intervalHours;
    private Integer intervalKm;
    private Integer intervalDays;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getMachineId() { return machineId; }
    public void setMachineId(UUID machineId) { this.machineId = machineId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Integer getIntervalHours() { return intervalHours; }
    public void setIntervalHours(Integer intervalHours) { this.intervalHours = intervalHours; }
    public Integer getIntervalKm() { return intervalKm; }
    public void setIntervalKm(Integer intervalKm) { this.intervalKm = intervalKm; }
    public Integer getIntervalDays() { return intervalDays; }
    public void setIntervalDays(Integer intervalDays) { this.intervalDays = intervalDays; }
}
