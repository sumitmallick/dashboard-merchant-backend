package com.freewayemi.merchant.commons.ntbservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.type.NTBLoanStatuses;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NtbLoanDisbursementResponse {

    private String status;

    private String prospectNumber;

    private String message;

    private Double finalDisbursalAmount;

    private Double processingFee;

    private Double pemiAmount;

    private NTBLoanStatuses ntbLoanStatus;

    private String code;

    @JsonCreator
    public NtbLoanDisbursementResponse(@JsonProperty(value = "status") String status,
                                       @JsonProperty(value = "prospectNumber") String prospectNumber,
                                       @JsonProperty(value = "message") String message,
                                       @JsonProperty(value = "finalDisbursalAmount") Double finalDisbursalAmount,
                                       @JsonProperty(value = "processingFee") Double processingFee,
                                       @JsonProperty(value = "pemiAmount") Double pemiAmount,
                                       @JsonProperty(value = "ntbLoanStatus") NTBLoanStatuses ntbLoanStatus,
                                       @JsonProperty(value = "code") String code) {
        this.status = status;
        this.prospectNumber = prospectNumber;
        this.message = message;
        this.finalDisbursalAmount = finalDisbursalAmount;
        this.processingFee = processingFee;
        this.pemiAmount = pemiAmount;
        this.ntbLoanStatus = ntbLoanStatus;
        this.code = code;
    }
}