package com.freewayemi.merchant.commons.dto.deliveryorder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StoreDetails {

    private final String storeOwnerName;
    private final String storeOwnerEmail;
    private final String storeOwnerMobile;
    private final Address storeAddress;

    @JsonCreator
    public StoreDetails(@JsonProperty("storeOwnerName") String storeOwnerName,
                        @JsonProperty("storeOwnerEmail") String storeOwnerEmail,
                        @JsonProperty("storeOwnerMobile") String storeOwnerMobile,
                        @JsonProperty("storeAddress") Address storeAddress) {
        this.storeOwnerName = storeOwnerName;
        this.storeOwnerEmail = storeOwnerEmail;
        this.storeOwnerMobile = storeOwnerMobile;
        this.storeAddress = storeAddress;
    }

}
