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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/personal-documents")
@Tag(name = "Персональные документы", description = "API для работы с персональными документами сотрудников")
public class PersonalDocumentController {
    private static final Logger log = LoggerFactory.getLogger(PersonalDocumentController.class);
    
    private final PersonalDocumentService service;
    private final SecurityUtil sec;
    private final AuditService auditService;

    public PersonalDocumentController(PersonalDocumentService service, SecurityUtil sec, AuditService auditService) {
        this.service = service;
        this.sec = sec;
        this.auditService = auditService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Получить список всех персональных документов", description = "Возвращает список всех персональных документов в системе")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список документов получен успешно"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<List<PersonalDocument>> list() {
        try {
            List<PersonalDocument> documents = service.list();
            log.info("Retrieved {} personal documents", documents.size());
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            log.error("Failed to retrieve personal documents", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Получить персональный документ по ID", description = "Возвращает персональный документ по указанному ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Документ найден"),
        @ApiResponse(responseCode = "404", description = "Документ не найден"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<PersonalDocument> get(
            @Parameter(description = "ID документа") @PathVariable Integer id) {
        try {
            PersonalDocument document = service.get(id);
            log.info("Retrieved personal document: id={}, type={}", id, document.getDocumentType());
            return ResponseEntity.ok(document);
        } catch (Exception e) {
            log.error("Failed to retrieve personal document: id={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/profile/{profileId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Получить документы профиля", description = "Возвращает все персональные документы указанного профиля")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список документов получен успешно"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<List<PersonalDocument>> getByProfile(
            @Parameter(description = "ID профиля") @PathVariable UUID profileId) {
        try {
            List<PersonalDocument> documents = service.getByProfileId(profileId);
            log.info("Retrieved {} personal documents for profile: {}", documents.size(), profileId);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            log.error("Failed to retrieve personal documents by profile: profileId={}", profileId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/type/{documentType}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Получить документы по типу", description = "Возвращает все персональные документы указанного типа")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список документов получен успешно"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<List<PersonalDocument>> getByType(
            @Parameter(description = "Тип документа") @PathVariable String documentType) {
        try {
            List<PersonalDocument> documents = service.getByDocumentType(documentType);
            log.info("Retrieved {} personal documents of type: {}", documents.size(), documentType);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            log.error("Failed to retrieve personal documents by type: type={}", documentType, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/expired")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Получить истекшие документы", description = "Возвращает все персональные документы с истекшим сроком действия")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список документов получен успешно"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<List<PersonalDocument>> getExpired() {
        try {
            List<PersonalDocument> documents = service.getExpiredDocuments();
            log.info("Retrieved {} expired personal documents", documents.size());
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            log.error("Failed to retrieve expired personal documents", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/expiring-soon")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Получить документы, истекающие в ближайшие 30 дней", description = "Возвращает все персональные документы, срок действия которых истекает в ближайшие 30 дней")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список документов получен успешно"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<List<PersonalDocument>> getExpiringSoon() {
        try {
            List<PersonalDocument> documents = service.getExpiringSoonDocuments();
            log.info("Retrieved {} personal documents expiring soon", documents.size());
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            log.error("Failed to retrieve personal documents expiring soon", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Создать новый персональный документ", description = "Создает новый персональный документ для сотрудника")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Документ создан успешно"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные"),
        @ApiResponse(responseCode = "409", description = "Документ такого типа уже существует для этого профиля"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<PersonalDocument> create(
            @Parameter(description = "Данные документа") @Valid @RequestBody PersonalDocument document) {
        try {
            UUID userId = sec.currentUserIdOrNull();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            Integer id = service.create(document);
            PersonalDocument createdDocument = service.get(id);
            
            auditService.log(userId, "PERSONAL_DOCUMENT_CREATED", "personal_document", id.toString(), 
                           "Created personal document: " + document.getDocumentType() + " for profile: " + document.getProfileId());
            
            log.info("Created personal document: id={}, type={}, profileId={}", id, document.getDocumentType(), document.getProfileId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDocument);
        } catch (Exception e) {
            log.error("Failed to create personal document: type={}", document.getDocumentType(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(summary = "Обновить персональный документ", description = "Обновляет существующий персональный документ")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Документ обновлен успешно"),
        @ApiResponse(responseCode = "404", description = "Документ не найден"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<PersonalDocument> update(
            @Parameter(description = "ID документа") @PathVariable Integer id,
            @Parameter(description = "Обновленные данные документа") @Valid @RequestBody PersonalDocument document) {
        try {
            UUID userId = sec.currentUserIdOrNull();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            service.update(id, document);
            PersonalDocument updatedDocument = service.get(id);
            
            auditService.log(userId, "PERSONAL_DOCUMENT_UPDATED", "personal_document", id.toString(), 
                           "Updated personal document: " + document.getDocumentType());
            
            log.info("Updated personal document: id={}, type={}", id, document.getDocumentType());
            return ResponseEntity.ok(updatedDocument);
        } catch (Exception e) {
            log.error("Failed to update personal document: id={}, type={}", id, document.getDocumentType(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить персональный документ", description = "Удаляет персональный документ из системы")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Документ удален успешно"),
        @ApiResponse(responseCode = "404", description = "Документ не найден"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID документа") @PathVariable Integer id) {
        try {
            UUID userId = sec.currentUserIdOrNull();
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            // Получаем информацию о документе перед удалением для аудита
            PersonalDocument document = service.get(id);
            
            service.delete(id);
            
            auditService.log(userId, "PERSONAL_DOCUMENT_DELETED", "personal_document", id.toString(), 
                           "Deleted personal document: " + document.getDocumentType());
            
            log.info("Deleted personal document: id={}, type={}", id, document.getDocumentType());
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to delete personal document: id={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

