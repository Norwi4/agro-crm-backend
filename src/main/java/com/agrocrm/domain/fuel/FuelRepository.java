package com.agrocrm.domain.fuel;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class FuelRepository {
    private final JdbcTemplate jdbc;

    public FuelRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public void bulkInsert(List<FuelTransaction> list) {
        for (FuelTransaction f : list) {
            UUID id = f.getId() != null ? f.getId() : UUID.randomUUID();
            String sql = "INSERT INTO fuel_transaction (id, card_number, vehicle_reg, liters, price, amount, ts, location, source, matched_task, anomalies, raw) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, to_jsonb(?::json), ?, ?, to_jsonb(?::json), to_jsonb(?::json))";
            jdbc.update(sql, id, f.getCardNumber(), f.getVehicleReg(), f.getLiters(), f.getPrice(), f.getAmount(),
                    f.getTs(), f.getLocation(), f.getSource(), f.getMatchedTask(), f.getAnomalies(), f.getRaw());
        }
    }

    public List<Map<String,Object>> nightRefuels() {
        String sql = "SELECT id, card_number, liters, ts, location FROM fuel_transaction " +
                     "WHERE EXTRACT(HOUR FROM ts) < 6 OR EXTRACT(HOUR FROM ts) >= 22 ORDER BY ts DESC";
        return jdbc.queryForList(sql);
    }
}
