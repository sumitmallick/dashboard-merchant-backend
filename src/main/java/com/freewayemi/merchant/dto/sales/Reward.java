package com.freewayemi.merchant.dto.sales;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Reward {
    private long initialActivation;
    private Integer moreActivation;
    private Integer threshHold;
    private String incentiveMessage;
}
