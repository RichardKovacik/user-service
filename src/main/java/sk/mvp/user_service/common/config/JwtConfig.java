package sk.mvp.user_service.common.config;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtConfig {
    @Value("${jwt.cookie.isSecure}")
    private boolean cookieIsSecure;
    @Value("${jwt.cookie.isHttpOnly}")
    private boolean cookieIsHttpOnly;
    @Value("${jwt.cookie.sameSite}")
    private String coikieSameSite;
    @Value("${jwt.cookie.domain}")
    private String cookieDomain;
    @Value("${jwt.acces_token.expiration}")
    private long accesTokenExpiration;
    @Value("${jwt.refresh_token.expiration}")
    private long refreshTokenExpiration;
    @Value("${jwt.cookie.refreshToken.path}")
    private String refreshTokenCookiePath;
    @Value("${jwt.secret}")
    private String secret;
    private SecretKey accesKey;
    private SecretKey refreshKey;

    // Initializes the key after the class is instantiated
    @PostConstruct
    private void init() {
        this.accesKey = Keys.hmacShaKeyFor(getSecret().getBytes(StandardCharsets.UTF_8));
        this.refreshKey = Keys.hmacShaKeyFor(getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public long getAccesTokenExpiration() {
        return accesTokenExpiration;
    }

    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    public String getSecret() {
        return secret;
    }

    public SecretKey getAccesKey() {
        return accesKey;
    }

    public SecretKey getRefreshKey() {
        return refreshKey;
    }

    public String getCookieDomain() {
        return cookieDomain;
    }

    public boolean isCookieIsSecure() {
        return cookieIsSecure;
    }

    public boolean isCookieIsHttpOnly() {
        return cookieIsHttpOnly;
    }

    public String getCoikieSameSite() {
        return coikieSameSite;
    }

    public String getRefreshTokenCookiePath() {
        return refreshTokenCookiePath;
    }
}
