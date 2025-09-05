package com.example.aloute.dto.User;

import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateDTO {
    @Size(min = 3, max = 50 , message = "USERNAME_VALIDATION")
    private String username;
    @Size(min = 8, max = 16, message = "PASSWORD_VALIDATION")
    private String password;
    private String firstName;
    private String lastName;
    private String birthday; // Use String to simplify date handling in DTO

}
