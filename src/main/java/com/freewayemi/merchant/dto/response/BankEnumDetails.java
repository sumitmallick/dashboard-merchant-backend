package com.freewayemi.merchant.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BankEnumDetails {

    private String code;
    private String bankName;

    @JsonCreator
    public BankEnumDetails(@JsonProperty("code") String code, @JsonProperty("bankName") String bankName) {
        this.code = code;
        this.bankName = bankName;
    }
}
