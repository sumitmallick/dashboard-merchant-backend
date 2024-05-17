package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PartnerInfo {
    private final String name;
    private final String partnerLogoUrl;
    private final List<String> merchantAppBannerUrl;

    @JsonCreator
    public PartnerInfo(
            @JsonProperty("name") String name,
            @JsonProperty("partnerLogoUrl") String partnerLogoUrl,
            @JsonProperty("merchantAppBannerUrl") List<String> merchantAppBannerUrl) {
        this.name = name;
        this.partnerLogoUrl = partnerLogoUrl;
        this.merchantAppBannerUrl = merchantAppBannerUrl;
    }
}
