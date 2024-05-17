package com.freewayemi.merchant.dto.sales;

import com.freewayemi.merchant.commons.dto.Kyc;
import lombok.Builder;
import lombok.Data;

@Data
public class UpdateMerchantProperties {
    private String stage;
    private KycProperties kycProperties;
    private Kyc kyc;
}
