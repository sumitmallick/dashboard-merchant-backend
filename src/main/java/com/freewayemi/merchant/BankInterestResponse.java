package com.freewayemi.merchant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.freewayemi.merchant.commons.dto.offer.BankInterestDto;
import lombok.Data;

@Data
public class BankInterestResponse {
    private Integer code;
    private String status;
    private String message;
    private BankInterestDto bankInterestDto;

    @JsonCreator
    public BankInterestResponse(Integer code, String status, String message, BankInterestDto bankInterestDto) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.bankInterestDto = bankInterestDto;
    }
}
