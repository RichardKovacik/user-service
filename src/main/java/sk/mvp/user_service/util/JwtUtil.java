package sk.mvp.user_service.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sk.mvp.user_service.config.JwtConfig;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    private JwtConfig jwtConfig;
    private SecretKey accesKey;
    private SecretKey refreshKey;

    public JwtUtil(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    // Initializes the key after the class is instantiated
    @PostConstruct
    private void init() {
        this.accesKey = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
        this.refreshKey = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    // Generate JWT token
    public String generateAccessToken(String username, int tokenVersion) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .claim("type", "access_token")
                .claim("version", tokenVersion)
                .setExpiration(new Date((new Date()).getTime() + jwtConfig.getAccesTokenExpiration()))
                .signWith(accesKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // Generate JWT token
    public String generateRefreshToken(String jti) {
        return Jwts.builder()
                .setId(jti)
                .claim("type", "refresh_token")
                .setExpiration(new Date((new Date()).getTime() + jwtConfig.getRefreshTokenExpiration()))
                .signWith(refreshKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // Get username from JWT token
    public String getUsernameFromAccessToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(accesKey).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public void validateAccessToken(String token, int expectedTokenVersion) throws JwtException {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(accesKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            assertType(claims, "access_token");
            assertTokenVersion(claims, expectedTokenVersion);
        } catch (Exception e) {
            throw new JwtException(e.getMessage());
        }
    }

    public void validateRefreshToken(String token) throws JwtException {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(refreshKey)
                    .build()
                    .parseClaimsJws(token).getBody();
            assertType(claims, "refresh_token");
        } catch (Exception e) {
            throw new JwtException(e.getMessage());
        }
    }

    private void assertType(Claims claims, String expected) {
        String type = claims.get("type", String.class);
        if (!expected.equals(type)) {
            throw new JwtException("Invalid token type");
        }
    }
    private void assertTokenVersion(Claims claims, int expectedVersion) {
        int tokenVersion = claims.get("version", Integer.class);
        if (tokenVersion != expectedVersion) {
            throw new JwtException("Token version mismatch");
        }
    }
}
