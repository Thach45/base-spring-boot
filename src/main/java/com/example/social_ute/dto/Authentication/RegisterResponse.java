package com.example.social_ute.dto.Authentication;

import com.example.social_ute.enums.UserStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterResponse {
    private String id;
    private String email;
    private String fullName;
    private UserStatus status;
    private String message;
}