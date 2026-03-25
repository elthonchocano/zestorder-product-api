package com.echocano.zestorder.product.domain;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class CorePage<T> {

    List<T> content;
    int pageNumber;
    int pageSize;
    long totalElements;
    int totalPages;
    boolean isLast;
}