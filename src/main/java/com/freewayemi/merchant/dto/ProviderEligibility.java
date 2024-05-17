package com.freewayemi.merchant.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProviderEligibility {
    private String creditType;
    private List<ProviderEligibilityData> lenderEligibilityList;
}
