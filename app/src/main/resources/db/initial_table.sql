CREATE TABLE api_status (
    api_name VARCHAR(255) PRIMARY KEY,
    last_status INTEGER,
    last_check TIMESTAMP,
    is_up BOOLEAN,
    last_error_message TEXT
);