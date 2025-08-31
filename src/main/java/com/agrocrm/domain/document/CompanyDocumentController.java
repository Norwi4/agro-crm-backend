package com.agrocrm.domain.document;

import com.agrocrm.config.AuditService;
import com.agrocrm.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/company-documents")
@Tag(name = "Общие документы", description = "API для работы с общими документами предприятия")
public class CompanyDocumentController {
    private static final Logger log = LoggerFactory.getLogger(CompanyDocumentController.class);
    
    private final CompanyDocumentService service;
    private final SecurityUtil sec;
    private final AuditService auditService;

    public CompanyDocumentController(CompanyDocumentService service, SecurityUtil sec, AuditService auditService) {
        this.service = service;
        this.sec = sec;
        this.auditService = auditService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Получить список всех общих документов", description = "Возвращает список всех общих документов предприятия")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список документов получен успешно"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<List<CompanyDocument>> list() {
        try {
            List<CompanyDocument> documents = service.list();
            log.info("Retrieved {} company documents", documents.size());
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            log.error("Failed to retrieve company documents", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Получить общий документ по ID", description = "Возвращает общий документ по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Документ найден"),
        @ApiResponse(responseCode = "404", description = "Документ не найден"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<CompanyDocument> get(
            @Parameter(description = "ID документа") @PathVariable UUID id) {
        try {
            CompanyDocument document = service.get(id);
            log.info("Retrieved company document: id={}, title={}", id, document.getTitle());
            return ResponseEntity.ok(document);
        } catch (Exception e) {
            log.error("Failed to retrieve company document: id={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/type/{documentType}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Получить документы по типу", description = "Возвращает все общие документы указанного типа")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список документов получен успешно"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<List<CompanyDocument>> getByType(
            @Parameter(description = "Тип документа") @PathVariable String documentType) {
        try {
            List<CompanyDocument> documents = service.getByDocumentType(documentType);
            log.info("Retrieved {} company documents of type: {}", documents.size(), documentType);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            log.error("Failed to retrieve company documents by type: type={}", documentType, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Получить документы по статусу", description = "Возвращает все общие документы с указанным статусом")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список документов получен успешно"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<List<CompanyDocument>> getByStatus(
            @Parameter(description = "Статус документа") @PathVariable String status) {
        try {
            List<CompanyDocument> documents = service.getByStatus(status);
            log.info("Retrieved {} company documents with status: {}", documents.size(), status);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            log.error("Failed to retrieve company documents by status: status={}", status, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/department/{departmentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Получить документы по департаменту", description = "Возвращает все общие документы указанного департамента")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список документов получен успешно"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<List<CompanyDocument>> getByDepartment(
            @Parameter(description = "ID департамента") @PathVariable Integer departmentId) {
        try {
            List<CompanyDocument> documents = service.getByDepartmentId(departmentId);
            log.info("Retrieved {} company documents for department: {}", documents.size(), departmentId);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            log.error("Failed to retrieve company documents by department: departmentId={}", departmentId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/expired")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Получить истекшие документы", description = "Возвращает все общие документы с истекшим сроком действия")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список документов получен успешно"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<List<CompanyDocument>> getExpired() {
        try {
            List<CompanyDocument> documents = service.getExpiredDocuments();
            log.info("Retrieved {} expired company documents", documents.size());
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            log.error("Failed to retrieve expired company documents", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/expiring-soon")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Получить документы, истекающие в ближайшие 30 дней", description = "Возвращает все общие документы, срок действия которых истекает в ближайшие 30 дней")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список документов получен успешно"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<List<CompanyDocument>> getExpiringSoon() {
        try {
            List<CompanyDocument> documents = service.getExpiringSoonDocuments();
            log.info("Retrieved {} company documents expiring soon", documents.size());
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            log.error("Failed to retrieve company documents expiring soon", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Создать новый общий документ", description = "Создает новый общий документ предприятия")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Документ создан успешно"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<CompanyDocument> create(
            @Parameter(description = "Данные документа") @Valid @RequestBody CompanyDocument document) {
        try {
            UUID userId = sec.currentUserIdOrNull();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            document.setCreatedBy(userId);
            
            UUID id = service.create(document);
            CompanyDocument createdDocument = service.get(id);
            
            auditService.log(userId, "COMPANY_DOCUMENT_CREATED", "company_document", id.toString(), 
                           "Created company document: " + document.getTitle());
            
            log.info("Created company document: id={}, title={}", id, document.getTitle());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDocument);
        } catch (Exception e) {
            log.error("Failed to create company document: title={}", document.getTitle(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/with-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Создать новый общий документ с файлом", description = "Создает новый общий документ предприятия с загруженным PDF файлом")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Документ создан успешно"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные или файл"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<CompanyDocument> createWithFile(
            @Parameter(description = "Название документа") @RequestParam("title") String title,
            @Parameter(description = "Описание документа") @RequestParam(value = "description", required = false) String description,
            @Parameter(description = "Тип документа") @RequestParam("documentType") String documentType,
            @Parameter(description = "Статус документа") @RequestParam("status") String status,
            @Parameter(description = "ID департамента") @RequestParam(value = "departmentId", required = false) Integer departmentId,
            @Parameter(description = "PDF файл") @RequestParam("file") MultipartFile file) {
        try {
            UUID userId = sec.currentUserIdOrNull();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Проверяем тип файла
            if (!file.getContentType().equals("application/pdf")) {
                log.warn("Invalid file type: {}", file.getContentType());
                return ResponseEntity.badRequest().build();
            }
            
            // Создаем документ
            CompanyDocument document = new CompanyDocument();
            document.setTitle(title);
            document.setDescription(description);
            document.setDocumentType(documentType);
            document.setStatus(status);
            document.setDepartmentId(departmentId);
            document.setCreatedBy(userId);
            
            UUID id = service.create(document);
            
            // Генерируем имя файла
            String fileName = id.toString() + ".pdf";
            
            // Загружаем файл
            service.uploadDocumentFile(id, fileName, file.getBytes(), file.getContentType());
            
            // Получаем созданный документ
            CompanyDocument createdDocument = service.get(id);
            
            auditService.log(userId, "COMPANY_DOCUMENT_CREATED_WITH_FILE", "company_document", id.toString(), 
                           "Created company document with file: " + title);
            
            log.info("Created company document with file: id={}, title={}, file={}", id, title, fileName);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDocument);
        } catch (Exception e) {
            log.error("Failed to create company document with file: title={}", title, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Обновить общий документ", description = "Обновляет существующий общий документ")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Документ обновлен успешно"),
        @ApiResponse(responseCode = "404", description = "Документ не найден"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<CompanyDocument> update(
            @Parameter(description = "ID документа") @PathVariable UUID id,
            @Parameter(description = "Обновленные данные документа") @Valid @RequestBody CompanyDocument document) {
        try {
            UUID userId = sec.currentUserIdOrNull();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            service.update(id, document);
            CompanyDocument updatedDocument = service.get(id);
            
            auditService.log(userId, "COMPANY_DOCUMENT_UPDATED", "company_document", id.toString(), 
                           "Updated company document: " + document.getTitle());
            
            log.info("Updated company document: id={}, title={}", id, document.getTitle());
            return ResponseEntity.ok(updatedDocument);
        } catch (Exception e) {
            log.error("Failed to update company document: id={}, title={}", id, document.getTitle(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить общий документ", description = "Удаляет общий документ из системы")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Документ удален успешно"),
        @ApiResponse(responseCode = "404", description = "Документ не найден"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID документа") @PathVariable UUID id) {
        try {
            UUID userId = sec.currentUserIdOrNull();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Получаем информацию о документе перед удалением для аудита
            CompanyDocument document = service.get(id);
            
            service.delete(id);
            
            auditService.log(userId, "COMPANY_DOCUMENT_DELETED", "company_document", id.toString(), 
                           "Deleted company document: " + document.getTitle());
            
            log.info("Deleted company document: id={}, title={}", id, document.getTitle());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to delete company document: id={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

        @GetMapping("/{id}/download")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('AGRONOM')")
    @Operation(summary = "Скачать PDF документ", description = "Генерирует PDF из данных документа, сохраняет файл и отправляет на скачивание")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "PDF файл успешно сгенерирован и скачан"),
        @ApiResponse(responseCode = "404", description = "Документ не найден"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<byte[]> downloadDocument(
            @Parameter(description = "ID документа") @PathVariable UUID id) {
        try {
            UUID userId = sec.currentUserIdOrNull();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            CompanyDocument document = service.get(id);
            
            // Генерируем PDF, сохраняем файл и получаем содержимое
            byte[] pdfContent = service.generateAndSaveDocumentPdf(id);
            
            // Настраиваем заголовки для скачивания
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            
            // Используем безопасное имя файла только с ASCII символами
            String safeFileName = document.getId().toString() + ".pdf";
            headers.set("Content-Disposition", "attachment; filename=\"" + safeFileName + "\"");
            headers.setContentLength(pdfContent.length);
            
            auditService.log(userId, "COMPANY_DOCUMENT_PDF_GENERATED", "company_document", id.toString(), 
                           "Generated and downloaded PDF for company document: " + document.getTitle());
            
            log.info("Generated and downloaded PDF for company document: id={}, title={}", id, document.getTitle());
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfContent);
        } catch (CompanyDocumentNotFoundException e) {
            log.warn("Company document not found for PDF generation: id={}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Failed to generate PDF for company document: id={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/upload")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Загрузить файл для документа", description = "Загружает PDF файл для существующего документа")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Файл успешно загружен"),
        @ApiResponse(responseCode = "404", description = "Документ не найден"),
        @ApiResponse(responseCode = "400", description = "Некорректный файл"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<CompanyDocument> uploadFile(
            @Parameter(description = "ID документа") @PathVariable UUID id,
            @Parameter(description = "PDF файл") @RequestParam("file") MultipartFile file) {
        try {
            UUID userId = sec.currentUserIdOrNull();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Проверяем тип файла
            if (!file.getContentType().equals("application/pdf")) {
                log.warn("Invalid file type: {}", file.getContentType());
                return ResponseEntity.badRequest().build();
            }
            
            CompanyDocument document = service.get(id);
            
            // Генерируем имя файла
            String fileName = id.toString() + ".pdf";
            
            // Сохраняем файл
            service.uploadDocumentFile(id, fileName, file.getBytes(), file.getContentType());
            
            // Получаем обновленный документ
            CompanyDocument updatedDocument = service.get(id);
            
            auditService.log(userId, "COMPANY_DOCUMENT_FILE_UPLOADED", "company_document", id.toString(), 
                           "Uploaded file for company document: " + document.getTitle());
            
            log.info("Uploaded file for company document: id={}, title={}, file={}", id, document.getTitle(), fileName);
            return ResponseEntity.ok(updatedDocument);
        } catch (CompanyDocumentNotFoundException e) {
            log.warn("Company document not found for file upload: id={}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Failed to upload file for company document: id={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

