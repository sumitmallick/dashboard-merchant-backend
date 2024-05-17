package com.freewayemi.merchant.dto.response;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PromotionResponse {
    private String status;
    private String statusMsg;
    private String icon;
}
