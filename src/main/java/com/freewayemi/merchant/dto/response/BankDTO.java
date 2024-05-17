package com.freewayemi.merchant.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BankDTO {
    private final String code;
    private final String name;
    private final String url;

    @JsonCreator
    public BankDTO(@JsonProperty("code") String code,
                   @JsonProperty("name") String name,
                   @JsonProperty("url") String url) {
        this.code = code;
        this.name = name;
        this.url = url;
    }
}
