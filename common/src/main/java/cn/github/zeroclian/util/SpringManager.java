package cn.github.zeroclian.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Author: qiyiguo
 * @Date: 2021-11-01 5:18 下午
 */
@Component
public class SpringManager implements ApplicationContextAware {

    private final Logger log = LoggerFactory.getLogger(SpringManager.class);

    /**
     * 声明一个静态变量保存
     */
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringManager.applicationContext == null) {
            SpringManager.applicationContext = applicationContext;
        }
        log.debug("========ApplicationContext配置成功,在普通类可以通过调用SpringManager.getApplicationContext()获取applicationContext对象========");
        log.debug("========applicationContext=" + SpringManager.applicationContext + "========");
    }

    /**
     * 获取applicationContext
     *
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 通过名称获取Bean
     *
     * @param name
     * @return
     */
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    /**
     * 通过名称获取Bean
     *
     * @param name
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBeanByName(String name) {
        return (T) getApplicationContext().getBean(name);
    }

    /**
     * 通过类获取Bean
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBeanByClazz(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }
}
