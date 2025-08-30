package com.agrocrm.domain.role;

public class UserRoleNotFoundException extends RuntimeException {
    
    public UserRoleNotFoundException(String message) {
        super(message);
    }
    
    public UserRoleNotFoundException(Integer id) {
        super("User role not found with id: " + id);
    }
    
    public UserRoleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
