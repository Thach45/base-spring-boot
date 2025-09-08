package com.example.social_ute.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.authentication.AnonymousAuthenticationToken;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
    @Value("${spring.jwt.secret}")
    private String jwtSecret;

    // Thêm một logger để gỡ lỗi
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    
    // Các endpoint công khai, không cần xác thực
    private static final String[] PUBLIC_ENDPOINTS = {
            "/auth/login",
            "/auth/logout",
            "/auth/refresh",
            "/auth/register",
            "/auth/verify-email",
            "/auth/resend-verification"
    };

    // Các endpoint yêu cầu cả đăng nhập và xác thực email
    private static final String[] EMAIL_VERIFICATION_REQUIRED_ENDPOINTS = {
            "/**"
    };
    
    /**
     * Tạo một AuthorizationManager tùy chỉnh để kiểm tra cả hai điều kiện:
     * 1. Người dùng đã được xác thực (đăng nhập thành công, có token hợp lệ).
     * 2. Claim 'is_email_verified' trong JWT phải là true.
     */
    private AuthorizationManager<RequestAuthorizationContext> isVerifiedAndAuthenticated() {
        return (authenticationSupplier, context) -> {
           
            Authentication authentication = authenticationSupplier.get();

            if (authentication == null || !authentication.isAuthenticated() 
                || authentication instanceof AnonymousAuthenticationToken
                || !(authentication.getPrincipal() instanceof Jwt)) {
                logger.warn("Authorization DENIED: User is not properly authenticated or principal is not a JWT.");
                return new AuthorizationDecision(false);
            }

            Jwt jwt = (Jwt) authentication.getPrincipal();
            Boolean isEmailVerified = jwt.getClaimAsBoolean("is_email_verified");
            

            boolean isGranted = isEmailVerified != null && isEmailVerified;
            

            return new AuthorizationDecision(isGranted);
        };
    }

    /**
     * Bean tùy chỉnh để xử lý lỗi Access Denied.
     * Sẽ trả về một response JSON thay vì trang lỗi mặc định.
     */
    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String message = "Access Denied. You do not have permission to access this resource.";

            // Kiểm tra xem có phải lỗi do email chưa xác thực không
            if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
                Jwt jwt = (Jwt) authentication.getPrincipal();
                Boolean isEmailVerified = jwt.getClaimAsBoolean("is_email_verified");
                if (isEmailVerified != null && !isEmailVerified) {
                    message = "Email has not been verified. Please check your inbox to verify your account.";
                }
            }
            
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("timestamp", Instant.now().toString());
            errorDetails.put("status", HttpServletResponse.SC_FORBIDDEN);
            errorDetails.put("error", "Forbidden");
            errorDetails.put("message", message);
            errorDetails.put("path", request.getRequestURI());

            ObjectMapper objectMapper = new ObjectMapper();
            response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(AbstractHttpConfigurer::disable)
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Cấu hình xử lý exception, trỏ đến handler tùy chỉnh của chúng ta
            .exceptionHandling(ex -> ex.accessDeniedHandler(customAccessDeniedHandler()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                .requestMatchers(EMAIL_VERIFICATION_REQUIRED_ENDPOINTS)
                    .access(isVerifiedAndAuthenticated()) // Quy tắc này vẫn được giữ để kích hoạt lỗi Access Denied
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .decoder(jwtDecoder())
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKeySpec secretKey = new SecretKeySpec(jwtSecret.getBytes(), "HMACSHA256");
        return NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }
}

