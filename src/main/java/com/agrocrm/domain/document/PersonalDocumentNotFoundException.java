package com.agrocrm.domain.document;

public class PersonalDocumentNotFoundException extends RuntimeException {
    
    public PersonalDocumentNotFoundException(String message) {
        super(message);
    }
    
    public PersonalDocumentNotFoundException(Integer id) {
        super("Personal document not found with id: " + id);
    }
    
    public PersonalDocumentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

