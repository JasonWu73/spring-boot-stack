package net.wuxianjie.commonkit.page;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页查询响应结果。
 *
 * @param <T> 列表项的泛型类型参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    /**
     * 数据列表。
     */
    private List<T> list;

    /**
     * 总条数。
     */
    private long total;

    /**
     * 偏移量，通常结合每页条数使用，比如
     * {@code url?offset=10&limit=20} 表示从第 11 条记录开始查询 20 条。
     */
    private long offset;

    /**
     * 每页条数。
     */
    private long limit;

}
