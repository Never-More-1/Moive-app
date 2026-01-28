# Movie App
The backend for the movie app: users, movie list, reviews, favorites section.
The project is implemented in Spring Boot using JWT authentication and PostgreSQL.

### Functionality
- User and Role Management
- JWT-Based Authentication and Authorization
- User Review Management
- Movie Management
- Adding and Removing Movies from Favorites
- Validating Business Logic and Entity Statuses
- API Documentation via Swagger

### Technology Stack
- Java 21
- Spring Boot
- Spring Security + JWT (jjwt)
- Spring Data JPA
- PostgreSQL
- Maven
- Swagger / OpenAPI

### Requirements
- JDK 21+
- Maven 3.8+
- PostgreSQL 14+
- Application Configuration

The application uses the following environment settings:

Variable	Assignment
| Variable | Assignment |
|------------|------------|
| `DB_URL` | JDBC database URL |
| `DB_USER` | PostgreSQL user |
| `DB_PASS`	| PostgreSQL Password |
| `JWT_SECRET` |	Secret key for JWT |
| `JWT_EXPIRATION_SECONDS` |	JWT lifetime in seconds |

Example of `application.properties`
```properties
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASS}
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.maximum-pool-size=10
jwt.expiration-seconds=${JWT_EXPIRATION}
jwt.secret=${JWT_SECRET}
```
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
# Launching the app

#### Via Maven
```
mvn clean spring-boot:run
```
#### Via JAR file
```
mvn clean package
java -jar target/moive-app.jar
```
#### Upon successful launch, the application will be available by default on port 8080.
# API Documentation (Swagger)
#### Swagger UI is automatically included with the project.
#### After launching the application, the documentation is available at:
```
http://localhost:8080/swagger-ui/index.html
```
Swagger allows you to:
- View all available endpoints,
- Inspect request and response models,
- Execute requests directly from the browser.
# Authentication and Authorization
The project uses JWT (JSON Web Token) for authentication and access control.

## Getting a JWT
## A JWT token is issued after successful user authentication.

Example:
```
POST /security/jwt
```
The response returns a JWT, which must be used to access secure endpoints.
## JWT Usage
The token is transmitted in the HTTP header of each request:
```
Authorization: Bearer <JWT_TOKEN>
```
##  The main entities of the system
User - System user
Movie - Application movies
Review - User movie reviews
Favorites - Movies added to the user's favorites
Security - User credentials (login, password, role)

## Project Architecture
The project is built according to a classic multi-tier architecture.

### Description of Layers
Controller - REST controllers, HTTP request processing
Service - Business logic and transactions
Repository - Database access (Spring Data JPA)
Model - JPA entities
Dto - Data transfer objects
Security - JWT, filters, Spring Security configuration
Exceptions - User exceptions and error handling

## Business Rules and Validations
### The following validations are implemented in the project:
- Username uniqueness;
- Check for review existence;
- Check for movie existence;
- Checking if a movie exists in your favorites.
## API Endpoints
### Authentication and authorization
*All authentication endpoints are available without authorization*
```
POST /security/jwt
```
- Description: Receipt of the JWT token
- Request body parameters: username, password
```
POST /security/registration
```
- Description: Registration of a new user
- Request body parameters: username, password, email, and other user data
### User (/user)
*Endpoints for user interaction*
```
GET /user/myself
```
- Description: Getting information about the currently authenticated user
- Required roles: USER, OPERATOR, ADMIN
```
PUT /user/{username}
```
- Description: Updating the profile of the user
- Required roles: MODERATOR, ADMIN
```
GET /user
```
- Description: Getting a list of all users
- Required roles: USER, MODERATOR, ADMIN
```
GET /user/{username}
```
- Description: Getting a user by username
- Required roles: USER, MODERATOR, ADMIN
```
PUT /user/{username}
```
- Description: User Update
- Required roles: USER, ADMIN
```
DELETE /user/{username}
```
- Description: Deleting a user
Required Roles: USER, MODERATOR, ADMIN
### Security (/security)
*All endpoints require the ADMIN role*
```
GET /security/{id}
```
- Description: Getting security record by ID
```
GET /security/role/{role}
```
- Description: Getting all users by role
```
POST /security/{username}
```
- Description: Assigning the MODERATOR role to a user

### Films (/films) 
*Endpoints for film interaction*
```
 GET /films
 ```
- Description: Get all movies
- Required roles: MODERATOR, ADMIN
```
POST /films
```
- Description: Add new movie
- Required roles: MODERATOR, ADMIN
```
GET /films/{filmTitle}
```
- Description: Find a movie by title
- Required roles: MODERATOR, ADMIN
```
DELETE /films/{filmTitle}
```
- Description: Delete movie
- Required roles: ADMIN

### Favorites (/favorites)
*Managing user's favorite movies*
```
 POST /favorites/{filmId}
 ```
- Description: Add movie to favorites
- Required roles: MODERATOR, ADMIN
```
DELETE /favorites/{filmId}
```
- Description: Remove movie from favorites
- Required roles: MODERATOR, ADMIN
```
GET /favorites/{username}
```
- Description: Get user's favorites
- Required roles: USER, MODERATOR, ADMIN
```
GET /favorites/count/{username}
```
- Description: Number of favorite films
- Required roles: USER, MODERATOR, ADMIN

### Reviews (/reviews)
*Movie review operations*
```
PUT /reviews/{filmTitle}
```
- Description: Update your movie review
- Required roles: USER, MODERATOR, ADMIN
```
DELETE /reviews/{filmTitle}
```
- Description: Delete movie review
- Required roles: USER, MODERATOR, ADMIN
```
POST /reviews
```
- Description: Create a new review
- Required roles: USER, MODERATOR, ADMIN
```
 GET /reviews/user/{username}
 ```
 - Description: Get user reviews
- Required roles: USER, MODERATOR, ADMIN
```
GET /reviews/myself
```
 - Description: Get my reviews
- Required roles: USER, MODERATOR, ADMIN
```
GET /reviews/film/{filmTitle}
```
 - Description: Get movie reviews
- Required roles: USER, MODERATOR, ADMIN
```
GET /reviews/count/{username}
```
 - Description: Get the number of user reviews
- Required roles: USER, MODERATOR, ADMIN

### Code Description

- 200 OK - The request was completed successfully
- 201 Created - The resource was created successfully
- 204 No Content - The request was completed, but the response body is missing
- 400 Bad Request - Invalid request parameters
- 401 Unauthorized - Authentication required
- 403 Forbidden - Access denied
- 404 Not Found - Resource not found
- 409 Conflict - Data conflict
- 500 Internal Server Error - Internal Server Error
## Possible development directions
- implementation of refresh tokens;
- pagination and filtering of data;
- containerization of the application using Docker Compose;
