package com.freewayemi.merchant.commons.dto.ntbservices;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.type.NTBLoanStatuses;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CancelLoanResponseDto {

    private String code;

    private String status;

    private String message;

    private String loanId;

    private NTBLoanStatuses ntbLoanStatus;

    private String refundReferenceIdByProvider;

    private String refundAmountByProvider;

    private String refundDescriptionByProvider;

    @JsonCreator
    public CancelLoanResponseDto(@JsonProperty(value = "code") String code,
                                 @JsonProperty(value = "status") String status,
                                 @JsonProperty(value = "message") String message,
                                 @JsonProperty(value = "loanId") String loanId,
                                 @JsonProperty(value = "ntbLoanStatus") NTBLoanStatuses ntbLoanStatus,
                                 @JsonProperty(value = "refundReferenceIdByProvider") String refundReferenceIdByProvider,
                                 @JsonProperty(value = "refundAmountByProvider") String refundAmountByProvider,
                                 @JsonProperty(value = "refundDescriptionByProvider") String refundDescriptionByProvider) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.loanId = loanId;
        this.ntbLoanStatus = ntbLoanStatus;
        this.refundReferenceIdByProvider = refundReferenceIdByProvider;
        this.refundAmountByProvider = refundAmountByProvider;
        this.refundDescriptionByProvider = refundDescriptionByProvider;
    }
}
