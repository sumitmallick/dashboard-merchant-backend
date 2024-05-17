package com.freewayemi.merchant.dto.sales;

import com.freewayemi.merchant.commons.type.CardTypeEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MerchantMdrs {
    private Integer tenure;
    private Double charge;
    private CardTypeEnum cardTypeEnum;
}
