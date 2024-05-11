package net.wuxianjie.myspringbootstarter.page;

import java.util.List;
import java.util.Objects;

public class PageResponse<T> {

    private List<T> list;
    private long total;
    private long offset;
    private long limit;

    public PageResponse() {
    }

    public PageResponse(List<T> list, long total, long offset, long limit) {
        this.list = list;
        this.total = total;
        this.offset = offset;
        this.limit = limit;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageResponse<?> that = (PageResponse<?>) o;
        return total == that.total && offset == that.offset && limit == that.limit && Objects.equals(list, that.list);
    }

    @Override
    public int hashCode() {
        return Objects.hash(list, total, offset, limit);
    }

    @Override
    public String toString() {
        return "PageResponse{" +
            "list=" + list +
            ", total=" + total +
            ", offset=" + offset +
            ", limit=" + limit +
            '}';
    }
}
