package com.freewayemi.merchant.commons.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConsumerAccountAuthRequest {
    private final String ifsc;
    private final String accountNumber;
    private final String source;
    private final String merchantId;
    private final String consumerId;

    public ConsumerAccountAuthRequest(String ifsc, String accountNumber, String source, String merchantId, String consumerId) {
        this.ifsc = ifsc;
        this.accountNumber = accountNumber;
        this.source = source;
        this.merchantId = merchantId;
        this.consumerId = consumerId;
    }
}
