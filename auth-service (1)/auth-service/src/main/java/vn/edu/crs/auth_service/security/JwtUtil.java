package vn.edu.crs.auth_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    // Lấy secret key từ application.properties (hoặc dùng key mặc định nếu chưa khai báo)
    @Value("${jwt.secret:1234567890123456789012345678901234567890}")
    private String secret;

    // Thời gian hết hạn JWT (ví dụ: 1 ngày = 86400000 ms)
    @Value("${jwt.expiration:86400000}")
    private long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Tạo JWT Token từ username
    // Tạo JWT Token từ username và role
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .subject(username)
                .claim("role", role) // Thêm thông tin role vào Claims của Token
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    // Trích xuất username từ Token
    public String getUsernameFromToken(String token) {
        return getClaims(token).getSubject();
    }

    // Kiểm tra Token có hợp lệ không
    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}