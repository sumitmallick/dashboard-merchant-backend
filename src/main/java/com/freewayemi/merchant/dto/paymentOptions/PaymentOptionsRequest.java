package com.freewayemi.merchant.dto.paymentOptions;

import com.freewayemi.merchant.commons.dto.AllOfferDetailsResponse;
import com.freewayemi.merchant.commons.dto.MerchantResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentOptionsRequest {
    private ConsumerInfo consumerInfo;
    private MerchantResponse merchantResponse;
    private String productId;
    private String partner;
    private Float productAmount;
    private AllOfferDetailsResponse allOfferDetailsResponse;
}
