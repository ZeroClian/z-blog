package cn.github.zeroclian.distributedlock.lock;

import cn.github.zeroclian.util.RedisManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.List;

/**
 * redis 分布式锁的守护线程，用于延长锁的有效期
 *
 * @Author: qiyiguo
 * @Date: 2021-12-09 3:14 下午
 */
@Slf4j
public class RedisReentrantLockDaemon implements Runnable {

    /**
     * 操作成功对比标志
     */
    private static final long REDIS_EXPIRE_SUCCESS = 1;

    /**
     * 锁键
     */
    private final String lockKey;
    /**
     * 锁值
     */
    private final String lockValue;
    /**
     * 过期时间
     */
    private final long timeOut;
    /**
     * 守护线程开关标志
     */
    private volatile Boolean signal;

    /**
     * 延时释放redis锁的Lua脚本
     */
    private final String REDIS_EXPIRE_LUA
            = " if redis.call('get',KEYS[1]) == ARGV[1] " +
            " then " +
            " return redis.call('expire',KEYS[1],ARGV[2]) " +
            " else " +
            " return 0 end";

    public RedisReentrantLockDaemon(String lockKey, String lockValue, long timeOut) {
        this.lockKey = lockKey;
        this.lockValue = lockValue;
        this.timeOut = timeOut;
        this.signal = true;
    }

    @Override
    public void run() {
        log.debug(">>>>>>守护线程启动");
        long waitTime = timeOut * 1000 * 2 / 3;
        while (signal) {
            try {
                Thread.sleep(waitTime);
                if (execExpandTimeScript(lockKey, lockValue, timeOut)) {
                    log.debug("锁 [ {} ] 延期成功", lockKey);
                } else {
                    log.debug("锁 [ {} ] 延期失败", lockKey);
                    this.stop();
                }
            } catch (InterruptedException e) {
                log.debug("锁 [ {} ] 的守护线程被中断", lockKey);
            }
        }
    }

    /**
     * 使用lua脚本进行过期时间延长
     */
    private Boolean execExpandTimeScript(String lockKey, String lockValue, long timeOut) {
        DefaultRedisScript unLockScript = new DefaultRedisScript();
        unLockScript.setResultType(Long.class);
        unLockScript.setScriptText(REDIS_EXPIRE_LUA);
        List<String> keys = Collections.singletonList(lockKey);
        return (Long) RedisManager.executeLuaScript(unLockScript, keys, lockValue, timeOut) == REDIS_EXPIRE_SUCCESS;
    }

    /**
     * 停止守护线程
     */
    private void stop() {
        this.signal = false;
    }
}
