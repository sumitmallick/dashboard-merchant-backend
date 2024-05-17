package com.freewayemi.merchant.commons.dto.karza;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DigilockerLinkRequest {
    private final String redirectUrl;
    private final String oAuthState;
    private final String consent;

    @JsonCreator
    public DigilockerLinkRequest(@JsonProperty("redirectUrl") String redirectUrl,
                                 @JsonProperty("oAuthState") String oAuthState,
                                 @JsonProperty("consent") String consent) {
        this.redirectUrl = redirectUrl;
        this.oAuthState = oAuthState;
        this.consent = consent;
    }

    @JsonProperty("oAuthState")
    public String getOAuthState() {
        return this.oAuthState;
    }

    @JsonProperty("consent")
    public String getConsent() {
        return this.consent;
    }

    @JsonProperty("redirectUrl")
    public String getRedirectUrl() {
        return this.redirectUrl;
    }
}
