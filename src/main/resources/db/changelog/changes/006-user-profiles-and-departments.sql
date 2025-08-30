--liquibase formatted sql

--changeset agrocrm:035-add-departments-table
-- Создание таблицы департаментов
CREATE TABLE department (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    manager_id UUID,
    parent_department_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_department_manager FOREIGN KEY (manager_id) REFERENCES app_user(id) ON DELETE SET NULL,
    CONSTRAINT fk_department_parent FOREIGN KEY (parent_department_id) REFERENCES department(id) ON DELETE SET NULL
);

-- Добавляем индекс для быстрого поиска по названию департамента
CREATE INDEX idx_department_name ON department(name);

--changeset agrocrm:036-add-user-profiles-table
-- Создание таблицы профилей пользователей
CREATE TABLE user_profile (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    middle_name VARCHAR(100),
    birth_date DATE NOT NULL,
    gender CHAR(1) NOT NULL CHECK (gender IN ('M', 'F')),
    phone VARCHAR(20),
    email VARCHAR(255),
    position VARCHAR(100) NOT NULL,
    department_id INT,
    hire_date DATE NOT NULL,
    employment_type VARCHAR(50) NOT NULL,
    education VARCHAR(255),
    employee_number VARCHAR(50) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_profile_user_id FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_profile_department_id FOREIGN KEY (department_id) REFERENCES department(id) ON DELETE SET NULL
);

-- Добавляем индексы для быстрого поиска
CREATE INDEX idx_user_profile_user_id ON user_profile(user_id);
CREATE INDEX idx_user_profile_department_id ON user_profile(department_id);
CREATE INDEX idx_user_profile_employee_number ON user_profile(employee_number);
CREATE INDEX idx_user_profile_email ON user_profile(email);

--changeset agrocrm:037-update-app-user-table
-- Удаляем столбцы full_name и department из таблицы app_user
ALTER TABLE app_user DROP COLUMN IF EXISTS full_name;
ALTER TABLE app_user DROP COLUMN IF EXISTS department;

--changeset agrocrm:038-add-default-departments
-- Добавляем базовые департаменты
INSERT INTO department (id, name, description) VALUES 
(1, 'Администрация', 'Руководство предприятия'),
(2, 'Агрономия', 'Агрономическая служба'),
(3, 'Механизация', 'Служба механизации и транспорта'),
(4, 'Бухгалтерия', 'Бухгалтерская служба'),
(5, 'Склад', 'Складское хозяйство'),
(6, 'IT отдел', 'Информационные технологии');

--changeset agrocrm:039-add-sample-user-profiles
-- Добавляем профили для существующих пользователей
INSERT INTO user_profile (user_id, first_name, last_name, middle_name, birth_date, gender, phone, email, position, department_id, hire_date, employment_type, education, employee_number) VALUES 
(
    (SELECT id FROM app_user WHERE username = 'admin'),
    'Администратор',
    'Системы',
    NULL,
    '1990-01-01',
    'M',
    '+7-999-123-45-67',
    'admin@agrocrm.com',
    'Системный администратор',
    6,
    '2024-01-01',
    'полная',
    'Высшее техническое',
    'EMP-001'
),
(
    (SELECT id FROM app_user WHERE username = 'agronom'),
    'Иван',
    'Петров',
    'Сергеевич',
    '1985-05-15',
    'M',
    '+7-999-234-56-78',
    'petrov@agrocrm.com',
    'Главный агроном',
    2,
    '2020-03-01',
    'полная',
    'Высшее агрономическое',
    'EMP-002'
),
(
    (SELECT id FROM app_user WHERE username = 'mechanic'),
    'Алексей',
    'Сидоров',
    'Петрович',
    '1988-08-20',
    'M',
    '+7-999-345-67-89',
    'sidorov@agrocrm.com',
    'Механик',
    3,
    '2019-06-15',
    'полная',
    'Среднее техническое',
    'EMP-003'
),
(
    (SELECT id FROM app_user WHERE username = 'driver1'),
    'Михаил',
    'Козлов',
    'Иванович',
    '1992-12-10',
    'M',
    '+7-999-456-78-90',
    'kozlov@agrocrm.com',
    'Водитель',
    3,
    '2021-02-01',
    'полная',
    'Среднее',
    'EMP-004'
),
(
    (SELECT id FROM app_user WHERE username = 'driver2'),
    'Дмитрий',
    'Новиков',
    'Александрович',
    '1990-07-25',
    'M',
    '+7-999-567-89-01',
    'novikov@agrocrm.com',
    'Водитель',
    3,
    '2021-03-15',
    'полная',
    'Среднее',
    'EMP-005'
),
(
    (SELECT id FROM app_user WHERE username = 'accountant'),
    'Елена',
    'Иванова',
    'Владимировна',
    '1987-11-05',
    'F',
    '+7-999-678-90-12',
    'ivanova@agrocrm.com',
    'Бухгалтер',
    4,
    '2018-09-01',
    'полная',
    'Высшее экономическое',
    'EMP-006'
),
(
    (SELECT id FROM app_user WHERE username = 'manager'),
    'Андрей',
    'Смирнов',
    'Николаевич',
    '1983-04-12',
    'M',
    '+7-999-789-01-23',
    'smirnov@agrocrm.com',
    'Менеджер',
    1,
    '2017-01-15',
    'полная',
    'Высшее экономическое',
    'EMP-007'
);

