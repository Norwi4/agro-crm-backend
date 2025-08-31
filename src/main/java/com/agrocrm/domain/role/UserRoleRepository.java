package com.agrocrm.domain.role;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
public class UserRoleRepository {
    private static final Logger log = LoggerFactory.getLogger(UserRoleRepository.class);

    private final JdbcTemplate jdbc;

    public UserRoleRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private RowMapper<UserRole> mapper = new RowMapper<UserRole>() {
        @Override
        public UserRole mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new UserRole(
                    rs.getInt("id"),
                    (UUID) rs.getObject("user_id"),
                    rs.getInt("role_id"),
                    (UUID) rs.getObject("assigned_by"),
                    rs.getObject("assigned_at", java.time.OffsetDateTime.class),
                    rs.getString("role_name"),
                    rs.getString("username")
            );
        }
    };

    public List<UserRole> findByUserId(UUID userId) {
        try {
            List<UserRole> userRoles = jdbc.query(
                "SELECT ur.id, ur.user_id, ur.role_id, ur.assigned_by, ur.assigned_at, " +
                "r.name as role_name, u.username " +
                "FROM user_role ur " +
                "JOIN role r ON ur.role_id = r.id " +
                "JOIN app_user u ON ur.user_id = u.id " +
                "WHERE ur.user_id = ? ORDER BY r.name", 
                mapper, userId
            );
            log.debug("Found {} user roles for user: {}", userRoles.size(), userId);
            return userRoles;
        } catch (Exception e) {
            log.error("Failed to find user roles by user: userId={}", userId, e);
            throw e;
        }
    }

    public List<UserRole> findByRoleId(Integer roleId) {
        try {
            List<UserRole> userRoles = jdbc.query(
                "SELECT ur.id, ur.user_id, ur.role_id, ur.assigned_by, ur.assigned_at, " +
                "r.name as role_name, u.username " +
                "FROM user_role ur " +
                "JOIN role r ON ur.role_id = r.id " +
                "JOIN app_user u ON ur.user_id = u.id " +
                "WHERE ur.role_id = ? ORDER BY u.username", 
                mapper, roleId
            );
            log.debug("Found {} user roles for role: {}", userRoles.size(), roleId);
            return userRoles;
        } catch (Exception e) {
            log.error("Failed to find user roles by role: roleId={}", roleId, e);
            throw e;
        }
    }

    public List<String> findRoleNamesByUserId(UUID userId) {
        try {
            List<String> roleNames = jdbc.queryForList(
                "SELECT r.name FROM user_role ur " +
                "JOIN role r ON ur.role_id = r.id " +
                "WHERE ur.user_id = ? ORDER BY r.name", 
                String.class, userId
            );
            log.debug("Found {} role names for user: {}", roleNames.size(), userId);
            return roleNames;
        } catch (Exception e) {
            log.error("Failed to find role names by user: userId={}", userId, e);
            throw e;
        }
    }

    public UserRole findById(Integer id) {
        try {
            UserRole userRole = jdbc.queryForObject(
                "SELECT ur.id, ur.user_id, ur.role_id, ur.assigned_by, ur.assigned_at, " +
                "r.name as role_name, u.username " +
                "FROM user_role ur " +
                "JOIN role r ON ur.role_id = r.id " +
                "JOIN app_user u ON ur.user_id = u.id " +
                "WHERE ur.id = ?", 
                mapper, id
            );
            log.debug("Found user role: id={}", id);
            return userRole;
        } catch (EmptyResultDataAccessException e) {
            log.debug("User role not found: id={}", id);
            return null;
        } catch (Exception e) {
            log.error("Failed to find user role: id={}", id, e);
            throw e;
        }
    }

    public UserRole findByUserIdAndRoleId(UUID userId, Integer roleId) {
        try {
            UserRole userRole = jdbc.queryForObject(
                "SELECT ur.id, ur.user_id, ur.role_id, ur.assigned_by, ur.assigned_at, " +
                "r.name as role_name, u.username " +
                "FROM user_role ur " +
                "JOIN role r ON ur.role_id = r.id " +
                "JOIN app_user u ON ur.user_id = u.id " +
                "WHERE ur.user_id = ? AND ur.role_id = ?", 
                mapper, userId, roleId
            );
            log.debug("Found user role: userId={}, roleId={}", userId, roleId);
            return userRole;
        } catch (EmptyResultDataAccessException e) {
            log.debug("User role not found: userId={}, roleId={}", userId, roleId);
            return null;
        } catch (Exception e) {
            log.error("Failed to find user role: userId={}, roleId={}", userId, roleId, e);
            throw e;
        }
    }

    public Integer create(UUID userId, Integer roleId, UUID assignedBy) {
        try {
            String sql = "INSERT INTO user_role (user_id, role_id, assigned_by) VALUES (?, ?, ?) RETURNING id";
            Integer id = jdbc.queryForObject(sql, Integer.class, userId, roleId, assignedBy);
            log.debug("Created user role: id={}, userId={}, roleId={}", id, userId, roleId);
            return id;
        } catch (Exception e) {
            log.error("Failed to create user role: userId={}, roleId={}", userId, roleId, e);
            throw e;
        }
    }

    public void delete(Integer id) {
        try {
            jdbc.update("DELETE FROM user_role WHERE id = ?", id);
            log.debug("Deleted user role: id={}", id);
        } catch (Exception e) {
            log.error("Failed to delete user role: id={}", id, e);
            throw e;
        }
    }

    public void deleteByUserIdAndRoleId(UUID userId, Integer roleId) {
        try {
            jdbc.update("DELETE FROM user_role WHERE user_id = ? AND role_id = ?", userId, roleId);
            log.debug("Deleted user role: userId={}, roleId={}", userId, roleId);
        } catch (Exception e) {
            log.error("Failed to delete user role: userId={}, roleId={}", userId, roleId, e);
            throw e;
        }
    }

    public void deleteAllByUserId(UUID userId) {
        try {
            jdbc.update("DELETE FROM user_role WHERE user_id = ?", userId);
            log.debug("Deleted all user roles for user: {}", userId);
        } catch (Exception e) {
            log.error("Failed to delete all user roles for user: {}", userId, e);
            throw e;
        }
    }
}

