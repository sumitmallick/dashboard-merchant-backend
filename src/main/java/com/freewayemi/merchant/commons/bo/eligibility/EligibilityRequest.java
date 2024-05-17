package com.freewayemi.merchant.commons.bo.eligibility;

import com.freewayemi.merchant.commons.type.PaymentProviderEnum;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EligibilityRequest {
    private final String mobile;
    private final Float amount;
    private final String longitude;
    private final String latitude;
    private final String ip;
    private final String appVersion;
    private final List<PaymentProviderEnum> providers;
    private final String partnerCode;
}
