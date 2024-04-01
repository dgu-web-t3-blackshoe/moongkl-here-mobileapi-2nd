CREATE TABLE IF NOT EXISTS users (
                                     id BINARY(16) NOT NULL PRIMARY KEY,
    email VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100),
    gender VARCHAR(10),
    country VARCHAR(50),
    nickname VARCHAR(50),
    phone_number VARCHAR(100),
    role VARCHAR(20),
    provider VARCHAR(20),
    `created_at` DATETIME(6) NULL,
    `updated_at` DATETIME(6) NULL,
    status_message VARCHAR(100),
    like_count INT,
    favorite_count INT,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
