package com.agrocrm.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

@Component
public class SecurityUtil {
    private static final Logger log = LoggerFactory.getLogger(SecurityUtil.class);
    
    private final JdbcTemplate jdbc;

    public SecurityUtil(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public String currentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }

    public UUID currentUserIdOrNull() {
        String username = currentUsername();
        if (username == null) return null;
        try {
            return jdbc.queryForObject("SELECT id FROM app_user WHERE username = ?", UUID.class, username);
        } catch (Exception e) {
            log.debug("Failed to get user ID for username: {}", username, e);
            return null;
        }
    }

    public boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
}
