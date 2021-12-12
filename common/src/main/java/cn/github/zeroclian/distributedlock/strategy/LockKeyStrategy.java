package cn.github.zeroclian.distributedlock.strategy;

/**
 * 生成key的策略
 *
 * @Author: qiyiguo
 * @Date: 2021-12-09 10:00 上午
 */
public enum LockKeyStrategy {
    /**
     * 直接使用方法签名
     */
    METHOD,
    /**
     * 使用用户信息 + 方法签名
     */
    USER_METHOD,
    /**
     * 自定义
     */
    CUSTOM
}
