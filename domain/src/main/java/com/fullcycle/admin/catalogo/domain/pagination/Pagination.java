package com.fullcycle.admin.catalogo.domain.pagination;

import java.util.List;
import java.util.function.Function;

public record Pagination<T>(
    int currentPage,
    int perPage,
    long total,
    List<T> items) {


    public <R> Pagination<R> map(final Function<T, R> mapper) {
        return new Pagination<>(
            currentPage(),
            perPage(),
            total(),
            this.items.stream().map(mapper).toList()
        );
    }

}
