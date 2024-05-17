package com.freewayemi.merchant.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsentRequestV2 {
    private final String stage;
    private final String source;
    private final String ipAddress;
    @NotBlank
    private final Long timestamp;
    private final String transactionId;
    private final String consumerId;
    private final List<ProviderConsent> providerConsents;

    @JsonCreator
    public ConsentRequestV2(@JsonProperty(value = "stage") String stage,
                            @JsonProperty(value = "source") String source,
                            @JsonProperty(value = "ipAddress") String ipAddress,
                            @JsonProperty(value = "timestamp") Long timestamp,
                            @JsonProperty(value = "transactionId") String transactionId,
                            @JsonProperty(value = "consumerId") String consumerId,
                            @JsonProperty(value = "providerConsents") List<ProviderConsent> providerConsents) {
        this.stage = stage;
        this.source = source;
        this.ipAddress = ipAddress;
        this.timestamp = timestamp;
        this.transactionId = transactionId;
        this.consumerId = consumerId;
        this.providerConsents = providerConsents;
    }
}
