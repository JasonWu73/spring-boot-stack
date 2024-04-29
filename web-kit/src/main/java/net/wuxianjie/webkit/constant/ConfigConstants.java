package net.wuxianjie.webkit.constant;

import java.nio.charset.StandardCharsets;

import org.springframework.http.MediaType;

/**
 * 配置常量。
 */
public final class ConfigConstants {

    /**
     * 系统中对于日期字符串的统一格式。
     * <p>
     * 对于仅包含日期的字符串，只有 {@code yyyy-MM-dd} 一种格式，故不再单独定义。
     */
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 包含 UTF-8 字符编码的 JSON MIME 类型。
     */
    public static final String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";

    /**
     * JSON MIME 类型字符串 {@value #APPLICATION_JSON_UTF8_VALUE} 的 {@link MediaType} 对象。
     */
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(
            "application", "json", StandardCharsets.UTF_8
    );

}