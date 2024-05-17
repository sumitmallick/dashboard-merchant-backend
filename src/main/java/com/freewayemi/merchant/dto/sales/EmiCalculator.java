package com.freewayemi.merchant.dto.sales;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EmiCalculator {
    private MerchantSettlement merchantSettlement;
    private List<CustomerEMI> customerEMIs;
}
