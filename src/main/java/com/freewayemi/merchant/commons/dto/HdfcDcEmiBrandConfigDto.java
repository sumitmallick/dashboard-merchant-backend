package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HdfcDcEmiBrandConfigDto {
    private String storeId;
    private String storeName;

    @JsonCreator
    public HdfcDcEmiBrandConfigDto(@JsonProperty("storeId") String storeId,
                                   @JsonProperty("storeName") String storeName) {
        this.storeId = storeId;
        this.storeName = storeName;
    }
}
