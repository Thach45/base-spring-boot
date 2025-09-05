package com.example.aloute.service;

import com.example.aloute.dto.Authentication.LoginDTO;
import com.example.aloute.dto.Authentication.LoginResponse;
import com.example.aloute.dto.Authentication.LogoutResponse;
import com.example.aloute.entity.BlacklistedToken;
import com.example.aloute.entity.User;
import com.example.aloute.exception.AppException;
import com.example.aloute.exception.ErrorCode;
import com.example.aloute.repository.BlacklistedTokenRepository;
import com.example.aloute.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;

@Service
public class AuthenticationService {
    @Value("${spring.jwt.secret}")
    private String jwtSecret;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;
    @Autowired
    BlacklistedTokenRepository blacklistedTokenRepository;

    public LoginResponse isLogin(LoginDTO loginDTO) throws JOSEException {

        var user = userRepository.findByUsername(loginDTO.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));


        Boolean isLogin =  passwordEncoder.matches(loginDTO.getPassword(), user.getPassword());
        if(!isLogin) {
            throw new AppException(ErrorCode.UNEXPECTED_ERROR);
        }
        var token = generateToken(user);
        return LoginResponse.builder()

                .isLogin(isLogin)
                .accessToken(token)
                .build();

    }
    public String generateToken(User user) throws JOSEException {

        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("learn-jwt")
                .claim("user_id", user.getId())
                .claim("scope", buildScope(user))
                .expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli() )) // 1 minute
                .build();
        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        jwsObject.sign(new MACSigner(jwtSecret.getBytes()));
        return jwsObject.serialize();
    }
    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
            });

        return stringJoiner.toString();
    }
    
    public LogoutResponse logout(String token) {
        try {
            System.out.println("Attempting to logout token: " + token.substring(0, Math.min(50, token.length())) + "...");
            
            // Parse JWT để lấy thông tin
            var jwt = com.nimbusds.jwt.JWTParser.parse(token);
            var claims = jwt.getJWTClaimsSet();
            
            System.out.println("JWT parsed successfully. Subject: " + claims.getSubject());
            
            // Kiểm tra token đã bị blacklist chưa
            if (blacklistedTokenRepository.existsByToken(token)) {
                System.out.println("Token already blacklisted");
                return LogoutResponse.builder()
                        .success(false)
                        .message("Token đã được logout trước đó")
                        .build();
            }
            
            // Lưu token vào blacklist
            var blacklistedToken = BlacklistedToken.builder()
                    .token(token)
                    .blacklistedAt(LocalDateTime.now())
                    .expiresAt(claims.getExpirationTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())
                    .username(claims.getSubject())
                    .build();
            
            blacklistedTokenRepository.save(blacklistedToken);
            System.out.println("Token blacklisted successfully");
            
            return LogoutResponse.builder()
                    .success(true)
                    .message("Logout thành công")
                    .build();
                    
        } catch (Exception e) {
            System.out.println("Error parsing JWT: " + e.getMessage());
            e.printStackTrace();
            return LogoutResponse.builder()
                    .success(false)
                    .message("Token không hợp lệ: " + e.getMessage())
                    .build();
        }
    }
    
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokenRepository.existsByToken(token);
    }
    
    // Scheduled task để xóa các token đã hết hạn khỏi blacklist
    @Scheduled(fixedRate = 3600000) // Chạy mỗi giờ
    public void cleanupExpiredTokens() {
        blacklistedTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}
