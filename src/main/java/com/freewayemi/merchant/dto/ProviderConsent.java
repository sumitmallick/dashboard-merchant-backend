package com.freewayemi.merchant.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProviderConsent {
    private String provider;
    private List<ConsentDto> consents;

    @JsonCreator
    public ProviderConsent(@JsonProperty(value = "provider") String provider,
                           @JsonProperty(value = "consents") List<ConsentDto> consents) {
        this.provider = provider;
        this.consents = consents;
    }
}