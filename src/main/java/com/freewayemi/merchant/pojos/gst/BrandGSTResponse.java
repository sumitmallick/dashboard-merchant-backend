package com.freewayemi.merchant.pojos.gst;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrandGSTResponse {
    @JsonProperty(value = "brandId")
    private final Integer brandId;
    @JsonProperty(value = "gst")
    private final String gst;
    @JsonProperty(value = "tag")
    private final String tag;
}
