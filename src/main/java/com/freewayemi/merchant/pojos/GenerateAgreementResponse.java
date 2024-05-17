package com.freewayemi.merchant.pojos;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.freewayemi.merchant.dto.sales.CommercialResponse;
import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenerateAgreementResponse {
    private final Integer code;
    private final String status;
    private final String statusMessage;
    private final String documentType;
    private final String documentUrl;
    private final String documentContent;
    private CommercialResponse commercialResponse;

    @JsonCreator
    public GenerateAgreementResponse(Integer code, String status, String statusMessage, String documentType, String documentUrl, String documentContent) {
        this.code = code;
        this.status = status;
        this.statusMessage = statusMessage;
        this.documentType = documentType;
        this.documentUrl = documentUrl;
        this.documentContent = documentContent;
    }
}

