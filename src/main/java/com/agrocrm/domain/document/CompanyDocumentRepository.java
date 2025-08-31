package com.agrocrm.domain.document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public class CompanyDocumentRepository {
    private static final Logger log = LoggerFactory.getLogger(CompanyDocumentRepository.class);

    private final JdbcTemplate jdbc;

    public CompanyDocumentRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private RowMapper<CompanyDocument> mapper = new RowMapper<CompanyDocument>() {
        @Override
        public CompanyDocument mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CompanyDocument(
                    (UUID) rs.getObject("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("document_type"),
                    rs.getString("file_path"),
                    rs.getLong("file_size"),
                    rs.getString("mime_type"),
                    rs.getString("status"),
                    (UUID) rs.getObject("created_by"),
                    (UUID) rs.getObject("assigned_to"),
                    rs.getInt("department_id"),
                    rs.getObject("created_at", java.time.OffsetDateTime.class),
                    rs.getObject("updated_at", java.time.OffsetDateTime.class),
                    rs.getObject("expires_at", LocalDate.class),
                    rs.getInt("version")
            );
        }
    };

    public List<CompanyDocument> findAll() {
        try {
            List<CompanyDocument> documents = jdbc.query(
                "SELECT id, title, description, document_type, file_path, file_size, mime_type, status, " +
                "created_by, assigned_to, department_id, created_at, updated_at, expires_at, version " +
                "FROM company_document ORDER BY created_at DESC", 
                mapper
            );
            log.debug("Found {} company documents", documents.size());
            return documents;
        } catch (Exception e) {
            log.error("Failed to find all company documents", e);
            throw e;
        }
    }

    public CompanyDocument findById(UUID id) {
        try {
            CompanyDocument document = jdbc.queryForObject(
                "SELECT id, title, description, document_type, file_path, file_size, mime_type, status, " +
                "created_by, assigned_to, department_id, created_at, updated_at, expires_at, version " +
                "FROM company_document WHERE id = ?", 
                mapper, id
            );
            log.debug("Found company document: id={}, title={}", id, document != null ? document.getTitle() : "null");
            return document;
        } catch (EmptyResultDataAccessException e) {
            log.debug("Company document not found: id={}", id);
            return null;
        } catch (Exception e) {
            log.error("Failed to find company document: id={}", id, e);
            throw e;
        }
    }

    public List<CompanyDocument> findByDocumentType(String documentType) {
        try {
            List<CompanyDocument> documents = jdbc.query(
                "SELECT id, title, description, document_type, file_path, file_size, mime_type, status, " +
                "created_by, assigned_to, department_id, created_at, updated_at, expires_at, version " +
                "FROM company_document WHERE document_type = ? ORDER BY created_at DESC", 
                mapper, documentType
            );
            log.debug("Found {} company documents of type: {}", documents.size(), documentType);
            return documents;
        } catch (Exception e) {
            log.error("Failed to find company documents by type: type={}", documentType, e);
            throw e;
        }
    }

    public List<CompanyDocument> findByStatus(String status) {
        try {
            List<CompanyDocument> documents = jdbc.query(
                "SELECT id, title, description, document_type, file_path, file_size, mime_type, status, " +
                "created_by, assigned_to, department_id, created_at, updated_at, expires_at, version " +
                "FROM company_document WHERE status = ? ORDER BY created_at DESC", 
                mapper, status
            );
            log.debug("Found {} company documents with status: {}", documents.size(), status);
            return documents;
        } catch (Exception e) {
            log.error("Failed to find company documents by status: status={}", status, e);
            throw e;
        }
    }

    public List<CompanyDocument> findByCreatedBy(UUID createdBy) {
        try {
            List<CompanyDocument> documents = jdbc.query(
                "SELECT id, title, description, document_type, file_path, file_size, mime_type, status, " +
                "created_by, assigned_to, department_id, created_at, updated_at, expires_at, version " +
                "FROM company_document WHERE created_by = ? ORDER BY created_at DESC", 
                mapper, createdBy
            );
            log.debug("Found {} company documents created by: {}", documents.size(), createdBy);
            return documents;
        } catch (Exception e) {
            log.error("Failed to find company documents by creator: createdBy={}", createdBy, e);
            throw e;
        }
    }

    public List<CompanyDocument> findByDepartmentId(Integer departmentId) {
        try {
            List<CompanyDocument> documents = jdbc.query(
                "SELECT id, title, description, document_type, file_path, file_size, mime_type, status, " +
                "created_by, assigned_to, department_id, created_at, updated_at, expires_at, version " +
                "FROM company_document WHERE department_id = ? ORDER BY created_at DESC", 
                mapper, departmentId
            );
            log.debug("Found {} company documents for department: {}", documents.size(), departmentId);
            return documents;
        } catch (Exception e) {
            log.error("Failed to find company documents by department: departmentId={}", departmentId, e);
            throw e;
        }
    }

    public List<CompanyDocument> findExpiredDocuments() {
        try {
            List<CompanyDocument> documents = jdbc.query(
                "SELECT id, title, description, document_type, file_path, file_size, mime_type, status, " +
                "created_by, assigned_to, department_id, created_at, updated_at, expires_at, version " +
                "FROM company_document WHERE expires_at IS NOT NULL AND expires_at < CURRENT_DATE " +
                "ORDER BY expires_at DESC", 
                mapper
            );
            log.debug("Found {} expired company documents", documents.size());
            return documents;
        } catch (Exception e) {
            log.error("Failed to find expired company documents", e);
            throw e;
        }
    }

    public List<CompanyDocument> findExpiringSoonDocuments() {
        try {
            List<CompanyDocument> documents = jdbc.query(
                "SELECT id, title, description, document_type, file_path, file_size, mime_type, status, " +
                "created_by, assigned_to, department_id, created_at, updated_at, expires_at, version " +
                "FROM company_document WHERE expires_at IS NOT NULL " +
                "AND expires_at >= CURRENT_DATE AND expires_at <= CURRENT_DATE + INTERVAL '30 days' " +
                "ORDER BY expires_at", 
                mapper
            );
            log.debug("Found {} company documents expiring soon", documents.size());
            return documents;
        } catch (Exception e) {
            log.error("Failed to find company documents expiring soon", e);
            throw e;
        }
    }

    public UUID create(CompanyDocument document) {
        try {
            UUID id = UUID.randomUUID();
            String sql = "INSERT INTO company_document (id, title, description, document_type, file_path, file_size, " +
                        "mime_type, status, created_by, assigned_to, department_id, expires_at, version) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            jdbc.update(sql, 
                id,
                document.getTitle(),
                document.getDescription(),
                document.getDocumentType(),
                document.getFilePath(),
                document.getFileSize(),
                document.getMimeType(),
                document.getStatus(),
                document.getCreatedBy(),
                document.getAssignedTo(),
                document.getDepartmentId(),
                document.getExpiresAt(),
                document.getVersion()
            );
            log.debug("Created company document: id={}, title={}", id, document.getTitle());
            return id;
        } catch (Exception e) {
            log.error("Failed to create company document: title={}", document.getTitle(), e);
            throw e;
        }
    }

    public void update(UUID id, CompanyDocument document) throws DataAccessException {
        try {
            String sql = "UPDATE company_document SET title=?, description=?, document_type=?, file_path=?, file_size=?, " +
                        "mime_type=?, status=?, assigned_to=?, department_id=?, expires_at=?, version=?, updated_at=now() " +
                        "WHERE id=?";
            jdbc.update(sql, 
                document.getTitle(),
                document.getDescription(),
                document.getDocumentType(),
                document.getFilePath(),
                document.getFileSize(),
                document.getMimeType(),
                document.getStatus(),
                document.getAssignedTo(),
                document.getDepartmentId(),
                document.getExpiresAt(),
                document.getVersion(),
                id
            );
            log.debug("Updated company document: id={}, title={}", id, document.getTitle());
        } catch (Exception e) {
            log.error("Failed to update company document: id={}, title={}", id, document.getTitle(), e);
            throw e;
        }
    }

    public void delete(UUID id) {
        try {
            jdbc.update("DELETE FROM company_document WHERE id = ?", id);
            log.debug("Deleted company document: id={}", id);
        } catch (Exception e) {
            log.error("Failed to delete company document: id={}", id, e);
            throw e;
        }
    }
}

