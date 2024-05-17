package com.freewayemi.merchant.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class MerchantInstantDiscountConfigResp {
    private String merchantId;
    private String brandId;
    private List<String> offerType;
    private Float additionalMdr;
    private String status;
    private Float brandFeeRate;

    @JsonCreator
    public MerchantInstantDiscountConfigResp(@JsonProperty("merchantId") String merchantId,
                                             @JsonProperty("brandId") String brandId,
                                             @JsonProperty("offerType") List<String> offerType,
                                             @JsonProperty("additionalMdr") Float additionalMdr,
                                             @JsonProperty("status") String status,
                                             @JsonProperty("brandFeeRate") Float brandFeeRate) {
        this.merchantId = merchantId;
        this.brandId = brandId;
        this.offerType = offerType;
        this.additionalMdr = additionalMdr;
        this.status = status;
        this.brandFeeRate = brandFeeRate;
    }
}
