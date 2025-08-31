package com.agrocrm.domain.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Профиль пользователя/сотрудника")
public class UserProfile {
    
    @Schema(description = "Уникальный идентификатор профиля")
    private UUID id;
    
    @Schema(description = "ID пользователя")
    @NotNull(message = "ID пользователя обязателен")
    private UUID userId;
    
    @Schema(description = "Имя", example = "Иван")
    @NotBlank(message = "Имя обязательно")
    @Size(max = 100, message = "Имя не может быть длиннее 100 символов")
    private String firstName;
    
    @Schema(description = "Фамилия", example = "Петров")
    @NotBlank(message = "Фамилия обязательна")
    @Size(max = 100, message = "Фамилия не может быть длиннее 100 символов")
    private String lastName;
    
    @Schema(description = "Отчество", example = "Сергеевич")
    @Size(max = 100, message = "Отчество не может быть длиннее 100 символов")
    private String middleName;
    
    @Schema(description = "Дата рождения", example = "1985-05-15")
    @NotNull(message = "Дата рождения обязательна")
    private LocalDate birthDate;
    
    @Schema(description = "Пол (M/F)", example = "M")
    @NotBlank(message = "Пол обязателен")
    @Pattern(regexp = "^[MF]$", message = "Пол должен быть M или F")
    private String gender;
    
    @Schema(description = "Контактный телефон", example = "+7-999-123-45-67")
    @Size(max = 20, message = "Телефон не может быть длиннее 20 символов")
    private String phone;
    
    @Schema(description = "Электронная почта", example = "ivan.petrov@agrocrm.com")
    @Size(max = 255, message = "Email не может быть длиннее 255 символов")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Некорректный формат email")
    private String email;
    
    @Schema(description = "Должность", example = "Главный агроном")
    @NotBlank(message = "Должность обязательна")
    @Size(max = 100, message = "Должность не может быть длиннее 100 символов")
    private String position;
    
    @Schema(description = "ID департамента")
    private Integer departmentId;
    
    @Schema(description = "Дата приема на работу", example = "2020-03-01")
    @NotNull(message = "Дата приема на работу обязательна")
    private LocalDate hireDate;
    
    @Schema(description = "Тип занятости", example = "полная")
    @NotBlank(message = "Тип занятости обязателен")
    @Size(max = 50, message = "Тип занятости не может быть длиннее 50 символов")
    private String employmentType;
    
    @Schema(description = "Образование", example = "Высшее агрономическое")
    @Size(max = 255, message = "Образование не может быть длиннее 255 символов")
    private String education;
    
    @Schema(description = "Табельный номер", example = "EMP-001")
    @NotBlank(message = "Табельный номер обязателен")
    @Size(max = 50, message = "Табельный номер не может быть длиннее 50 символов")
    private String employeeNumber;
    
    @Schema(description = "Дата создания")
    private OffsetDateTime createdAt;
    
    @Schema(description = "Дата последнего обновления")
    private OffsetDateTime updatedAt;
    
    public UserProfile() {}
    
    public UserProfile(UUID id, UUID userId, String firstName, String lastName, String middleName,
                      LocalDate birthDate, String gender, String phone, String email, String position,
                      Integer departmentId, LocalDate hireDate, String employmentType, String education,
                      String employeeNumber, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.position = position;
        this.departmentId = departmentId;
        this.hireDate = hireDate;
        this.employmentType = employmentType;
        this.education = education;
        this.employeeNumber = employeeNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }
    
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    
    public Integer getDepartmentId() { return departmentId; }
    public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }
    
    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
    
    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }
    
    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }
    
    public String getEmployeeNumber() { return employeeNumber; }
    public void setEmployeeNumber(String employeeNumber) { this.employeeNumber = employeeNumber; }
    
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Вспомогательные методы
    public String getFullName() {
        StringBuilder fullName = new StringBuilder(lastName);
        if (firstName != null && !firstName.isEmpty()) {
            fullName.append(" ").append(firstName);
        }
        if (middleName != null && !middleName.isEmpty()) {
            fullName.append(" ").append(middleName);
        }
        return fullName.toString();
    }
}

