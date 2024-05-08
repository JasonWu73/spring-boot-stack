package net.wuxianjie.commonkit.util;

import lombok.NonNull;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Spring 上下文工具类。
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

    @Override
    public void setApplicationContext(
        @NonNull ApplicationContext applicationContext
    ) throws BeansException {
        SpringUtils.applicationContext = applicationContext;
    }

}
