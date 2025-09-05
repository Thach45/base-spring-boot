package com.example.aloute.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.aloute.dto.User.UserCreateDTO;
import com.example.aloute.dto.User.UserResponse;
import com.example.aloute.dto.User.UserUpdateDTO;
import com.example.aloute.entity.User;
import com.example.aloute.enums.Role;
import com.example.aloute.exception.AppException;
import com.example.aloute.exception.ErrorCode;
import com.example.aloute.repository.RoleRepository;
import com.example.aloute.repository.UserRepository;

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
    public User createUser(UserCreateDTO request)
    {

        if(userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        String hashPassword = passwordEncoder.encode(request.getPassword());
        HashSet<String> roles = new HashSet<>();
        roles.add(Role.USER.name());
        User user = User.builder()
                .username(request.getUsername())
                .firstName(request.getFirstName())
                .password(hashPassword)
                .lastName(request.getLastName())
//                .roles(roles)
                .birthday(java.time.LocalDate.parse(request.getBirthday())) // Parse the birthday string to
                .build();
        return userRepository.save(user);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }
    public User getUserById(String id) {;
        return userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }
    public User updateUser(String id, UserUpdateDTO request) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        if(user == null) {
            throw new RuntimeException("User not found");
        }

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setBirthday(java.time.LocalDate.parse(request.getBirthday()));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));
        return userRepository.save(user);
    }
    public void deleteUser(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        if(user == null) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }
}
