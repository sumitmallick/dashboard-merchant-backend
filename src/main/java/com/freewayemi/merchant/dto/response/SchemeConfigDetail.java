package com.freewayemi.merchant.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SchemeConfigDetail {
    private String cardType;
    private String bankCode;
    private String merchantId;
    private String brandId;
    private String productId;
    private final String schemeMappingId;
    private List<SchemeData> schemeData;

    @JsonCreator
    public SchemeConfigDetail(
            @JsonProperty("cardType") String cardType,
            @JsonProperty("bankCode") String bankCode,
            @JsonProperty("merchantId") String merchantId,
            @JsonProperty("brandId") String brandId,
            @JsonProperty("productId") String productId,
            @JsonProperty("schemeMappingId") String schemeMappingId,
            @JsonProperty("schemeData") List<SchemeData> schemeData) {
        this.cardType = cardType;
        this.bankCode = bankCode;
        this.merchantId = merchantId;
        this.brandId = brandId;
        this.productId = productId;
        this.schemeData = schemeData;
        this.schemeMappingId = schemeMappingId;
    }
}
