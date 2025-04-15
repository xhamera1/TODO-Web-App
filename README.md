# TODO-Web-App

**Final project for the *Amazon Java Junior Developer* course.**  
A simple Spring Boot application for managing tasks (a personal "To-Do List").

## Table of Contents

1. [Overview](#overview)
2. [Features](#features)
3. [Tech Stack](#tech-stack)
4. [Technologies & Requirements](#technologies--requirements)
5. [Project Structure](#project-structure)
6. [Database](#database)
7. [Setup & Installation](#setup--installation)
8. [Running the Application](#running-the-application)
9. [Usage](#usage)
10. [License](#license)

---

## Overview

This application allows users to register, log in, create tasks, mark them as completed, and view their task history. It is intended as a final project demonstration for the Amazon Java Junior Developer course.

## Features

- User registration and login (Spring Security)
- Adding, editing, and deleting tasks
- Marking tasks as completed
- Viewing pending and completed tasks
- Simple statistics on total and completed tasks

## Tech Stack & Requirements

This project is built using a modern Java/Spring Boot stack with the following key components:

- **Backend**:  
  - **Java 21**  
  - **Spring Boot 3.4.4**, with essential dependencies:
    - spring-boot-starter-web
    - spring-boot-starter-thymeleaf
    - spring-boot-starter-data-jpa
    - spring-boot-starter-validation
    - spring-boot-starter-security
    - spring-boot-devtools
- **Build Tool**: Maven 
- **Database**: MySQL
- **Frontend**: Thymeleaf

## Project Structure

```plaintext
TODO-Web-App
├── .idea
├── .mvn
├── sql
│   └── schema.sql 
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.todo.rails.elite.starter.code
│   │   │       ├── config   
│   │   │       ├── controller
│   │   │       ├── exceptions
│   │   │       ├── model
│   │   │       ├── repository
│   │   │       └── service
│   │   └── resources
│   │       ├── static       
│   │       └── templates    
│   │           └── application.properties
│   └── test
│       └── java
│           └── com.todo.rails.elite.starter.code
│               └── ...
├── target                   
├── pom.xml                   
└── README.md                 
```

## Database

A sample SQL schema is located in `sql/schema.sql`.

By default, the application expects a MySQL database running locally (e.g., localhost:3306), configured in `src/main/resources/application.properties`.

Make sure you import or execute `schema.sql` on your local MySQL before running the application, to initialize the required tables.

## Setup & Installation

Clone the repository:

```bash
git clone https://github.com/xhamera1/TODO-Web-App.git
cd TODO-Web-App
```

Configure the database:

In `src/main/resources/application.properties`, set the proper username, password, and database URL for MySQL.

## Running the Application

### Option A: Using Maven

Run directly from Maven:

```bash
mvn spring-boot:run
```

Package and run as a JAR:

```bash
mvn clean package
java -jar target/starter.code-0.0.1-SNAPSHOT.jar
```

### Option B: From an IDE

Open the project, find the Application class in `com.todo.rails.elite.starter.code`, and run it directly as a Spring Boot application.

Once running, the application will be available at http://localhost:8080.

## Usage

**Registration**: Navigate to `/register`. Fill out the form with your username, email, and password.

**Login**: After successful registration, go to `/login`. You must log in using the username and password you just created.

**Manage Tasks**:
- Add Tasks: Go to `/tasks/add` and fill out the new task form.
- View Tasks: Visit `/tasks` to see all tasks.
- Mark as Completed: Mark tasks to indicate they are done.
- Delete Tasks: Remove a task if it is no longer needed.

**Dashboard**:
- On the home page (`/`), view a summary of pending tasks, completed tasks, and total tasks.

**Profile**: Access the `/profile` page to see user-specific info (number of tasks, etc.).

## License

This project is licensed under the MIT License. 

For detailed information, please refer to the [LICENSE](LICENSE.md) file.
