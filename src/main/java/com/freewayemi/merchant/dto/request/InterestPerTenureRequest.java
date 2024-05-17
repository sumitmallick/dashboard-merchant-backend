package com.freewayemi.merchant.dto.request;

import com.freewayemi.merchant.commons.type.BankEnum;
import com.freewayemi.merchant.commons.type.CardTypeEnum;
import lombok.Data;

import java.time.Instant;

@Data
public class InterestPerTenureRequest {
    private String cardInterestId;
    private BankEnum bankCode;
    private CardTypeEnum cardType;
    private Integer tenure;
    private Integer tenureInDays;

    private Double irr;
    private Double brandIrr;

    //lender scheme code
    private String providerSchemeCode;
    private String providerDetailedSchemeCode;

    private Boolean isActive;

    private Instant validFrom;
    private Instant validTo;

    private Double minAmount;
    private Double maxAmount;

    /*
     * if bankPfInPercentage is zero then default bankPfFlatAmount will be applicable
     * if bankPfFlatAmount is zero then default MIN(bankPfInPercentage * txnAmount, bankPfInMaxAmount) will be applicable
     * if bankPfFlatAmount and bankPfMaxAmount is zero then default bankPfInPercentage * txnAmount will be applicable
     * */
    private Double bankPfInPercentage;
    private Double bankPfMaxAmount;
    private Double bankPfFlatAmount;
}
