package sk.mvp.user_service.service.redis;

import java.time.Duration;
import java.util.Optional;

public interface IRedisService {
    void set(String key, String value, Duration duration);

    Optional<String> get(String key);

    boolean has(String key);
}
