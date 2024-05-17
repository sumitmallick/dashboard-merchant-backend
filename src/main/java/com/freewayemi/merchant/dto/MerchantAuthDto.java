package com.freewayemi.merchant.dto;

import com.freewayemi.merchant.enums.MerchantAuthSource;
import lombok.Builder;
import lombok.Data;

import javax.servlet.http.HttpServletRequest;

@Data
@Builder
public class MerchantAuthDto {

    private final MerchantAuthSource source;
    private final HttpServletRequest request;
    private final String merchantIdOrDisplayId;
}
