package com.agrocrm.domain.document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PersonalDocumentService {
    private static final Logger log = LoggerFactory.getLogger(PersonalDocumentService.class);
    
    private final PersonalDocumentRepository repo;

    public PersonalDocumentService(PersonalDocumentRepository repo) {
        this.repo = repo;
    }

    public List<PersonalDocument> list() { 
        try {
            return repo.findAll();
        } catch (Exception e) {
            log.error("Failed to list personal documents", e);
            throw e;
        }
    }
    
    public PersonalDocument get(Integer id) { 
        try {
            PersonalDocument document = repo.findById(id);
            if (document == null) {
                log.warn("Personal document not found: id={}", id);
                throw new PersonalDocumentNotFoundException(id);
            }
            return document;
        } catch (Exception e) {
            log.error("Failed to get personal document: id={}", id, e);
            throw e;
        }
    }
    
    public List<PersonalDocument> getByProfileId(UUID profileId) { 
        try {
            return repo.findByProfileId(profileId);
        } catch (Exception e) {
            log.error("Failed to get personal documents by profile: profileId={}", profileId, e);
            throw e;
        }
    }
    
    public List<PersonalDocument> getByDocumentType(String documentType) { 
        try {
            return repo.findByDocumentType(documentType);
        } catch (Exception e) {
            log.error("Failed to get personal documents by type: type={}", documentType, e);
            throw e;
        }
    }
    
    public List<PersonalDocument> getExpiredDocuments() { 
        try {
            return repo.findExpiredDocuments();
        } catch (Exception e) {
            log.error("Failed to get expired personal documents", e);
            throw e;
        }
    }
    
    public List<PersonalDocument> getExpiringSoonDocuments() { 
        try {
            return repo.findExpiringSoonDocuments();
        } catch (Exception e) {
            log.error("Failed to get personal documents expiring soon", e);
            throw e;
        }
    }
    
    public PersonalDocument getByProfileIdAndType(UUID profileId, String documentType) { 
        try {
            PersonalDocument document = repo.findByProfileIdAndType(profileId, documentType);
            if (document == null) {
                log.warn("Personal document not found: profileId={}, type={}", profileId, documentType);
                throw new PersonalDocumentNotFoundException("Personal document not found for profile: " + profileId + ", type: " + documentType);
            }
            return document;
        } catch (Exception e) {
            log.error("Failed to get personal document: profileId={}, type={}", profileId, documentType, e);
            throw e;
        }
    }
    
    public Integer create(PersonalDocument document) { 
        try {
            // Проверяем, не существует ли уже документ такого типа для этого профиля
            PersonalDocument existingDocument = repo.findByProfileIdAndType(document.getProfileId(), document.getDocumentType());
            if (existingDocument != null) {
                log.warn("Personal document already exists: profileId={}, type={}", document.getProfileId(), document.getDocumentType());
                throw new PersonalDocumentAlreadyExistsException("Document of type '" + document.getDocumentType() + "' already exists for this profile");
            }
            
            return repo.create(document);
        } catch (Exception e) {
            log.error("Failed to create personal document: type={}", document.getDocumentType(), e);
            throw e;
        }
    }
    
    public void update(Integer id, PersonalDocument document) { 
        try {
            // Проверяем существование документа перед обновлением
            PersonalDocument existingDocument = repo.findById(id);
            if (existingDocument == null) {
                log.warn("Personal document not found for update: id={}", id);
                throw new PersonalDocumentNotFoundException(id);
            }
            
            repo.update(id, document);
        } catch (Exception e) {
            log.error("Failed to update personal document: id={}, type={}", id, document.getDocumentType(), e);
            throw e;
        }
    }
    
    public void delete(Integer id) { 
        try {
            // Проверяем существование документа перед удалением
            PersonalDocument document = repo.findById(id);
            if (document == null) {
                log.warn("Personal document not found for deletion: id={}", id);
                throw new PersonalDocumentNotFoundException(id);
            }
            repo.delete(id);
        } catch (Exception e) {
            log.error("Failed to delete personal document: id={}", id, e);
            throw e;
        }
    }
}
