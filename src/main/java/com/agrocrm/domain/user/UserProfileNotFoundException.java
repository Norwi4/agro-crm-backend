package com.agrocrm.domain.user;

import java.util.UUID;

public class UserProfileNotFoundException extends RuntimeException {
    
    public UserProfileNotFoundException(String message) {
        super(message);
    }
    
    public UserProfileNotFoundException(UUID id) {
        super("User profile not found with id: " + id);
    }
    
    public UserProfileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
