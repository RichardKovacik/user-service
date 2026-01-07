package sk.mvp.user_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    @Value("${jwt.acces_token.expiration}")
    private long accesTokenExpiration;
    @Value("${jwt.refresh_token.expiration}")
    private long refreshTokenExpiration;
    @Value("${jwt.secret}")
    private String secret;

    public long getAccesTokenExpiration() {
        return accesTokenExpiration;
    }

    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    public String getSecret() {
        return secret;
    }
}
