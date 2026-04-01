CREATE TABLE IF NOT EXISTS roles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL,
    description VARCHAR(255) NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_roles_code (code)
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NULL,
    full_name VARCHAR(255) NOT NULL,
    avatar_url VARCHAR(500) NULL,
    provider VARCHAR(20) NOT NULL,
    provider_id VARCHAR(255) NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_users_username (username),
    UNIQUE KEY uq_users_email (email)
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    KEY idx_user_roles_role_id (role_id),
    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role
        FOREIGN KEY (role_id) REFERENCES roles(id)
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS api_keys (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    key_hash VARCHAR(255) NOT NULL,
    key_prefix VARCHAR(20) NOT NULL,
    role VARCHAR(20) NOT NULL,
    last_used_at DATETIME NULL,
    expires_at DATETIME NULL,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_api_keys_hash (key_hash),
    KEY idx_api_keys_user_id (user_id),
    KEY idx_api_keys_project_id (project_id)
);

-- ======================================
-- project context
-- ======================================
CREATE TABLE IF NOT EXISTS projects (
    id BIGINT NOT NULL AUTO_INCREMENT,
    owner_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    KEY idx_projects_owner_id (owner_id),
    CONSTRAINT fk_projects_owner
        FOREIGN KEY (owner_id) REFERENCES users(id)
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS phases (
    id BIGINT NOT NULL AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT NULL,
    start_date DATE NULL,
    end_date DATE NULL,
    order_index INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    accumulated_rules LONGTEXT NULL,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    KEY idx_phases_project_id (project_id),
    KEY idx_phases_created_by (created_by),
    CONSTRAINT fk_phases_project
        FOREIGN KEY (project_id) REFERENCES projects(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_phases_created_by
        FOREIGN KEY (created_by) REFERENCES users(id)
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS project_members (
    id BIGINT NOT NULL AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role VARCHAR(20) NOT NULL,
    invited_by BIGINT NULL,
    joined_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_member (project_id, user_id),
    KEY idx_project_members_user_id (user_id),
    CONSTRAINT fk_project_members_project
        FOREIGN KEY (project_id) REFERENCES projects(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_project_members_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
);

ALTER TABLE api_keys
    ADD CONSTRAINT fk_api_keys_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE,
    ADD CONSTRAINT fk_api_keys_project
        FOREIGN KEY (project_id) REFERENCES projects(id)
        ON DELETE CASCADE;

-- ======================================
-- spec context
-- ======================================
CREATE TABLE IF NOT EXISTS specs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    phase_id BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    title VARCHAR(500) NOT NULL,
    raw_content LONGTEXT NULL,
    structured_spec_json LONGTEXT NULL,
    openapi_content LONGTEXT NULL,
    openapi_format VARCHAR(10) NULL,
    phase_context_snapshot LONGTEXT NULL,
    impact_analysis_json LONGTEXT NULL,
    parse_status VARCHAR(20) NOT NULL,
    parse_error TEXT NULL,
    gemini_tokens_used INT NULL,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    KEY idx_specs_phase_id (phase_id),
    KEY idx_specs_created_by (created_by),
    CONSTRAINT fk_specs_phase
        FOREIGN KEY (phase_id) REFERENCES phases(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_specs_created_by
        FOREIGN KEY (created_by) REFERENCES users(id)
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS api_modules (
    id BIGINT NOT NULL AUTO_INCREMENT,
    spec_id BIGINT NOT NULL,
    project_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    base_path VARCHAR(255) NULL,
    description TEXT NULL,
    order_index INT NOT NULL,
    PRIMARY KEY (id),
    KEY idx_api_modules_spec_id (spec_id),
    KEY idx_api_modules_project_id (project_id),
    CONSTRAINT fk_api_modules_spec
        FOREIGN KEY (spec_id) REFERENCES specs(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_api_modules_project
        FOREIGN KEY (project_id) REFERENCES projects(id)
        ON DELETE CASCADE
);

-- ======================================
-- testmanagement context
-- ======================================
CREATE TABLE IF NOT EXISTS test_cases (
    id BIGINT NOT NULL AUTO_INCREMENT,
    api_module_id BIGINT NOT NULL,
    phase_id BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    source VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    test_category VARCHAR(30) NOT NULL,
    replaced_by_id BIGINT NULL,
    name VARCHAR(500) NOT NULL,
    method VARCHAR(10) NOT NULL,
    endpoint VARCHAR(500) NOT NULL,
    auth_type VARCHAR(20) NOT NULL,
    request_params_json LONGTEXT NULL,
    path_variables_json LONGTEXT NULL,
    headers_json LONGTEXT NULL,
    request_body_json LONGTEXT NULL,
    file_attachments_json LONGTEXT NULL,
    db_seed_query LONGTEXT NULL,
    db_seed_params_json LONGTEXT NULL,
    db_teardown_query LONGTEXT NULL,
    db_teardown_params_json LONGTEXT NULL,
    expected_status INT NULL,
    expected_response_json LONGTEXT NULL,
    match_mode VARCHAR(20) NOT NULL,
    db_verify_query LONGTEXT NULL,
    db_verify_params_json LONGTEXT NULL,
    db_verify_expected LONGTEXT NULL,
    gemini_data_hints LONGTEXT NULL,
    test_rationale TEXT NULL,
    source_evidence TEXT NULL,
    failure_implication TEXT NULL,
    order_index INT NULL,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    KEY idx_test_cases_api_module_id (api_module_id),
    KEY idx_test_cases_phase_id (phase_id),
    KEY idx_test_cases_created_by (created_by),
    KEY idx_test_cases_replaced_by_id (replaced_by_id),
    CONSTRAINT fk_test_cases_api_module
        FOREIGN KEY (api_module_id) REFERENCES api_modules(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_test_cases_phase
        FOREIGN KEY (phase_id) REFERENCES phases(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_test_cases_created_by
        FOREIGN KEY (created_by) REFERENCES users(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_test_cases_replaced_by
        FOREIGN KEY (replaced_by_id) REFERENCES test_cases(id)
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS test_flows (
    id BIGINT NOT NULL AUTO_INCREMENT,
    phase_id BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    name VARCHAR(500) NOT NULL,
    status VARCHAR(20) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_test_flows_phase_id (phase_id),
    KEY idx_test_flows_created_by (created_by),
    CONSTRAINT fk_test_flows_phase
        FOREIGN KEY (phase_id) REFERENCES phases(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_test_flows_created_by
        FOREIGN KEY (created_by) REFERENCES users(id)
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS flow_steps (
    id BIGINT NOT NULL AUTO_INCREMENT,
    flow_id BIGINT NOT NULL,
    test_case_id BIGINT NOT NULL,
    step_order INT NOT NULL,
    override_headers_json LONGTEXT NULL,
    override_body_json LONGTEXT NULL,
    override_params_json LONGTEXT NULL,
    extract_field VARCHAR(255) NULL,
    inject_to_var VARCHAR(255) NULL,
    note TEXT NULL,
    on_failure_action VARCHAR(20) NOT NULL,
    PRIMARY KEY (id),
    KEY idx_flow_steps_flow_id (flow_id),
    KEY idx_flow_steps_test_case_id (test_case_id),
    CONSTRAINT fk_flow_steps_flow
        FOREIGN KEY (flow_id) REFERENCES test_flows(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_flow_steps_test_case
        FOREIGN KEY (test_case_id) REFERENCES test_cases(id)
        ON DELETE CASCADE
);

-- ======================================
-- environment context
-- ======================================
CREATE TABLE IF NOT EXISTS target_databases (
    id BIGINT NOT NULL AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    db_type VARCHAR(50) NOT NULL,
    host VARCHAR(255) NOT NULL,
    port INT NOT NULL,
    database_name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    password_enc VARCHAR(500) NOT NULL,
    status VARCHAR(20) NOT NULL,
    last_tested_at DATETIME NULL,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    KEY idx_target_databases_project_id (project_id),
    CONSTRAINT fk_target_databases_project
        FOREIGN KEY (project_id) REFERENCES projects(id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS test_data_profiles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    project_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    variables_json LONGTEXT NOT NULL,
    is_default TINYINT(1) NOT NULL,
    created_at DATETIME NULL,
    updated_at DATETIME NULL,
    PRIMARY KEY (id),
    KEY idx_test_data_profiles_project_id (project_id),
    CONSTRAINT fk_test_data_profiles_project
        FOREIGN KEY (project_id) REFERENCES projects(id)
        ON DELETE CASCADE
);

-- ======================================
-- execution context
-- ======================================
CREATE TABLE IF NOT EXISTS test_runs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    phase_id BIGINT NOT NULL,
    triggered_by BIGINT NOT NULL,
    db_connection_id BIGINT NOT NULL,
    test_data_profile_id BIGINT NULL,
    run_type VARCHAR(20) NOT NULL,
    ref_id BIGINT NULL,
    target_url VARCHAR(500) NOT NULL,
    profile_snapshot_json LONGTEXT NULL,
    idempotency_key VARCHAR(128) NULL,
    status VARCHAR(20) NOT NULL,
    total INT NOT NULL,
    passed INT NOT NULL,
    failed INT NOT NULL,
    skipped INT NOT NULL,
    started_at DATETIME NULL,
    finished_at DATETIME NULL,
    PRIMARY KEY (id),
    KEY idx_test_runs_phase_id (phase_id),
    KEY idx_test_runs_triggered_by (triggered_by),
    KEY idx_test_runs_db_connection_id (db_connection_id),
    KEY idx_test_runs_test_data_profile_id (test_data_profile_id),
    UNIQUE KEY uq_test_runs_idempotency_key (idempotency_key),
    CONSTRAINT fk_test_runs_phase
        FOREIGN KEY (phase_id) REFERENCES phases(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_test_runs_triggered_by
        FOREIGN KEY (triggered_by) REFERENCES users(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_test_runs_db_connection
        FOREIGN KEY (db_connection_id) REFERENCES target_databases(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_test_runs_test_data_profile
        FOREIGN KEY (test_data_profile_id) REFERENCES test_data_profiles(id)
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS test_results (
    id BIGINT NOT NULL AUTO_INCREMENT,
    test_run_id BIGINT NOT NULL,
    test_case_id BIGINT NOT NULL,
    step_order INT NULL,
    passed TINYINT(1) NOT NULL,
    execution_status VARCHAR(20) NOT NULL,
    actual_status INT NULL,
    actual_response_json LONGTEXT NULL,
    db_verify_actual LONGTEXT NULL,
    db_verify_passed TINYINT(1) NULL,
    extracted_vars_json LONGTEXT NULL,
    failure_reason TEXT NULL,
    error_detail TEXT NULL,
    duration_ms INT NULL,
    PRIMARY KEY (id),
    KEY idx_test_results_test_run_id (test_run_id),
    KEY idx_test_results_test_case_id (test_case_id),
    CONSTRAINT fk_test_results_test_run
        FOREIGN KEY (test_run_id) REFERENCES test_runs(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_test_results_test_case
        FOREIGN KEY (test_case_id) REFERENCES test_cases(id)
        ON DELETE CASCADE
);

-- Seed default system roles
INSERT INTO roles (code, description)
VALUES
    ('OWNER', 'Project owner role'),
    ('ADMIN', 'Project admin role'),
    ('DEVELOPER', 'Default developer role'),
    ('VIEWER', 'Read-only viewer role')
ON DUPLICATE KEY UPDATE
    description = VALUES(description);
