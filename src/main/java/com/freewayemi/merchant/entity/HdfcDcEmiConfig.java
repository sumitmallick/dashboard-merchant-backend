package com.freewayemi.merchant.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HdfcDcEmiConfig {
    private String storeId;
    private String storeName;
    @JsonCreator
    public HdfcDcEmiConfig(@JsonProperty("storeId") String storeId,
                       @JsonProperty("storeName") String storeName){
        this.storeId = storeId;
        this.storeName = storeName;
    }
}