package com.freewayemi.merchant.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BandRequest {
    private String provider;
    private String band;
}
