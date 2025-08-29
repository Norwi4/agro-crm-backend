package com.agrocrm.domain.field;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Сельскохозяйственное поле")
public class Field {
    @Schema(description = "Уникальный идентификатор поля")
    private UUID id;
    
    @Schema(description = "Название поля", example = "Поле №1")
    @NotBlank(message = "Field name is required")
    private String name;
    
    @Schema(description = "Площадь поля в гектарах", example = "50.5")
    @NotNull(message = "Area is required")
    @DecimalMin(value = "0.01", message = "Area must be greater than 0")
    private BigDecimal areaHa;
    
    @Schema(description = "Культура", example = "Пшеница")
    private String crop;
    
    @Schema(description = "Сезон", example = "2024")
    private String season;
    
    @Schema(description = "Тип почвы", example = "Чернозем")
    private String soilType;
    
    @Schema(description = "Геоданные поля в формате GeoJSON")
    private String geojson;

    public Field() {}

    public Field(UUID id, String name, BigDecimal areaHa, String crop, String season, String soilType, String geojson) {
        this.id = id;
        this.name = name;
        this.areaHa = areaHa;
        this.crop = crop;
        this.season = season;
        this.soilType = soilType;
        this.geojson = geojson;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getAreaHa() { return areaHa; }
    public void setAreaHa(BigDecimal areaHa) { this.areaHa = areaHa; }
    public String getCrop() { return crop; }
    public void setCrop(String crop) { this.crop = crop; }
    public String getSeason() { return season; }
    public void setSeason(String season) { this.season = season; }
    public String getSoilType() { return soilType; }
    public void setSoilType(String soilType) { this.soilType = soilType; }
    public String getGeojson() { return geojson; }
    public void setGeojson(String geojson) { this.geojson = geojson; }
}
