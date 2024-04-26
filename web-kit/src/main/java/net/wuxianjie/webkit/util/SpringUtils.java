package net.wuxianjie.webkit.util;

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

    private static ApplicationContext context;

    /**
     * 获取 Spring 管理的 Bean 实例。
     *
     * @param clazz Bean 的类类型
     * @param <T> Bean 的泛型类型参数
     * @return Spring IoC 容器中的 Bean 实例
     */
    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext ctx) throws BeansException {
        SpringUtils.context = ctx;
    }
}