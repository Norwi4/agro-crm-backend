package com.agrocrm.domain.material;

import java.math.BigDecimal;
import java.util.UUID;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Материал")
public class Material {
    @Schema(description = "Уникальный идентификатор материала")
    private UUID id;
    
    @Schema(description = "Название материала", example = "Семена пшеницы")
    private String name;
    
    @Schema(description = "Тип материала", example = "СЕМЕНА", allowableValues = {"СЕМЕНА", "УДОБРЕНИЯ", "СРЕДСТВА_ЗАЩИТЫ", "ПРОЧЕЕ"})
    private String type;
    
    @Schema(description = "Единица измерения", example = "кг")
    private String unit;
    
    @Schema(description = "Цена за единицу", example = "150.50")
    private BigDecimal pricePerUnit;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public BigDecimal getPricePerUnit() { return pricePerUnit; }
    public void setPricePerUnit(BigDecimal pricePerUnit) { this.pricePerUnit = pricePerUnit; }
}
