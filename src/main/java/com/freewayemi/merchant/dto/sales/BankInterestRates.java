package com.freewayemi.merchant.dto.sales;

import com.freewayemi.merchant.commons.type.CardTypeEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BankInterestRates {
    private Integer tenure;
    private Double interest;
    private CardTypeEnum cardTypeEnum;
}
