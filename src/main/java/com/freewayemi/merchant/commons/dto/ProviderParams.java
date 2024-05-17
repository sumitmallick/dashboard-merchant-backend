package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.type.BankEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProviderParams {
    private String state;
    private BankEnum bankEnum;

    @JsonCreator
    public ProviderParams(@JsonProperty("state") String state, @JsonProperty("bank") BankEnum bankEnum) {
        this.state = state;
        this.bankEnum = bankEnum;
    }
}
