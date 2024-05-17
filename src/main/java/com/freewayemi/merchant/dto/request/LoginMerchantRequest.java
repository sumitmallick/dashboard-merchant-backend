package com.freewayemi.merchant.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.utils.Util;
import lombok.Data;

@Data
public class LoginMerchantRequest {
    private final String mobile;
    @JsonCreator
    public LoginMerchantRequest(@JsonProperty("mobile") String mobile) {
        this.mobile = Util.formatMobile(mobile);
    }

    @Override
    public String toString() {
        return "mobile: " + Util.truncateMobile(mobile);
    }
}
