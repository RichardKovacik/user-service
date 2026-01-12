package sk.mvp.user_service.common.reddis;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

public interface IRedisService {
    void set(String key, String value, Duration duration);
    void delete(String key);
    void addValueToSet(String key, String value);
    Set<String> getSet(String key);
    void deleteValueFromSet(String key, String value);

    Optional<String> get(String key);

    boolean has(String key);
}
