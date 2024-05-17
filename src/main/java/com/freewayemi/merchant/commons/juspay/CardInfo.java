package com.freewayemi.merchant.commons.juspay;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CardInfo {
    private String id;
    private String object;
    private String brand;
    private String bank;
    private String country;
    private String type;
    private String atm_pin_auth_support;
    private String code;
    private String cardSubType;
    private String juspayBankCode;

    @JsonCreator
    public CardInfo(@JsonProperty("id") String id,
                    @JsonProperty("object") String object,
                    @JsonProperty("brand") String brand,
                    @JsonProperty("bank") String bank,
                    @JsonProperty("country") String country,
                    @JsonProperty("type") String type,
                    @JsonProperty("atm_pin_auth_support") String atm_pin_auth_support,
                    @JsonProperty("code") String code,
                    @JsonProperty("card_sub_type") String cardSubType,
                    @JsonProperty("juspay_bank_code") String juspayBankCode) {
        this.id = id;
        this.object = object;
        this.brand = brand;
        this.bank = bank;
        this.country = country;
        this.type = type;
        this.atm_pin_auth_support = atm_pin_auth_support;
        this.code = code;
        this.cardSubType = cardSubType;
        this.juspayBankCode = juspayBankCode;
    }
}
