package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.dto.sales.BaseResponse;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PgExpireTransactionResponse extends BaseResponse {
    private String transactionId;
    private String transactionStatus;
    @JsonCreator
    @Builder(builderMethodName = "baseResponseBuilder")
    public PgExpireTransactionResponse(@JsonProperty("code") Integer code,
                                       @JsonProperty("status") String status,
                                       @JsonProperty("message") String message,
                                       @JsonProperty("transactionId") String transactionId,
                                       @JsonProperty("transactionStatus") String transactionStatus) {
        super(code, status, message);
        this.transactionId = transactionId;
        this.transactionStatus = transactionStatus;
    }
}
