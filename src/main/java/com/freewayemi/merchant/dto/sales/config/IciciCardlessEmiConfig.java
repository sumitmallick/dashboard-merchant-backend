package com.freewayemi.merchant.dto.sales.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
public class IciciCardlessEmiConfig {

    private String merchantCode;
    private String merchantName;

    @JsonCreator
    public IciciCardlessEmiConfig(@JsonProperty("merchantCode") String merchantCode,
                            @JsonProperty("merchantName") String merchantName){
        this.merchantCode = merchantCode;
        this.merchantName = merchantName;
    }
}
