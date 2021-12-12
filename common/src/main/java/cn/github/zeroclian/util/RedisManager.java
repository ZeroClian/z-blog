package cn.github.zeroclian.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Redis 工具类
 *
 * @Author: ZeroClian
 * @Date: 2021-10-09 6:04 下午
 */
@SuppressWarnings(value = {"unchecked", "rawtypes"})
public class RedisManager {

    private static final Logger log = LoggerFactory.getLogger(RedisManager.class);

    private static RedisTemplate getRedisTemplate() {
        RedisTemplate<String, Object> redisTemplate = (RedisTemplate<String, Object>) SpringManager.getBean("redisTemplate");
        if (redisTemplate == null) {
            throw new RuntimeException("请务必保证spring容器中存在RedisTemplate的实例");
        }
        return redisTemplate;
    }

    /**
     * 写入缓存
     *
     * @param str 键
     * @param o   值
     */
    public static Boolean set(String str, Object o) {
        boolean result = false;
        try {
            ValueOperations<String, Object> operations = getRedisTemplate().opsForValue();
            operations.set(str, o);
            result = true;
        } catch (Exception e) {
            log.error("set cache error", e);
        }
        return result;
    }

    public static <T> T get(final String key) {
        return (T) getRedisTemplate().opsForValue().get(key);
    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key 键
     * @return
     */
    public static Boolean exists(final String key) {
        return getRedisTemplate().hasKey(key);
    }

    /**
     * 执行lua脚本
     *
     * @param script lua脚本
     * @param keys   键
     * @param value  值
     * @return
     */
    public static Object executeLuaScript(RedisScript script, List<String> keys, Object... value) {
        return getRedisTemplate().execute(script, keys, value);
    }


    /**
     * key不存在时设置，成功返回1，失败返回0
     *
     * @param key     键
     * @param value   值
     * @param timeOut 过期时间
     * @return
     */
    public static Boolean setIfAbsent(final String key, final Object value, long timeOut) {
        return getRedisTemplate().opsForValue().setIfAbsent(key, value, timeOut, TimeUnit.SECONDS);
    }
}
