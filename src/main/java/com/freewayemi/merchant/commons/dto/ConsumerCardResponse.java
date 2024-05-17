package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsumerCardResponse {

    private final String bankCode;
    private final String bankName;
    private final String cardType;
    private final String cardId;
    private final String vaultUuid;
    private final Boolean isVaultToken;

    @JsonCreator
    public ConsumerCardResponse(@JsonProperty("bankCode") String bankCode, @JsonProperty("bankName") String bankName,
                                @JsonProperty("cardType") String cardType, @JsonProperty("cardId") String cardId,
                                @JsonProperty("vaultUuid") String vaultUuid,
                                @JsonProperty("isVaultToken") Boolean isVaultToken) {
        this.bankCode = bankCode;
        this.bankName = bankName;
        this.cardType = cardType;
        this.cardId = cardId;
        this.vaultUuid = vaultUuid;
        this.isVaultToken = isVaultToken;
    }

}
