package net.wuxianjie.commonkit.util;

import lombok.NonNull;

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
    public static ApplicationContext applicationContext;

    /**
     * 获取 Spring 管理的 Bean 实例。
     *
     * @param beanClass Bean 的类类型
     * @param <T> Bean 的泛型类型参数
     * @return Spring IoC 容器中的 Bean 实例
     */
    public static <T> T getBean(Class<T> beanClass) {
        return applicationContext.getBean(beanClass);
    }

    /**
     * 获取 Spring 管理的 Bean 实例。
     *
     * @param beanName Bean 的名称
     * @return Spring IoC 容器中的 Bean 实例
     */
    public static Object getBean(String beanName) {
        return applicationContext.getBean(beanName);
    }

    @Override
    public void setApplicationContext(
        @NonNull ApplicationContext applicationContext
    ) throws BeansException {
        SpringUtils.applicationContext = applicationContext;
    }

}
