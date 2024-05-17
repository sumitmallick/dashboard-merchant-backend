package com.freewayemi.merchant.dto.response;

import com.freewayemi.merchant.commons.type.MerchantResponseCode;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Builder
@Data
public class BrandMerchantResponse {

    public String message;
    public Map<String,String> brandMerchantResp;

    public Integer code;

    public String status;
}
