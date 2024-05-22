package net.wuxianjie.myspringbootstarter.util;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

public class RedisLockUtils {

    private static final Logger LOG = LoggerFactory.getLogger(RedisLockUtils.class);
    private static final int LOCK_TIMEOUT_SECONDS = 30;
    private static final int LOCK_CHECK_SECONDS = LOCK_TIMEOUT_SECONDS - 10;
    private static final String UNLOCK_SCRIPT = """
        if redis.call('GET', KEYS[1]) == ARGV[1] then
          return redis.call('DEL', KEYS[1])
        else
          return 0
        end""";

    private static final ConcurrentHashMap<String, Boolean> RENEW_KEYS = new ConcurrentHashMap<>();
    private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

    /**
     * 上锁，支持对锁的自动续期。
     *
     * @return 是否上锁成功
     */
    public static boolean lock(String key, String value) {
        StringRedisTemplate redisTemplate = SpringUtils.getBean(StringRedisTemplate.class);
        Boolean locked = redisTemplate.opsForValue()
            .setIfAbsent(key, value, LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        if (locked == null || !locked) return false;

        // 开启一个线程自动续期
        autoRenew(key, value);
        return true;
    }

    /**
     * 解锁。
     */
    public static void unlock(String key, String value) {
        StringRedisTemplate redisTemplate = SpringUtils.getBean(StringRedisTemplate.class);
        DefaultRedisScript<Long> script = new DefaultRedisScript<>(UNLOCK_SCRIPT, Long.class);
        Long lockDelete = redisTemplate.execute(script, List.of(key), value);
        if (lockDelete != null && lockDelete == 1) {
            RENEW_KEYS.remove(key);
        }
    }

    private static void autoRenew(String key, String value) {
        RENEW_KEYS.put(key, true);

        StringRedisTemplate redisTemplate = SpringUtils.getBean(StringRedisTemplate.class);
        THREAD_POOL.execute(() -> {
            while (RENEW_KEYS.getOrDefault(key, false)) {
                waitRenew();

                String currentValue = redisTemplate.opsForValue().get(key);
                if (!Objects.equals(currentValue, value)) {
                    RENEW_KEYS.remove(key);
                    return;
                }

                // 续期
                redisTemplate.expire(key, LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            }
        });
    }

    private static void waitRenew() {
        try {
            TimeUnit.SECONDS.sleep(LOCK_CHECK_SECONDS);
        } catch (InterruptedException e) {
            LOG.warn("Redis 分布式锁自动续期休眠异常：{}", e.getMessage());
        }
    }
}
