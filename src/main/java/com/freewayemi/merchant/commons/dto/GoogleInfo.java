package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GoogleInfo {
    private String googlePlaceId;
    private String googlePlaceName;
    private String googlePlaceAddress;
    private String pincode;

    @JsonCreator
    public GoogleInfo(@JsonProperty("googlePlaceId") String googlePlaceId,
                      @JsonProperty("googlePlaceName") String googlePlaceName,
                      @JsonProperty("googlePlaceAddress") String googlePlaceAddress,
                      @JsonProperty("pincode") String pincode) {
        this.googlePlaceId = googlePlaceId;
        this.googlePlaceName = googlePlaceName;
        this.googlePlaceAddress = googlePlaceAddress;
        this.pincode = pincode;
    }
}