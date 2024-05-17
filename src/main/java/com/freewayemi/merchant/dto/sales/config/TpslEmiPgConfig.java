package com.freewayemi.merchant.dto.sales.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
public class TpslEmiPgConfig {

    private String merchantCode;
    private String ivKey;
    private String key;
    @JsonCreator
    public TpslEmiPgConfig(@JsonProperty("merchantCode") String merchantCode,
                           @JsonProperty("ivKey") String ivKey,
                           @JsonProperty("key") String key
                           ){
        this.merchantCode = merchantCode;
        this.ivKey = ivKey;
        this.key = key;
    }
}
