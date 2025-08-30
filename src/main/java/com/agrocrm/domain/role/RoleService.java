package com.agrocrm.domain.role;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    private static final Logger log = LoggerFactory.getLogger(RoleService.class);
    
    private final RoleRepository repo;

    public RoleService(RoleRepository repo) {
        this.repo = repo;
    }

    public List<Role> list() { 
        try {
            return repo.findAll();
        } catch (Exception e) {
            log.error("Failed to list roles", e);
            throw e;
        }
    }
    
    public Role get(Integer id) { 
        try {
            Role role = repo.findById(id);
            if (role == null) {
                log.warn("Role not found: id={}", id);
                throw new RoleNotFoundException(id);
            }
            return role;
        } catch (Exception e) {
            log.error("Failed to get role: id={}", id, e);
            throw e;
        }
    }
    
    public Role getByName(String name) { 
        try {
            Role role = repo.findByName(name);
            if (role == null) {
                log.warn("Role not found: name={}", name);
                throw new RoleNotFoundException("Role not found with name: " + name);
            }
            return role;
        } catch (Exception e) {
            log.error("Failed to get role: name={}", name, e);
            throw e;
        }
    }
    
    public Integer create(Role role) { 
        try {
            // Проверяем, не существует ли уже роль с таким именем
            Role existingRole = repo.findByName(role.getName());
            if (existingRole != null) {
                log.warn("Role already exists: name={}", role.getName());
                throw new RoleAlreadyExistsException("Role with name '" + role.getName() + "' already exists");
            }
            return repo.create(role);
        } catch (Exception e) {
            log.error("Failed to create role: name={}", role.getName(), e);
            throw e;
        }
    }
    
    public void update(Integer id, Role role) { 
        try {
            // Проверяем существование роли перед обновлением
            Role existingRole = repo.findById(id);
            if (existingRole == null) {
                log.warn("Role not found for update: id={}", id);
                throw new RoleNotFoundException(id);
            }
            
            // Проверяем, не существует ли уже роль с таким именем (кроме текущей)
            Role roleWithSameName = repo.findByName(role.getName());
            if (roleWithSameName != null && !roleWithSameName.getId().equals(id)) {
                log.warn("Role already exists: name={}", role.getName());
                throw new RoleAlreadyExistsException("Role with name '" + role.getName() + "' already exists");
            }
            
            repo.update(id, role);
        } catch (Exception e) {
            log.error("Failed to update role: id={}, name={}", id, role.getName(), e);
            throw e;
        }
    }
    
    public void delete(Integer id) { 
        try {
            // Проверяем существование роли перед удалением
            Role role = repo.findById(id);
            if (role == null) {
                log.warn("Role not found for deletion: id={}", id);
                throw new RoleNotFoundException(id);
            }
            repo.delete(id);
        } catch (Exception e) {
            log.error("Failed to delete role: id={}", id, e);
            throw e;
        }
    }
}
