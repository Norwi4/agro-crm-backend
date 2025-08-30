package com.agrocrm.domain.field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
public class FieldRepository {
    private static final Logger log = LoggerFactory.getLogger(FieldRepository.class);

    private final JdbcTemplate jdbc;

    public FieldRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private RowMapper<Field> mapper = new RowMapper<Field>() {
        @Override
        public Field mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Field(
                    (UUID) rs.getObject("id"),
                    rs.getString("name"),
                    rs.getBigDecimal("area_ha"),
                    rs.getString("crop"),
                    rs.getString("season"),
                    rs.getString("soil_type"),
                    rs.getString("geojson")
            );
        }
    };

    public List<Field> findAll() {
        try {
            List<Field> fields = jdbc.query("SELECT id, name, area_ha, crop, season, soil_type, geojson::text FROM field ORDER BY name", mapper);
            log.debug("Found {} fields", fields.size());
            return fields;
        } catch (Exception e) {
            log.error("Failed to find all fields", e);
            throw e;
        }
    }
    
    public List<Field> findAllPaginated(int page, int size) {
        try {
            int offset = page * size;
            String sql = "SELECT id, name, area_ha, crop, season, soil_type, geojson::text FROM field ORDER BY name LIMIT ? OFFSET ?";
            List<Field> fields = jdbc.query(sql, mapper, size, offset);
            log.debug("Found {} fields for page {} with size {}", fields.size(), page, size);
            return fields;
        } catch (Exception e) {
            log.error("Failed to find fields with pagination: page={}, size={}", page, size, e);
            throw e;
        }
    }
    
    public long countAll() {
        try {
            Long count = jdbc.queryForObject("SELECT COUNT(*) FROM field", Long.class);
            log.debug("Total fields count: {}", count);
            return count != null ? count : 0;
        } catch (Exception e) {
            log.error("Failed to count fields", e);
            throw e;
        }
    }

    public Field findById(UUID id) {
        try {
            Field field = jdbc.queryForObject("SELECT id, name, area_ha, crop, season, soil_type, geojson::text FROM field WHERE id = ?", mapper, id);
            log.debug("Found field: id={}, name={}", id, field != null ? field.getName() : "null");
            return field;
        } catch (Exception e) {
            log.error("Failed to find field: id={}", id, e);
            throw e;
        }
    }

    public UUID create(Field f, UUID userId) {
        try {
            UUID id = UUID.randomUUID();
            String sql = "INSERT INTO field (id, name, area_ha, crop, season, soil_type, geojson, created_by) VALUES (?,?,?,?,?,?, to_jsonb(?::json), ?)";
            jdbc.update(sql, id, f.getName(), f.getAreaHa(), f.getCrop(), f.getSeason(), f.getSoilType(), f.getGeojson(), userId);
            log.debug("Created field: id={}, name={}, creator={}", id, f.getName(), userId);
            return id;
        } catch (Exception e) {
            log.error("Failed to create field: name={}, creator={}", f.getName(), userId, e);
            throw e;
        }
    }

    public void update(UUID id, Field f) throws DataAccessException {
        try {
            String sql = "UPDATE field SET name=?, area_ha=?, crop=?, season=?, soil_type=?, geojson=to_jsonb(?::json), updated_at=now() WHERE id=?";
            jdbc.update(sql, f.getName(), f.getAreaHa(), f.getCrop(), f.getSeason(), f.getSoilType(), f.getGeojson(), id);
            log.debug("Updated field: id={}, name={}", id, f.getName());
        } catch (Exception e) {
            log.error("Failed to update field: id={}, name={}", id, f.getName(), e);
            throw e;
        }
    }

    public void delete(UUID id) {
        try {
            jdbc.update("DELETE FROM field WHERE id = ?", id);
            log.debug("Deleted field: id={}", id);
        } catch (Exception e) {
            log.error("Failed to delete field: id={}", id, e);
            throw e;
        }
    }
}
