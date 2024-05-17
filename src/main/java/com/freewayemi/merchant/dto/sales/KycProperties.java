package com.freewayemi.merchant.dto.sales;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class KycProperties {
    private Boolean isKycDone;
    private Instant successDate;
}
