package com.freewayemi.merchant.dto.sales;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class PinCodeResult {
    @JsonProperty("address_components")
    private List<AddressComponents> addressComponents;
    @JsonProperty("formatted_address")
    private String formattedAddress;
}
