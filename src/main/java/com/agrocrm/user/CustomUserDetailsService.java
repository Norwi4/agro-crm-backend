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
                    "SELECT id, username, password, full_name, role, department FROM app_user WHERE username = ?",
                    new RowMapper<User>() {
                        @Override
                        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return new User(
                                    (UUID) rs.getObject("id"),
                                    rs.getString("username"),
                                    rs.getString("password"),
                                    rs.getString("full_name"),
                                    rs.getString("role"),
                                    rs.getString("department")
                            );
                        }
                    },
                    username
            );
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
            log.debug("Loaded user: username={}, role={}", username, user.getRole());
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
        } catch (EmptyResultDataAccessException e) {
            log.warn("User not found: username={}", username);
            throw new UsernameNotFoundException("User not found: " + username);
        } catch (Exception e) {
            log.error("Error loading user: username={}", username, e);
            throw new UsernameNotFoundException("Error loading user: " + username, e);
        }
    }
}
