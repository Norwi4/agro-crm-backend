package com.agrocrm.domain.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public class UserProfileRepository {
    private static final Logger log = LoggerFactory.getLogger(UserProfileRepository.class);

    private final JdbcTemplate jdbc;

    public UserProfileRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private RowMapper<UserProfile> mapper = new RowMapper<UserProfile>() {
        @Override
        public UserProfile mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new UserProfile(
                    (UUID) rs.getObject("id"),
                    (UUID) rs.getObject("user_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("middle_name"),
                    rs.getObject("birth_date", LocalDate.class),
                    rs.getString("gender"),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getString("position"),
                    rs.getInt("department_id"),
                    rs.getObject("hire_date", LocalDate.class),
                    rs.getString("employment_type"),
                    rs.getString("education"),
                    rs.getString("employee_number"),
                    rs.getObject("created_at", java.time.OffsetDateTime.class),
                    rs.getObject("updated_at", java.time.OffsetDateTime.class)
            );
        }
    };

    public List<UserProfile> findAll() {
        try {
            List<UserProfile> profiles = jdbc.query(
                "SELECT id, user_id, first_name, last_name, middle_name, birth_date, gender, phone, email, " +
                "position, department_id, hire_date, employment_type, education, employee_number, created_at, updated_at " +
                "FROM user_profile ORDER BY last_name, first_name", 
                mapper
            );
            log.debug("Found {} user profiles", profiles.size());
            return profiles;
        } catch (Exception e) {
            log.error("Failed to find all user profiles", e);
            throw e;
        }
    }

    public UserProfile findById(UUID id) {
        try {
            UserProfile profile = jdbc.queryForObject(
                "SELECT id, user_id, first_name, last_name, middle_name, birth_date, gender, phone, email, " +
                "position, department_id, hire_date, employment_type, education, employee_number, created_at, updated_at " +
                "FROM user_profile WHERE id = ?", 
                mapper, id
            );
            log.debug("Found user profile: id={}, name={}", id, profile != null ? profile.getFullName() : "null");
            return profile;
        } catch (EmptyResultDataAccessException e) {
            log.debug("User profile not found: id={}", id);
            return null;
        } catch (Exception e) {
            log.error("Failed to find user profile: id={}", id, e);
            throw e;
        }
    }

    public UserProfile findByUserId(UUID userId) {
        try {
            UserProfile profile = jdbc.queryForObject(
                "SELECT id, user_id, first_name, last_name, middle_name, birth_date, gender, phone, email, " +
                "position, department_id, hire_date, employment_type, education, employee_number, created_at, updated_at " +
                "FROM user_profile WHERE user_id = ?", 
                mapper, userId
            );
            log.debug("Found user profile: userId={}, name={}", userId, profile != null ? profile.getFullName() : "null");
            return profile;
        } catch (EmptyResultDataAccessException e) {
            log.debug("User profile not found for userId: {}", userId);
            return null;
        } catch (Exception e) {
            log.error("Failed to find user profile: userId={}", userId, e);
            throw e;
        }
    }

    public UserProfile findByEmployeeNumber(String employeeNumber) {
        try {
            UserProfile profile = jdbc.queryForObject(
                "SELECT id, user_id, first_name, last_name, middle_name, birth_date, gender, phone, email, " +
                "position, department_id, hire_date, employment_type, education, employee_number, created_at, updated_at " +
                "FROM user_profile WHERE employee_number = ?", 
                mapper, employeeNumber
            );
            log.debug("Found user profile: employeeNumber={}, name={}", employeeNumber, profile != null ? profile.getFullName() : "null");
            return profile;
        } catch (EmptyResultDataAccessException e) {
            log.debug("User profile not found for employee number: {}", employeeNumber);
            return null;
        } catch (Exception e) {
            log.error("Failed to find user profile: employeeNumber={}", employeeNumber, e);
            throw e;
        }
    }

    public UUID create(UserProfile profile) {
        try {
            UUID id = UUID.randomUUID();
            String sql = "INSERT INTO user_profile (id, user_id, first_name, last_name, middle_name, birth_date, gender, " +
                        "phone, email, position, department_id, hire_date, employment_type, education, employee_number) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            jdbc.update(sql, 
                id,
                profile.getUserId(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getMiddleName(),
                profile.getBirthDate(),
                profile.getGender(),
                profile.getPhone(),
                profile.getEmail(),
                profile.getPosition(),
                profile.getDepartmentId(),
                profile.getHireDate(),
                profile.getEmploymentType(),
                profile.getEducation(),
                profile.getEmployeeNumber()
            );
            log.debug("Created user profile: id={}, name={}", id, profile.getFullName());
            return id;
        } catch (Exception e) {
            log.error("Failed to create user profile: name={}", profile.getFullName(), e);
            throw e;
        }
    }

    public void update(UUID id, UserProfile profile) throws DataAccessException {
        try {
            String sql = "UPDATE user_profile SET first_name=?, last_name=?, middle_name=?, birth_date=?, gender=?, " +
                        "phone=?, email=?, position=?, department_id=?, hire_date=?, employment_type=?, education=?, " +
                        "employee_number=?, updated_at=now() WHERE id=?";
            jdbc.update(sql, 
                profile.getFirstName(),
                profile.getLastName(),
                profile.getMiddleName(),
                profile.getBirthDate(),
                profile.getGender(),
                profile.getPhone(),
                profile.getEmail(),
                profile.getPosition(),
                profile.getDepartmentId(),
                profile.getHireDate(),
                profile.getEmploymentType(),
                profile.getEducation(),
                profile.getEmployeeNumber(),
                id
            );
            log.debug("Updated user profile: id={}, name={}", id, profile.getFullName());
        } catch (Exception e) {
            log.error("Failed to update user profile: id={}, name={}", id, profile.getFullName(), e);
            throw e;
        }
    }

    public void delete(UUID id) {
        try {
            jdbc.update("DELETE FROM user_profile WHERE id = ?", id);
            log.debug("Deleted user profile: id={}", id);
        } catch (Exception e) {
            log.error("Failed to delete user profile: id={}", id, e);
            throw e;
        }
    }
}
