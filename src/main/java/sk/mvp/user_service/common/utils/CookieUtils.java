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
    public static Cookie removed(String key, String domain, boolean isHttpOnly) {
        Cookie cookie = new Cookie(key, null);
        cookie.setMaxAge(0);
        cookie.setDomain(domain);
        cookie.setPath("/");
        cookie.setHttpOnly(isHttpOnly);
        return cookie;
    }
}
