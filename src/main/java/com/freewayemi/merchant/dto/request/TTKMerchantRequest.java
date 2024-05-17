package com.freewayemi.merchant.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.utils.Util;
import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class TTKMerchantRequest {
    private final String mobile;
    private final String email;
    private final String storeCode;
    private final String otp;

    @JsonCreator
    public TTKMerchantRequest(@JsonProperty("mobile") String mobile,
                              @JsonProperty("email") String email,
                              @JsonProperty("storeCode") String storeCode,
                              @JsonProperty("otp") String otp) {
        this.mobile = StringUtils.hasText(mobile) ? Util.formatMobile(mobile) : "";
        this.email = email;
        this.storeCode = storeCode;
        this.otp = otp;
    }
}
