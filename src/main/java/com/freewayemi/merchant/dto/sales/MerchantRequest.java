package com.freewayemi.merchant.dto.sales;

import lombok.Data;

@Data
public class MerchantRequest {
    private String text;
    private String status;
    private String searchType;
    private String fieldNe;
    private String transacting;
    int skip = 0;
    int limit = 25;
}
