package com.freewayemi.merchant.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class SchemeData {
    private final String providerSchemeId;
    // This will be sent to ICICI CL in loan booking request parameter: PINELABS_STORE_NAME
    private final String merchantStoreCode1;
    // This can be used for HDFC DC or CL or NTB
    private final String merchantStoreCode2;
    private final Map<String, ProviderSchemeDetail> tenureConfig;
    private final String applicabilityType;

    @JsonCreator
    public SchemeData(@JsonProperty("providerSchemeId") String providerSchemeId,
                      @JsonProperty("merchantStoreCode1") String merchantStoreCode1,
                      @JsonProperty("merchantStoreCode2") String merchantStoreCode2,
                      @JsonProperty("tenureConfig") Map<String, ProviderSchemeDetail> tenureConfig,
                      @JsonProperty("applicabilityType") String applicabilityType) {
        this.providerSchemeId = providerSchemeId;
        this.merchantStoreCode1 = merchantStoreCode1;
        this.merchantStoreCode2 = merchantStoreCode2;
        this.tenureConfig = tenureConfig;
        this.applicabilityType = applicabilityType;
    }
}
