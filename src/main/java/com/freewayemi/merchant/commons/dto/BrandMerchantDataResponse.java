package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BrandMerchantDataResponse {
    private String brandId;
    private String gst;
    private String storeCode;
    private String distributorCode;
    private String merchantId;
    private String displayBrandId;

    @JsonCreator
    public BrandMerchantDataResponse(@JsonProperty("brandId") String brandId,
                                     @JsonProperty("gst") String gst,
                                     @JsonProperty("storeCode") String storeCode,
                                     @JsonProperty("distributorCode") String distributorCode,
                                     @JsonProperty("merchantId") String merchantId,
                                     @JsonProperty("displayBrandId") String displayBrandId) {
        this.brandId = brandId;
        this.gst = gst;
        this.storeCode = storeCode;
        this.distributorCode = distributorCode;
        this.merchantId = merchantId;
        this.displayBrandId = displayBrandId;
    }
}
