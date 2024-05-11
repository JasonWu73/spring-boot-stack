package net.wuxianjie.myspringbootstarter.util;

import java.nio.charset.StandardCharsets;

import org.springframework.util.StringUtils;

public class StrUtils {

    /**
     * 按如下规则生成用于数据库模糊查询的字符串值。
     *
     * <p>当 {@code val} 不为空时，则将字符串中任意数量的 {@code 空白字符} 替换为 {@code %}，例如：</p>
     *
     * <pre>{@code
     *  "  KeyOne    KeyTwo  " -> "%KeyOne%KeyTwo%"
     * }</pre>
     *
     * @param val 原始值
     * @return 如果 {@code val} 非空，则返回模糊查询字符串，否则返回 {@code null}
     */
    public static String toLikeVal(String val) {
        if (!StringUtils.hasText(val)) return null;
        return "%" + val.trim().replaceAll(" +", "%") + "%";
    }

    /**
     * 将字符串转换为 UTF-8 编码的字节数组。
     */
    public static byte[] toUtf8Bytes(String text) {
        if (!StringUtils.hasText(text)) return new byte[0];
        return text.getBytes(StandardCharsets.UTF_8);
    }
}
