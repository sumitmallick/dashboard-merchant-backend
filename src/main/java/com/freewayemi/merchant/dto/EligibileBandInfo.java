package com.freewayemi.merchant.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EligibileBandInfo {
    private Float minEligibleAmount;
    private Float maxEligibleAmount;
}
