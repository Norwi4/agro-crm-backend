package com.agrocrm.domain.waybill;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Путевой лист техники")
public class Waybill {
    @Schema(description = "Уникальный идентификатор путевого листа")
    private UUID id;
    
    @Schema(description = "ID связанной задачи")
    @NotNull(message = "Task ID is required")
    private UUID taskId;
    
    @Schema(description = "ID водителя")
    @NotNull(message = "Driver ID is required")
    private UUID driverId;
    
    @Schema(description = "ID техники")
    @NotNull(message = "Machine ID is required")
    private UUID machineId;
    
    @Schema(description = "Маршрут в формате JSON")
    private String route; // JSON
    
    @Schema(description = "Время начала работы")
    private OffsetDateTime startTs;
    
    @Schema(description = "Время окончания работы")
    private OffsetDateTime endTs;
    
    @Schema(description = "Начальный пробег (км)", example = "1250.5")
    private Double odometerStart;
    
    @Schema(description = "Конечный пробег (км)", example = "1280.2")
    private Double odometerEnd;
    
    @Schema(description = "Начальные моточасы", example = "150.0")
    private Double engineHoursStart;
    
    @Schema(description = "Конечные моточасы", example = "155.5")
    private Double engineHoursEnd;
    
    @Schema(description = "Начальное количество топлива (л)", example = "50.0")
    private Double fuelStart;
    
    @Schema(description = "Конечное количество топлива (л)", example = "25.0")
    private Double fuelEnd;
    
    @Schema(description = "Путь к PDF файлу")
    private String pdfPath;
    
    @Schema(description = "Статус путевого листа", example = "DRAFT", allowableValues = {"DRAFT", "ISSUED", "SIGNED", "ARCHIVED"})
    private String status;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getTaskId() { return taskId; }
    public void setTaskId(UUID taskId) { this.taskId = taskId; }
    public UUID getDriverId() { return driverId; }
    public void setDriverId(UUID driverId) { this.driverId = driverId; }
    public UUID getMachineId() { return machineId; }
    public void setMachineId(UUID machineId) { this.machineId = machineId; }
    public String getRoute() { return route; }
    public void setRoute(String route) { this.route = route; }
    public OffsetDateTime getStartTs() { return startTs; }
    public void setStartTs(OffsetDateTime startTs) { this.startTs = startTs; }
    public OffsetDateTime getEndTs() { return endTs; }
    public void setEndTs(OffsetDateTime endTs) { this.endTs = endTs; }
    public Double getOdometerStart() { return odometerStart; }
    public void setOdometerStart(Double odometerStart) { this.odometerStart = odometerStart; }
    public Double getOdometerEnd() { return odometerEnd; }
    public void setOdometerEnd(Double odometerEnd) { this.odometerEnd = odometerEnd; }
    public Double getEngineHoursStart() { return engineHoursStart; }
    public void setEngineHoursStart(Double engineHoursStart) { this.engineHoursStart = engineHoursStart; }
    public Double getEngineHoursEnd() { return engineHoursEnd; }
    public void setEngineHoursEnd(Double engineHoursEnd) { this.engineHoursEnd = engineHoursEnd; }
    public Double getFuelStart() { return fuelStart; }
    public void setFuelStart(Double fuelStart) { this.fuelStart = fuelStart; }
    public Double getFuelEnd() { return fuelEnd; }
    public void setFuelEnd(Double fuelEnd) { this.fuelEnd = fuelEnd; }
    public String getPdfPath() { return pdfPath; }
    public void setPdfPath(String pdfPath) { this.pdfPath = pdfPath; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
