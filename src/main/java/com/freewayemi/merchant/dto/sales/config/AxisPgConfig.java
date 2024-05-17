package com.freewayemi.merchant.dto.sales.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
public class AxisPgConfig {

    private String merchantId;
    private String transactionSecureKey;

    @JsonCreator
    public AxisPgConfig(@JsonProperty("merchantId") String merchantId,
                           @JsonProperty("transactionSecureKey") String transactionSecureKey){
        this.merchantId = merchantId;
        this.transactionSecureKey = transactionSecureKey;
    }
}
