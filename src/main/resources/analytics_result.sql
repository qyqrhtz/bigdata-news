-- Run once (since spring.jpa.hibernate.ddl-auto=none)
CREATE TABLE IF NOT EXISTS analytics_result (
  result_key VARCHAR(64) PRIMARY KEY,
  updated_at DATETIME NULL,
  json LONGTEXT NULL
);

