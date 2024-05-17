package com.freewayemi.merchant.dto.request;

import lombok.Data;

@Data
public class PartnerInfoResponse {
    public Integer code;
    public String status;
    public String message;
    public PartnerInfo partnerInfo;
}
