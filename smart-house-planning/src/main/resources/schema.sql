-- Reference schema (JPA ddl-auto=update also creates tables)
-- Run manually if using MySQL without auto-ddl

CREATE DATABASE IF NOT EXISTS house_planning;
USE house_planning;

CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    reset_token VARCHAR(255),
    reset_token_expiry DATETIME
);

CREATE TABLE IF NOT EXISTS house_plans (
    plan_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    plan_name VARCHAR(100),
    plot_width DOUBLE NOT NULL,
    plot_height DOUBLE NOT NULL,
    floors INT DEFAULT 1,
    bedrooms INT NOT NULL,
    bathrooms INT NOT NULL,
    kitchen BOOLEAN DEFAULT TRUE,
    hall_required BOOLEAN DEFAULT TRUE,
    parking BOOLEAN DEFAULT FALSE,
    staircase BOOLEAN DEFAULT FALSE,
    balcony BOOLEAN DEFAULT FALSE,
    preferred_room_dim VARCHAR(50),
    generated_plan_data TEXT,
    total_built_up_area DOUBLE,
    free_area DOUBLE,
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS rooms (
    room_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    plan_id BIGINT NOT NULL,
    room_name VARCHAR(50) NOT NULL,
    width DOUBLE NOT NULL,
    height DOUBLE NOT NULL,
    x_position DOUBLE NOT NULL,
    y_position DOUBLE NOT NULL,
    has_door BOOLEAN DEFAULT TRUE,
    has_window BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (plan_id) REFERENCES house_plans(plan_id) ON DELETE CASCADE
);
