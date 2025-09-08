package com.example.social_ute.helper;

import org.springframework.stereotype.Component;

@Component
public class EmailTemplateHelper {

    public String createVerificationEmailTemplate(String token, String fullName) {
        String verificationLink = "http://localhost:5173/verify-email?token=" + token;
        
        return String.format("""
            <!DOCTYPE html>
            <html lang="vi">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Xác thực tài khoản Social-ute</title>
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                        background-color: #f4f4f4;
                    }
                    .container {
                        background: white;
                        border-radius: 10px;
                        padding: 30px;
                        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                    }
                    .header {
                        text-align: center;
                        margin-bottom: 30px;
                    }
                    .logo {
                        font-size: 28px;
                        font-weight: bold;
                        color: #4a90e2;
                        margin-bottom: 10px;
                    }
                    .title {
                        color: #2c3e50;
                        font-size: 24px;
                        margin-bottom: 20px;
                    }
                    .content {
                        margin-bottom: 30px;
                    }
                    .greeting {
                        font-size: 18px;
                        margin-bottom: 15px;
                    }
                    .message {
                        font-size: 16px;
                        margin-bottom: 25px;
                        line-height: 1.8;
                    }
                    .button-container {
                        text-align: center;
                        margin: 30px 0;
                    }
                    .verify-button {
                        display: inline-block;
                        background: linear-gradient(135deg, #4a90e2, #357abd);
                        color: white;
                        padding: 15px 30px;
                        text-decoration: none;
                        border-radius: 25px;
                        font-weight: bold;
                        font-size: 16px;
                        transition: transform 0.2s;
                        box-shadow: 0 4px 15px rgba(74, 144, 226, 0.3);
                    }
                    .verify-button:hover {
                        transform: translateY(-2px);
                        box-shadow: 0 6px 20px rgba(74, 144, 226, 0.4);
                    }
                    .token-info {
                        background: #f8f9fa;
                        border-left: 4px solid #4a90e2;
                        padding: 15px;
                        margin: 20px 0;
                        border-radius: 0 5px 5px 0;
                    }
                    .token-label {
                        font-weight: bold;
                        color: #2c3e50;
                        margin-bottom: 5px;
                    }
                    .token-value {
                        font-family: 'Courier New', monospace;
                        background: #e9ecef;
                        padding: 8px;
                        border-radius: 4px;
                        word-break: break-all;
                        font-size: 14px;
                    }
                    .footer {
                        text-align: center;
                        margin-top: 40px;
                        padding-top: 20px;
                        border-top: 1px solid #eee;
                        color: #666;
                        font-size: 14px;
                    }
                    .warning {
                        background: #fff3cd;
                        border: 1px solid #ffeaa7;
                        color: #856404;
                        padding: 15px;
                        border-radius: 5px;
                        margin: 20px 0;
                    }
                    .social-links {
                        margin-top: 20px;
                    }
                    .social-links a {
                        color: #4a90e2;
                        text-decoration: none;
                        margin: 0 10px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo">🎓 Social-ute</div>
                        <h1 class="title">Xác thực tài khoản</h1>
                    </div>
                    
                    <div class="content">
                        <div class="greeting">Xin chào %s!</div>
                        
                        <div class="message">
                            Cảm ơn bạn đã đăng ký tài khoản tại <strong>Social-ute</strong> - nền tảng kết nối sinh viên!
                            <br><br>
                            Để hoàn tất quá trình đăng ký và kích hoạt tài khoản, vui lòng xác thực email của bạn bằng cách nhấn vào nút bên dưới:
                        </div>
                        
                        <div class="button-container">
                            <a href="%s" class="verify-button">✅ Xác thực tài khoản</a>
                        </div>
                        
                        <div class="token-info">
                            <div class="token-label">Hoặc sử dụng mã xác thực:</div>
                            <div class="token-value">%s</div>
                        </div>
                        
                        <div class="warning">
                            <strong>⚠️ Lưu ý quan trọng:</strong>
                            <ul style="margin: 10px 0; padding-left: 20px;">
                                <li>Mã xác thực có hiệu lực trong <strong>24 giờ</strong></li>
                                <li>Nếu bạn không xác thực trong thời gian này, tài khoản sẽ bị xóa tự động</li>
                                <li>Nếu bạn không đăng ký tài khoản này, vui lòng bỏ qua email này</li>
                            </ul>
                        </div>
                    </div>
                    
                    <div class="footer">
                        <p>Trân trọng,<br><strong>Đội ngũ Social-ute</strong></p>
                        <div class="social-links">
                            <a href="#">Website</a> |
                            <a href="#">Facebook</a> |
                            <a href="#">Instagram</a>
                        </div>
                        <p style="margin-top: 15px; font-size: 12px; color: #999;">
                            Email này được gửi tự động, vui lòng không trả lời.
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """, fullName, verificationLink, token);
    }

