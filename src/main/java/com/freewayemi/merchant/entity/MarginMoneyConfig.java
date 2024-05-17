package com.freewayemi.merchant.entity;

import lombok.Data;

import java.util.List;

@Data
public class MarginMoneyConfig {

    private List<String> downPaymentAvailableOptions;
    private Float marginDpIntervalPercentage;
}
