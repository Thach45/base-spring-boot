package com.example.social_ute.filter;

import com.example.social_ute.exception.AppException;
import com.example.social_ute.exception.ErrorCode;
import com.example.social_ute.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EmailVerificationFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;
    private final String[] publicEndpoints;
    private final ObjectMapper objectMapper;

    public EmailVerificationFilter(UserRepository userRepository, String[] publicEndpoints) {
        this.userRepository = userRepository;
        this.publicEndpoints = publicEndpoints;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return Arrays.stream(publicEndpoints).anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            String email = jwt.getSubject();
            System.out.println("Checking email verification for: " + email);

            var userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent() && !userOpt.get().isEmailVerified()) {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                
                Map<String, Object> errorDetails = new HashMap<>();
                errorDetails.put("code", ErrorCode.EMAIL_NOT_VERIFIED.getCode());
                errorDetails.put("message", ErrorCode.EMAIL_NOT_VERIFIED.getMessage());
                
                response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}