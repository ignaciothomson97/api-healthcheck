CREATE TABLE api_status (
    id SERIAL PRIMARY KEY,
    api_name VARCHAR(255),
    status INTEGER,
    checked TIMESTAMP,
    is_up BOOLEAN,
    error_message TEXT
);