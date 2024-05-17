package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrandMerchantCredentialDTO {
    private String externalCode;
    private String gstIn;
    private String emailId;
    private String storeName;
    private String storePinCode;
    private String merchantDisplayId;
    private MerchantApiCredentialsDto merchantApiCredentialsDto;

    @JsonCreator
    public BrandMerchantCredentialDTO(@JsonProperty(value = "externalCode") String externalCode,
                                      @JsonProperty(value = "gstIn") String gstIn,
                                      @JsonProperty(value = "emailId") String emailId,
                                      @JsonProperty(value = "storeName") String storeName,
                                      @JsonProperty(value = "storePinCode") String storePinCode,
                                      @JsonProperty(value = "merchantDisplayId") String merchantDisplayId,
                                      @JsonProperty(value = "merchantApiCredentials") MerchantApiCredentialsDto merchantApiCredentialsDto) {
        this.externalCode = externalCode;
        this.gstIn = gstIn;
        this.emailId = emailId;
        this.storeName = storeName;
        this.storePinCode = storePinCode;
        this.merchantDisplayId = merchantDisplayId;
        this.merchantApiCredentialsDto = merchantApiCredentialsDto;
    }
}
