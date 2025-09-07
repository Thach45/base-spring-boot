package com.example.social_ute.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.social_ute.dto.User.UserCreateDTO;
import com.example.social_ute.dto.User.UserResponse;
import com.example.social_ute.dto.User.UserUpdateDTO;
import com.example.social_ute.entity.User;
import com.example.social_ute.enums.Role;
import com.example.social_ute.enums.UserStatus;
import com.example.social_ute.exception.AppException;
import com.example.social_ute.exception.ErrorCode;
import com.example.social_ute.repository.RoleRepository;
import com.example.social_ute.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;

    public User createUser(UserCreateDTO request) {
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.UNEXPECTED_ERROR);
        }

        String hashPassword = passwordEncoder.encode(request.getPassword());
        // HashSet<String> roles = new HashSet<>();
        // roles.add(Role.USER.name());
        User user = User.builder()
                .email(request.getEmail())
                .password(hashPassword)
                .fullName(request.getFullName())
                .major(request.getMajor())
                .schoolYear(request.getSchoolYear())
                .bio(request.getBio())
                .isEmailVerified(false)
                .status(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                
                .build();
                
        // Add default USER role
        var userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new AppException(ErrorCode.UNEXPECTED_ERROR));
        user.setRoles(new HashSet<>(List.of(userRole)));
        
        return userRepository.save(user);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public User updateUser(String id, UserUpdateDTO request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        
        if (request.getMajor() != null) {
            user.setMajor(request.getMajor());
        }
        
        if (request.getSchoolYear() != null) {
            user.setSchoolYear(request.getSchoolYear());
        }
        
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        
        if (request.getCoverPhotoUrl() != null) {
            user.setCoverPhotoUrl(request.getCoverPhotoUrl());
        }

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            var roles = roleRepository.findAllById(request.getRoles());
            user.setRoles(new HashSet<>(roles));
        }

        return userRepository.save(user);
    }

    public void deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        
        // Soft delete by setting deletedAt
        user.setDeletedAt(LocalDateTime.now());
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }
    
    public void verifyEmail(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        
        user.setEmailVerified(true);
        userRepository.save(user);
    }
}