package com.agrocrm.domain.material;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
public class MaterialBatchRepository {
    private final JdbcTemplate jdbc;

    public MaterialBatchRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private RowMapper<MaterialBatch> mapper = new RowMapper<MaterialBatch>() {
        @Override
        public MaterialBatch mapRow(ResultSet rs, int rowNum) throws SQLException {
            MaterialBatch b = new MaterialBatch();
            b.setId((UUID) rs.getObject("id"));
            b.setMaterialId((UUID) rs.getObject("material_id"));
            b.setBatchNo(rs.getString("batch_no"));
            b.setCertNo(rs.getString("cert_no"));
            b.setQty(rs.getBigDecimal("qty"));
            b.setUnit(rs.getString("unit"));
            java.sql.Date d = rs.getDate("expires_at");
            b.setExpiresAt(d == null ? null : d.toLocalDate());
            return b;
        }
    };

    public List<MaterialBatch> listByMaterial(UUID materialId) {
        return jdbc.query("SELECT * FROM material_batch WHERE material_id=? ORDER BY expires_at NULLS LAST",
                mapper, materialId);
    }

    public UUID create(MaterialBatch b) {
        UUID id = UUID.randomUUID();
        jdbc.update("INSERT INTO material_batch(id, material_id, batch_no, cert_no, qty, unit, expires_at) VALUES (?,?,?,?,?,?,?)",
                id, b.getMaterialId(), b.getBatchNo(), b.getCertNo(), b.getQty(), b.getUnit(), b.getExpiresAt());
        return id;
    }

    public void adjustQty(UUID batchId, java.math.BigDecimal delta) {
        jdbc.update("UPDATE material_batch SET qty = qty + ? WHERE id=?", delta, batchId);
    }
}
