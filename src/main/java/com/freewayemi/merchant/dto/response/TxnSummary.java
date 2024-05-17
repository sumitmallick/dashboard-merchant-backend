package com.freewayemi.merchant.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TxnSummary {
    private final Double amount;
    private final Integer txnCount;
}
