package com.example.social_ute.dto.User;

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
    
    @Size(min = 3, max = 255, message = "FULLNAME_VALIDATION")
    String fullName;
    
    String major;
    String schoolYear;
    String bio;
    String avatarUrl;
    String coverPhotoUrl;
    List<String> roles;
}