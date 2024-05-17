package com.freewayemi.merchant.commons.dto.karza;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderClassName = "OKycUploadRequestBuilder", toBuilder = true)
@JsonDeserialize(builder = OKycUploadRequest.OKycUploadRequestBuilder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OKycUploadRequest {
    private final String consumerId;
    private final String merchantId;
    private final String kycFileKey;
    private final String passcode;
    private final String prospectId;
    private final String mobile;

    @JsonCreator
    public OKycUploadRequest(String consumerId, String merchantId, String kycFileKey, String passcode, String prospectId, String mobile) {
        this.consumerId = consumerId;
        this.merchantId = merchantId;
        this.kycFileKey = kycFileKey;
        this.passcode = passcode;
        this.prospectId = prospectId;
        this.mobile = mobile;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class OKycUploadRequestBuilder {
    }
}
