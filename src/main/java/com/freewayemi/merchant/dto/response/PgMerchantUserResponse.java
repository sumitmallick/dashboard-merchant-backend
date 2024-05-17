package com.freewayemi.merchant.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;

@Data
public class PgMerchantUserResponse {
    private final String displayId;
    private final String apiKey;

    @JsonCreator
    public PgMerchantUserResponse(String displayId, String apiKey) {
        this.displayId = displayId;
        this.apiKey = apiKey;
    }
}
