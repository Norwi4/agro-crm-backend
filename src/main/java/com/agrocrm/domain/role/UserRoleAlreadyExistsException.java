package com.agrocrm.domain.role;

public class UserRoleAlreadyExistsException extends RuntimeException {
    
    public UserRoleAlreadyExistsException(String message) {
        super(message);
    }
    
    public UserRoleAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
