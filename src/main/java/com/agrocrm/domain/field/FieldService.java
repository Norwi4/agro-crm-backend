package com.agrocrm.domain.field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FieldService {
    private static final Logger log = LoggerFactory.getLogger(FieldService.class);
    
    private final FieldRepository repo;

    public FieldService(FieldRepository repo) {
        this.repo = repo;
    }

    public List<Field> list() { 
        try {
            return repo.findAll();
        } catch (Exception e) {
            log.error("Failed to list fields", e);
            throw e;
        }
    }
    
    public PageableFieldResponse listPaginated(int page, int size) { 
        try {
            List<Field> fields = repo.findAllPaginated(page, size);
            long totalElements = repo.countAll();
            int totalPages = (int) Math.ceil((double) totalElements / size);
            int numberOfElements = fields.size();
            long offset = (long) page * size;
            
            // Создаем ссылки навигации
            String baseUrl = "/api/fields";
            String first = baseUrl + "?page=0&size=" + size;
            String previous = page > 0 ? baseUrl + "?page=" + (page - 1) + "&size=" + size : null;
            String current = baseUrl + "?page=" + page + "&size=" + size;
            String next = page < totalPages - 1 ? baseUrl + "?page=" + (page + 1) + "&size=" + size : null;
            String last = baseUrl + "?page=" + (totalPages - 1) + "&size=" + size;
            
            PageableFieldResponse.NavigationLinks navigationLinks = 
                new PageableFieldResponse.NavigationLinks(first, previous, current, next, last);
            
            PageableFieldResponse.PageMetadata metadata = 
                new PageableFieldResponse.PageMetadata(totalElements, totalPages, page, size, offset, numberOfElements, navigationLinks);
            
            return new PageableFieldResponse(fields, metadata);
        } catch (Exception e) {
            log.error("Failed to list fields with pagination: page={}, size={}", page, size, e);
            throw e;
        }
    }
    
    public Field get(UUID id) { 
        try {
            return repo.findById(id);
        } catch (Exception e) {
            log.error("Failed to get field: id={}", id, e);
            throw e;
        }
    }
    
    public UUID create(Field f, UUID userId) { 
        try {
            return repo.create(f, userId);
        } catch (Exception e) {
            log.error("Failed to create field: name={}, userId={}", f.getName(), userId, e);
            throw e;
        }
    }
    
    public void update(UUID id, Field f) { 
        try {
            repo.update(id, f);
        } catch (Exception e) {
            log.error("Failed to update field: id={}, name={}", id, f.getName(), e);
            throw e;
        }
    }
    
    public void delete(UUID id) { 
        try {
            repo.delete(id);
        } catch (Exception e) {
            log.error("Failed to delete field: id={}", id, e);
            throw e;
        }
    }
}
