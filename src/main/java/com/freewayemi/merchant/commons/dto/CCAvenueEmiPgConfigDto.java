package com.freewayemi.merchant.commons.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CCAvenueEmiPgConfigDto {

    private Boolean isSubMerchantIdMandatory;
    private String subMerchantId;
    private String merchantId;
    private String terminalId;
    private String accessCode;
    private String encryptionKey;

}
