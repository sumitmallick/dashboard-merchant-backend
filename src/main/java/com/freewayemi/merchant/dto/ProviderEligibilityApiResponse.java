package com.freewayemi.merchant.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.enums.EligibilityStatus;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProviderEligibilityApiResponse {
    private String providerName;
    private Boolean isEligible;
    private Double approvedLimit;
    private Double maxApprovedLimit;
    private Double minTransactionAmount;
    private Double maxTransactionAmount;
    private EligibilityStatus eligibilityStatus;
    private String emiOffersCode;
    private Boolean isAccountAggregationRequired;

    @JsonCreator
    public ProviderEligibilityApiResponse(@JsonProperty("providerName") String providerName,
                                          @JsonProperty("isEligible") Boolean isEligible,
                                          @JsonProperty("approvedLimit") Double approvedLimit,
                                          @JsonProperty("maxApprovedLimit") Double maxApprovedLimit,
                                          @JsonProperty("minTransactionAmount") Double minTransactionAmount,
                                          @JsonProperty("maxTransactionAmount") Double maxTransactionAmount,
                                          @JsonProperty("eligibilityStatus") EligibilityStatus eligibilityStatus,
                                          @JsonProperty("emiOffersCode") String emiOffersCode,
                                          @JsonProperty("isAccountAggregationRequired") Boolean isAccountAggregationRequired) {
        this.providerName = providerName;
        this.isEligible = isEligible;
        this.approvedLimit = approvedLimit;
        this.maxApprovedLimit = maxApprovedLimit;
        this.minTransactionAmount = minTransactionAmount;
        this.maxTransactionAmount = maxTransactionAmount;
        this.eligibilityStatus = eligibilityStatus;
        this.emiOffersCode = emiOffersCode;
        this.isAccountAggregationRequired = isAccountAggregationRequired;
    }
}
