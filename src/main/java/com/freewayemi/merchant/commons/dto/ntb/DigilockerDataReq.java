package com.freewayemi.merchant.commons.dto.ntb;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DigilockerDataReq {
    private String consumerMobile;
    private String requestId;
}
