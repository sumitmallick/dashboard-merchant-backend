package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BrandResponseDTO {
    private String brandId;
    private String name;
    private String emiOption;
    private String icon;
    private String sideBanner;
    private String displayHeader;
    private String displaySubHeader;
    private Boolean hideOtherProducts;
    private Boolean hideSerialNumber;

    @JsonCreator
    public BrandResponseDTO(@JsonProperty("brandId") String brandId,
                            @JsonProperty("name") String name,
                            @JsonProperty("emiOption") String emiOption,
                            @JsonProperty("icon")  String icon,
                            @JsonProperty("sideBanner") String sideBanner,
                            @JsonProperty("displayHeader") String displayHeader,
                            @JsonProperty("displaySubHeader") String displaySubHeader,
                            @JsonProperty("hideOtherProducts") Boolean hideOtherProducts,
                            @JsonProperty("hideSerialNumber") Boolean hideSerialNumber) {
        this.brandId = brandId;
        this.name = name;
        this.emiOption = emiOption;
        this.icon = icon;
        this.sideBanner = sideBanner;
        this.displayHeader = displayHeader;
        this.displaySubHeader = displaySubHeader;
        this.hideOtherProducts = hideOtherProducts;
        this.hideSerialNumber = hideSerialNumber;
    }
}