--changeset agrocrm:040-add-personal-documents-table
-- Создание таблицы персональных документов сотрудников
CREATE TABLE personal_document (
    id SERIAL PRIMARY KEY,
    profile_id UUID NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    document_number VARCHAR(100) NOT NULL,
    issue_date DATE NOT NULL,
    expiry_date DATE,
    issuing_authority VARCHAR(255) NOT NULL,
    document_scan TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_personal_document_profile_id FOREIGN KEY (profile_id) REFERENCES user_profile(id) ON DELETE CASCADE
);

-- Добавляем индексы для быстрого поиска персональных документов
CREATE INDEX idx_personal_document_profile_id ON personal_document(profile_id);
CREATE INDEX idx_personal_document_type ON personal_document(document_type);
CREATE INDEX idx_personal_document_number ON personal_document(document_number);
CREATE INDEX idx_personal_document_expiry_date ON personal_document(expiry_date);

--changeset agrocrm:041-add-sample-personal-documents
-- Добавляем примеры персональных документов для сотрудников
INSERT INTO personal_document (profile_id, document_type, document_number, issue_date, expiry_date, issuing_authority) VALUES 
(
    (SELECT id FROM user_profile WHERE employee_number = 'EMP-001'),
    'паспорт',
    '1234 567890',
    '2010-01-15',
    '2030-01-15',
    'УФМС России по Московской области'
),
(
    (SELECT id FROM user_profile WHERE employee_number = 'EMP-001'),
    'СНИЛС',
    '123-456-789 01',
    '2010-01-15',
    NULL,
    'ПФР'
),
(
    (SELECT id FROM user_profile WHERE employee_number = 'EMP-002'),
    'паспорт',
    '2345 678901',
    '2005-05-20',
    '2025-05-20',
    'УФМС России по Московской области'
),
(
    (SELECT id FROM user_profile WHERE employee_number = 'EMP-002'),
    'СНИЛС',
    '234-567-890 12',
    '2005-05-20',
    NULL,
    'ПФР'
),
(
    (SELECT id FROM user_profile WHERE employee_number = 'EMP-003'),
    'паспорт',
    '3456 789012',
    '2008-08-25',
    '2028-08-25',
    'УФМС России по Московской области'
),
(
    (SELECT id FROM user_profile WHERE employee_number = 'EMP-003'),
    'СНИЛС',
    '345-678-901 23',
    '2008-08-25',
    NULL,
    'ПФР'
),
(
    (SELECT id FROM user_profile WHERE employee_number = 'EMP-003'),
    'водительское удостоверение',
    '77 АА 123456',
    '2010-06-15',
    '2030-06-15',
    'ГИБДД МВД России'
),
(
    (SELECT id FROM user_profile WHERE employee_number = 'EMP-004'),
    'паспорт',
    '4567 890123',
    '2012-12-15',
    '2032-12-15',
    'УФМС России по Московской области'
),
(
    (SELECT id FROM user_profile WHERE employee_number = 'EMP-004'),
    'СНИЛС',
    '456-789-012 34',
    '2012-12-15',
    NULL,
    'ПФР'
),
(
    (SELECT id FROM user_profile WHERE employee_number = 'EMP-004'),
    'водительское удостоверение',
    '77 АА 234567',
    '2015-02-01',
    '2035-02-01',
    'ГИБДД МВД России'
),
(
    (SELECT id FROM user_profile WHERE employee_number = 'EMP-005'),
    'паспорт',
    '5678 901234',
    '2010-07-30',
    '2030-07-30',
    'УФМС России по Московской области'
),
(
    (SELECT id FROM user_profile WHERE employee_number = 'EMP-005'),
    'СНИЛС',
    '567-890-123 45',
    '2010-07-30',
    NULL,
    'ПФР'
),
(
    (SELECT id FROM user_profile WHERE employee_number = 'EMP-005'),
    'водительское удостоверение',
    '77 АА 345678',
    '2012-03-15',
    '2032-03-15',
    'ГИБДД МВД России'
),
(
    (SELECT id FROM user_profile WHERE employee_number = 'EMP-006'),
    'паспорт',
    '6789 012345',
    '2007-11-10',
    '2027-11-10',
    'УФМС России по Московской области'
),
(
    (SELECT id FROM user_profile WHERE employee_number = 'EMP-006'),
    'СНИЛС',
    '678-901-234 56',
    '2007-11-10',
    NULL,
    'ПФР'
),
(
    (SELECT id FROM user_profile WHERE employee_number = 'EMP-007'),
    'паспорт',
    '7890 123456',
    '2003-04-17',
    '2023-04-17',
    'УФМС России по Московской области'
),
(
    (SELECT id FROM user_profile WHERE employee_number = 'EMP-007'),
    'СНИЛС',
    '789-012-345 67',
    '2003-04-17',
    NULL,
    'ПФР'
);

