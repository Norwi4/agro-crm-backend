package com.agrocrm.domain.role;

public class RoleNotFoundException extends RuntimeException {
    
    public RoleNotFoundException(String message) {
        super(message);
    }
    
    public RoleNotFoundException(Integer id) {
        super("Role not found with id: " + id);
    }
    
    public RoleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

