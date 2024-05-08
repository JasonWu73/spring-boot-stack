package net.wuxianjie.commonkit.util;

import org.springframework.util.StringUtils;

/**
 * 数据库工具类。
 */
public class DatabaseUtils {

    /**
     * 按如下规则生成用于数据库模糊查询的字符串值。
     *
     * <p>当 {@code value} 不为空时，则将字符串中任意数量的
     * {@code 空白字符} 替换为 {@code %}，例如：</p>
     *
     * <pre>{@code
     *  "  KeyOne    KeyTwo  " -> "%KeyOne%KeyTwo%"
     * }</pre>
     *
     * @param value 原始值
     * @return 如果 {@code value} 非空（即至少包含一个非空字符），则返回相应字符串，
     * 否则返回 {@code null}
     */
    public static String toLikeValue(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return "%" + value.trim().replaceAll(" +", "%") + "%";
    }

}
