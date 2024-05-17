package com.freewayemi.merchant.commons.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Kyc {
    private Boolean kycDone;
}
