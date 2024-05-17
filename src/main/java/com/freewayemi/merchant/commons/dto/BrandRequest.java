package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.StringUtils;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrandRequest {
    private final String serialNumber;
    private final String brandProductId;
    private final String modelNumber;
    private final String brandId;
    private final String productSkuCode;

    @JsonCreator
    public BrandRequest(@JsonProperty("serialNumber") String serialNumber,
                        @JsonProperty("brandProductId") String brandProductId,
                        @JsonProperty("modelNumber") String modelNumber,
                        @JsonProperty("brandId") String brandId,
                        @JsonProperty("productSkuCode") String productSkuCode) {
        this.serialNumber = serialNumber;
        this.brandProductId = brandProductId;
        this.modelNumber = modelNumber;
        this.brandId = brandId;
        this.productSkuCode = productSkuCode;
    }

    public String getUrlParam() {
        String url = "?";
        if (StringUtils.hasText(brandId)) {
            url += "brandId=" + brandId + "&";
        }
        if (StringUtils.hasText(brandProductId)) {
            url += "brandProductId=" + brandProductId + "&";
        }
        if (StringUtils.hasText(serialNumber)) {
            url += "serialNumber=" + serialNumber + "&";
        }
        if (StringUtils.hasText(modelNumber)) {
            url += "modelNumber=" + modelNumber + "&";
        }
        if (StringUtils.hasText(productSkuCode)) {
            url += "productSkuCode=" + productSkuCode + "&";
        }
        return url;
    }
}
