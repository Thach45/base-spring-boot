package com.example.aloute.exception;

public enum ErrorCode {
    USER_NOT_FOUND("User not found", 1001),
    UNEXPECTED_ERROR("Unexpected error", 9999),
    USERNAME_VALIDATION("Username must be between 3 and 20 characters", 2001),
    PASSWORD_VALIDATION("Password must be between 8 and 16 characters", 2002)
    ;
    private int code;
    private String message;
    public int getCode() {
        return code;
    }

    ErrorCode(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }


}
