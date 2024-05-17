package com.freewayemi.merchant.dto.sales;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Pending {
    private long reSubmission;
    private long onboarding;
    private long activations;
}
