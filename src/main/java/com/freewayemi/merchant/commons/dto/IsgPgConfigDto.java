package com.freewayemi.merchant.commons.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IsgPgConfigDto {
    private String merchantCode;
    private String bankId;
    private String secureSecret;
    private String accessCode;
    private String encryptionKey;
    private String mccCode;
    private String terminalId;

}
