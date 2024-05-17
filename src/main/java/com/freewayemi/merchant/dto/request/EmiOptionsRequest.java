package com.freewayemi.merchant.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EmiOptionsRequest {
    private final String id;
    private final Float lat;
    private final Float lng;


    @JsonCreator
    EmiOptionsRequest(@JsonProperty("id") String id,
                      @JsonProperty("lat") Float lat,
                      @JsonProperty("lng") Float lng) {
        this.id = id;
        this.lat = lat;
        this.lng = lng;
    }
}
