package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionSearchFilter {

    private final Integer page;
    private final String status;
    private final String cardType;
    private final String bankName;
    private final String startDate;
    private final String endDate;

    @JsonCreator
    public TransactionSearchFilter(@JsonProperty(value = "page") Integer page, @JsonProperty(value = "status") String status,
                                   @JsonProperty(value = "cardType") String cardType, @JsonProperty(value = "bankName") String bankName,
                                   @JsonProperty(value = "startDate") String startDate, @JsonProperty(value = "endDate") String endDate) {
        this.page = page;
        this.status = status;
        this.cardType = cardType;
        this.bankName = bankName;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
