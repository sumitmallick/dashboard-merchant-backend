package com.freewayemi.merchant.dto.sales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class GstReq {
    private String mid;
    private String type;
    private String user;
    private String pan;
    private String gst;
    private String account_number;
    private String account_ifsc;
    private String lat;
    private String lon;

}
