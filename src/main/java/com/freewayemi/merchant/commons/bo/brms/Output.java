package com.freewayemi.merchant.commons.bo.brms;

import lombok.Data;

@Data
public class Output {
    private Float discount;
    private Float cashback;
    private Integer effectiveTenure;
    private Float advanceDownPaymentRate;
    private Integer offlineAdvanceEmiTenure;

    public Output(Float discount) {
        this.discount = discount;
    }

    public Output(Float discount, Integer effectiveTenure) {
        this.discount = discount;
        this.effectiveTenure = effectiveTenure;
    }

    public Output(Float discount, Float cashback, Integer effectiveTenure, Integer offlineAdvanceEmiTenure) {
        this.discount = discount;
        this.cashback = cashback;
        this.effectiveTenure = effectiveTenure;
        this.offlineAdvanceEmiTenure = offlineAdvanceEmiTenure;
    }

    public Output(Float discount, Float cashback, Integer effectiveTenure, Float advanceDownPaymentRate, Integer offlineAdvanceEmiTenure) {
        this.discount = discount;
        this.cashback = cashback;
        this.effectiveTenure = effectiveTenure;
        this.advanceDownPaymentRate = advanceDownPaymentRate;
        this.offlineAdvanceEmiTenure = offlineAdvanceEmiTenure;
    }
}
