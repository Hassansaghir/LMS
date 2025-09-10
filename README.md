# Library Management System (LMS) - Final Version

## Overview
The **Library Management System (LMS)** is a Spring Boot-based application designed to manage library operations efficiently.  
It provides functionalities for managing books, students, courses, borrowing transactions, and email notifications.  

This final version integrates best practices for database management, transaction handling, and a clean microservice architecture.

---

## Features

### Core Features
- **Book Management**
  - Add, update, delete, and search books
  - Track book availability

- **Student Management**
  - Add, update, and delete student records
  - View student details

- **Course & Exam Management**
  - Add and manage courses
  - Manage exams and student submissions

- **Borrowing Transactions**
  - Record book borrow and return transactions
  - Automatic update of book availability

- **Email Notification Microservice**
  - Sends email notifications when books are borrowed
  - Logging of email-sending activity

---

### Additional Features
- Role-based access (Admin, Teacher, Student)
- Fully integrated with MySQL / SQL Server
- Transaction handling for critical operations
- REST APIs for integration with frontend or other services

---

## Technology Stack
- **Backend:** Java, Spring Boot
- **Database:** MySQL / SQL Server
- **Build Tool:** Maven
- **Email Service:** Spring Email
- **Version Control:** Git & GitHub

---

## Installation & Setup

1. **Clone the repository**
```bash
git clone https://github.com/YourUsername/LMS.git
cd LMS
Configure database

Create a MySQL / SQL Server database

Update application.properties or application.yml with your DB credentials

Build & Run

bash
Copy code
mvn clean install
mvn spring-boot:run
Access the application

Backend APIs available at: http://localhost:8080

Use Postman or a frontend app to test features

Project Structure
bash
Copy code
LMS/
│
├─ src/main/java/com/lms
│   ├─ controller/      # REST controllers
│   ├─ service/         # Service classes
│   ├─ repository/      # Spring Data JPA repositories
│   ├─ model/           # Entity classes
│   └─ dto/             # Data Transfer Objects
│
├─ src/main/resources/
│   ├─ application.properties
│   └─ static/
│
└─ pom.xml
API Documentation
API endpoints are available under /api/*

Swagger UI can be accessed at: http://localhost:9090/swagger-ui.html.
Email Service swagger:  http://localhost:9091/swagger-ui/index.html.
