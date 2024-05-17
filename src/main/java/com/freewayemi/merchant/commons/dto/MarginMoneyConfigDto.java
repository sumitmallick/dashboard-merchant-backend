package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.entity.MarginMoneyConfig;
import lombok.Data;

import java.util.List;

@Data
public class MarginMoneyConfigDto {

    private List<String> downPaymentAvailableOptions;
    private Float marginDpIntervalPercentage;
    private Float maxMarginDpAmount;
    private Float minMarginDpAmount;

    public MarginMoneyConfigDto(MarginMoneyConfig marginMoneyConfig) {
        if (marginMoneyConfig != null) {
            this.downPaymentAvailableOptions = marginMoneyConfig.getDownPaymentAvailableOptions();
            this.marginDpIntervalPercentage = marginMoneyConfig.getMarginDpIntervalPercentage();
        }
    }

    @JsonCreator
    public MarginMoneyConfigDto(@JsonProperty("downPaymentAvailableOptions") List<String> downPaymentAvailableOptions,
                                @JsonProperty("marginDpIntervalPercentage") Float marginDpIntervalPercentage,
                                @JsonProperty("maxMarginDpAmount") Float maxMarginDpAmount,
                                @JsonProperty("minMarginDpAmount") Float minMarginDpAmount) {
        this.downPaymentAvailableOptions = downPaymentAvailableOptions;
        this.marginDpIntervalPercentage = marginDpIntervalPercentage;
        this.maxMarginDpAmount = maxMarginDpAmount;
        this.minMarginDpAmount = minMarginDpAmount;
    }
}
