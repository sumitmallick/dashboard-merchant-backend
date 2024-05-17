package com.freewayemi.merchant.dto.sales;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProposalRates {
    private List<MerchantMdrs> merchantMdrs;
    private List<BankInterestRates> bankInterestRates;
    private List<CustomerInterestRates> customerInterestRates;
}
