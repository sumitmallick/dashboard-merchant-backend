package com.freewayemi.merchant.pojos.pan;

import com.freewayemi.merchant.type.Source;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PanDetailsRequest {
    private final String orderId;
    private final String panNumber;
    private final String provider;
    private final Source source;

    public PanDetailsRequest(String orderId, String panNumber, String provider, Source source) {
        this.orderId = orderId;
        this.panNumber = panNumber;
        this.provider = provider;
        this.source = source;
    }
}
