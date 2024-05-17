package com.freewayemi.merchant.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PartnerData {
    private String partner;
    private String status;
    private Boolean display;
    private String settlementDocumentsStatus;
}
