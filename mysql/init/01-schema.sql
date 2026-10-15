CREATE TABLE IF NOT EXISTS news (
    uniquekey VARCHAR(64) NOT NULL,
    title VARCHAR(500),
    date DATETIME,
    category VARCHAR(50),
    author_name VARCHAR(100),
    url VARCHAR(500),
    thumbnail_pic_s VARCHAR(500),
    thumbnail_pic_s02 VARCHAR(500),
    thumbnail_pic_s03 VARCHAR(500),
    is_content INT,
    PRIMARY KEY (uniquekey),
    INDEX idx_news_date (date),
    INDEX idx_news_category (category),
    INDEX idx_news_author_name (author_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS analytics_result (
    result_key VARCHAR(64) NOT NULL,
    updated_at DATETIME,
    json LONGTEXT,
    PRIMARY KEY (result_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
