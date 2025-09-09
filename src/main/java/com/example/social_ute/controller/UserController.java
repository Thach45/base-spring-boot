package com.example.social_ute.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.social_ute.annotation.CurrentUserId;
import com.example.social_ute.dto.User.ApiResponse;
import com.example.social_ute.dto.User.UserCreateDTO;
import com.example.social_ute.dto.User.UserResponse;
import com.example.social_ute.dto.User.UserUpdateDTO;
import com.example.social_ute.entity.User;
import com.example.social_ute.service.AuthenticationService;
import com.example.social_ute.service.UserService;

import java.util.List;

@Slf4j
@RestController

public class UserController {
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/users")
    // Không yêu cầu xác thực email cho endpoint tạo user
    User createUser(@RequestBody @Valid UserCreateDTO request ) {
        return userService.createUser(request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    ApiResponse<List<User>> getUsers(@CurrentUserId String userId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        authentication.getAuthorities().forEach(authority -> {
            log.warn(authority.getAuthority().toString());
        });
        ApiResponse<List<User>> response = new ApiResponse<>();
        response.setData(userService.getUsers());
        return response;
    }

    @PreAuthorize("#id == authentication.principal.claims['user_id']")
    @GetMapping("/user/{id}")
    ApiResponse<User> getUserById(@PathVariable("id") String id) {
        ApiResponse<User> response = new ApiResponse<>();
        response.setData(userService.getUserById(id));
        return response;
    }

    @PutMapping("/user/{id}")
    User updateUser(@PathVariable("id") String id, @RequestBody UserUpdateDTO request) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/user/{id}")
    void deleteUser(@PathVariable("id") String id) {
        userService.deleteUser(id);
    }

}