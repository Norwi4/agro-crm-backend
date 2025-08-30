package com.agrocrm.domain.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserProfileService {
    private static final Logger log = LoggerFactory.getLogger(UserProfileService.class);
    
    private final UserProfileRepository repo;

    public UserProfileService(UserProfileRepository repo) {
        this.repo = repo;
    }

    public List<UserProfile> list() { 
        try {
            return repo.findAll();
        } catch (Exception e) {
            log.error("Failed to list user profiles", e);
            throw e;
        }
    }
    
    public UserProfile get(UUID id) { 
        try {
            UserProfile profile = repo.findById(id);
            if (profile == null) {
                log.warn("User profile not found: id={}", id);
                throw new UserProfileNotFoundException(id);
            }
            return profile;
        } catch (Exception e) {
            log.error("Failed to get user profile: id={}", id, e);
            throw e;
        }
    }
    
    public UserProfile getByUserId(UUID userId) { 
        try {
            UserProfile profile = repo.findByUserId(userId);
            if (profile == null) {
                log.warn("User profile not found for userId: {}", userId);
                throw new UserProfileNotFoundException("User profile not found for userId: " + userId);
            }
            return profile;
        } catch (Exception e) {
            log.error("Failed to get user profile by userId: userId={}", userId, e);
            throw e;
        }
    }
    
    public UserProfile getByEmployeeNumber(String employeeNumber) { 
        try {
            UserProfile profile = repo.findByEmployeeNumber(employeeNumber);
            if (profile == null) {
                log.warn("User profile not found for employee number: {}", employeeNumber);
                throw new UserProfileNotFoundException("User profile not found for employee number: " + employeeNumber);
            }
            return profile;
        } catch (Exception e) {
            log.error("Failed to get user profile by employee number: employeeNumber={}", employeeNumber, e);
            throw e;
        }
    }
    
    public UUID create(UserProfile profile) { 
        try {
            return repo.create(profile);
        } catch (Exception e) {
            log.error("Failed to create user profile: name={}", profile.getFullName(), e);
            throw e;
        }
    }
    
    public void update(UUID id, UserProfile profile) { 
        try {
            repo.update(id, profile);
        } catch (Exception e) {
            log.error("Failed to update user profile: id={}, name={}", id, profile.getFullName(), e);
            throw e;
        }
    }
    
    public void delete(UUID id) { 
        try {
            repo.delete(id);
        } catch (Exception e) {
            log.error("Failed to delete user profile: id={}", id, e);
            throw e;
        }
    }
}
