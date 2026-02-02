package com.mkomarov.spring.mapper;

public interface AbstractMapper<F, T> {
    T map(F from);
}
