package com.freewayemi.merchant.dto.request;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class OfferBankRequest {
    String[] debit;
    String[] credit;
}
