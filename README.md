# Let's Play Project
## Table of Contents
1. **[Objectives](#objectives)**
2. **[Features](#features)**
3. **[Requirements](#requirements)**
4. **[Installation and Setup](#installation-and-setup)**
5. **[API Documentation](#api-documentation)**
6. **[Authentication and Authorization](#authentication-and-authorization)**
7. **[Security Measures](#security-measures)**
8. **[Testing the application](#testing-the-application)**
9. **[Code Quality and Standards](#code-quality-and-standards)**
10. **[Bonus Features](#bonus-features)**
11. **[Credit](#credit)**
12. **[License](#license)**
13. **[Annex](#annex)**

## Objectives
This project aims to develop a **CRUD (Create, Update, Read, Delete) API** using **Spring Boot** and **MongoDB**. The application provides user management and product management functionalities while adhering to RESTful principes. It also includes token-based authentication, rol-based access control, and robust security measures.

## Features
### Core Features
- **CRUD Operations**:
  * Create, read, update, and delete users
  * Create, read, update, and delete products
- **Relationships**:
  * A user can own multiple products(`User` ↔ `Product`)
- **Public Access**:
  * The `GET /products` endpoint is accessible without authentication
- **Token-based Authentication**:
  * Users can register and log in to obtain a JWT token
  * The token is required for accessing protected endpoints
- **Role-based Access Control**:
  * Admin users can manage all resources
  * Regular users have restricted access

### Security Features
- Passwords are hashed and salted before storage
- Input validation to prevent MongoDB injection attacks
- Sensitive information (e.g. passwords) is not exposed in API responses
- HTTPS is enabled for secure data transmission

### Bonus
- **CORS Policies**:
  * Appropriate Cross-Origin Resource Sharing (CORS) policies are implemented.
- **Rate Limiting**:
  * Rate limiting is applied to prevent abuse of the API.

## Requirements
### Prerequisites
- Java 17 or higher
- Maven 3.x
- MongoDB (local or cloud instance)
- IDE (e.g., IntelliJ, Eclipse, or VSCode with Java extension)
- Postman or any API testing tool

### Dependencies
- Spring Boot 3.x
- Spring Data MongoDB
- Spring Security
- Validation
- Lombok (optional for reducing boilerplate code)
- JWT (Java Web Token)
- Springdoc OpenAPI (for API documentation)
- Spring Boot DevTools (for development convenience)

## Installation and Setup
### Step 1: Clone the Repository
1. Ensure you have Git installed on your machine.
2. Open a terminal and run the following command to clone the repository:
    ```bash
    git clone https://github.com/pro12x/lets-play.git
    cd lets-play
    ```

### Step 2: Configure Environment Variables
1. Duplicate the `.env.example` file and rename it to `.env`.
2. Update the `.env` file with your variable values:

### Step 3: Configure MongoDB
1. Install and Start MongoDB on your machine or use a cloud instance (e.g., MongoDB Atlas).
2. Update the MongoDB connection URI in `application.properties` or `application.yml` file:
    ```properties
    spring.data.mongodb.uri=mongodb://localhost:27017/mydb
    ```

### Step 4: Build the Project
1. Ensure you have Maven installed and configured.
2. Open a terminal in the project root directory.
3. Run the following command to build the project:
    ```bash
    mvn clean install
    ```
### Step 5: Run the Application
1. Start the application using Maven:
    ```bash
    mvn spring-boot:run
    ```
2. The application will start on port 8080 by default. You can change this in the `application.properties` or `application.yml` file:
    ```properties
    server.port=8080
    ```
3. Open your web browser and navigate to `http://localhost:8080` to access the API.

### Step 6: Access Swagger UI
To view the API documentation and test endpoints interactively, open:
```plaintext
http://localhost:8080/swagger-ui.html
```

## API Documentation
### Authentication Endpoints
| METHOD | ENDPOINT             | DESCRIPTION                                | AUTH REQUIRED |
|--------|----------------------|--------------------------------------------|---------------|
| POST   | `/api/auth/register` | Register a new user                        | No            |
| POST   | `/api/auth/login`    | Authenticate a user and return a JWT token | No            |

### User Endpoints
| METHOD | ENDPOINT             | DESCRIPTION                                | AUTH REQUIRED |
|--------|----------------------|--------------------------------------------|---------------|
| GET    | `/api/users`         | Get all users                              | Yes           |
| GET    | `/api/users/{id}`    | Get a user by ID                           | Yes           |
| POST   | `/api/users`         | Create a new user                          | Yes           |
| PUT    | `/api/users/{id}`    | Update a user by ID                        | Yes           |
| DELETE | `/api/users/{id}`    | Delete a user by ID                        | Yes           |

### Product Endpoints
| METHOD | ENDPOINT             | DESCRIPTION            | AUTH REQUIRED |
|--------|----------------------|------------------------|---------------|
| GET    | `/api/products`      | Get all products       | No            |
| GET    | `/api/products/{id}` | Get a product by ID    | Yes           |
| POST   | `/api/products`      | Create a new product   | Yes           |
| PUT    | `/api/products/{id}` | Update a product by ID | Yes           |
| DELETE | `/api/products/{id}` | Delete a product by ID | Yes           |

## Authentication and Authorization
### Token-based Authentication
1. Authenticate using the `/api/auth/login` endpoint to receive a JWT token.
2. Include the token in the `Authorization` header for all protected endpoints:
    ```plaintext
    Authorization: Bearer <token>
    ```

### Role-based Access Control
- **ADMIN**:
  * Full access to all endpoints
- **USER**:
  * Restricted access (e.g., can only manage their own products).

## Security Measures
1. **Password Hashing**:
   - Passwords are hashed and salted using BCrypt before storage.
2. **Input Validation**:
   - Annotations like `@NotBlank`, `@Size`, and `@Email` are used for input validation.
3. **Sensitive Information Protection**:
   - Passwords and other sensitive data are not exposed in API responses.
4. **HTTPS**:
   - HTTPS is enabled to encrypt data in transit.

## Testing the application
### Functional Testing
1. Use `Postman`, `cURL`, or any API testing tool to test the endpoints.
2. Verify that all CRUD operations work as expected.
3. Test authentication and role-based access control.

### Error Handling
- Ensure the application handles exceptions gracefully and returns appropriate HTTP response codes. (e.g., `400 for bad requests`, `Unauthorized`)

### Public Access
- Verify that the `GET /products` endpoint is accessible without authentication.

## Code Quality and Standards
### Annotations
- **Data Classes**:
  * `@Document`, `@Id`, `@Field` are used correctly in MongoDB models.
- **Controllers**:
  * `@RestController`, `@RequestMapping`, `@PostMapping`, `@PutMapping`, `@GetMapping` and `@DeleteMapping` are used appropriately.
- **Security**:
  * `@EnableWebSecurity`, `@PreAuthorize` are used for authencication and authorization.
### Validation
- Input validation is implemented using annotations like `@Size`, `@NotBlank`, and `@Email`.

## Bonus Features
### CORS Policies
- CORS policies are configured in `SecurityConfig` to allow specific domains

### Rate Limiting
- Rate limiting is implemented manually in the application to prevent abuse of the API.

## Credit
This project was developed by **[Franchis Janel MOKOMBA](https://github.com/pro12x)** as part of a learning exercise in building a RESTful API using Spring Boot and MongoDB.

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Annex
### Generate the SSL Certificate
1. To generate a self-signed SSL certificate for local development, you can use the following command:
    ```bash
    keytool -genkeypair -alias lets-play -keyalg RSA -keystore keystore.p12 -storetype PKCS12 -storepass yourpassword -validity 365
    ```
2. Move the generated `keystore.p12` file to the `src/main/resources` directory of your project.
