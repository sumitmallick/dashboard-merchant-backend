package com.freewayemi.merchant.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CCAvenueEmiPgConfigRequest {

    private Boolean isSubMerchantIdMandatory;
    private String subMerchantId;
    private String merchantId;
    private String terminalId;
    private String accessCode;
    private String encryptionKey;

    @JsonCreator
    public CCAvenueEmiPgConfigRequest(@JsonProperty("isSubMerchantIdMandatory") Boolean isSubMerchantIdMandatory,
                                      @JsonProperty("subMerchantId") String subMerchantId,
                                      @JsonProperty("merchantId") String merchantId,
                                      @JsonProperty("terminalId") String terminalId,
                                      @JsonProperty("accessCode") String accessCode,
                                      @JsonProperty("encryptionKey") String encryptionKey) {
        this.isSubMerchantIdMandatory = isSubMerchantIdMandatory;
        this.subMerchantId = subMerchantId;
        this.merchantId = merchantId;
        this.terminalId = terminalId;
        this.accessCode = accessCode;
        this.encryptionKey = encryptionKey;
    }

}
