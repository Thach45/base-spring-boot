package com.example.social_ute.service;

import com.example.social_ute.dto.Admin.UserGetDetailDTO;
import com.example.social_ute.dto.Admin.UserUpdateRequest;
import com.example.social_ute.dto.Admin.UsersGetDTO;
import com.example.social_ute.entity.User;
import com.example.social_ute.exception.AppException;
import com.example.social_ute.exception.ErrorCode;
import com.example.social_ute.repository.UserRepository;
import com.example.social_ute.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AdminService {
    private final UserRepository userRepository;


    // Get list User with paging and filter
    public Page<UsersGetDTO> getUserWithFilter(String q, String role, String status,
                                               String sortBy, String sortDir,
                                               int page, int size){
        Specification<User> spec = UserSpecification.filterUsers(q,role,status);
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return userRepository.findAll(spec,pageable)
                .map(user -> UsersGetDTO.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .roles(user.getRoles().stream()
                                .map(roleObj -> roleObj.getName()) // Role entity -> String
                                .collect(Collectors.toSet()))
                        .status(user.getStatus().name())
                        .createdAt(user.getCreatedAt())
                        .build());
    }

    // Get Detail user
    public UserGetDetailDTO getUserDetail(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return UserGetDetailDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .major(user.getMajor())
                .schoolYear(user.getSchoolYear())
                .roles(user.getRoles().stream()
                        .map(r -> r.getName())
                        .collect(Collectors.toSet()))
                .status(user.getStatus().name())
                .createdAt(user.getCreatedAt())
                .deletedAt(user.getDeletedAt())
                .build();
    }

    public UserGetDetailDTO UpdateUser(String id, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(id)
                        .orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));
        if(userUpdateRequest.getFullName() != null )
            user.setFullName(userUpdateRequest.getFullName());
        if(userUpdateRequest.getMajor() != null )
            user.setMajor(userUpdateRequest.getMajor());
        if(userUpdateRequest.getSchoolYear() != null )
            user.setSchoolYear(userUpdateRequest.getSchoolYear());
        userRepository.save(user);

        return UserGetDetailDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .major(user.getMajor())
                .schoolYear(user.getSchoolYear())
                .roles(user.getRoles().stream()
                .map(r -> r.getName())
                .collect(Collectors.toSet()))
                .status(user.getStatus().name())
                .createdAt(user.getCreatedAt())
                .deletedAt(user.getDeletedAt())
                .build();
    }

}
