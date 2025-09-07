package com.example.social_ute.exception;

public enum ErrorCode {
    // Authentication & Authorization Errors (1xxx)
    USER_NOT_FOUND("User not found", 1001),
    INVALID_CREDENTIALS("Invalid email or password", 1002),
    TOKEN_EXPIRED("Token has expired", 1003),
    INVALID_TOKEN("Invalid or malformed token", 1004),
    TOKEN_BLACKLISTED("Token has been blacklisted", 1005),
    REFRESH_TOKEN_NOT_FOUND("Refresh token not found", 1006),
    INSUFFICIENT_PERMISSIONS("Insufficient permissions to perform this action", 1007),
    ACCOUNT_LOCKED("Account has been locked", 1008),
    ACCOUNT_DISABLED("Account has been disabled", 1009),
    EMAIL_NOT_VERIFIED("Email address has not been verified", 1010),

    // Validation Errors (2xxx)
    VALIDATION_ERROR("Validation error", 2000),
    USERNAME_VALIDATION("Username must be between 3 and 20 characters", 2001),
    PASSWORD_VALIDATION("Password must be between 8 and 16 characters", 2002),
    EMAIL_VALIDATION("Invalid email format", 2003),
    REQUIRED_FIELD_MISSING("Required field is missing", 2004),
    INVALID_DATE_FORMAT("Invalid date format", 2005),
    FILE_SIZE_EXCEEDED("File size exceeded maximum limit", 2006),
    INVALID_FILE_TYPE("Invalid file type", 2007),
    INVALID_PHONE_NUMBER("Invalid phone number format", 2008),

    // Resource Errors (3xxx)
    RESOURCE_NOT_FOUND("Requested resource not found", 3001),
    RESOURCE_ALREADY_EXISTS("Resource already exists", 3002),
    RESOURCE_ACCESS_DENIED("Access to resource denied", 3003),
    RESOURCE_LOCKED("Resource is locked", 3004),
    RESOURCE_QUOTA_EXCEEDED("Resource quota exceeded", 3005),

    // Business Logic Errors (4xxx)
    INVALID_OPERATION("Invalid operation", 4001),
    OPERATION_NOT_ALLOWED("Operation not allowed", 4002),
    DEPENDENCY_CONFLICT("Operation failed due to dependency conflict", 4003),
    BUSINESS_RULE_VIOLATION("Business rule violation", 4004),
    INVALID_STATE_TRANSITION("Invalid state transition", 4005),

    // Database Errors (5xxx)
    DATABASE_ERROR("Database error occurred", 5001),
    TRANSACTION_FAILED("Transaction failed", 5002),
    DEADLOCK_DETECTED("Deadlock detected", 5003),
    DATA_INTEGRITY_VIOLATION("Data integrity violation", 5004),
    CONNECTION_ERROR("Database connection error", 5005),

    // File Operations Errors (6xxx)
    FILE_NOT_FOUND("File not found", 6001),
    FILE_UPLOAD_FAILED("File upload failed", 6002),
    FILE_DOWNLOAD_FAILED("File download failed", 6003),
    INVALID_FILE_FORMAT("Invalid file format", 6004),
    FILE_PROCESSING_ERROR("Error processing file", 6005),

    // External Service Errors (7xxx)
    EXTERNAL_SERVICE_ERROR("External service error", 7001),
    SERVICE_UNAVAILABLE("Service temporarily unavailable", 7002),
    TIMEOUT_ERROR("Operation timed out", 7003),
    API_LIMIT_EXCEEDED("API rate limit exceeded", 7004),
    INTEGRATION_ERROR("Integration error", 7005),

    // Security Errors (8xxx)
    SECURITY_VIOLATION("Security violation detected", 8001),
    SUSPICIOUS_ACTIVITY("Suspicious activity detected", 8002),
    RATE_LIMIT_EXCEEDED("Rate limit exceeded", 8003),
    IP_BLOCKED("IP address has been blocked", 8004),
    CSRF_TOKEN_INVALID("Invalid CSRF token", 8005),

    // System Errors (9xxx)
    SYSTEM_ERROR("System error occurred", 9001),
    CONFIGURATION_ERROR("Configuration error", 9002),
    INITIALIZATION_ERROR("System initialization error", 9003),
    MEMORY_ERROR("Out of memory error", 9004),
    UNEXPECTED_ERROR("Unexpected error occurred", 9999);

    private final int code;
    private final String message;

    ErrorCode(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}