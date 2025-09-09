package com.example.social_ute.dto.Admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserGetDetailDTO {
    String id;
    String fullName;
    String email;
    String avatarUrl;
    String major;
    String schoolYear;
    Set<String> roles;
    String status;
    LocalDateTime createdAt;
    LocalDateTime deletedAt;
}
