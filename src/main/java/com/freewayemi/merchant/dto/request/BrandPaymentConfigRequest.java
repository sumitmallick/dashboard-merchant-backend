package com.freewayemi.merchant.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrandPaymentConfigRequest {

    private final String brandId;
    private final HdfcDcEmiBrandConfigRequest hdfcDcEmiConfig;
    private final IsgPgConfigRequest isgPgConfig;
    private final CCAvenueEmiPgConfigRequest ccavenueEmiPgConfig;

    @JsonCreator
    public BrandPaymentConfigRequest(@NonNull @JsonProperty("brandId") String brandId,
                                     @JsonProperty("hdfcDcEmiConfig") HdfcDcEmiBrandConfigRequest hdfcDcEmiConfig,
                                     @JsonProperty("isgPgConfig") IsgPgConfigRequest isgPgConfig,
                                     @JsonProperty("ccavenueEmiPgConfig")
                                     CCAvenueEmiPgConfigRequest ccavenueEmiPgConfig) {
        this.brandId = brandId;
        this.hdfcDcEmiConfig = hdfcDcEmiConfig;
        this.isgPgConfig = isgPgConfig;
        this.ccavenueEmiPgConfig = ccavenueEmiPgConfig;
    }
}
