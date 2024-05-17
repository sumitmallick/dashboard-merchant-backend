package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductSkuResponse {
    private Integer code;
    private String status;
    private String message;
    private String productSkuId;
}
