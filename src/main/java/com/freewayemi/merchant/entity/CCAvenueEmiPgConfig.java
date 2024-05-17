package com.freewayemi.merchant.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CCAvenueEmiPgConfig {

    private Boolean isSubMerchantIdMandatory;
    private String subMerchantId;
    private String merchantId;
    private String terminalId;
    private String accessCode;
    private String encryptionKey;

}
