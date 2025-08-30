package com.agrocrm.errors;

import com.agrocrm.domain.document.PersonalDocumentNotFoundException;
import com.agrocrm.domain.document.PersonalDocumentAlreadyExistsException;
import com.agrocrm.domain.document.CompanyDocumentNotFoundException;
import com.agrocrm.domain.role.RoleNotFoundException;
import com.agrocrm.domain.role.RoleAlreadyExistsException;
import com.agrocrm.domain.role.UserRoleNotFoundException;
import com.agrocrm.domain.role.UserRoleAlreadyExistsException;
import com.agrocrm.domain.user.UserProfileNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        
        log.warn("Validation error: {}", errors);
        return ResponseEntity.badRequest().body(Map.of(
            "error", "validation_error", 
            "message", "Validation failed", 
            "details", errors
        ));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleData(DataAccessException ex) {
        log.error("Database error", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
            "error", "db_error", 
            "message", "Database operation failed"
        ));
    }

    @ExceptionHandler(UserProfileNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserProfileNotFound(UserProfileNotFoundException ex) {
        log.warn("User profile not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
            "error", "user_profile_not_found", 
            "message", ex.getMessage()
        ));
    }



    @ExceptionHandler(PersonalDocumentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePersonalDocumentNotFound(PersonalDocumentNotFoundException ex) {
        log.warn("Personal document not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
            "error", "personal_document_not_found", 
            "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(PersonalDocumentAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handlePersonalDocumentAlreadyExists(PersonalDocumentAlreadyExistsException ex) {
        log.warn("Personal document already exists: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
            "error", "personal_document_already_exists", 
            "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(CompanyDocumentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCompanyDocumentNotFound(CompanyDocumentNotFoundException ex) {
        log.warn("Company document not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
            "error", "company_document_not_found", 
            "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleRoleNotFound(RoleNotFoundException ex) {
        log.warn("Role not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
            "error", "role_not_found", 
            "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(RoleAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleRoleAlreadyExists(RoleAlreadyExistsException ex) {
        log.warn("Role already exists: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
            "error", "role_already_exists", 
            "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(UserRoleNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserRoleNotFound(UserRoleNotFoundException ex) {
        log.warn("User role not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
            "error", "user_role_not_found", 
            "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(UserRoleAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUserRoleAlreadyExists(UserRoleAlreadyExistsException ex) {
        log.warn("User role already exists: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
            "error", "user_role_already_exists", 
            "message", ex.getMessage()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
            "error", "internal_error", 
            "message", "An unexpected error occurred"
        ));
    }
}
