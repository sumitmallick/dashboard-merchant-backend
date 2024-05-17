package com.freewayemi.merchant.dto.request;

import lombok.Data;

@Data
public class MerchantLeadRequest {

    private String mobile;
    private String email;
    private String gst;
    private String meCode;
    private String merchantCategory;
    private String pinCode;
}
