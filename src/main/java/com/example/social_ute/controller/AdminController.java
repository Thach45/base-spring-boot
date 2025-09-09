package com.example.social_ute.controller;

import com.example.social_ute.dto.Admin.UserGetDetailDTO;
import com.example.social_ute.dto.Admin.UserUpdateRequest;
import com.example.social_ute.dto.Admin.UsersGetDTO;
import com.example.social_ute.dto.User.ApiResponse;
import com.example.social_ute.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    @GetMapping
    public ResponseEntity<?> getAllUsers(
        @RequestParam(required = false) String q,
        @RequestParam(required = false) String role,
        @RequestParam(required = false) String status,
        @RequestParam(defaultValue = "createdAt") String sortBy,
        @RequestParam(defaultValue = "DESC") String sortDir,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ){
        Page<UsersGetDTO> usersGetDTOS = adminService.getUserWithFilter(q, role, status, sortBy, sortDir, page, size);
        Map<String,Object> response = new HashMap<>();
        response.put("code",200);
        response.put("message", "User list retrieved successfully.");
        response.put("data", Map.of(
                "content", usersGetDTOS.getContent(),
                "page", usersGetDTOS.getNumber(),
                "size", usersGetDTOS.getSize(),
                "totalPages", usersGetDTOS.getTotalPages(),
                "totalElements", usersGetDTOS.getTotalElements()
        ));
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable String userId){
        UserGetDetailDTO userDetail =  adminService.getUserDetail(userId);

        if(userDetail == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.builder()
                    .code(404)
                    .message("User not found.")
                    .data(null)
                    .build());

        }
        return  ResponseEntity.ok(
                ApiResponse.<UserGetDetailDTO>builder()
                .code(200)
                .message("User retrieved successfully")
                .data(userDetail)
                .build()
        );

    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest userUpdateRequest){
        UserGetDetailDTO userDetail = adminService.UpdateUser(userId,userUpdateRequest);
        return ResponseEntity.ok(
                ApiResponse.<UserGetDetailDTO>builder()
                .code(200)
                .message("User profile updated successfully.")
                .data(userDetail)
                .build()
        );
    }
}
