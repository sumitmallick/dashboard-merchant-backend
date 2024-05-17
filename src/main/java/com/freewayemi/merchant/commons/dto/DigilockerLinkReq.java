package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DigilockerLinkReq {
    private final String merchantId;
    private final String consumerMobile;
    private final String redirectionUrl;
    private final String postRedirectionUrl;

    public DigilockerLinkReq(@JsonProperty("merchantId") String merchantId,
                             @JsonProperty("consumerMobile") String consumerMobile,
                             @JsonProperty("redirectionUrl") String redirectionUrl,
                             @JsonProperty("postRedirectionUrl") String postRedirectionUrl) {
        this.merchantId = merchantId;
        this.consumerMobile = consumerMobile;
        this.redirectionUrl = redirectionUrl;
        this.postRedirectionUrl = postRedirectionUrl;
    }
}
