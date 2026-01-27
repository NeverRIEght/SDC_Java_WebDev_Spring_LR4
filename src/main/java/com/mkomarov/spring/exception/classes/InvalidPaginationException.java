package com.mkomarov.spring.exception.classes;

import lombok.Getter;

@Getter
public class InvalidPaginationException extends RuntimeException {
    private final Integer page;
    private final Integer pageSize;

    public InvalidPaginationException(String message, Integer page, Integer pageSize) {
        super(message);
        this.page = page;
        this.pageSize = pageSize;
    }
}
