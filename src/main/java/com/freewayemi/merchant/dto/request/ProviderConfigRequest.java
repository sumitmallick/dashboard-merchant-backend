package com.freewayemi.merchant.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProviderConfigRequest {
    private String searchType;
    private List<BandRequest> providerBands;
}
