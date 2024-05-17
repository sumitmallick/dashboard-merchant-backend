package com.freewayemi.merchant.dto.sales;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Monthly {
    private long onboarded;
    private long activated;
    private long incentive;
}
