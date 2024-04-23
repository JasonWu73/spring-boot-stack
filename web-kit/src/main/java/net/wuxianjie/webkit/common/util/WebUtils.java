package net.wuxianjie.webkit.common.util;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Web 工具类。
 */
public class WebUtils {

    /**
     * 获取当前请求对象。
     *
     * @return 当前线程中的 `HttpServletRequest` 对象，如果不存在（即非 Web 环境下）则返回 `null`
     * @throws IllegalStateException 当前线程中不存在 `HttpServletRequest` 对象时抛出
     */
    public static HttpServletRequest getCurrentRequest() throws IllegalStateException {
        var attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attr == null) {
            throw new IllegalStateException("当前线程中不存在 HttpServletRequest 对象");
        }
        return attr.getRequest();
    }

}