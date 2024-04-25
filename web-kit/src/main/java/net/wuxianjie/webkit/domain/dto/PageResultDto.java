package net.wuxianjie.webkit.domain.dto;

import java.util.List;

/**
 * 分页查询结果。
 *
 * @param pageNum 页码
 * @param pageSize 每页条数
 * @param total 总条数
 * @param list 数据列表
 * @param <T> 列表项的泛型类型参数
 */
public record PageResultDto<T>(int pageNum, int pageSize, long total, List<T> list) {
}