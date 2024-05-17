package com.freewayemi.merchant.commons.entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class DownPaymentConfig {

    private List<DownPaymentRule> downPaymentRules;

}
