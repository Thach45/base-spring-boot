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
public class UsersGetDTO {
    private String id;
    private String fullName;
    private String email;
    private Set<String> roles;
    private String status;
    private LocalDateTime createdAt;

}
