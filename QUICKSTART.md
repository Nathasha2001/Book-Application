# Setup Instructions for JWT & MySQL Integration

## Prerequisites
- MySQL Server 8.0+ installed and running
- Java 17+ installed
- Port 3306 accessible

## Step 1: Create MySQL Database

Open MySQL Command Line or MySQL Client and run:

```sql
CREATE DATABASE book_db;
```

## Step 2: Update Configuration (Optional)

If you want to use different credentials, edit [src/main/resources/application.properties](src/main/resources/application.properties):

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/book_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=Password
```

## Step 3: Build the Project

```bash
./mvnw clean package -DskipTests
```

Or on Windows:
```cmd
mvnw.cmd clean package -DskipTests
```

## Step 4: Run the Application

```bash
./mvnw spring-boot:run
```

Or from the JAR file:
```bash
java -jar target/BookApplication-0.0.1-SNAPSHOT.jar
```

The application will be available at: `http://localhost:8080`

## Quick API Test

### 1. Register a User

```bash
curl -X POST http://localhost:8080/auth/v1/register \
  -H "Content-Type: application/json" \
  -d '{
    "username":"john_doe",
    "email":"john@example.com",
    "password":"password123"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "john_doe",
  "email": "john@example.com",
  "message": "User registered successfully"
}
```

### 2. Login

```bash
curl -X POST http://localhost:8080/auth/v1/login \
  -H "Content-Type: application/json" \
  -d '{
    "username":"john_doe",
    "password":"password123"
  }'
```

### 3. Add Book (Protected - Requires Token)

Replace `YOUR_JWT_TOKEN` with the token from login/register response:

```bash
curl -X POST http://localhost:8080/book/v1/addBook \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "title":"Spring Boot Guide",
    "author":"John Smith",
    "genre":"Technology"
  }'
```

### 4. Get All Books (Public)

```bash
curl http://localhost:8080/book/v1/all
```

### 5. Update Book (Protected)

```bash
curl -X PUT http://localhost:8080/book/v1/updateBook \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "id":1,
    "title":"Advanced Spring Boot",
    "author":"Jane Doe",
    "genre":"Technology"
  }'
```

### 6. Delete Book (Protected)

```bash
curl -X DELETE http://localhost:8080/book/v1/deleteBook/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Features Implemented

✅ **JWT Authentication**
- User registration with email validation
- Login with JWT token generation
- Token-based stateless authentication
- 24-hour token expiration (configurable)

✅ **MySQL Database**
- User entity with authentication details
- Book entity with full CRUD operations
- Automatic schema creation with Hibernate

✅ **Security**
- Password encryption with BCrypt
- Role-based access control (USER role)
- Protected endpoints requiring authentication
- Public endpoints for reading books

✅ **API Documentation**
- See [JWT_MYSQL_SETUP.md](JWT_MYSQL_SETUP.md) for full API documentation

## Troubleshooting

### Connection refused error
- Ensure MySQL is running: `sudo systemctl status mysql` (Linux/Mac) or check MySQLServices (Windows)
- Verify credentials in application.properties
- Ensure database `book_db` exists

### "User not found" during login
- Make sure you're using the correct username (not email)
- Verify the user was registered successfully

### JWT Token invalid/expired
- Check if token is included in Authorization header with "Bearer " prefix
- Verify token hasn't expired (default 24 hours)
- Make sure you're copying the full token from login response

## Database Verification

Connect to MySQL and verify tables were created:

```sql
USE book_db;
SHOW TABLES;
DESC users;
DESC books;
```

## Security Notes

- **Change JWT Secret**: Update `jwt.secret` in application.properties for production (use 32+ characters)
- **Change DB Password**: Don't use 'Password' in production
- **Enable HTTPS**: Use HTTPS in production environments
- **Token Expiration**: Adjust `jwt.expiration` if needed (milliseconds)

---

For detailed API documentation, see [JWT_MYSQL_SETUP.md](JWT_MYSQL_SETUP.md)
