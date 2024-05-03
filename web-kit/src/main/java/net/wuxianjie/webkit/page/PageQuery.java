package net.wuxianjie.webkit.page;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

import lombok.Data;

/**
 * 分页查询参数。
 */
@Data
public class PageQuery {

    /**
     * 页码。
     */
    @Min(value = 1, message = "页码不能小于 1")
    private int page;

    /**
     * 每页的结果数量。
     */
    @Min(value = 1, message = "每页的结果数量不能小于 1")
    private int size;

    /**
     * 偏移量，从数据集的哪个位置开始返回结果。
     *
     * <pre>{@code
     * select * from table_name limit #{offset}, #{size}
     *
     * select * from table_name limit #{size} offset #{offset}
     * }</pre>
     */
    private int offset;

    /**
     * 按照哪个列排序。
     */
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "列名只能包含字母、数字和下划线")
    private String sortBy;

    /**
     * 是否降序排序。
     */
    private boolean desc;

    /**
     * 构造分页查询参数。
     *
     * @param page 页码
     * @param size 每页的结果数量
     * @param sortBy 按照哪个列排序
     * @param desc 是否降序排序
     */
    public PageQuery(int page, int size, String sortBy, Boolean desc) {
        this.page = page;
        this.size = size;
        this.offset = (page - 1) * size;
        this.sortBy = sortBy;
        this.desc = desc != null && desc;
    }

}