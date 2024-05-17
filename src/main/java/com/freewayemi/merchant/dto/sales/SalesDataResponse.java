package com.freewayemi.merchant.dto.sales;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalesDataResponse extends BaseResponse {
    private SalesData salesData;

    @Builder(builderMethodName = "baseResponseBuilder")
    public SalesDataResponse(Integer code, String status, String message, SalesData salesData) {
        super(code, status, message);
        this.salesData = salesData;
    }
}