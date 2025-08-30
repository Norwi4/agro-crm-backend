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
public class PersonalDocumentRepository {
    private static final Logger log = LoggerFactory.getLogger(PersonalDocumentRepository.class);

    private final JdbcTemplate jdbc;

    public PersonalDocumentRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private RowMapper<PersonalDocument> mapper = new RowMapper<PersonalDocument>() {
        @Override
        public PersonalDocument mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new PersonalDocument(
                    rs.getInt("id"),
                    (UUID) rs.getObject("profile_id"),
                    rs.getString("document_type"),
                    rs.getString("document_number"),
                    rs.getObject("issue_date", LocalDate.class),
                    rs.getObject("expiry_date", LocalDate.class),
                    rs.getString("issuing_authority"),
                    rs.getString("document_scan"),
                    rs.getObject("created_at", java.time.OffsetDateTime.class),
                    rs.getObject("updated_at", java.time.OffsetDateTime.class)
            );
        }
    };

    public List<PersonalDocument> findAll() {
        try {
            List<PersonalDocument> documents = jdbc.query(
                "SELECT id, profile_id, document_type, document_number, issue_date, expiry_date, " +
                "issuing_authority, document_scan, created_at, updated_at " +
                "FROM personal_document ORDER BY created_at DESC", 
                mapper
            );
            log.debug("Found {} personal documents", documents.size());
            return documents;
        } catch (Exception e) {
            log.error("Failed to find all personal documents", e);
            throw e;
        }
    }

    public PersonalDocument findById(Integer id) {
        try {
            PersonalDocument document = jdbc.queryForObject(
                "SELECT id, profile_id, document_type, document_number, issue_date, expiry_date, " +
                "issuing_authority, document_scan, created_at, updated_at " +
                "FROM personal_document WHERE id = ?", 
                mapper, id
            );
            log.debug("Found personal document: id={}, type={}", id, document != null ? document.getDocumentType() : "null");
            return document;
        } catch (EmptyResultDataAccessException e) {
            log.debug("Personal document not found: id={}", id);
            return null;
        } catch (Exception e) {
            log.error("Failed to find personal document: id={}", id, e);
            throw e;
        }
    }

    public List<PersonalDocument> findByProfileId(UUID profileId) {
        try {
            List<PersonalDocument> documents = jdbc.query(
                "SELECT id, profile_id, document_type, document_number, issue_date, expiry_date, " +
                "issuing_authority, document_scan, created_at, updated_at " +
                "FROM personal_document WHERE profile_id = ? ORDER BY document_type", 
                mapper, profileId
            );
            log.debug("Found {} personal documents for profile: {}", documents.size(), profileId);
            return documents;
        } catch (Exception e) {
            log.error("Failed to find personal documents by profile: profileId={}", profileId, e);
            throw e;
        }
    }

    public List<PersonalDocument> findByDocumentType(String documentType) {
        try {
            List<PersonalDocument> documents = jdbc.query(
                "SELECT id, profile_id, document_type, document_number, issue_date, expiry_date, " +
                "issuing_authority, document_scan, created_at, updated_at " +
                "FROM personal_document WHERE document_type = ? ORDER BY created_at DESC", 
                mapper, documentType
            );
            log.debug("Found {} personal documents of type: {}", documents.size(), documentType);
            return documents;
        } catch (Exception e) {
            log.error("Failed to find personal documents by type: type={}", documentType, e);
            throw e;
        }
    }

    public List<PersonalDocument> findExpiredDocuments() {
        try {
            List<PersonalDocument> documents = jdbc.query(
                "SELECT id, profile_id, document_type, document_number, issue_date, expiry_date, " +
                "issuing_authority, document_scan, created_at, updated_at " +
                "FROM personal_document WHERE expiry_date IS NOT NULL AND expiry_date < CURRENT_DATE " +
                "ORDER BY expiry_date DESC", 
                mapper
            );
            log.debug("Found {} expired personal documents", documents.size());
            return documents;
        } catch (Exception e) {
            log.error("Failed to find expired personal documents", e);
            throw e;
        }
    }

    public List<PersonalDocument> findExpiringSoonDocuments() {
        try {
            List<PersonalDocument> documents = jdbc.query(
                "SELECT id, profile_id, document_type, document_number, issue_date, expiry_date, " +
                "issuing_authority, document_scan, created_at, updated_at " +
                "FROM personal_document WHERE expiry_date IS NOT NULL " +
                "AND expiry_date >= CURRENT_DATE AND expiry_date <= CURRENT_DATE + INTERVAL '30 days' " +
                "ORDER BY expiry_date", 
                mapper
            );
            log.debug("Found {} personal documents expiring soon", documents.size());
            return documents;
        } catch (Exception e) {
            log.error("Failed to find personal documents expiring soon", e);
            throw e;
        }
    }

    public PersonalDocument findByProfileIdAndType(UUID profileId, String documentType) {
        try {
            PersonalDocument document = jdbc.queryForObject(
                "SELECT id, profile_id, document_type, document_number, issue_date, expiry_date, " +
                "issuing_authority, document_scan, created_at, updated_at " +
                "FROM personal_document WHERE profile_id = ? AND document_type = ?", 
                mapper, profileId, documentType
            );
            log.debug("Found personal document: profileId={}, type={}", profileId, documentType);
            return document;
        } catch (EmptyResultDataAccessException e) {
            log.debug("Personal document not found: profileId={}, type={}", profileId, documentType);
            return null;
        } catch (Exception e) {
            log.error("Failed to find personal document: profileId={}, type={}", profileId, documentType, e);
            throw e;
        }
    }

    public Integer create(PersonalDocument document) {
        try {
            String sql = "INSERT INTO personal_document (profile_id, document_type, document_number, " +
                        "issue_date, expiry_date, issuing_authority, document_scan) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
            Integer id = jdbc.queryForObject(sql, Integer.class,
                document.getProfileId(),
                document.getDocumentType(),
                document.getDocumentNumber(),
                document.getIssueDate(),
                document.getExpiryDate(),
                document.getIssuingAuthority(),
                document.getDocumentScan()
            );
            log.debug("Created personal document: id={}, type={}", id, document.getDocumentType());
            return id;
        } catch (Exception e) {
            log.error("Failed to create personal document: type={}", document.getDocumentType(), e);
            throw e;
        }
    }

    public void update(Integer id, PersonalDocument document) throws DataAccessException {
        try {
            String sql = "UPDATE personal_document SET profile_id=?, document_type=?, document_number=?, " +
                        "issue_date=?, expiry_date=?, issuing_authority=?, document_scan=?, updated_at=now() " +
                        "WHERE id=?";
            jdbc.update(sql, 
                document.getProfileId(),
                document.getDocumentType(),
                document.getDocumentNumber(),
                document.getIssueDate(),
                document.getExpiryDate(),
                document.getIssuingAuthority(),
                document.getDocumentScan(),
                id
            );
            log.debug("Updated personal document: id={}, type={}", id, document.getDocumentType());
        } catch (Exception e) {
            log.error("Failed to update personal document: id={}, type={}", id, document.getDocumentType(), e);
            throw e;
        }
    }

    public void delete(Integer id) {
        try {
            jdbc.update("DELETE FROM personal_document WHERE id = ?", id);
            log.debug("Deleted personal document: id={}", id);
        } catch (Exception e) {
            log.error("Failed to delete personal document: id={}", id, e);
            throw e;
        }
    }
}
