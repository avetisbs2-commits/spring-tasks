# Spring Tasks

This project contains separate Spring Boot practice modules.

Each module is an independent Maven module and can be started separately.

The modules will grow from simple to harder topics:

```text
REST API
Spring Data JPA / Hibernate
PostgreSQL
validation
service layer
exceptions
DTOs
relationships
```

## Project Structure

```text
spring_tasks/
├── README.md
├── pom.xml
├── .gitignore
├── user-crud-rest/
│   ├── README.md
│   ├── pom.xml
│   ├── requests.http
│   └── src/
├── user-crud-mvc/
│   ├── README.md
│   ├── pom.xml
│   └── src/
├── product-validation-rest/
│   ├── README.md
│   ├── pom.xml
│   ├── requests.http
│   └── src/
└── job-basics/
    ├── README.md
    ├── pom.xml
    ├── requests.http
    └── src/
```

## Current Modules

```text
user-crud-rest   - simple CRUD for User: REST API (JSON) + Hibernate + PostgreSQL   (port 8080)
user-crud-mvc    - simple CRUD for User: Spring MVC + Thymeleaf pages + Hibernate + PostgreSQL   (port 8081)
product-validation-rest - product API: DTOs, validation, filtering, layers, HTTP responses (port 8082)
job-basics      - one-time jobs, job status, and a recurring scheduler example (port 8083)
```

The two user modules use the same `user_crud` database and `users` table. The product module uses a separate `product_catalog` database. The jobs module stores state in memory and needs no database.

## What You Need

- JDK 21 or newer
- Maven (or the Maven in IntelliJ IDEA)
- PostgreSQL installed and running on your computer

## Start A Module From IntelliJ

1. Open the `spring_tasks` folder as a Maven project.
2. Open the main class of the module (for example `UserCrudApplication`).
3. Click the green `Run` button.

## Start A Module From The Terminal

```bash
cd job-basics      # or another module
mvn spring-boot:run
```

## Run The Tests

From `spring_tasks`:

```bash
mvn test
```

Tests for student tasks are intentionally red until their TODOs are completed.

## Notes

- Each module has its own `README.md` with a step-by-step guide.
- The modules are separate and do not share code.
