package com.freewayemi.merchant.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EligibilityCheckRequest {
    private String transactionId;
    private String consumerId;
    private String source;
    private List<String> bankCode;
}
