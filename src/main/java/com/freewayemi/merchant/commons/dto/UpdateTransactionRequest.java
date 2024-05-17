package com.freewayemi.merchant.commons.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateTransactionRequest {

    private final String updateType;
    private final String serialNumber;
}
