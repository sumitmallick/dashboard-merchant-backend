package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

@Data
@ToString
public class PaymentProviderTransactionResponse {
    private final String name;
    private final String redirectUrl;
    private final Map<String, String> params;

    @JsonCreator
    public PaymentProviderTransactionResponse(@JsonProperty("name") String name,
                                              @JsonProperty("redirectUrl") String redirectUrl,
                                              @JsonProperty("params") Map<String, String> params) {
        this.name = name;
        this.redirectUrl = redirectUrl;
        this.params = params;
    }

}
