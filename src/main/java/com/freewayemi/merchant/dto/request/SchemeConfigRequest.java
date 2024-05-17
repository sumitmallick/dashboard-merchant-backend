package com.freewayemi.merchant.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SchemeConfigRequest {
    private String partnerCode;
    private String merchantId;
    private String brandId;
    private String productId;
    private String cardType;
    private String bankCode;
}
