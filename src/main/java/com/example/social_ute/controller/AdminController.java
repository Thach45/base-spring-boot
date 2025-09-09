package com.example.social_ute.controller;

import com.example.social_ute.dto.User.UsersGetDTO;
import com.example.social_ute.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
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
        Page<UsersGetDTO> usersGetDTOS = userService.getUserWithFilter(q, role, status, sortBy, sortDir, page, size);
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
}
