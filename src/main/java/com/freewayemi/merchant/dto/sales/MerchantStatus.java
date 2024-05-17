package com.freewayemi.merchant.dto.sales;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MerchantStatus {
    private long approved;
    private long registered;
    private long profiled;
    private long reSubmission;
}
