CREATE DATABASE chatapp;
USE chatapp;

CREATE TABLE messages (
    message_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50),
    message TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
