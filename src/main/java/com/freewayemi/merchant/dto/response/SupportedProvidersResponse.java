package com.freewayemi.merchant.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SupportedProvidersResponse {
    private List<BankDTO> debitBanks;
    private List<BankDTO> creditBanks;
    private List<BankDTO> cardlessBanks;
    private List<BankDTO> ntbBanks;
}
