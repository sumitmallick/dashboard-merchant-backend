package com.freewayemi.merchant.dto.sales.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
public class KotakDcEmiConfig {

    private String merchantSubId;
    private String merchantSubName;

    @JsonCreator
    public KotakDcEmiConfig(@JsonProperty("merchantSubId") String merchantSubId,
                        @JsonProperty("merchantSubName") String merchantSubName){
        this.merchantSubId = merchantSubId;
        this.merchantSubName = merchantSubName;
    }
}
