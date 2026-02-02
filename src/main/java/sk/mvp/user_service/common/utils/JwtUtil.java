package sk.mvp.user_service.common.utils;

import io.jsonwebtoken.*;
import sk.mvp.user_service.auth.dto.UserDetail;
import sk.mvp.user_service.common.exception.InvalidTokenException;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.List;

public class JwtUtil {

    // Generate JWT token
    public static String generateAccessToken(String username,
                                             int tokenVersion,
                                             String jti,
                                             String[] roles,
                                             SecretKey accessKey,
                                             long accesTokenExp) {
        return Jwts.builder()
                .setId(jti)
                .setSubject(username)
                .setIssuedAt(new Date())
                .claim("type", "access_token")
                .claim("version", tokenVersion)
                .claim("roles", roles)
                .setExpiration(new Date((new Date()).getTime() + accesTokenExp))
                .signWith(accessKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // Generate JWT token
    public static String generateRefreshToken(String jti, SecretKey refreshKey, long refresTokenExp) {
        return Jwts.builder()
                .setId(jti)
                .claim("type", "refresh_token")
                .setExpiration(new Date((new Date()).getTime() + refresTokenExp))
                .signWith(refreshKey, SignatureAlgorithm.HS256)
                .compact();
    }
    public static void validateAccessToken(String token, int expectedTokenVersion,SecretKey accessKey) throws JwtException {
        try {
            Claims claims = parseClaimsFromJwtToken(token, accessKey);
            assertType(claims, "access_token");
            assertTokenVersion(claims, expectedTokenVersion);
        } catch (Exception e) {
            throw new JwtException(e.getMessage());
        }
    }

    public static void validateRefreshToken(String token, SecretKey refreshKey) throws JwtException {
        try {
            Claims claims = parseClaimsFromJwtToken(token, refreshKey);
            assertType(claims, "refresh_token");
        } catch (Exception e) {
            throw new JwtException(e.getMessage());
        }
    }

    private static void assertType(Claims claims, String expected) {
        String type = claims.get("type", String.class);
        if (!expected.equals(type)) {
            throw new JwtException("Invalid token type");
        }
    }
    private static void assertTokenVersion(Claims claims, int expectedVersion) {
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
    public static Duration ttlUntilExpiration(Claims claims) {

        Date expiration = claims.getExpiration();
        long now = System.currentTimeMillis();

        long ttlMillis = expiration.getTime() - now;

        if (ttlMillis <= 0) {
            return Duration.ZERO;
        }

        return Duration.ofMillis(ttlMillis);
    }

    public static UserDetail getUserDetailFromAccessToken(String token, SecretKey accessKey) throws InvalidTokenException {
        UserDetail userDetail = null;
        try {
            Claims claims = parseClaimsFromJwtToken(token, accessKey);

            return new UserDetail(
                    claims.getSubject(),
                    claims.get("roles", List.class));
        }catch (Exception e) {
            throw new InvalidTokenException(e.getMessage());
        }
    }
    public static Claims parseClaimsFromJwtToken(String token, SecretKey secretKey) throws JwtException {
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
