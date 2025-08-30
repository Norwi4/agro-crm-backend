package com.agrocrm.domain.document;

import java.util.UUID;

public class CompanyDocumentNotFoundException extends RuntimeException {
    
    public CompanyDocumentNotFoundException(String message) {
        super(message);
    }
    
    public CompanyDocumentNotFoundException(UUID id) {
        super("Company document not found with id: " + id);
    }
    
    public CompanyDocumentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
