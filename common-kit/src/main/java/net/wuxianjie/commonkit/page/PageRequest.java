package net.wuxianjie.commonkit.page;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页查询请求参数。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {

    /**
     * 偏移量，通常结合每页条数使用，比如
     * {@code url?offset=10&limit=20} 表示从第 11 条记录开始查询 20 条。
     *
     * <p>如 MySQL 中查询语句如下：</p>
     *
     * <pre>{@code
     * select * from table_name limit #{offset}, #{limit}
     * // 或
     * select * from table_name limit #{limit} offset #{offset}
     * }</pre>
     */
    @Min(value = 0, message = "偏移量不能小于 0")
    private int offset;

    /**
     * 每页条数。
     */
    @Min(value = 1, message = "每页条数不能小于 1")
    private int limit;

    /**
     * 排序字段。
     */
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "排序字段只能包含字母、数字和下划线")
    private String sortBy;

    /**
     * 排序方式，是否降序。
     */
    private boolean desc;

}
