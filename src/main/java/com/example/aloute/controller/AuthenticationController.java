package com.example.aloute.controller;

import com.example.aloute.dto.Authentication.LoginDTO;
import com.example.aloute.dto.Authentication.LoginResponse;
import com.example.aloute.dto.Authentication.LogoutResponse;
import com.example.aloute.dto.Authentication.RefreshTokenRequest;
import com.example.aloute.dto.Authentication.TokenResponse;
import com.example.aloute.dto.User.ApiResponse;
import com.example.aloute.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    AuthenticationService authenticationService;
    @PostMapping("/login")
    ApiResponse<LoginResponse> login(@RequestBody LoginDTO loginDTO) throws JOSEException {
        LoginResponse login = authenticationService.isLogin(loginDTO);
        ApiResponse<LoginResponse> response = ApiResponse.<LoginResponse>builder()
                .data(login)
                .build();
        return response;
    }
    
    @PostMapping("/logout")
    ApiResponse<LogoutResponse> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ApiResponse.<LogoutResponse>builder()
                    .data(LogoutResponse.builder()
                            .success(false)
                            .message("Token không được cung cấp")
                            .build())
                    .build();
        }
        
        String token = authHeader.substring(7);
        LogoutResponse logoutResponse = authenticationService.logout(token);
        
        return ApiResponse.<LogoutResponse>builder()
                .data(logoutResponse)
                .build();
    }
    
    @PostMapping("/refresh")
    ApiResponse<TokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) throws JOSEException {
        TokenResponse tokenResponse = authenticationService.refreshToken(request);
        return ApiResponse.<TokenResponse>builder()
                .data(tokenResponse)
                .build();
    }
}
