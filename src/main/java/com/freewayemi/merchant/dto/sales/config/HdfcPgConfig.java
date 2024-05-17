package com.freewayemi.merchant.dto.sales.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
public class HdfcPgConfig {

    private String terminalId;
    private String tranportalPassword;
    private String encryptionKey;
    @JsonCreator
    public HdfcPgConfig(@JsonProperty("terminalId") String terminalId,
                                  @JsonProperty("tranportalPassword") String tranportalPassword,
                                  @JsonProperty("encryptionKey") String encryptionKey){
        this.terminalId = terminalId;
        this.tranportalPassword = tranportalPassword;
        this.encryptionKey = encryptionKey;
    }
}
