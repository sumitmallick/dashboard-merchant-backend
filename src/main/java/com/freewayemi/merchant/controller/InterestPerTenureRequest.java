package com.freewayemi.merchant.controller;

import java.time.Instant;

public class InterestPerTenureRequest {

    private String cardInterestId;
    private Integer tenure;
    private Integer tenureInDays;
    private Double irr;
    private Double brandIrr;
    //lender scheme code
    private String providerSchemeCode;
    private String providerDetailedSchemeCode;
    private Boolean isActive;
    // By default applicable for all format:- dd/MM/yyyy
    private Instant validFrom;
    // By default applicable for all format:- dd/MM/yyyy
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
