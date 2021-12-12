package cn.github.zeroclian.distributedlock.strategy;

/**
 * @Author: qiyiguo
 * @Date: 2021-12-09 10:00 上午
 */
public enum LockStrategy {

    /**
     * 只尝试获取一次
     */
    ONCE,
    /**
     * 重复获取直到成功
     */
    RETRY
}
