package com.freewayemi.merchant.dto.paymentOptions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.dto.CCAvenueEmiPgConfigDto;
import com.freewayemi.merchant.commons.dto.HdfcDcEmiBrandConfigDto;
import com.freewayemi.merchant.commons.dto.IsgPgConfigDto;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrandPaymentConfigDto {

    private final String brandId;
    private final HdfcDcEmiBrandConfigDto hdfcDcEmiBrandConfig;
    private final IsgPgConfigDto isgPgConfig;
    private final CCAvenueEmiPgConfigDto ccavenueEmiPgConfig;

    @JsonCreator
    public BrandPaymentConfigDto(@JsonProperty("brandId") String brandId,
                                 @JsonProperty("hdfcDcEmiBrandConfig") HdfcDcEmiBrandConfigDto hdfcDcEmiBrandConfig,
                                 @JsonProperty("isgPgConfig") IsgPgConfigDto isgPgConfig,
                                 @JsonProperty("ccavenueEmiPgConfig") CCAvenueEmiPgConfigDto ccavenueEmiPgConfig) {
        this.brandId = brandId;
        this.hdfcDcEmiBrandConfig = hdfcDcEmiBrandConfig;
        this.isgPgConfig = isgPgConfig;
        this.ccavenueEmiPgConfig = ccavenueEmiPgConfig;
    }

}
