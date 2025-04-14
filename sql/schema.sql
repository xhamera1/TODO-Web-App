-- Drop the database if it exists
DROP DATABASE IF EXISTS todorails;

-- Create the todorails schema
CREATE DATABASE todorails;

-- Select the todorails schema for use
USE todorails;

-- Create User table
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       roles VARCHAR(255) NOT NULL
);

-- Create Task table
CREATE TABLE tasks (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       title VARCHAR(255) NOT NULL UNIQUE,
                       description TEXT NOT NULL,
                       completed BOOLEAN NOT NULL,
                       due_date DATE NOT NULL
);

CREATE INDEX idx_task_completed ON tasks(completed);
CREATE INDEX idx_task_due_date ON tasks(due_date);
CREATE INDEX idx_user_username ON users(username); -- Optional: Index for username lookup
CREATE INDEX idx_user_email ON users(email); -- Optional: Index for email lookup

