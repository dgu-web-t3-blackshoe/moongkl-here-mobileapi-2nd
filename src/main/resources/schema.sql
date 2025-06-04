-- Create the logo_img_urls table
CREATE TABLE IF NOT EXISTS logo_img_urls (
    id BINARY(16) NOT NULL,
    s3Url VARCHAR(255) DEFAULT NULL,
    cloudfrontUrl VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create the enterprises table
CREATE TABLE IF NOT EXISTS enterprises (
    id BINARY(16) NOT NULL,
    name VARCHAR(50) NOT NULL,
    country VARCHAR(50) NOT NULL,
    manager_email VARCHAR(50) NOT NULL,
    logo_img_url_id BINARY(16), -- Foreign key to logo_img_urls
    PRIMARY KEY (id),
    UNIQUE INDEX idx_enterprises_logo_img_url_id (logo_img_url_id),
    CONSTRAINT fk_enterprises_logo_img_url_id FOREIGN KEY (logo_img_url_id)
    REFERENCES logo_img_urls (id)
    ON DELETE SET NULL
    ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
