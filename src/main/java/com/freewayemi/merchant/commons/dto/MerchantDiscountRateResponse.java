package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@JsonDeserialize(builder = MerchantDiscountRateResponse.MerchantDiscountRateResponseBuilder.class)
@Builder(builderClassName = "MerchantDiscountRateResponseBuilder", toBuilder = true)
public class MerchantDiscountRateResponse {
    private String id;
    private String cardType;
    private String bankCode;
    private Integer tenure;
    private String productId;
    private Float rate;
    private boolean active;
    private Integer score;
    private String merchantId;
    private String brandId;
    private List<String> productIds;
    private String createdBy;
    private String updatedBy;

    @JsonPOJOBuilder(withPrefix = "")
    public static class MerchantDiscountRateResponseBuilder {
    }
}
