package com.freewayemi.merchant.commons.bo.brms;

import com.freewayemi.merchant.commons.dto.ConvFeeRate;
import com.freewayemi.merchant.commons.dto.OfferResponse;
import com.freewayemi.merchant.commons.entity.PaymentProviderInfo;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@ToString
public class Input {
    private String cardType;
    private String bankCode;
    private String productId;
    private String brandProductId;
    private Integer tenure;
    private List<OfferResponse> offers;
    private List<ConvFeeRate> convFeeRates;
    private List<OfferResponse> brandSubventions;
    private Float txnAmount;
    private String merchantId;

    // Merchant attribute
    // When isSubvented is false no type of subvention is given to customer, by default subvention is applied
    private Boolean isSubvented;

    private Integer effectiveTenure;

    private List<PaymentProviderInfo> providers;

    private String merchantState;
}
