package net.wuxianjie.webkit.util;

import org.springframework.util.StringUtils;

/**
 * 数据库工具类。
 */
public class DbUtils {

    /**
     * 按如下规则生成数据库 LIKE 值。
     *
     * <p>当 {@code value} 不为空时，则将字符串中任意数量的 {@code 空白字符} 替换为 {@code %}，例如：</p>
     *
     * <pre>{@code
     *  "  KeyOne    KeyTwo  " -> "%KeyOne%KeyTwo%"
     * }</pre>
     *
     * @param value 原始值
     * @return 如果 {@code value} 非空（即至少包含一个非空字符），则返回符合数据库 LIKE 操作的字符串，否则返回 {@code null}
     */
    public static String toLike(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return "%" + value.trim().replaceAll(" +", "%") + "%";
    }

}