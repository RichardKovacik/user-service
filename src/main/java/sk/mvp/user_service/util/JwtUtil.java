package sk.mvp.user_service.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import sk.mvp.user_service.config.JwtConfig;
import sk.mvp.user_service.dto.auth.UserDetail;
import sk.mvp.user_service.exception.InvalidTokenException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {
    private JwtConfig jwtConfig;;

    public JwtUtil(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    // Generate JWT token
    public String generateAccessToken(String username, int tokenVersion, String jti, String[] roles) {
        return Jwts.builder()
                .setId(jti)
                .setSubject(username)
                .setIssuedAt(new Date())
                .claim("type", "access_token")
                .claim("version", tokenVersion)
                .claim("roles", roles)
                .setExpiration(new Date((new Date()).getTime() + jwtConfig.getAccesTokenExpiration()))
                .signWith(jwtConfig.getAccesKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Generate JWT token
    public String generateRefreshToken(String jti) {
        return Jwts.builder()
                .setId(jti)
                .claim("type", "refresh_token")
                .setExpiration(new Date((new Date()).getTime() + jwtConfig.getRefreshTokenExpiration()))
                .signWith(jwtConfig.getRefreshKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public void validateAccessToken(String token, int expectedTokenVersion) throws JwtException {
        try {
            Claims claims = parseClaimsFromJwtToken(token, jwtConfig.getAccesKey());
            assertType(claims, "access_token");
            assertTokenVersion(claims, expectedTokenVersion);
        } catch (Exception e) {
            throw new JwtException(e.getMessage());
        }
    }

    public void validateRefreshToken(String token) throws JwtException {
        try {
            Claims claims = parseClaimsFromJwtToken(token, jwtConfig.getRefreshKey());
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

    /**
     * get time to live from claims
     * @param claims
     * @return
     */
    public Duration ttlUntilExpiration(Claims claims) {

        Date expiration = claims.getExpiration();
        long now = System.currentTimeMillis();

        long ttlMillis = expiration.getTime() - now;

        if (ttlMillis <= 0) {
            return Duration.ZERO;
        }

        return Duration.ofMillis(ttlMillis);
    }

    public UserDetail getUserDetailFromAccessToken(String token) throws InvalidTokenException {
        UserDetail userDetail = null;
        try {
            Claims claims = parseClaimsFromJwtToken(token, jwtConfig.getAccesKey());

            return new UserDetail(
                    claims.getSubject(),
                    claims.get("roles", List.class));
        }catch (Exception e) {
            throw new InvalidTokenException(e.getMessage());
        }
    }
    public Claims parseClaimsFromJwtToken(String token, SecretKey secretKey) throws JwtException {
        Claims claims = null;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (Exception e) {
            throw new JwtException(e.getMessage());
        }
        return claims;
    }
}
