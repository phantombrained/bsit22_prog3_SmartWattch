CREATE DATABASE IF NOT EXISTS smartwattch_db;
USE smartwattch_db;

CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    login_streak INT DEFAULT 1,
    electricity_rate DOUBLE DEFAULT 12.0,
    monthly_goal_kwh DOUBLE DEFAULT 0.0
);

CREATE TABLE IF NOT EXISTS appliances (
    app_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    appliance_name VARCHAR(100) NOT NULL,
    watts DOUBLE NOT NULL,
    hours_per_day DOUBLE NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);