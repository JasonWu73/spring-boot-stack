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

    /**
     * Spring IoC 容器上下文。
     */
    public static ApplicationContext ctx;

    /**
     * 获取 Spring 管理的 Bean 实例。
     */
    public static <T> T getBean(Class<T> beanClass) {
        return ctx.getBean(beanClass);
    }

    /**
     * 获取 Spring 管理的 Bean 实例。
     */
    public static Object getBean(String beanName) {
        return ctx.getBean(beanName);
    }

    @Override
    public void setApplicationContext(
        @SuppressWarnings("NullableProblems") ApplicationContext ctx
    ) throws BeansException {
        SpringUtils.ctx = ctx;
    }
}
