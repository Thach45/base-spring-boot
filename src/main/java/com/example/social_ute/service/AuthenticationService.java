package com.example.social_ute.service;

import com.example.social_ute.dto.Authentication.LoginDTO;
import com.example.social_ute.dto.Authentication.LoginResponse;
import com.example.social_ute.dto.Authentication.LogoutResponse;
import com.example.social_ute.dto.Authentication.RefreshTokenRequest;
import com.example.social_ute.dto.Authentication.TokenResponse;
import com.example.social_ute.entity.RefreshToken;
import com.example.social_ute.entity.User;
import com.example.social_ute.exception.AppException;
import com.example.social_ute.exception.ErrorCode;
import com.example.social_ute.repository.RefreshTokenRepository;
import com.example.social_ute.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
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
    RefreshTokenRepository refreshTokenRepository;

    public LoginResponse isLogin(LoginDTO loginDTO) throws JOSEException {
        var user = userRepository.findByEmail(loginDTO.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Boolean isLogin = passwordEncoder.matches(loginDTO.getPassword(), user.getPassword());
        if(!isLogin) {
            throw new AppException(ErrorCode.UNEXPECTED_ERROR);
        }
        var tokenResponse = generateTokens(user);
        return LoginResponse.builder()
                .isLogin(isLogin)
                .accessToken(tokenResponse.getAccessToken())
                .refreshToken(tokenResponse.getRefreshToken())
                .build();
    }

    public String generateToken(User user) throws JOSEException {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);
        System.out.println("user.getEmail(): " + buildScope(user));
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("alo-ute")
                .claim("user_id", user.getId())
                .claim("scope", buildScope(user))
                .expirationTime(new Date(Instant.now().plus(5, ChronoUnit.MINUTES).toEpochMilli()))
                .build();
        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        jwsObject.sign(new MACSigner(jwtSecret.getBytes()));
        return jwsObject.serialize();
    }
    
    public TokenResponse generateTokens(User user) throws JOSEException {
        // Tạo access token (5 phút)
        String accessToken = generateToken(user);
        
        // Tạo refresh token (7 ngày)
        String refreshToken = generateRefreshToken(user);
        
        // Lưu refresh token vào database
        saveRefreshToken(user.getEmail(), refreshToken);
        
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(300) // 5 phút = 300 giây
                .build();
    }
    
    private void saveRefreshToken(String email, String refreshToken) {
        try {
            // Parse refresh token để lấy thời gian hết hạn
            var jwt = com.nimbusds.jwt.JWTParser.parse(refreshToken);
            var claims = jwt.getJWTClaimsSet();
            
            var refreshTokenEntity = RefreshToken.builder()
                    .token(refreshToken)
                    .email(email)
                    .createdAt(LocalDateTime.now())
                    .expiresAt(claims.getExpirationTime().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())
                    .revoked(false)
                    .build();
            
            refreshTokenRepository.save(refreshTokenEntity);
        } catch (Exception e) {
            System.err.println("Error saving refresh token: " + e.getMessage());
        }
    }
    
    public String generateRefreshToken(User user) throws JOSEException {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issuer("alo-ute")
                .claim("type", "refresh")
                .expirationTime(new Date(Instant.now().plus(RefreshToken.REFRESH_TOKEN_EXPIRATION_DAYS, ChronoUnit.DAYS).toEpochMilli()))
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
    
    // Refresh token method
    public TokenResponse refreshToken(RefreshTokenRequest request) throws JOSEException {
        String refreshToken = request.getRefreshToken();
        
        // Kiểm tra refresh token có hợp lệ và chưa hết hạn không
        if (!refreshTokenRepository.existsByTokenAndNotRevokedAndNotExpired(refreshToken, LocalDateTime.now())) {
            throw new AppException(ErrorCode.UNEXPECTED_ERROR);
        }
        
        try {
            // Parse refresh token để lấy email
            var jwt = JWTParser.parse(refreshToken);
            var claims = jwt.getJWTClaimsSet();
            String email = claims.getSubject();
            
            // Lấy user
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            
            // Revoke refresh token cũ
            System.out.println("Deleting refresh token: " + refreshToken.substring(0, Math.min(50, refreshToken.length())) + "...");
            refreshTokenRepository.deleteToken(refreshToken);
            System.out.println("Refresh token deleted successfully");
            
            // Tạo token mới
            return generateTokens(user);
        } catch (Exception e) {
            throw new AppException(ErrorCode.UNEXPECTED_ERROR);
        }
    }
    
    public LogoutResponse logout(String token) {
        try {
            System.out.println("Attempting to logout token: " + token.substring(0, Math.min(50, token.length())) + "...");
            
            // Parse JWT để lấy thông tin
            var jwt = com.nimbusds.jwt.JWTParser.parse(token);
            var claims = jwt.getJWTClaimsSet();
            String email = claims.getSubject();
            
            System.out.println("JWT parsed successfully. Subject: " + email);
            
            // Xóa tất cả refresh token của user
            refreshTokenRepository.deleteByEmail(email);
            
            System.out.println("Refresh tokens revoked successfully");
            
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
    
    // Scheduled task để xóa các refresh token đã hết hạn
    @Scheduled(fixedRate = 3600000) // Chạy mỗi giờ
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}