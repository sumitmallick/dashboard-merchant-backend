package com.freewayemi.merchant.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BrandPaymentConfigResponse {
    private final Integer code;
    private final String status;
    private final String message;

}
