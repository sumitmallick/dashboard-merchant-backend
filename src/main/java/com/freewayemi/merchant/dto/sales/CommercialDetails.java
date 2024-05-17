package com.freewayemi.merchant.dto.sales;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommercialDetails {
    private String cardType;
    private String charge;
    private String tenure;
    private String mdr;
    private String emiType;
}
