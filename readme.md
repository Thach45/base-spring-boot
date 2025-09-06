# Learn JWT - Spring Boot Application

A Spring Boot application for learning JWT (JSON Web Token) authentication.

## Environment Setup

1. Copy the environment template file:
   ```bash
   cp .env.example .env
   ```

2. Update the `.env` file with your actual configuration values:
   - `DB_URL`: Database connection URL
   - `DB_USERNAME`: Database username
   - `DB_PASSWORD`: Database password
   - `JWT_SECRET`: Secret key for JWT token signing
   - `JWT_EXPIRATION`: JWT token expiration time in milliseconds
   - `SERVER_PORT`: Server port (optional, defaults to 8080)

## Running the Application

```bash
./mvnw spring-boot:run
```

The application will start on the configured port (default: 8080).

## Security Note

- Never commit the `.env` file to version control
- The `.env.example` file serves as a template for other developers
- Update your `.env` file with production values when deploying
