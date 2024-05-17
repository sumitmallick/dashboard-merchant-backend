package com.freewayemi.merchant.commons.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StoreUserTransactionStatusReq {
    private final String transFrom;
    private final String transTo;
    private final String status;
}