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

@Repository
public class RoleRepository {
    private static final Logger log = LoggerFactory.getLogger(RoleRepository.class);

    private final JdbcTemplate jdbc;

    public RoleRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private RowMapper<Role> mapper = new RowMapper<Role>() {
        @Override
        public Role mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Role(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getObject("created_at", java.time.OffsetDateTime.class),
                    rs.getObject("updated_at", java.time.OffsetDateTime.class)
            );
        }
    };

    public List<Role> findAll() {
        try {
            List<Role> roles = jdbc.query(
                "SELECT id, name, description, created_at, updated_at FROM role ORDER BY name", 
                mapper
            );
            log.debug("Found {} roles", roles.size());
            return roles;
        } catch (Exception e) {
            log.error("Failed to find all roles", e);
            throw e;
        }
    }

    public Role findById(Integer id) {
        try {
            Role role = jdbc.queryForObject(
                "SELECT id, name, description, created_at, updated_at FROM role WHERE id = ?", 
                mapper, id
            );
            log.debug("Found role: id={}, name={}", id, role != null ? role.getName() : "null");
            return role;
        } catch (EmptyResultDataAccessException e) {
            log.debug("Role not found: id={}", id);
            return null;
        } catch (Exception e) {
            log.error("Failed to find role: id={}", id, e);
            throw e;
        }
    }

    public Role findByName(String name) {
        try {
            Role role = jdbc.queryForObject(
                "SELECT id, name, description, created_at, updated_at FROM role WHERE name = ?", 
                mapper, name
            );
            log.debug("Found role: name={}", name);
            return role;
        } catch (EmptyResultDataAccessException e) {
            log.debug("Role not found: name={}", name);
            return null;
        } catch (Exception e) {
            log.error("Failed to find role: name={}", name, e);
            throw e;
        }
    }

    public Integer create(Role role) {
        try {
            String sql = "INSERT INTO role (name, description) VALUES (?, ?) RETURNING id";
            Integer id = jdbc.queryForObject(sql, Integer.class, role.getName(), role.getDescription());
            log.debug("Created role: id={}, name={}", id, role.getName());
            return id;
        } catch (Exception e) {
            log.error("Failed to create role: name={}", role.getName(), e);
            throw e;
        }
    }

    public void update(Integer id, Role role) {
        try {
            String sql = "UPDATE role SET name=?, description=?, updated_at=now() WHERE id=?";
            jdbc.update(sql, role.getName(), role.getDescription(), id);
            log.debug("Updated role: id={}, name={}", id, role.getName());
        } catch (Exception e) {
            log.error("Failed to update role: id={}, name={}", id, role.getName(), e);
            throw e;
        }
    }

    public void delete(Integer id) {
        try {
            jdbc.update("DELETE FROM role WHERE id = ?", id);
            log.debug("Deleted role: id={}", id);
        } catch (Exception e) {
            log.error("Failed to delete role: id={}", id, e);
            throw e;
        }
    }
}

