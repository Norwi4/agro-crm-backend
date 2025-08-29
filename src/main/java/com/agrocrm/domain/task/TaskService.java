package com.agrocrm.domain.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);
    
    private final TaskRepository repo;

    public TaskService(TaskRepository repo) { this.repo = repo; }

    public UUID create(Task t, UUID creator) { 
        try {
            return repo.create(t, creator);
        } catch (Exception e) {
            log.error("Failed to create task: title={}, creator={}", t.getTitle(), creator, e);
            throw e;
        }
    }
    
    public List<Task> find(String status, UUID fieldId) { 
        try {
            return repo.find(status, fieldId);
        } catch (Exception e) {
            log.error("Failed to find tasks: status={}, fieldId={}", status, fieldId, e);
            throw e;
        }
    }
    
    public void setStatus(UUID id, String status) { 
        try {
            repo.setStatus(id, status);
        } catch (Exception e) {
            log.error("Failed to set task status: id={}, status={}", id, status, e);
            throw e;
        }
    }
}
