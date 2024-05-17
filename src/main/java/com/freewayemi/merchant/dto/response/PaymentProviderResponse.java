package com.freewayemi.merchant.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class PaymentProviderResponse {

    public Integer code;

    public String status;

    public String message;
    public List<PPCBMappings> ppcbMappings;
}
