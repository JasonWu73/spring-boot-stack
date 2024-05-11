package net.wuxianjie.myspringbootstarter.page;

import java.util.Objects;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public class PageRequest {

    @Min(value = 0, message = "偏移量不能小于 0")
    private int offset;

    @Min(value = 1, message = "每页条数不能小于 1")
    private int limit;

    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "排序字段只能包含字母、数字和下划线")
    private String sortBy;

    private boolean desc;

    public int getOffset() {
        return offset;
    }

    public void setOffset(@Min(value = 0, message = "偏移量不能小于 0") int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(@Min(value = 1, message = "每页条数不能小于 1") int limit) {
        this.limit = limit;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public boolean isDesc() {
        return desc;
    }

    public void setDesc(boolean desc) {
        this.desc = desc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageRequest that = (PageRequest) o;
        return offset == that.offset && limit == that.limit && desc == that.desc && Objects.equals(sortBy, that.sortBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(offset, limit, sortBy, desc);
    }

    @Override
    public String toString() {
        return "PageRequest{" +
            "offset=" + offset +
            ", limit=" + limit +
            ", sortBy='" + sortBy + '\'' +
            ", desc=" + desc +
            '}';
    }
}
