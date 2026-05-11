package com.travel.pojo.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
@AllArgsConstructor
public class PageResult<T> {
    private List<T> list;
    private int page;
    private int pageSize;
    private long total;
    private long totalPages;

    public static <T> PageResult<T> of(List<T> list, int page, int pageSize, long total) {
        long safeTotalPages = pageSize <= 0 ? 0 : (total + pageSize - 1) / pageSize;
        return new PageResult<>(list, page, pageSize, total, safeTotalPages);
    }

    public static <T> PageResult<T> empty(int page, int pageSize) {
        return new PageResult<>(Collections.emptyList(), page, pageSize, 0, 0);
    }
}
