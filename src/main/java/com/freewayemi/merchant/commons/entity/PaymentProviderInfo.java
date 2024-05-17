package com.freewayemi.merchant.commons.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.type.BankEnum;
import com.freewayemi.merchant.commons.type.PaymentModeEnum;
import com.freewayemi.merchant.commons.type.PaymentProviderEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentProviderInfo {

    private PaymentProviderEnum provider;
    private PaymentModeEnum type;
    private BankEnum bank;
    private Boolean disabled;

    @JsonCreator
    public PaymentProviderInfo(@JsonProperty("provider") PaymentProviderEnum provider,
                               @JsonProperty("type") PaymentModeEnum type, @JsonProperty("bank") BankEnum bank,
                               @JsonProperty("disabled") Boolean disabled) {
        this.provider = provider;
        this.type = type;
        this.bank = bank;
        this.disabled = disabled;
    }

}
