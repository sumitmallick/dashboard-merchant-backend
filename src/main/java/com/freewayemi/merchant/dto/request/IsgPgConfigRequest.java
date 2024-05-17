package com.freewayemi.merchant.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IsgPgConfigRequest {
    private String merchantCode;
    private String bankId;
    private String secureSecret;
    private String accessCode;
    private String encryptionKey;
    private String mccCode;
    private String terminalId;

    @JsonCreator
    public IsgPgConfigRequest(@JsonProperty("merchantCode") String merchantCode, @JsonProperty("bankId") String bankId,
                              @JsonProperty("secureSecret") String secureSecret,
                              @JsonProperty("accessCode") String accessCode,
                              @JsonProperty("encryptionKey") String encryptionKey, @JsonProperty("mccCode") String mccCode,
                              @JsonProperty("terminalId") String terminalId) {
        this.merchantCode = merchantCode;
        this.bankId = bankId;
        this.secureSecret = secureSecret;
        this.accessCode = accessCode;
        this.encryptionKey = encryptionKey;
        this.mccCode = mccCode;
        this.terminalId = terminalId;
    }
}
