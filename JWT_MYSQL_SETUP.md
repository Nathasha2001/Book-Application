# Book Application - JWT & MySQL Integration

## Overview
This Spring Boot application has been enhanced with JWT (JSON Web Token) authentication and MySQL database integration.

## Features Implemented

### 1. JWT Authentication
- User registration and login endpoints
- JWT token generation and validation
- Stateless session management
- Token-based endpoint security

### 2. MySQL Database Integration
- Replaced H2 in-memory database with MySQL
- User entity for authentication
- Book entity with JPA mapping
- Automatic schema creation with Hibernate

## Database Setup

### Prerequisites
- MySQL Server 8.0 or higher installed and running
- Port 3306 accessible (default MySQL port)

### Create Database

```sql
CREATE DATABASE book_db;
```

Or modify `spring.datasource.url` in `application.properties` to use a different database name.

## Configuration Files

### application.properties
Located at `src/main/resources/application.properties`

```properties
# MySQL Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/book_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=Password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JWT Configuration
jwt.secret=your_secret_key_change_this_in_production_at_least_32_characters_long
jwt.expiration=86400000  # 24 hours in milliseconds
```

**Important**: Change the JWT secret in production to a secure key with at least 32 characters.

## API Endpoints

### Authentication Endpoints (No Authorization Required)

#### 1. Register User
- **URL**: `POST /auth/v1/register`
- **Body**:
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "secure_password"
}
```
- **Response**:
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "john_doe",
  "email": "john@example.com",
  "message": "User registered successfully"
}
```

#### 2. Login
- **URL**: `POST /auth/v1/login`
- **Body**:
```json
{
  "username": "john_doe",
  "password": "secure_password"
}
```
- **Response**:
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "john_doe",
  "email": "john@example.com",
  "message": "Login successful"
}
```

### Book Endpoints

#### 1. Get All Books (No Authorization Required)
- **URL**: `GET /book/v1/all`
- **Response**: List of all books

#### 2. Get Book by Name (No Authorization Required)
- **URL**: `GET /book/v1/getBook/{bookname}`
- **Response**: Book details

#### 3. Add Book (Authorization Required)
- **URL**: `POST /book/v1/addBook`
- **Header**: `Authorization: Bearer <JWT_TOKEN>`
- **Body**:
```json
{
  "title": "Spring Boot Guide",
  "author": "John Smith",
  "genre": "Technology"
}
```
- **Response**: Created book with ID

#### 4. Update Book (Authorization Required)
- **URL**: `PUT /book/v1/updateBook`
- **Header**: `Authorization: Bearer <JWT_TOKEN>`
- **Body**:
```json
{
  "id": 1,
  "title": "Spring Boot Guide",
  "author": "John Smith",
  "genre": "Technology"
}
```

#### 5. Delete Book (Authorization Required)
- **URL**: `DELETE /book/v1/deleteBook/{id}`
- **Header**: `Authorization: Bearer <JWT_TOKEN>`

## Using JWT Tokens

### How to Include JWT Token in Requests

When making requests to protected endpoints, include the token in the Authorization header:

```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

### Token Expiration

- Default expiration: 24 hours
- Configure via `jwt.expiration` in `application.properties`
- Time in milliseconds (86400000 = 24 hours)

## Project Structure

```
src/main/java/com/example/BookApplication/
├── Controller/
│   ├── AuthController.java        # Authentication endpoints
│   └── BookController.java        # Book endpoints
├── Entity/
│   ├── Book.java                 # Book entity
│   └── User.java                 # User entity for authentication
├── DTO/
│   ├── LoginRequest.java         # Login request DTO
│   ├── SignupRequest.java        # Registration request DTO
│   └── LoginResponse.java        # Authentication response DTO
├── Repository/
│   ├── BookRepository.java       # Book repository
│   └── UserRepository.java       # User repository
├── Service/
│   ├── AuthService.java          # Authentication service
│   ├── BookService.java          # Book service
│   └── CustomUserDetailsService.java  # User details service
├── Security/
│   ├── JwtTokenProvider.java     # JWT token generation/validation
│   ├── JwtAuthenticationFilter.java  # JWT authentication filter
│   └── SecurityConfig.java       # Spring Security configuration
└── BookApplication.java          # Main application class
```

## Building and Running

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- MySQL Server 8.0 or higher

### Build the Project
```bash
mvn clean build
```

### Run the Application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## Security Features

1. **Password Encryption**: Passwords are encrypted using BCrypt
2. **Stateless Authentication**: Using JWT tokens instead of sessions
3. **CORS Support**: Enabled for cross-origin requests
4. **Method-Level Security**: Using @PreAuthorize annotations
5. **Token Validation**: Every request is validated against JWT token expiration

## Testing the APIs

### Using cURL

#### Register
```bash
curl -X POST http://localhost:8080/auth/v1/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john_doe","email":"john@example.com","password":"password123"}'
```

#### Login
```bash
curl -X POST http://localhost:8080/auth/v1/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john_doe","password":"password123"}'
```

#### Add Book (with token)
```bash
curl -X POST http://localhost:8080/book/v1/addBook \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"title":"Spring Boot Guide","author":"John Smith","genre":"Technology"}'
```

## Database Schema

### users table
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    enabled BOOLEAN DEFAULT TRUE
);
```

### books table
```sql
CREATE TABLE books (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255),
    author VARCHAR(255),
    genre VARCHAR(255)
);
```

## Troubleshooting

### Connection Refused
- Ensure MySQL server is running
- Check if port 3306 is accessible
- Verify credentials in application.properties

### JWT Token Invalid
- Check if token is expired
- Ensure token is included in Authorization header with "Bearer " prefix
- Verify JWT secret matches configuration

### User Already Exists
- Username or email must be unique
- Try with different credentials

## Future Enhancements

- Role-based access control (RBAC)
- Token refresh mechanism
- Rate limiting
- Audit logging
- API documentation with Swagger/OpenAPI
