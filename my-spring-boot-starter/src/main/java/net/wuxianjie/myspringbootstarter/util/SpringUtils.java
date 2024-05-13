package net.wuxianjie.myspringbootstarter.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring 上下文工具类。
 *
 * <p>用于在非 Spring 管理的类中访问 Spring 管理的 Bean。</p>
 */
@Component
public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext context;

    /**
     * 获取 Spring 管理的 Bean 实例。
     */
    public static <T> T getBean(Class<T> beanClass) {
        return context.getBean(beanClass);
    }

    /**
     * 获取 Spring 管理的 Bean 实例。
     */
    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }

    @Override
    public void setApplicationContext(
        @SuppressWarnings("NullableProblems") ApplicationContext context
    ) throws BeansException {
        SpringUtils.context = context;
    }
}
