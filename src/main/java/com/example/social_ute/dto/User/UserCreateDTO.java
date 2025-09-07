package com.example.social_ute.dto.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateDTO {
    @Email(message = "Invalid email format")
    private String email;
    
    @Size(min = 8, max = 16, message = "PASSWORD_VALIDATION")
    private String password;
    
    @Size(min = 3, max = 255, message = "FULLNAME_VALIDATION")
    private String fullName;
    
    private String major;
    private String schoolYear;
    private String bio;
}