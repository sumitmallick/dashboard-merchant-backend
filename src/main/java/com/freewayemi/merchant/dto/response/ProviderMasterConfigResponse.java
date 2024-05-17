package com.freewayemi.merchant.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.dto.sales.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProviderMasterConfigResponse extends BaseResponse {
    private final ProviderMasterConfigInfo providerMasterConfigInfo;
    private final List<ProviderMasterConfigInfo> providerMasterConfigs;

    @JsonCreator
    public ProviderMasterConfigResponse(@JsonProperty("code") Integer code, @JsonProperty("status") String status,
                                        @JsonProperty("message") String message,
                                        @JsonProperty("providerMasterConfigInfo")
                                        ProviderMasterConfigInfo providerMasterConfigInfo,
                                        @JsonProperty("providerMasterConfigs")
                                        List<ProviderMasterConfigInfo> providerMasterConfigs) {
        super(code, status, message);
        this.providerMasterConfigInfo = providerMasterConfigInfo;
        this.providerMasterConfigs = providerMasterConfigs;
    }
}
