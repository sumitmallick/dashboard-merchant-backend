package com.freewayemi.merchant.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HdfcDcEmiBrandConfigRequest {
    private String storeId;
    private String storeName;

    @JsonCreator
    public HdfcDcEmiBrandConfigRequest(@JsonProperty("storeId") String storeId,
                                       @JsonProperty("storeName") String storeName) {
        this.storeId = storeId;
        this.storeName = storeName;
    }
}
