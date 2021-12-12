package cn.github.zeroclian.distributedlock.lock;

import cn.github.zeroclian.distributedlock.strategy.LockStrategy;
import cn.github.zeroclian.util.JSON;
import cn.github.zeroclian.util.RedisManager;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基于redis的分布式可重入(非公平)自旋锁
 * 1.   只有一个客户端 (唯一标识为 mac地址+jvm的id+thread的id) 能获取锁
 * 2.   客户端崩溃后超出规定时间后锁也会自动解除
 * 3.   同一客户端在请求到锁后可重复获取锁
 * 4.   加锁和解锁必须是同一个客户端
 * 5.   非公平锁
 * 6.   如果客户端没有崩溃,守护线程会在锁超时时间的2/3阶段进行锁的续期
 *
 * @Author: qiyiguo
 * @Date: 2021-12-09 3:53 下午
 */
public class RedisReentrantLock {

    private final Logger log = LoggerFactory.getLogger(RedisReentrantLock.class);
    /**
     * 同个线程的重入次数
     */
    private final ThreadLocal<AtomicInteger> lockCount;
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
     * 获取锁的策略
     */
    private final LockStrategy strategy;
    /**
     * 自旋间隔 单位毫秒
     */
    private static final long SPIN_TIME = 500;
    /**
     * 释放redis锁成功的标识
     */
    private final long REDIS_UNLOCK_SUCCESS = 1;
    /**
     * 释放锁的lua脚本
     */
    private final String REDIS_UNLOCK_LUA
            = " if redis.call('get',KEYS[1]) == ARGV[1] " +
            " then " +
            " return redis.call('del',KEYS[1]) " +
            " else " +
            " return 0 end";

    public RedisReentrantLock(String lockKey, String lockValue, long timeOut, LockStrategy strategy) {
        this.lockKey = lockKey;
        this.lockValue = lockValue;
        this.timeOut = timeOut;
        this.strategy = strategy;
        //初始化当前线程的重入次数
        lockCount = ThreadLocal.withInitial(AtomicInteger::new);
    }

    public void lock() throws Exception {
        Boolean getLock = RedisManager.setIfAbsent(lockKey, lockValue, timeOut);
        if (!getLock) {
            if (isLockOwner(lockKey)) {
                int count = lockCount.get().incrementAndGet();
                log.debug("重入锁[ {} ] 成功,当前LockCount: {}", lockKey, count);
                getLock = true;
            }
        }
        switch (strategy) {
            case RETRY:
                while (!getLock) {
                    log.debug("获取锁[ {}-{} ]失败", lockKey, lockValue);
                    getLock = RedisManager.setIfAbsent(lockKey, lockValue, timeOut);
                    Thread.sleep(SPIN_TIME);
                }
                log.debug("获取锁[ {}-{} ]成功", lockKey, lockValue);
                break;
            case ONCE:
                if (!getLock) {
                    throw new Exception("获取锁 " + lockKey + " 失败 , 退出方法");
                }
                log.debug("获取锁[ {}-{} ]成功", lockKey, lockValue);
                break;
            default:
                break;
        }
    }

    public void unlock() {
        int count = lockCount.get().get();
        if (count == 0) {
            if (execUnlockScript(lockKey, RedisReentrantLockUtils.getLockValue())) {
                log.debug("释放锁 [ {}-{} ] 成功", lockKey, RedisReentrantLockUtils.getLockValue());
            } else {
                log.debug("释放锁 [ {}-{} ] 失败", lockKey, RedisReentrantLockUtils.getLockValue());
            }
        } else {
            lockCount.get().decrementAndGet();
            log.debug("重入[ {} ] 锁 LockCount -1 ,当前lockCount : {}", lockKey, lockCount);
        }
    }

    /**
     * 使用lua脚本释放锁
     *
     * @param lockKey   锁键
     * @param lockValue 锁值
     */
    private Boolean execUnlockScript(String lockKey, String lockValue) {
        DefaultRedisScript unlockScript = new DefaultRedisScript();
        unlockScript.setResultType(Long.class);
        unlockScript.setScriptText(REDIS_UNLOCK_LUA);
        List<String> keys = Collections.singletonList(lockKey);
        return (Long) RedisManager.executeLuaScript(unlockScript, keys, lockValue) == REDIS_UNLOCK_SUCCESS;
    }

    /**
     * 是否锁的拥有者
     *
     * @param lockKey 锁键
     */
    private Boolean isLockOwner(String lockKey) {
        if (RedisManager.exists(lockKey)) {
            String lockValueJsonString = RedisManager.get(lockKey);
            JsonNode lockValue = JSON.parse(lockValueJsonString, JsonNode.class);
            // return RedisReentrantLockUtils.getMacAddress().equals(lockValue.get(RedisReentrantLockUtils.MAC).textValue()) &&
            assert lockValue != null;
            return RedisReentrantLockUtils.getJvmId() == lockValue.get(RedisReentrantLockUtils.JVM).intValue() &&
                    RedisReentrantLockUtils.getThreadId() == lockValue.get(RedisReentrantLockUtils.THREAD).intValue();
        }
        return false;
    }

}