--changeset agrocrm:042-add-company-documents-table
-- Создание таблицы общих документов предприятия
CREATE TABLE company_document (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    document_type VARCHAR(100) NOT NULL,
    file_path VARCHAR(500),
    file_size BIGINT,
    mime_type VARCHAR(100),
    status VARCHAR(50) NOT NULL DEFAULT 'draft',
    created_by UUID NOT NULL,
    assigned_to UUID,
    department_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at DATE,
    version INTEGER DEFAULT 1,
    CONSTRAINT fk_company_document_created_by FOREIGN KEY (created_by) REFERENCES app_user(id) ON DELETE RESTRICT,
    CONSTRAINT fk_company_document_assigned_to FOREIGN KEY (assigned_to) REFERENCES app_user(id) ON DELETE SET NULL,
    CONSTRAINT fk_company_document_department_id FOREIGN KEY (department_id) REFERENCES department(id) ON DELETE SET NULL
);

-- Добавляем индексы для быстрого поиска общих документов
CREATE INDEX idx_company_document_title ON company_document(title);
CREATE INDEX idx_company_document_type ON company_document(document_type);
CREATE INDEX idx_company_document_status ON company_document(status);
CREATE INDEX idx_company_document_created_by ON company_document(created_by);
CREATE INDEX idx_company_document_assigned_to ON company_document(assigned_to);
CREATE INDEX idx_company_document_department_id ON company_document(department_id);
CREATE INDEX idx_company_document_created_at ON company_document(created_at);
CREATE INDEX idx_company_document_expires_at ON company_document(expires_at);

--changeset agrocrm:043-add-company-document-history-table
-- Создание таблицы истории изменений общих документов
CREATE TABLE company_document_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    document_id UUID NOT NULL,
    action VARCHAR(50) NOT NULL,
    changed_by UUID NOT NULL,
    old_values JSONB,
    new_values JSONB,
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_company_document_history_document_id FOREIGN KEY (document_id) REFERENCES company_document(id) ON DELETE CASCADE,
    CONSTRAINT fk_company_document_history_changed_by FOREIGN KEY (changed_by) REFERENCES app_user(id) ON DELETE RESTRICT
);

-- Добавляем индексы для истории общих документов
CREATE INDEX idx_company_document_history_document_id ON company_document_history(document_id);
CREATE INDEX idx_company_document_history_changed_by ON company_document_history(changed_by);
CREATE INDEX idx_company_document_history_created_at ON company_document_history(created_at);

