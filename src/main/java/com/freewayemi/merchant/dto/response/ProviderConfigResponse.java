package com.freewayemi.merchant.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.dto.sales.BaseResponse;
import lombok.Data;

import java.util.List;

@Data
public class ProviderConfigResponse extends BaseResponse {
    private List<ProviderConfig> providerConfigs;

    @JsonCreator
    public ProviderConfigResponse(@JsonProperty("code") Integer code,
                                  @JsonProperty("status") String status,
                                  @JsonProperty("message") String message,
                                  @JsonProperty("providerConfigs") List<ProviderConfig> providerConfigs) {
        super(code, status, message);
        this.providerConfigs = providerConfigs;
    }
}
