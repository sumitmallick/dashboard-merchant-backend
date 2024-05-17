package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SerialNumberTxnsResponse {
    private Long successTxnCount;

    @JsonCreator
    public SerialNumberTxnsResponse(@JsonProperty("statusCode") Long successTxnCount){
        this.successTxnCount = successTxnCount;
    }
}
