# Hướng dẫn sử dụng chức năng Logout JWT

## Tổng quan
Chức năng logout JWT đã được implement với cơ chế blacklist token để đảm bảo tính bảo mật. Khi user logout, token sẽ được thêm vào blacklist và không thể sử dụng được nữa.

## Các thành phần đã thêm

### 1. Entity
- `BlacklistedToken.java`: Lưu trữ các token đã logout

### 2. Repository
- `BlacklistedTokenRepository.java`: Quản lý blacklist tokens

### 3. DTO
- `LogoutResponse.java`: Response cho logout API

### 4. Service
- `AuthenticationService.java`: Thêm method logout() và isTokenBlacklisted()
- `CustomUserDetailsService.java`: Hỗ trợ JWT authentication

### 5. Controller
- `AuthenticationController.java`: Thêm endpoint `/auth/logout`

### 6. Config
- `JwtAuthenticationFilter.java`: Filter kiểm tra blacklist token
- `SecurityConfig.java`: Cập nhật security configuration

## API Endpoints

### POST /auth/logout
Logout user và vô hiệu hóa token

**Headers:**
```
Authorization: Bearer <your-jwt-token>
```

**Response:**
```json
{
  "data": {
    "success": true,
    "message": "Logout thành công"
  }
}
```

## Cách sử dụng

### 1. Login để lấy token
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "your_username",
    "password": "your_password"
  }'
```

### 2. Logout với token
```bash
curl -X POST http://localhost:8080/auth/logout \
  -H "Authorization: Bearer <your-jwt-token>"
```

### 3. Sau khi logout, token sẽ không thể sử dụng
```bash
curl -X GET http://localhost:8080/protected-endpoint \
  -H "Authorization: Bearer <logged-out-token>"
# Sẽ trả về 401 Unauthorized
```

## Tính năng bảo mật

1. **Blacklist Token**: Token sau khi logout sẽ được lưu vào database và bị từ chối
2. **Auto Cleanup**: Hệ thống tự động xóa các token đã hết hạn khỏi blacklist mỗi giờ
3. **JWT Filter**: Mọi request đều được kiểm tra qua filter để đảm bảo token không bị blacklist
4. **Validation**: Kiểm tra token hợp lệ trước khi thêm vào blacklist

## Lưu ý

- Token sau khi logout sẽ không thể sử dụng được nữa
- Hệ thống tự động dọn dẹp các token đã hết hạn
- Cần có database để lưu trữ blacklist tokens
- Endpoint logout cần Authorization header với Bearer token
