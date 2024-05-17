package com.freewayemi.merchant.dto;

import com.freewayemi.merchant.commons.dto.offer.InterestPerTenureDto;
import com.freewayemi.merchant.dto.response.ProviderMasterConfigInfo;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class SchemeDataDTO {

    private Map<String, ProviderMasterConfigInfo> providerMasterConfigInfoMap;
    private List<InterestPerTenureDto> configuredSchemes;
    private String brandId;
    private String productId;
}
