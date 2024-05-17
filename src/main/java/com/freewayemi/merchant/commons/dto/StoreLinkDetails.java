package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@JsonDeserialize(builder = StoreLinkDetails.StoreLinkDetailsBuilder.class)
@Builder(builderClassName = "StoreLinkDetailsBuilder", toBuilder = true)
public class StoreLinkDetails {

    private String merchantId;
    private String orderId;
    private String email;
    private String mobile;
    private Float amount;
    private String name;
    private String transactionId;
    private String productId;
    private String productName;
    private String status;
    private Integer statusCode;
    private String statusMessage;
    private String createdBy;
    private Address address;
    private String returnUrl;
    private Integer maxTenure;
    private Map<String, String> customParams;

    @JsonPOJOBuilder(withPrefix = "")
    public static class StoreLinkDetailsBuilder {
    }

}
