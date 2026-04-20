CREATE TABLE IF NOT EXISTS category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS master (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    gender VARCHAR(20),
    birth_date DATETIME,
    introduction TEXT,
    achievements TEXT,
    image VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS heritage (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    description TEXT,
    history TEXT,
    process TEXT,
    materials TEXT,
    images VARCHAR(1000),
    video_url VARCHAR(500),
    level INT,
    create_time DATETIME,
    update_time DATETIME,
    category_id BIGINT,
    master_id BIGINT
);

CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100),
    phone VARCHAR(30) NOT NULL,
    password VARCHAR(255),
    avatar VARCHAR(500),
    role VARCHAR(20),
    status INT,
    verification_code VARCHAR(20),
    code_expire_time DATETIME,
    create_time DATETIME,
    update_time DATETIME
);

CREATE TABLE IF NOT EXISTS community_post (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    images VARCHAR(1000),
    cover_image VARCHAR(500),
    author_id BIGINT NOT NULL,
    author_name VARCHAR(100) NOT NULL,
    tags VARCHAR(500),
    view_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    comment_count INT DEFAULT 0,
    status INT DEFAULT 1,
    create_time DATETIME,
    update_time DATETIME
);

CREATE TABLE IF NOT EXISTS community_post_like (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_post_user (post_id, user_id)
);

CREATE TABLE IF NOT EXISTS community_comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    user_name VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    parent_id BIGINT DEFAULT 0,
    like_count INT DEFAULT 0,
    status INT DEFAULT 0,
    create_time DATETIME,
    update_time DATETIME
);

CREATE TABLE IF NOT EXISTS activity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    summary VARCHAR(500),
    description TEXT,
    cover_image VARCHAR(500),
    images VARCHAR(1000),
    location VARCHAR(255),
    organizer VARCHAR(255),
    contact VARCHAR(255),
    type VARCHAR(100),
    start_time DATETIME,
    end_time DATETIME,
    capacity INT,
    signup_count INT DEFAULT 0,
    status INT DEFAULT 1,
    heritage_id BIGINT,
    create_time DATETIME,
    update_time DATETIME
);

CREATE TABLE IF NOT EXISTS activity_registration (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    activity_id BIGINT NOT NULL,
    user_id BIGINT,
    participant_name VARCHAR(100) NOT NULL,
    phone VARCHAR(30) NOT NULL,
    message VARCHAR(500),
    status INT DEFAULT 1,
    create_time DATETIME,
    update_time DATETIME
);
