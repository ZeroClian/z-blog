package cn.github.zeroclian.distributedlock.annotation;

import cn.github.zeroclian.distributedlock.strategy.LockKeyStrategy;
import cn.github.zeroclian.distributedlock.strategy.LockStrategy;

import java.lang.annotation.*;

/**
 * 分布式锁
 *
 * @Author: ZeroClian
 * @Date: 2021-12-09 11:03 上午
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributeLock {

    String lockKey() default "";

    /**
     * 锁的有效时间，单位秒
     */
    long timeOut() default 60;

    /**
     * 使用第几个参数作为 key 的前缀
     */
    String param() default "";

    /**
     * 生成锁的key的策略，默认自定义key
     */
    LockKeyStrategy keyStrategy() default LockKeyStrategy.CUSTOM;

    /**
     * 获取锁的策略，默认进行自旋重试
     */
    LockStrategy strategy() default LockStrategy.RETRY;

}
