package com.freewayemi.merchant.dto.sales;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AddressComponents {
    @JsonProperty("long_name")
    private String longName;
    @JsonProperty("short_name")
    private String shortName;
    private List<String> types;
}