    public String createResendVerificationEmailTemplate(String token, String fullName) {
        String verificationLink = "https://your-frontend-domain.com/verify-email?token=" + token;
        
        return String.format("""
            <!DOCTYPE html>
            <html lang="vi">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Gửi lại email xác thực - Social-ute</title>
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                        background-color: #f4f4f4;
                    }
                    .container {
                        background: white;
                        border-radius: 10px;
                        padding: 30px;
                        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                    }
                    .header {
                        text-align: center;
                        margin-bottom: 30px;
                    }
                    .logo {
                        font-size: 28px;
                        font-weight: bold;
                        color: #4a90e2;
                        margin-bottom: 10px;
                    }
                    .title {
                        color: #2c3e50;
                        font-size: 24px;
                        margin-bottom: 20px;
                    }
                    .content {
                        margin-bottom: 30px;
                    }
                    .greeting {
                        font-size: 18px;
                        margin-bottom: 15px;
                    }
                    .message {
                        font-size: 16px;
                        margin-bottom: 25px;
                        line-height: 1.8;
                    }
                    .button-container {
                        text-align: center;
                        margin: 30px 0;
                    }
                    .verify-button {
                        display: inline-block;
                        background: linear-gradient(135deg, #4a90e2, #357abd);
                        color: white;
                        padding: 15px 30px;
                        text-decoration: none;
                        border-radius: 25px;
                        font-weight: bold;
                        font-size: 16px;
                        transition: transform 0.2s;
                        box-shadow: 0 4px 15px rgba(74, 144, 226, 0.3);
                    }
                    .verify-button:hover {
                        transform: translateY(-2px);
                        box-shadow: 0 6px 20px rgba(74, 144, 226, 0.4);
                    }
                    .token-info {
                        background: #f8f9fa;
                        border-left: 4px solid #4a90e2;
                        padding: 15px;
                        margin: 20px 0;
                        border-radius: 0 5px 5px 0;
                    }
                    .token-label {
                        font-weight: bold;
                        color: #2c3e50;
                        margin-bottom: 5px;
                    }
                    .token-value {
                        font-family: 'Courier New', monospace;
                        background: #e9ecef;
                        padding: 8px;
                        border-radius: 4px;
                        word-break: break-all;
                        font-size: 14px;
                    }
                    .footer {
                        text-align: center;
                        margin-top: 40px;
                        padding-top: 20px;
                        border-top: 1px solid #eee;
                        color: #666;
                        font-size: 14px;
                    }
                    .info-box {
                        background: #e3f2fd;
                        border: 1px solid #bbdefb;
                        color: #1565c0;
                        padding: 15px;
                        border-radius: 5px;
                        margin: 20px 0;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo">🎓 Social-ute</div>
                        <h1 class="title">Email xác thực mới</h1>
                    </div>
                    
                    <div class="content">
                        <div class="greeting">Xin chào %s!</div>
                        
                        <div class="message">
                            Chúng tôi đã nhận được yêu cầu gửi lại email xác thực cho tài khoản của bạn.
                            <br><br>
                            Dưới đây là thông tin xác thực mới để kích hoạt tài khoản:
                        </div>
                        
                        <div class="button-container">
                            <a href="%s" class="verify-button">✅ Xác thực tài khoản</a>
                        </div>
                        
                        <div class="token-info">
                            <div class="token-label">Mã xác thực mới:</div>
                            <div class="token-value">%s</div>
                        </div>
                        
                        <div class="info-box">
                            <strong>ℹ️ Thông tin:</strong>
                            <ul style="margin: 10px 0; padding-left: 20px;">
                                <li>Mã xác thực cũ đã bị vô hiệu hóa</li>
                                <li>Mã mới có hiệu lực trong <strong>24 giờ</strong></li>
                                <li>Chỉ mã xác thực mới nhất mới có hiệu lực</li>
                            </ul>
                        </div>
                    </div>
                    
                    <div class="footer">
                        <p>Trân trọng,<br><strong>Đội ngũ Social-ute</strong></p>
                        <p style="margin-top: 15px; font-size: 12px; color: #999;">
                            Email này được gửi tự động, vui lòng không trả lời.
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """, fullName, verificationLink, token);
    }
}
