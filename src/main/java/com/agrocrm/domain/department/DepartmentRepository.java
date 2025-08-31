package com.agrocrm.domain.department;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
public class DepartmentRepository {
    private static final Logger log = LoggerFactory.getLogger(DepartmentRepository.class);

    private final JdbcTemplate jdbc;

    public DepartmentRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private RowMapper<Department> mapper = new RowMapper<Department>() {
        @Override
        public Department mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Department(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    (UUID) rs.getObject("manager_id"),
                    rs.getInt("parent_department_id"),
                    rs.getObject("created_at", java.time.OffsetDateTime.class),
                    rs.getObject("updated_at", java.time.OffsetDateTime.class)
            );
        }
    };

    public List<Department> findAll() {
        try {
            List<Department> departments = jdbc.query(
                "SELECT id, name, description, manager_id, parent_department_id, created_at, updated_at FROM department ORDER BY name", 
                mapper
            );
            log.debug("Found {} departments", departments.size());
            return departments;
        } catch (Exception e) {
            log.error("Failed to find all departments", e);
            throw e;
        }
    }

    public Department findById(Integer id) {
        try {
            Department department = jdbc.queryForObject(
                "SELECT id, name, description, manager_id, parent_department_id, created_at, updated_at FROM department WHERE id = ?", 
                mapper, id
            );
            log.debug("Found department: id={}, name={}", id, department != null ? department.getName() : "null");
            return department;
        } catch (Exception e) {
            log.error("Failed to find department: id={}", id, e);
            throw e;
        }
    }

    public Integer create(Department department) {
        try {
            String sql = "INSERT INTO department (name, description, manager_id, parent_department_id) VALUES (?, ?, ?, ?) RETURNING id";
            Integer id = jdbc.queryForObject(sql, Integer.class, 
                department.getName(), 
                department.getDescription(), 
                department.getManagerId(), 
                department.getParentDepartmentId()
            );
            log.debug("Created department: id={}, name={}", id, department.getName());
            return id;
        } catch (Exception e) {
            log.error("Failed to create department: name={}", department.getName(), e);
            throw e;
        }
    }

    public void update(Integer id, Department department) throws DataAccessException {
        try {
            String sql = "UPDATE department SET name=?, description=?, manager_id=?, parent_department_id=?, updated_at=now() WHERE id=?";
            jdbc.update(sql, 
                department.getName(), 
                department.getDescription(), 
                department.getManagerId(), 
                department.getParentDepartmentId(), 
                id
            );
            log.debug("Updated department: id={}, name={}", id, department.getName());
        } catch (Exception e) {
            log.error("Failed to update department: id={}, name={}", id, department.getName(), e);
            throw e;
        }
    }

    public void delete(Integer id) {
        try {
            jdbc.update("DELETE FROM department WHERE id = ?", id);
            log.debug("Deleted department: id={}", id);
        } catch (Exception e) {
            log.error("Failed to delete department: id={}", id, e);
            throw e;
        }
    }
}

