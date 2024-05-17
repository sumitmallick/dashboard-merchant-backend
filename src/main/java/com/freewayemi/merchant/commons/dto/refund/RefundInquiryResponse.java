package com.freewayemi.merchant.commons.dto.refund;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.dto.TransactionV2Response;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RefundInquiryResponse {
    private String status;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private TransactionV2Response.RefundInfo data;

    @JsonCreator
    public RefundInquiryResponse(@JsonProperty("status") String status,
                                 @JsonProperty("data") TransactionV2Response.RefundInfo data) {
        this.status = status;
        this.data = data;
    }
}
