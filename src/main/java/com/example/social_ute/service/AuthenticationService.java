package com.example.social_ute.service;

import com.example.social_ute.dto.Authentication.LoginDTO;
import com.example.social_ute.dto.Authentication.LoginResponse;
import com.example.social_ute.dto.Authentication.LogoutResponse;
import com.example.social_ute.dto.Authentication.RefreshTokenRequest;
import com.example.social_ute.dto.Authentication.RegisterDTO;
import com.example.social_ute.dto.Authentication.RegisterResponse;
import com.example.social_ute.dto.Authentication.TokenResponse;
import com.example.social_ute.entity.EmailVerificationToken;
import com.example.social_ute.entity.RefreshToken;
import com.example.social_ute.entity.User;
import com.example.social_ute.enums.UserStatus;
import com.example.social_ute.exception.AppException;
import com.example.social_ute.exception.ErrorCode;
import com.example.social_ute.repository.EmailVerificationTokenRepository;
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
import org.springframework.web.bind.annotation.RequestBody;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.StringJoiner;
import java.util.UUID;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.social_ute.helper.EmailTemplateHelper;

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
    @Autowired
    EmailVerificationTokenRepository emailVerificationTokenRepository;
    
    @Autowired
    EmailTemplateHelper emailTemplateHelper;

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
                .claim("is_email_verified", user.isEmailVerified())
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
                .claim("is_email_verified", user.isEmailVerified())
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
    
    public RegisterResponse register(@RequestBody RegisterDTO registerDTO) {
        // Kiểm tra email đã tồn tại chưa
        if (userRepository.existsByEmail(registerDTO.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // Tạo user mới với status ACTIVE nhưng chưa verify email
        User user = User.builder()
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .fullName(registerDTO.getFullName())
                .major(registerDTO.getMajor())
                .schoolYear(registerDTO.getSchoolYear())
                .bio(registerDTO.getBio())
                .status(UserStatus.ACTIVE)
                .isEmailVerified(false)
                .createdAt(LocalDateTime.now())
                .build();

        user = userRepository.save(user);

        // Tạo token xác thực email
        String verificationToken = generateEmailVerificationToken(user);
        
        // Gửi email xác thực
        sendVerificationEmail(user.getEmail(), verificationToken);

        return RegisterResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .status(user.getStatus())
                .message("Đăng ký thành công. Vui lòng kiểm tra email để xác thực tài khoản.")
                .build();
    }

    private String generateEmailVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                .token(token)
                .user(user)
                .expiresAt(LocalDateTime.now().plusDays(7)) // Hết hạn sau 7 ngày
                .build();
        EmailVerificationToken savedToken = emailVerificationTokenRepository.save(verificationToken);
        
        return token;
    }

    public String verifyEmail(String token) {
        EmailVerificationToken verificationToken = emailVerificationTokenRepository
                .findValidToken(token, LocalDateTime.now())
                .orElseThrow(() -> new AppException(ErrorCode.UNEXPECTED_ERROR));

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        // Không cần thay đổi status vì đã là ACTIVE từ lúc đăng ký
        userRepository.save(user);
        System.out.println("User verified successfully: " + user.getEmail());
        // Xóa token đã sử dụng
        emailVerificationTokenRepository.delete(verificationToken);

        return "Email đã được xác thực thành công";
    }

    public String resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.isEmailVerified()) {
            throw new AppException(ErrorCode.INVALID_OPERATION);
        }

        // Xóa token cũ nếu có
        emailVerificationTokenRepository.deleteByUser(user);

        // Tạo token mới
        String newToken = generateEmailVerificationToken(user);
        
        // Gửi email mới với template resend
        sendResendVerificationEmail(email, newToken);

        return "Email xác thực đã được gửi lại";
    }

  

    // Scheduled task để xóa các refresh token đã hết hạn
    @Scheduled(fixedRate = 86400000) // Chạy mỗi ngày
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }

    // Scheduled task để xóa tài khoản chưa verify sau 8 giờ
    @Scheduled(fixedRate = 3600000) // Chạy mỗi giờ
    public void cleanupUnverifiedUsers() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(8); // 8 giờ trước
        
        // Lấy danh sách user cần xóa để log
        List<User> usersToDelete = userRepository.findByUnverifiedEmailAndCreatedAtBefore(cutoffTime);
        System.out.println("Found " + usersToDelete.size() + " unverified users to delete");
        
        // Xóa tài khoản chưa verify email
        userRepository.deleteByUnverifiedEmailAndCreatedAtBefore(cutoffTime);
        
        // Xóa các token hết hạn
        emailVerificationTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }

    private void sendVerificationEmail(String email, String token) {
        try {
            // Lấy thông tin user để tạo email cá nhân hóa
            User user = userRepository.findByEmail(email).orElse(null);
            String fullName = user != null ? user.getFullName() : "Bạn";
            
            // Tạo nội dung email HTML
            String htmlContent = emailTemplateHelper.createVerificationEmailTemplate(token, fullName);
            
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();
            
            // Tạo request body
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("recipientEmail", email);
            requestBody.put("content", htmlContent);
            
            String jsonBody = mapper.writeValueAsString(requestBody);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://send-email-self-one.vercel.app/api/email/send"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                System.out.println("Verification email sent successfully to: " + email);
            } else {
                System.err.println("Failed to send verification email. Status: " + response.statusCode());
            }
            
        } catch (Exception e) {
            System.err.println("Error sending verification email: " + e.getMessage());
        }
    }

    private void sendResendVerificationEmail(String email, String token) {
        try {
            // Lấy thông tin user để tạo email cá nhân hóa
            User user = userRepository.findByEmail(email).orElse(null);
            String fullName = user != null ? user.getFullName() : "Bạn";
            
            // Tạo nội dung email HTML cho resend
            String htmlContent = emailTemplateHelper.createResendVerificationEmailTemplate(token, fullName);
            
            HttpClient client = HttpClient.newHttpClient();
            ObjectMapper mapper = new ObjectMapper();
            
            // Tạo request body
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("recipientEmail", email);
            requestBody.put("content", htmlContent);
            
            String jsonBody = mapper.writeValueAsString(requestBody);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://send-email-self-one.vercel.app/api/email/send"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                System.out.println("Resend verification email sent successfully to: " + email);
            } else {
                System.err.println("Failed to send resend verification email. Status: " + response.statusCode());
            }
            
        } catch (Exception e) {
            System.err.println("Error sending resend verification email: " + e.getMessage());
        }
    }
}