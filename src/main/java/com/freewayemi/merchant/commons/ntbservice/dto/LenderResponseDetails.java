package com.freewayemi.merchant.commons.ntbservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class LenderResponseDetails {
    private String lenderStatusCode;
    private String lenderStatus;
    private String lenderStatusMessage;
    private Instant updatedAt;

}
