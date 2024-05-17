package com.freewayemi.merchant.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConsentRequest {
    private String consumerId;
    private String stage;
    private String source;
    private String transactionId;
    private String type;
    private Long timeStamp;
    private String ipAddress;
    private ConsentInfo consentInfo;
}
