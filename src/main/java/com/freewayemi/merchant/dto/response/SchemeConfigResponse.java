package com.freewayemi.merchant.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.dto.sales.BaseResponse;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SchemeConfigResponse extends BaseResponse {
    private final String partnerCode;
    private final List<SchemeConfigDetail> configData;

    @JsonCreator
    public SchemeConfigResponse(
            @JsonProperty("code") Integer code,
            @JsonProperty("status") String status,
            @JsonProperty("message") String message,
            @JsonProperty("partnerCode") String partnerCode,
            @JsonProperty("configData") List<SchemeConfigDetail> configData) {
        super(code, status, message);
        this.partnerCode = partnerCode;
        this.configData = configData;
    }

}
