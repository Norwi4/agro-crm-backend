package com.agrocrm.domain.fuel;

import java.time.OffsetDateTime;
import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Топливная транзакция")
public class FuelTransaction {
    @Schema(description = "Уникальный идентификатор транзакции")
    private UUID id;
    
    @Schema(description = "Номер топливной карты", example = "CARD-123456")
    private String cardNumber;
    
    @Schema(description = "Регистрационный номер техники", example = "А123БВ77")
    private String vehicleReg;
    
    @Schema(description = "Количество литров", example = "50.0")
    private double liters;
    
    @Schema(description = "Цена за литр", example = "45.50")
    private Double price;
    
    @Schema(description = "Общая сумма", example = "2275.0")
    private Double amount;
    
    @Schema(description = "Время транзакции")
    private OffsetDateTime ts;
    
    @Schema(description = "Место заправки", example = "АЗС №1")
    private String location;
    
    @Schema(description = "Источник данных", example = "1C", allowableValues = {"1C", "TELEMATICS", "MANUAL"})
    private String source;
    
    @Schema(description = "ID связанной задачи")
    private UUID matchedTask;
    
    @Schema(description = "Аномалии в транзакции")
    private String anomalies;
    
    @Schema(description = "Исходные данные транзакции")
    private String raw;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public String getVehicleReg() { return vehicleReg; }
    public void setVehicleReg(String vehicleReg) { this.vehicleReg = vehicleReg; }
    public double getLiters() { return liters; }
    public void setLiters(double liters) { this.liters = liters; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public OffsetDateTime getTs() { return ts; }
    public void setTs(OffsetDateTime ts) { this.ts = ts; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public UUID getMatchedTask() { return matchedTask; }
    public void setMatchedTask(UUID matchedTask) { this.matchedTask = matchedTask; }
    public String getAnomalies() { return anomalies; }
    public void setAnomalies(String anomalies) { this.anomalies = anomalies; }
    public String getRaw() { return raw; }
    public void setRaw(String raw) { this.raw = raw; }
}
