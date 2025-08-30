package com.agrocrm.domain.role;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserRoleService {
    private static final Logger log = LoggerFactory.getLogger(UserRoleService.class);
    
    private final UserRoleRepository repo;

    public UserRoleService(UserRoleRepository repo) {
        this.repo = repo;
    }

    public List<UserRole> getUserRoles(UUID userId) { 
        try {
            return repo.findByUserId(userId);
        } catch (Exception e) {
            log.error("Failed to get user roles: userId={}", userId, e);
            throw e;
        }
    }
    
    public List<UserRole> getUsersByRole(Integer roleId) { 
        try {
            return repo.findByRoleId(roleId);
        } catch (Exception e) {
            log.error("Failed to get users by role: roleId={}", roleId, e);
            throw e;
        }
    }
    
    public List<String> getUserRoleNames(UUID userId) { 
        try {
            return repo.findRoleNamesByUserId(userId);
        } catch (Exception e) {
            log.error("Failed to get user role names: userId={}", userId, e);
            throw e;
        }
    }
    
    public UserRole getUserRole(Integer id) { 
        try {
            UserRole userRole = repo.findById(id);
            if (userRole == null) {
                log.warn("User role not found: id={}", id);
                throw new UserRoleNotFoundException(id);
            }
            return userRole;
        } catch (Exception e) {
            log.error("Failed to get user role: id={}", id, e);
            throw e;
        }
    }
    
    public Integer assignRole(UUID userId, Integer roleId, UUID assignedBy) { 
        try {
            // Проверяем, не назначена ли уже эта роль пользователю
            UserRole existingUserRole = repo.findByUserIdAndRoleId(userId, roleId);
            if (existingUserRole != null) {
                log.warn("Role already assigned to user: userId={}, roleId={}", userId, roleId);
                throw new UserRoleAlreadyExistsException("Role is already assigned to this user");
            }
            return repo.create(userId, roleId, assignedBy);
        } catch (Exception e) {
            log.error("Failed to assign role: userId={}, roleId={}", userId, roleId, e);
            throw e;
        }
    }
    
    public void removeRole(Integer id) { 
        try {
            // Проверяем существование связи перед удалением
            UserRole userRole = repo.findById(id);
            if (userRole == null) {
                log.warn("User role not found for removal: id={}", id);
                throw new UserRoleNotFoundException(id);
            }
            repo.delete(id);
        } catch (Exception e) {
            log.error("Failed to remove role: id={}", id, e);
            throw e;
        }
    }
    
    public void removeRole(UUID userId, Integer roleId) { 
        try {
            // Проверяем существование связи перед удалением
            UserRole userRole = repo.findByUserIdAndRoleId(userId, roleId);
            if (userRole == null) {
                log.warn("User role not found for removal: userId={}, roleId={}", userId, roleId);
                throw new UserRoleNotFoundException("User role not found for user: " + userId + ", role: " + roleId);
            }
            repo.deleteByUserIdAndRoleId(userId, roleId);
        } catch (Exception e) {
            log.error("Failed to remove role: userId={}, roleId={}", userId, roleId, e);
            throw e;
        }
    }
    
    public void removeAllUserRoles(UUID userId) { 
        try {
            repo.deleteAllByUserId(userId);
        } catch (Exception e) {
            log.error("Failed to remove all user roles: userId={}", userId, e);
            throw e;
        }
    }
}
