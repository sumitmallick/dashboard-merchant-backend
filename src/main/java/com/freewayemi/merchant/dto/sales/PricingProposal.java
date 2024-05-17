package com.freewayemi.merchant.dto.sales;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PricingProposal {
    private ProposalRates noCostEmi;
    private ProposalRates standardEmi;
    private ProposalRates convenienceFee;
    private Boolean showConvFeeSettings;
    private Boolean showCustomerBearingSettings;
    private Boolean hidePricingProposal;
}
