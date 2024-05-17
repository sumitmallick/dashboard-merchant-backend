package com.freewayemi.merchant.dto.response;

import lombok.Data;

@Data
public class PartnerCredentials {
    public String secretKey;
    public String ivKey;
}
