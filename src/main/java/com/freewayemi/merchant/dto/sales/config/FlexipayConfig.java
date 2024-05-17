package com.freewayemi.merchant.dto.sales.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
public class FlexipayConfig {

    private String storeId;
    private String storeName;
    @JsonCreator
    public FlexipayConfig(@JsonProperty("storeId") String storeId,
                           @JsonProperty("storeName") String storeName){
        this.storeId = storeId;
        this.storeName = storeName;
    }
}
