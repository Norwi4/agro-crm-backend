package com.agrocrm.domain.document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class CompanyDocumentService {
    private static final Logger log = LoggerFactory.getLogger(CompanyDocumentService.class);
    
    private final CompanyDocumentRepository repo;
    private final FileStorageService fileStorageService;
    private final PdfGeneratorService pdfGeneratorService;

    public CompanyDocumentService(CompanyDocumentRepository repo, FileStorageService fileStorageService, 
                                PdfGeneratorService pdfGeneratorService) {
        this.repo = repo;
        this.fileStorageService = fileStorageService;
        this.pdfGeneratorService = pdfGeneratorService;
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
    
    /**
     * Получить файл документа для скачивания
     */
    public byte[] getDocumentFile(UUID id) {
        try {
            CompanyDocument document = get(id);
            
            if (document.getFilePath() == null || document.getFilePath().trim().isEmpty()) {
                log.warn("Document file path is empty: id={}", id);
                throw new CompanyDocumentNotFoundException("Файл документа не найден");
            }
            
            // Проверяем существование файла
            if (!fileStorageService.fileExists(document.getFilePath())) {
                log.warn("Document file not found on disk: id={}, path={}", id, document.getFilePath());
                throw new CompanyDocumentNotFoundException("Файл документа не найден на диске");
            }
            
            // Читаем файл с диска
            byte[] fileContent = fileStorageService.readFile(document.getFilePath());
            log.info("Retrieved document file: id={}, path={}, size={} bytes", id, document.getFilePath(), fileContent.length);
            return fileContent;
        } catch (IOException e) {
            log.error("Failed to read document file: id={}", id, e);
            throw new CompanyDocumentNotFoundException("Ошибка при чтении файла документа: " + e.getMessage());
        } catch (Exception e) {
            log.error("Failed to get document file: id={}", id, e);
            throw e;
        }
    }
    
    /**
     * Загрузить файл для документа
     */
    public void uploadDocumentFile(UUID id, String fileName, byte[] fileContent, String mimeType) {
        try {
            CompanyDocument document = get(id);
            
            // Сохраняем файл в хранилище
            fileStorageService.saveFile(fileName, fileContent);
            
            // Обновляем информацию о файле в документе
            document.setFilePath(fileName);
            document.setFileSize((long) fileContent.length);
            document.setMimeType(mimeType);
            
            // Сохраняем обновленный документ
            repo.update(id, document);
            
            log.info("Uploaded document file: id={}, file={}, size={} bytes", id, fileName, fileContent.length);
        } catch (IOException e) {
            log.error("Failed to save document file: id={}, file={}", id, fileName, e);
            throw new RuntimeException("Ошибка при сохранении файла: " + e.getMessage());
        } catch (Exception e) {
            log.error("Failed to upload document file: id={}, file={}", id, fileName, e);
            throw e;
        }
    }
    
    /**
     * Генерирует PDF документ, сохраняет его и возвращает содержимое для скачивания
     */
    public byte[] generateAndSaveDocumentPdf(UUID id) {
        try {
            CompanyDocument document = get(id);
            
            // Генерируем PDF
            byte[] pdfContent = pdfGeneratorService.generateCompanyDocumentPdf(document);
            
            // Генерируем имя файла
            String fileName = id.toString() + ".pdf";
            
            // Сохраняем файл в хранилище
            fileStorageService.saveFile(fileName, pdfContent);
            
            // Обновляем информацию о файле в документе
            document.setFilePath(fileName);
            document.setFileSize((long) pdfContent.length);
            document.setMimeType("application/pdf");
            
            // Сохраняем обновленный документ
            repo.update(id, document);
            
            log.info("Generated and saved PDF for document: id={}, title={}, file={}, size={} bytes", 
                    id, document.getTitle(), fileName, pdfContent.length);
            
            return pdfContent;
        } catch (IOException e) {
            log.error("Failed to generate PDF for document: id={}", id, e);
            throw new RuntimeException("Ошибка при генерации PDF: " + e.getMessage());
        } catch (Exception e) {
            log.error("Failed to generate and save PDF for document: id={}", id, e);
            throw e;
        }
    }
}

