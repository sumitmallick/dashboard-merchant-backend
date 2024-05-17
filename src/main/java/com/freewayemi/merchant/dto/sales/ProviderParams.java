package com.freewayemi.merchant.dto.sales;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.type.BankEnum;
import lombok.Data;

@Data
public class ProviderParams {
    private String state;
    private BankEnum bankEnum;

    @JsonCreator
    public ProviderParams(@JsonProperty("state") String state, @JsonProperty("bank") BankEnum bankEnum) {
        this.state = state;
        this.bankEnum = bankEnum;
    }

}
