package net.wuxianjie.webkit.page;

import java.util.List;

/**
 * 分页查询结果。
 *
 * @param page 当前页码
 * @param size 每页的结果数量
 * @param total 总的结果数量
 * @param list 数据列表
 * @param <T> 列表项的泛型类型参数
 */
public record PageResult<T>(int page, int size, long total, List<T> list) {
}