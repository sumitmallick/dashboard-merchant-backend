package com.freewayemi.merchant.pojos.gst;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GstDetailsRequest {
    private final String gst;
    private final String provider;

    public GstDetailsRequest(String gst, String provider) {
        this.gst = gst;
        this.provider = provider;
    }
}
