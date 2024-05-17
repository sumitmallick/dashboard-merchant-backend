package com.freewayemi.merchant.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;
import java.util.Map;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProviderMasterConfigInfo {
    private String cardType;
    private String bankCode;
    private Map<String, ProviderSchemeDetail> tenureConfig;
    private List<String> binSupported;
    private Boolean isActive;
    @JsonCreator
    public ProviderMasterConfigInfo(@JsonProperty("cardType") String cardType,
                                    @JsonProperty("bankCode") String bankCode,
                                    @JsonProperty("tenureConfig") Map<String, ProviderSchemeDetail> tenureConfig,
                                    @JsonProperty("binSupported") List<String> binSupported,
                                    @JsonProperty("isActive") Boolean isActive) {
        this.cardType = cardType;
        this.bankCode = bankCode;
        this.tenureConfig = tenureConfig;
        this.binSupported = binSupported;
        this.isActive = isActive;
    }
}