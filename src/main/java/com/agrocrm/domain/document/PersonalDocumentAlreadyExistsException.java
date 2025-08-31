package com.agrocrm.domain.document;

public class PersonalDocumentAlreadyExistsException extends RuntimeException {
    
    public PersonalDocumentAlreadyExistsException(String message) {
        super(message);
    }
    
    public PersonalDocumentAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

