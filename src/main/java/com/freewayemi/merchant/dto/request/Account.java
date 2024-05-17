package com.freewayemi.merchant.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Account {
    private final String ifsc;
    private final String number;
    private final String name;

    @JsonCreator
    public Account(@JsonProperty("ifsc") String ifsc,
                   @JsonProperty("number") String number,
                   @JsonProperty("name") String name) {
        this.ifsc = ifsc;
        this.number = number;
        this.name = name;
    }
}
