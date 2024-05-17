package com.freewayemi.merchant.dto.sales;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class EligibilityResponse {

    private final Boolean eligible;
    private final String bankCode;
    private final Integer maxEligibleTenure;
    private final String eligibleStatus;
    private final String eligibilityUrl;
    private final Integer minEligibleTenure;
    private final String maxEligibleAmount;
    private final String eligibleAmount;
    private final String processingRate;
    private final String maxProcessingFee;
    private List<EligibilityResponse> eligibilityTraces;
    private final String cardType;
    private final String loanId;
    private ProviderParams providerParams;

    @JsonCreator
    public EligibilityResponse(@JsonProperty("eligible") Boolean eligible, @JsonProperty("bankCode") String bankCode,
                               @JsonProperty("maxEligibleTenure") Integer maxEligibleTenure,
                               @JsonProperty("eligibleStatus") String eligibleStatus,
                               @JsonProperty("eligibilityUrl") String eligibilityUrl,
                               @JsonProperty("minEligibleTenure") Integer minEligibleTenure,
                               @JsonProperty("maxEligibleAmount") String maxEligibleAmount,
                               @JsonProperty("eligibleAmount") String eligibleAmount,
                               @JsonProperty("processingRate") String processingRate,
                               @JsonProperty("maxProcessingFee") String maxProcessingFee,
                               @JsonProperty("eligibilityTraces") List<EligibilityResponse> eligibilityTraces,
                               @JsonProperty("cardType") String cardType, @JsonProperty("loanId") String loanId,
                               @JsonProperty("providerParams") ProviderParams providerParams) {
        this.eligible = eligible;
        this.bankCode = bankCode;
        this.maxEligibleTenure = maxEligibleTenure;
        this.eligibleStatus = eligibleStatus;
        this.eligibilityUrl = eligibilityUrl;
        this.minEligibleTenure = minEligibleTenure;
        this.maxEligibleAmount = maxEligibleAmount;
        this.eligibleAmount = eligibleAmount;
        this.processingRate = processingRate;
        this.maxProcessingFee = maxProcessingFee;
        this.eligibilityTraces = eligibilityTraces;
        this.cardType = cardType;
        this.loanId = loanId;
        this.providerParams = providerParams;
    }

    public void setEligibilityTraceList(EligibilityResponse eligibility) {
        List<EligibilityResponse> eligibilityTraces =
                CollectionUtils.isEmpty(getEligibilityTraces()) ? new ArrayList<>() : getEligibilityTraces();
        eligibilityTraces.add(eligibility);
        setEligibilityTraces(eligibilityTraces);
    }
}