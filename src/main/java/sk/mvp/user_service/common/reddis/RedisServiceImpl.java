package sk.mvp.user_service.common.reddis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

@Service
public class RedisServiceImpl implements IRedisService {
    private final RedisTemplate<String, String> redisTemplate;

    public RedisServiceImpl(RedisTemplate<String, String>  redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void set(String key, String value, Duration duration) {
        redisTemplate.opsForValue().setIfAbsent(key, value, duration);

    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void addValueToSet(String key, String value) {
        redisTemplate.opsForSet().add(key, value);
    }

    @Override
    public Set<String> getSet(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    @Override
    public void deleteValueFromSet(String key, String value) {
        redisTemplate.opsForSet().remove(key, value);
    }

    @Override
    public Optional<String> get(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    @Override
    public boolean has(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
