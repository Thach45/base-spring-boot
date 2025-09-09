package com.example.social_ute.dto.Admin;


import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserUpdateRequest {
    private String fullName;
    private String major;
    private String schoolYear;
}
