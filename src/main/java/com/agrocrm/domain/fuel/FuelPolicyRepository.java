package com.agrocrm.domain.fuel;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FuelPolicyRepository {
    private final JdbcTemplate jdbc;

    public FuelPolicyRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public void setDailyLimit(String cardNumber, double liters) {
        jdbc.update("INSERT INTO fuel_limit(card_number, daily_limit_liters) VALUES (?, ?) ON CONFLICT (card_number) DO UPDATE SET daily_limit_liters = EXCLUDED.daily_limit_liters",
                cardNumber, liters);
    }
}
