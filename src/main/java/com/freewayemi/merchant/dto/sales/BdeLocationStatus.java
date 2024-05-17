package com.freewayemi.merchant.dto.sales;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BdeLocationStatus {
    private Boolean status;
    private String statusHeader;
    private String statusMsg;
}
