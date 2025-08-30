package com.agrocrm.domain.document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CompanyDocumentService {
    private static final Logger log = LoggerFactory.getLogger(CompanyDocumentService.class);
    
    private final CompanyDocumentRepository repo;

    public CompanyDocumentService(CompanyDocumentRepository repo) {
        this.repo = repo;
    }

    public List<CompanyDocument> list() { 
        try {
            return repo.findAll();
        } catch (Exception e) {
            log.error("Failed to list company documents", e);
            throw e;
        }
    }
    
    public CompanyDocument get(UUID id) { 
        try {
            CompanyDocument document = repo.findById(id);
            if (document == null) {
                log.warn("Company document not found: id={}", id);
                throw new CompanyDocumentNotFoundException(id);
            }
            return document;
        } catch (Exception e) {
            log.error("Failed to get company document: id={}", id, e);
            throw e;
        }
    }
    
    public List<CompanyDocument> getByDocumentType(String documentType) { 
        try {
            return repo.findByDocumentType(documentType);
        } catch (Exception e) {
            log.error("Failed to get company documents by type: type={}", documentType, e);
            throw e;
        }
    }
    
    public List<CompanyDocument> getByStatus(String status) { 
        try {
            return repo.findByStatus(status);
        } catch (Exception e) {
            log.error("Failed to get company documents by status: status={}", status, e);
            throw e;
        }
    }
    
    public List<CompanyDocument> getByCreatedBy(UUID createdBy) { 
        try {
            return repo.findByCreatedBy(createdBy);
        } catch (Exception e) {
            log.error("Failed to get company documents by creator: createdBy={}", createdBy, e);
            throw e;
        }
    }
    
    public List<CompanyDocument> getByDepartmentId(Integer departmentId) { 
        try {
            return repo.findByDepartmentId(departmentId);
        } catch (Exception e) {
            log.error("Failed to get company documents by department: departmentId={}", departmentId, e);
            throw e;
        }
    }
    
    public List<CompanyDocument> getExpiredDocuments() { 
        try {
            return repo.findExpiredDocuments();
        } catch (Exception e) {
            log.error("Failed to get expired company documents", e);
            throw e;
        }
    }
    
    public List<CompanyDocument> getExpiringSoonDocuments() { 
        try {
            return repo.findExpiringSoonDocuments();
        } catch (Exception e) {
            log.error("Failed to get company documents expiring soon", e);
            throw e;
        }
    }
    
    public UUID create(CompanyDocument document) { 
        try {
            // Устанавливаем версию по умолчанию, если не указана
            if (document.getVersion() == null) {
                document.setVersion(1);
            }
            return repo.create(document);
        } catch (Exception e) {
            log.error("Failed to create company document: title={}", document.getTitle(), e);
            throw e;
        }
    }
    
    public void update(UUID id, CompanyDocument document) { 
        try {
            // Получаем текущий документ для обновления версии
            CompanyDocument existingDocument = repo.findById(id);
            if (existingDocument == null) {
                log.warn("Company document not found for update: id={}", id);
                throw new CompanyDocumentNotFoundException(id);
            }
            
            // Увеличиваем версию
            document.setVersion(existingDocument.getVersion() + 1);
            repo.update(id, document);
        } catch (Exception e) {
            log.error("Failed to update company document: id={}, title={}", id, document.getTitle(), e);
            throw e;
        }
    }
    
    public void delete(UUID id) { 
        try {
            // Проверяем существование документа перед удалением
            CompanyDocument document = repo.findById(id);
            if (document == null) {
                log.warn("Company document not found for deletion: id={}", id);
                throw new CompanyDocumentNotFoundException(id);
            }
            repo.delete(id);
        } catch (Exception e) {
            log.error("Failed to delete company document: id={}", id, e);
            throw e;
        }
    }
}
