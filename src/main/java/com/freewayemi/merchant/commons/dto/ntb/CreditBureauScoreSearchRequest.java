package com.freewayemi.merchant.commons.dto.ntb;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditBureauScoreSearchRequest {
    private String identifierKey;
    private String identifierValue;

    @JsonCreator
    public CreditBureauScoreSearchRequest(@JsonProperty("identifierKey") String identifierKey, @JsonProperty("identifierValue") String identifierValue) {
        this.identifierKey = identifierKey;
        this.identifierValue = identifierValue;
    }
}
