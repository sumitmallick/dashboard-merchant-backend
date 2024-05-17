package com.freewayemi.merchant.dto.sales;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Switch {
    private String pan;
    private String gst;
    private String account_number;
    private String account_ifsc;
}
