package com.freewayemi.merchant.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.dto.Mdr;
import com.freewayemi.merchant.dto.response.PartnerConfig;
import lombok.Data;

import java.util.List;

@Data
public class PartnerInfo {
    public String code;
    public String displayName;
    public String uuid;
    public PartnerConfig configs;
    public String name;
    public String partnerLogoUrl;
    public List<String> merchantAppBannerUrl;
    public List<String> stages;
    private List<Mdr> mdrs;
    private List<Mdr> subventions;

    @JsonCreator
    public PartnerInfo(@JsonProperty("code") String code,
                       @JsonProperty("displayName") String displayName,
                       @JsonProperty("uuid") String uuid,
                       @JsonProperty("configs") PartnerConfig configs,
                       @JsonProperty("name") String name,
                       @JsonProperty("partnerLogoUrl") String partnerLogoUrl,
                       @JsonProperty("merchantAppBannerUrl") List<String> merchantAppBannerUrl,
                       @JsonProperty("stages") List<String> stages,
                       @JsonProperty("mdrs") List<Mdr> mdrs,
                       @JsonProperty("subventions") List<Mdr> subventions) {
        this.code = code;
        this.displayName = displayName;
        this.uuid = uuid;
        this.configs = configs;
        this.name = name;
        this.partnerLogoUrl = partnerLogoUrl;
        this.merchantAppBannerUrl = merchantAppBannerUrl;
        this.stages = stages;
        this.mdrs = mdrs;
        this.subventions = subventions;
    }
}
