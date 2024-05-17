package com.freewayemi.merchant.dto.request;

import lombok.Data;

@Data
public class ConsumerTransactionNotificationRequest {

    private String merchantId;

    private String consumerMobile;
}
