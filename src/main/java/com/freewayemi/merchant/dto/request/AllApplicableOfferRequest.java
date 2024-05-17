package com.freewayemi.merchant.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class AllApplicableOfferRequest {
//    @NotNull(message = "Please provide brand id.")
    String brandId;
    String transactionId;
    Float marginDownPaymentAmount;
    String merchantState;
    @NotNull(message = "Please provide list of applicable offer request.")
    List<ApplicableOfferRequest> applicableOfferRequestList;
    String merchantId;
    String brandProductId;
    Float productAmount;

    @JsonCreator
    public AllApplicableOfferRequest(@JsonProperty("brandId") String brandId,
                                     @JsonProperty("transactionId") String transactionId,
                                     @JsonProperty("marginDownPaymentAmount") Float marginDownPaymentAmount,
                                     @JsonProperty("merchantState") String merchantState,
                                     @JsonProperty("applicableOfferRequestList") List<ApplicableOfferRequest> applicableOfferRequestList,
                                     @JsonProperty("merchantId") String merchantId,
                                     @JsonProperty("productAmount") Float productAmount,
                                     @JsonProperty("brandProductId") String brandProductId){
        this.brandId = brandId;
        this.transactionId = transactionId;
        this.marginDownPaymentAmount = marginDownPaymentAmount;
        this.merchantState = merchantState;
        this.applicableOfferRequestList = applicableOfferRequestList;
        this.merchantId = merchantId;
        this.brandProductId = brandProductId;
        this.productAmount = productAmount;
    }
}
