package com.agrocrm.domain.department;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {
    private static final Logger log = LoggerFactory.getLogger(DepartmentService.class);
    
    private final DepartmentRepository repo;

    public DepartmentService(DepartmentRepository repo) {
        this.repo = repo;
    }

    public List<Department> list() { 
        try {
            return repo.findAll();
        } catch (Exception e) {
            log.error("Failed to list departments", e);
            throw e;
        }
    }
    
    public Department get(Integer id) { 
        try {
            return repo.findById(id);
        } catch (Exception e) {
            log.error("Failed to get department: id={}", id, e);
            throw e;
        }
    }
    
    public Integer create(Department department) { 
        try {
            return repo.create(department);
        } catch (Exception e) {
            log.error("Failed to create department: name={}", department.getName(), e);
            throw e;
        }
    }
    
    public void update(Integer id, Department department) { 
        try {
            repo.update(id, department);
        } catch (Exception e) {
            log.error("Failed to update department: id={}, name={}", id, department.getName(), e);
            throw e;
        }
    }
    
    public void delete(Integer id) { 
        try {
            repo.delete(id);
        } catch (Exception e) {
            log.error("Failed to delete department: id={}", id, e);
            throw e;
        }
    }
}

