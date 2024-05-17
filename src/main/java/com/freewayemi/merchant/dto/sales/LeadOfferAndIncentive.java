package com.freewayemi.merchant.dto.sales;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeadOfferAndIncentive {
    private Integer leadOffer;
    private Integer incentive;
}
