package cn.github.zeroclian.distributedlock.lock;

import cn.github.zeroclian.distributedlock.annotation.DistributeLock;
import cn.github.zeroclian.distributedlock.strategy.LockKeyStrategy;
import cn.github.zeroclian.util.JSON;
import org.apache.logging.log4j.util.Strings;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import sun.management.VMManagement;

import javax.servlet.http.HttpServletRequest;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.HashMap;

/**
 * 锁的工具类
 * 1.生成key、value
 * 2.获取分布式注解
 *
 * @Author: qiyiguo
 * @Date: 2021-12-09 10:16 上午
 */
public class RedisReentrantLockUtils {

    /**
     * mac地址
     */
    public static final String MAC = "macAddress";
    /**
     * 虚拟机ID
     */
    public static final String JVM = "jvmPid";
    /**
     * 线程ID
     */
    public static final String THREAD = "threadId";

    public static String getLockKey(ProceedingJoinPoint joinPoint) {
        DistributeLock distributeLock = getDistributeLock(joinPoint);
        StringBuffer lockKey = new StringBuffer();
        //key前缀
        if (Strings.isNotBlank(distributeLock.param())) {
            Object[] args = joinPoint.getArgs();
            try {
                Integer index = new Integer(distributeLock.param());
                Object prefix = args[index];
                if (args.length >= index) {
                    lockKey.append(prefix.toString()).append("@");
                }
            } catch (Exception e) {
            }
        }
        //根据策略生成key
        LockKeyStrategy lockKeyStrategy = distributeLock.keyStrategy();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String signature = getSignature(methodSignature.getMethod());
        switch (lockKeyStrategy) {
            case USER_METHOD:
                // 获取请求头中的token
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                return lockKey.append(request.getHeader(HttpHeaders.AUTHORIZATION) + "@" + signature).toString();
            case CUSTOM:
                return lockKey.append(Strings.isNotBlank(distributeLock.lockKey()) ? distributeLock.lockKey() : signature).toString();
            case METHOD:
            default:
                return lockKey.append(signature).toString();
        }
    }

    /**
     * mac地址 + 虚拟机ID + 线程ID
     */
    public static String getLockValue() {
        HashMap<String, Object> map = new HashMap<>(3);
        // map.put(MAC, getMacAddress());
        map.put(JVM, getJvmId());
        map.put(THREAD, getThreadId());
        return JSON.toJSON(map);
    }

    public static String getMacAddress() {
        String address = "";
        try {
            // 获取localhost的网卡设备
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            // 获取硬件地址
            byte[] hardwareAddress = networkInterface.getHardwareAddress();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < hardwareAddress.length; i++) {
                builder.append(String.format("%02X%s", hardwareAddress[i], (i < hardwareAddress.length - 1) ? "-" : ""));
            }
            address = builder.toString();
        } catch (Exception e) {
            return "";
        }
        return address;
    }

    public static Integer getJvmId() {
        try {
            RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
            Field jvm = runtimeMxBean.getClass().getDeclaredField("jvm");
            jvm.setAccessible(true);
            VMManagement vmManagement = (VMManagement) jvm.get(runtimeMxBean);
            Method pidMethod = vmManagement.getClass().getDeclaredMethod("getProcessId");
            pidMethod.setAccessible(true);
            return (Integer) pidMethod.invoke(vmManagement);
        } catch (Exception e) {
            return -1;
        }
    }

    public static long getThreadId() {
        return Thread.currentThread().getId();
    }

    /**
     * 获取分布式锁注解
     *
     * @param joinPoint 切入点
     * @return {@link DistributeLock} 锁
     */
    public static DistributeLock getDistributeLock(JoinPoint joinPoint) {
        // 获取被增强的方法相关信息
        // getSignature()：修饰符+ 包名+组件名(类名) +方法名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 作用目标类是否有锁
        Class<?> targetClass = joinPoint.getTarget().getClass();
        DistributeLock distributeLock = targetClass.getAnnotation(DistributeLock.class);
        if (distributeLock == null) {
            //获取方法上的锁
            Method method = signature.getMethod();
            distributeLock = method.getAnnotation(DistributeLock.class);
            return distributeLock;
        } else {
            return distributeLock;
        }
    }

    private static String getSignature(Method method) {
        StringBuilder builder = new StringBuilder();
        Class<?> returnType = method.getReturnType();
        if (returnType != null) {
            builder.append(returnType.getName()).append("#");
        }
        builder.append(method.getName());
        Class<?>[] parameters = method.getParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            if (i == 0) {
                builder.append(":");
            } else {
                builder.append(",");
            }
            builder.append(parameters[i].getName());
        }
        return builder.toString();
    }

}
