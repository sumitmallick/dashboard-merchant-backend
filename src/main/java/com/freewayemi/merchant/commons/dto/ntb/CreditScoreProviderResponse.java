package com.freewayemi.merchant.commons.dto.ntb;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreditScoreProviderResponse {
    private String scoreValue;
    private String scoreStatus;
    private Instant issuedOn;
    private Instant expiredOn;
    private String bureau;
    private List<CreditScoreFactor> creditScoreFactors;
    private String code;
    private String statusMsg;
    @JsonCreator
    public CreditScoreProviderResponse(@JsonProperty("scoreValue") String scoreValue,
                                       @JsonProperty("scoreStatus") String scoreStatus,
                                       @JsonProperty("issuedOn") Instant issuedOn,
                                       @JsonProperty("expiredOn") Instant expiredOn,
                                       @JsonProperty("bureau") String bureau,
                                       @JsonProperty("creditScoreFactors") List<CreditScoreFactor> creditScoreFactors,
                                       @JsonProperty("code") String code,
                                       @JsonProperty("statusMsg") String statusMsg
    ) {
        this.scoreValue = scoreValue;
        this.scoreStatus = scoreStatus;
        this.issuedOn = issuedOn;
        this.expiredOn = expiredOn;
        this.bureau = bureau;
        this.creditScoreFactors = creditScoreFactors;
        this.code=code;
        this.statusMsg =statusMsg;
    }
}