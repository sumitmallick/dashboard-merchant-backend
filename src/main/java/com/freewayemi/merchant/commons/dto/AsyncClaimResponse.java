package com.freewayemi.merchant.commons.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AsyncClaimResponse {

    private Boolean result;
    private String code;
    private String message;
}
