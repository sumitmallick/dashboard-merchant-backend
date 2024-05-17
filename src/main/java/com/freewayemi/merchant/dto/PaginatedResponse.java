package com.freewayemi.merchant.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PaginatedResponse<T> {

    private int currentPage;
    private int pageSize;
    private int numberOfElements;
    private Boolean hasNextPage;
    private Boolean hasPreviousPage;
    private List<T> elements;
}
