package sk.mvp.user_service.common.utils;

import io.jsonwebtoken.*;
import sk.mvp.user_service.auth.dto.QUserDetail;
import sk.mvp.user_service.common.exception.InvalidTokenException;
import sk.mvp.user_service.common.exception.QApplicationException;
import sk.mvp.user_service.common.exception.data.ErrorType;

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

    public static void validateAccessToken(Claims claims, String token, int expectedTokenVersion) {
        assertType(claims, "access_token");
        assertTokenVersion(claims, expectedTokenVersion);
    }

    public static void validateRefreshToken(String token, SecretKey refreshKey) {
        Claims claims = parseClaimsFromJwtToken(token, refreshKey);
        assertType(claims, "refresh_token");

    }

    private static void assertType(Claims claims, String expected) {
        String type = claims.get("type", String.class);
        if (!expected.equals(type)) {
            throw new QApplicationException("Token Invalid", ErrorType.TOKEN_INVALID, null);
        }
    }
    private static void assertTokenVersion(Claims claims, int expectedVersion) {
        int tokenVersion = claims.get("version", Integer.class);
        if (tokenVersion != expectedVersion) {
            throw new QApplicationException("Token version mismatch", ErrorType.TOKEN_INVALID, null);
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

    public static QUserDetail getUserDetailFromAccessToken(String token, SecretKey accessKey)  {
        QUserDetail QUserDetail = null;
        Claims claims = parseClaimsFromJwtToken(token, accessKey);

        return new QUserDetail(
                claims.getSubject(),
                claims.get("roles", List.class));
    }
    public static Claims parseClaimsFromJwtToken(String token, SecretKey secretKey) {
        Claims claims = null;
            claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        return claims;
    }
}
