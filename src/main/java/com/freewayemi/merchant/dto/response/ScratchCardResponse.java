package com.freewayemi.merchant.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScratchCardResponse {
    private boolean isScratched;
    private long amount;
}
