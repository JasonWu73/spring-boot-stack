package net.wuxianjie.myspringbootstarter.util;

import java.nio.charset.StandardCharsets;

import org.springframework.util.StringUtils;

public class StrUtils {

    /**
     * 按如下规则生成用于数据库模糊查询的字符串值。
     *
     * <p>当 {@code value} 不为空时，则将字符串中任意数量的 {@code 空白字符} 替换为 {@code %}，例如：</p>
     *
     * <pre>{@code
     *  "  KeyOne    KeyTwo  " -> "%KeyOne%KeyTwo%"
     * }</pre>
     *
     * @param value 原始值
     * @return 如果 {@code value} 非空，则返回模糊查询字符串，否则返回 {@code null}
     */
    public static String toLikeValue(String value) {
        if (!StringUtils.hasText(value)) return null;
        return "%" + value.trim().replaceAll(" +", "%") + "%";
    }

    /**
     * 将字符串转换为 UTF-8 编码的字节数组。
     */
    public static byte[] toUtf8Bytes(String text) {
        if (!StringUtils.hasText(text)) return new byte[0];
        return text.getBytes(StandardCharsets.UTF_8);
    }
}
