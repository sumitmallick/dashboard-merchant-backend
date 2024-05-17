package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AsyncClaimRequest {

    private String transactionId;
    private String serialNumber;
}
