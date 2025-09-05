package com.example.aloute.dto.User;

import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateDTO {
    @Size(min=8, max=16, message="Password must be between 8 and 16 characters")
    String password;
    String firstName;
    String lastName;
    String birthday; // Use String to simplify date handling in DTO
    List<String> roles;
}
