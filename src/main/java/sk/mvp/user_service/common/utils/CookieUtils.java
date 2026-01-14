package sk.mvp.user_service.common.utils;

import jakarta.servlet.http.Cookie;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

public class CookieUtils {
    public static ResponseCookie create(
            String key, String value, String domain,
            Duration maxAge, boolean isHttpOnly, boolean isSecure, String path, String sameSite) {
        return ResponseCookie.from(key, value)
                .domain(domain)
                .maxAge(maxAge)
                .httpOnly(isHttpOnly)
                .secure(isSecure)
                .path(path)
                .sameSite(sameSite)
                .build();
    }
    public static ResponseCookie removed(String key, String domain, boolean isHttpOnly) {
        return ResponseCookie.from(key, "")
                .maxAge(Duration.ZERO)
                .domain(domain)
                .path("/")
                .httpOnly(isHttpOnly)
                .build();
    }
}
