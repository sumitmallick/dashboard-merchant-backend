package com.freewayemi.merchant.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SupportedBankResponse {
    private final List<BankDTO> creditBanks;
    private final List<BankDTO> debitBanks;
    private final List<BankDTO> cardlessBanks;

    @JsonCreator
    public SupportedBankResponse(@JsonProperty("creditBanks") List<BankDTO> creditBanks,
                                 @JsonProperty("debitBanks") List<BankDTO> debitBanks,
                                 @JsonProperty("cardlessBanks") List<BankDTO> cardlessBanks) {
        this.creditBanks = creditBanks;
        this.debitBanks = debitBanks;
        this.cardlessBanks = cardlessBanks;
    }
}
