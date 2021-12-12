package cn.github.zeroclian.distributedlock.aop;

import cn.github.zeroclian.distributedlock.annotation.DistributeLock;
import cn.github.zeroclian.distributedlock.lock.RedisReentrantLock;
import cn.github.zeroclian.distributedlock.lock.RedisReentrantLockDaemon;
import cn.github.zeroclian.distributedlock.lock.RedisReentrantLockUtils;
import cn.github.zeroclian.distributedlock.strategy.LockStrategy;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @Author: qiyiguo
 * @Date: 2021-12-10 11:40 上午
 */
@Slf4j
@Aspect
@Component
public class DistributeLockAspect {

    /**
     * 初次重试间隔时间
     */
    private static final long RETRY_TIME = 1000;

    /**
     * 重试时间倍数
     */
    private static final long RETRY_TIME_MULTIPLE = 2;

    /**
     * 注解切点
     */
    @Pointcut()
    public void pointCut() {

    }

    @Around("@annotation(cn.github.zeroclian.distributedlock.annotation.DistributeLock)")
    public Object distributeLockAroundAop(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取注解
        DistributeLock distributeLock = RedisReentrantLockUtils.getDistributeLock(joinPoint);
        // 获取锁键
        String lockKey = RedisReentrantLockUtils.getLockKey(joinPoint);
        log.debug("分布式注解获取锁键:[ {} ]", lockKey);
        // 获取锁值
        String lockValue = RedisReentrantLockUtils.getLockValue();
        log.debug("分布式注解获取锁值:[ {} ]", lockValue);
        // 过期时间，避免死锁
        long timeOut = distributeLock.timeOut();
        // 获取锁的策略
        LockStrategy strategy = distributeLock.strategy();
        // 初始化锁
        RedisReentrantLock lock = new RedisReentrantLock(lockKey, lockValue, timeOut, strategy);
        // 初始化守护线程
        RedisReentrantLockDaemon daemon = new RedisReentrantLockDaemon(lockKey, lockValue, timeOut);
        Thread daemonThread = new Thread(daemon);
        try {
            lock.lock();
            daemonThread.setDaemon(Boolean.TRUE);
            daemonThread.start();
            return joinPoint.proceed();
        } finally {
            // daemonThread.interrupt();
            lock.unlock();
        }
    }
}
