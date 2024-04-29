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
    private int pageNum;

    /**
     * 每页条数。
     */
    @Min(value = 1, message = "每页条数不能小于 1")
    private int pageSize;

    /**
     * 偏移量。
     *
     * <pre>{@code
     * select * from table_name limit #{offset}, #{pageSize}
     *
     * select * from table_name limit #{pageSize} offset #{offset}
     * }</pre>
     */
    private int offset;

    /**
     * 排序列。
     */
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "列名只能包含字母、数字和下划线")
    private String sortColumn;

    /**
     * 是否降序。
     */
    private boolean desc;

    /**
     * 构造分页查询参数。
     *
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param sortColumn 排序列
     * @param desc 是否降序
     */
    public PageQuery(int pageNum, int pageSize, String sortColumn, Boolean desc) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.offset = (pageNum - 1) * pageSize;
        this.sortColumn = sortColumn;
        this.desc = desc != null && desc;
    }

}