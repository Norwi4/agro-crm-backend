package com.agrocrm.domain.fuel;

import java.time.OffsetDateTime;
import java.util.UUID;

public class FuelTransaction {
    private UUID id;
    private String cardNumber;
    private String vehicleReg;
    private double liters;
    private Double price;
    private Double amount;
    private OffsetDateTime ts;
    private String location;
    private String source;
    private UUID matchedTask;
    private String anomalies;
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
