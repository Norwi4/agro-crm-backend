package com.agrocrm.domain.material;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
public class MaterialRepository {
    private final JdbcTemplate jdbc;

    public MaterialRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private RowMapper<Material> mapper = new RowMapper<Material>() {
        @Override
        public Material mapRow(ResultSet rs, int rowNum) throws SQLException {
            Material m = new Material();
            m.setId((UUID) rs.getObject("id"));
            m.setName(rs.getString("name"));
            m.setType(rs.getString("type"));
            m.setUnit(rs.getString("unit"));
            m.setPricePerUnit(rs.getBigDecimal("price_per_unit"));
            return m;
        }
    };

    public List<Material> list() {
        return jdbc.query("SELECT * FROM material ORDER BY name", mapper);
    }

    public UUID create(Material m) {
        UUID id = UUID.randomUUID();
        jdbc.update("INSERT INTO material(id, name, type, unit, price_per_unit) VALUES (?,?,?,?,?)",
                id, m.getName(), m.getType(), m.getUnit(), m.getPricePerUnit());
        return id;
    }

    public void update(UUID id, Material m) {
        jdbc.update("UPDATE material SET name=?, type=?, unit=?, price_per_unit=? WHERE id=?",
                m.getName(), m.getType(), m.getUnit(), m.getPricePerUnit(), id);
    }

    public void delete(UUID id) { jdbc.update("DELETE FROM material WHERE id=?", id); }
}
