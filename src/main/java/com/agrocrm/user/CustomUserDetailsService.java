package com.agrocrm.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final JdbcTemplate jdbc;

    public CustomUserDetailsService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = jdbc.queryForObject(
                    "SELECT id, username, password FROM app_user WHERE username = ?",
                    new RowMapper<User>() {
                        @Override
                        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return new User(
                                    (UUID) rs.getObject("id"),
                                    rs.getString("username"),
                                    rs.getString("password"),
                                    null, // full_name больше не используется
                                    null, // role больше не используется
                                    null  // department больше не используется
                            );
                        }
                    },
                    username
            );
            
            // Получаем роли пользователя из новой системы ролей
            List<String> roles = getUserRoles(user.getId());
            List<GrantedAuthority> authorities = getAuthorities(roles);
            
            log.debug("Loaded user: username={}, roles={}", username, roles);
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
        } catch (EmptyResultDataAccessException e) {
            log.warn("User not found: username={}", username);
            throw new UsernameNotFoundException("User not found: " + username);
        } catch (Exception e) {
            log.error("Error loading user: username={}", username, e);
            throw new UsernameNotFoundException("Error loading user: " + username, e);
        }
    }
    
    private List<String> getUserRoles(UUID userId) {
        try {
            String sql = "SELECT r.name FROM user_role ur " +
                        "JOIN role r ON ur.role_id = r.id " +
                        "WHERE ur.user_id = ? ORDER BY r.name";
            List<String> roles = jdbc.queryForList(sql, String.class, userId);
            return roles.isEmpty() ? List.of("VIEWER") : roles;
        } catch (Exception e) {
            log.error("Failed to get user roles for userId: {}", userId, e);
            return List.of("VIEWER");
        }
    }
    
    private List<GrantedAuthority> getAuthorities(List<String> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        return authorities;
    }
}
