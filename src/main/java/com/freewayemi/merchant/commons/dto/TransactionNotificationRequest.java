package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TransactionNotificationRequest {
    private final String consumerMobile;

    @JsonCreator
    public TransactionNotificationRequest(@JsonProperty("consumerMobile") String consumerMobile) {
        this.consumerMobile = consumerMobile;
    }
}
