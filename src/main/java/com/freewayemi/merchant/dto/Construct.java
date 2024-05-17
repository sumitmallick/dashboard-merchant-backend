package com.freewayemi.merchant.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Construct {
    private Integer minTransactions;

    private Integer minAmount;

    private Integer incentivesPerTransaction;

    private Integer additionalPayout;
}
