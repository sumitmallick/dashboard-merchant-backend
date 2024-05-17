package com.freewayemi.merchant.commons.dto.offer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.freewayemi.merchant.commons.type.BankEnum;
import com.freewayemi.merchant.commons.type.BankInterestTypeEnum;
import com.freewayemi.merchant.commons.type.CardTypeEnum;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@JsonDeserialize(builder = InterestPerTenureDto.InterestPerTenureDtoBuilder.class)
@Builder(builderClassName = "InterestPerTenureDtoBuilder", toBuilder = true)
public class InterestPerTenureDto {
    private String cardInterestId;
    private BankEnum bankEnum;
    private CardTypeEnum cardType;
    private Integer tenure;
    private Integer tenureInDays;

    private Double irr;
    private Double brandIrr;
    private String calculationType;

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

    private BankInterestTypeEnum bankInterestTypeEnum;
    private Double actualIrr;
    private String applicabilityType;

    //scheme related details
    private String providerSchemeId;
    private String masterId;
    private String schemeMappingId;
    private Float pf;

    @JsonPOJOBuilder(withPrefix = "")
    public static class InterestPerTenureDtoBuilder {
    }
}