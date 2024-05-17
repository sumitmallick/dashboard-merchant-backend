package com.freewayemi.merchant.commons.dto.karza;

import com.freewayemi.merchant.type.Source;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GstAuthReq {
    private final Source source;
    private final String merchantId;
    private final String gstin;
    private final String provider;
    private final String paymentRefId;
}
