package com.freewayemi.merchant.commons.dto.karza;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PanAuthReq {
    private final String source;
    private final String merchantId;
    private final String pan;
}
