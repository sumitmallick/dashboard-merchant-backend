package com.freewayemi.merchant.commons.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StoreLinkInfo {

    private String merchantId;
    private String orderId;
    private String email;
    private String mobile;
    private Float amount;
    private String transactionId;
    private String status;

}