--changeset agrocrm:044-add-sample-company-documents
-- Добавляем примеры общих документов предприятия
INSERT INTO company_document (title, description, document_type, status, created_by, department_id, expires_at) VALUES 
(
    'Политика безопасности труда',
    'Основные правила безопасности при работе на предприятии',
    'policy',
    'active',
    (SELECT id FROM app_user WHERE username = 'admin'),
    1,
    '2025-12-31'
),
(
    'Инструкция по работе с техникой',
    'Техника безопасности при работе с сельскохозяйственной техникой',
    'instruction',
    'active',
    (SELECT id FROM app_user WHERE username = 'mechanic'),
    3,
    '2025-06-30'
),
(
    'План посевной кампании 2025',
    'Детальный план проведения посевных работ',
    'plan',
    'draft',
    (SELECT id FROM app_user WHERE username = 'agronom'),
    2,
    '2025-11-30'
),
(
    'Отчет о расходах за январь 2025',
    'Финансовый отчет за первый месяц года',
    'report',
    'archived',
    (SELECT id FROM app_user WHERE username = 'accountant'),
    4,
    '2026-01-31'
);

--changeset agrocrm:045-add-roles-system
-- Создание системы ролей
CREATE TABLE role (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Создание связующей таблицы пользователь-роль
CREATE TABLE user_role (
    id SERIAL PRIMARY KEY,
    user_id UUID NOT NULL,
    role_id INT NOT NULL,
    assigned_by UUID,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_role_user_id FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_role_role_id FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_role_assigned_by FOREIGN KEY (assigned_by) REFERENCES app_user(id) ON DELETE SET NULL,
    CONSTRAINT uk_user_role UNIQUE (user_id, role_id)
);

-- Добавляем индексы для системы ролей
CREATE INDEX idx_role_name ON role(name);
CREATE INDEX idx_user_role_user_id ON user_role(user_id);
CREATE INDEX idx_user_role_role_id ON user_role(role_id);

--changeset agrocrm:046-add-default-roles
-- Добавляем базовые роли
INSERT INTO role (id, name, description) VALUES 
(1, 'ADMIN', 'Системный администратор - полный доступ ко всем функциям'),
(2, 'MANAGER', 'Менеджер - управление персоналом и документами'),
(3, 'AGRONOM', 'Агроном - управление полями и агрономическими процессами'),
(4, 'MECHANIC', 'Механик - управление техникой и обслуживанием'),
(5, 'DRIVER', 'Водитель - управление транспортом и путевыми листами'),
(6, 'ACCOUNTANT', 'Бухгалтер - управление финансами и отчетностью'),
(7, 'WAREHOUSE', 'Кладовщик - управление складом и материалами'),
(8, 'VIEWER', 'Просмотрщик - только просмотр данных');

--changeset agrocrm:047-migrate-existing-roles
-- Миграция существующих ролей в новую систему
INSERT INTO user_role (user_id, role_id, assigned_at) VALUES 
-- admin получает роль ADMIN
((SELECT id FROM app_user WHERE username = 'admin'), 1, CURRENT_TIMESTAMP),
-- agronom получает роль AGRONOM
((SELECT id FROM app_user WHERE username = 'agronom'), 3, CURRENT_TIMESTAMP),
-- mechanic получает роль MECHANIC
((SELECT id FROM app_user WHERE username = 'mechanic'), 4, CURRENT_TIMESTAMP),
-- driver1 получает роль DRIVER
((SELECT id FROM app_user WHERE username = 'driver1'), 5, CURRENT_TIMESTAMP),
-- driver2 получает роль DRIVER
((SELECT id FROM app_user WHERE username = 'driver2'), 5, CURRENT_TIMESTAMP),
-- accountant получает роль ACCOUNTANT
((SELECT id FROM app_user WHERE username = 'accountant'), 6, CURRENT_TIMESTAMP),
-- manager получает роль MANAGER
((SELECT id FROM app_user WHERE username = 'manager'), 2, CURRENT_TIMESTAMP);

-- Добавляем дополнительные роли для некоторых пользователей
INSERT INTO user_role (user_id, role_id, assigned_at) VALUES 
-- admin также получает роль MANAGER
((SELECT id FROM app_user WHERE username = 'admin'), 2, CURRENT_TIMESTAMP),
-- agronom также получает роль VIEWER
((SELECT id FROM app_user WHERE username = 'agronom'), 8, CURRENT_TIMESTAMP),
-- mechanic также получает роль DRIVER
((SELECT id FROM app_user WHERE username = 'mechanic'), 5, CURRENT_TIMESTAMP),
-- manager также получает роль VIEWER
((SELECT id FROM app_user WHERE username = 'manager'), 8, CURRENT_TIMESTAMP);

--changeset agrocrm:048-remove-old-role-column
-- Удаляем старый столбец role из таблицы app_user
ALTER TABLE app_user DROP COLUMN IF EXISTS role;
