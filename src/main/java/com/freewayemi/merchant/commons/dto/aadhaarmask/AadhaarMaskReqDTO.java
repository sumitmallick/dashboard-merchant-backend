package com.freewayemi.merchant.commons.dto.aadhaarmask;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class AadhaarMaskReqDTO {
        private String merchantId;
        private String key;
        private String source;
}
