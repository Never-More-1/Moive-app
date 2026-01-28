#Movie App
The backend for the movie app: users, movie list, reviews, favorites section.
The project is implemented in Spring Boot using JWT authentication and PostgreSQL.

Functionality
User and Role Management
JWT-Based Authentication and Authorization
User Review Management
Movie Management
Adding and Removing Movies from Favorites
Validating Business Logic and Entity Statuses
API Documentation via Swagger

Technology Stack
Java 21
Spring Boot
Spring Security + JWT (jjwt)
Spring Data JPA
PostgreSQL
Maven
Swagger / OpenAPI

Requirements
JDK 21+
Maven 3.8+
PostgreSQL 14+
Application Configuration

The application uses the following environment settings:
Variable	Assignment
DB_URL	JDBC database URL
DB_USER	PostgreSQL user
DB_PASS	PostgreSQL Password
JWT_SECRET	Secret key for JWT
JWT_EXPIRATION_SECONDS	JWT lifetime in seconds

Example of application.properties
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASS}
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.maximum-pool-size=10
jwt.expiration-seconds=${JWT_EXPIRATION}
jwt.secret=${JWT_SECRET}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
Launching the app

Via Maven
mvn clean spring-boot:run

Via JAR file
mvn clean package
java -jar target/moive-app.jar
