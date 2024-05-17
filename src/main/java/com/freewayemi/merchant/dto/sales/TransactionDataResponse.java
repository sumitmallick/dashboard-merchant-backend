package com.freewayemi.merchant.dto.sales;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.dto.sales.PaymentConfigInfo;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDataResponse extends BaseResponse {

    private final List<TransactionModel> transactions;
    private final Long transactionCount;
    private final Volume volume;
    private final List<String> merchants;
    private final List<PaymentConfigInfo> paymentConfigInfos;
    private final PaymentConfigInfo paymentConfigInfo;
    private final TransactionDataByMerchant transactionDataByMerchant;

    @Builder(builderMethodName = "baseResponseBuilder")
    public TransactionDataResponse(@JsonProperty("code") Integer code,
                                   @JsonProperty("status") String status,
                                   @JsonProperty("message") String message,
                                   @JsonProperty("transactions") List<TransactionModel> transactions,
                                   @JsonProperty("transactionCount") Long transactionCount,
                                   @JsonProperty("volume") Volume volume,
                                   @JsonProperty("merchants") List<String> merchants,
                                   @JsonProperty("paymentConfigInfos") List<PaymentConfigInfo> paymentConfigInfos,
                                   @JsonProperty("paymentConfigInfo") PaymentConfigInfo paymentConfigInfo,
                                   @JsonProperty("transactionDataByMerchant") TransactionDataByMerchant transactionDataByMerchant) {
        super(code, status, message);
        this.transactions = transactions;
        this.transactionCount = transactionCount;
        this.volume = volume;
        this.merchants = merchants;
        this.paymentConfigInfos = paymentConfigInfos;
        this.paymentConfigInfo = paymentConfigInfo;
        this.transactionDataByMerchant = transactionDataByMerchant;
    }
}
