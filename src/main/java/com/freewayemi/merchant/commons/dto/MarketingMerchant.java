package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@JsonDeserialize(builder = MarketingMerchant.MarketingMerchantBuilder.class)
@Builder(builderClassName = "MarketingMerchantBuilder", toBuilder = true)
public class MarketingMerchant {
    private String merchantId;
    private String shopName;
    private String category;
    private Address address;
    private String brand;
    private String logo;
    private String EMIOption;
    private Boolean listEmpty;

    @JsonPOJOBuilder(withPrefix = "")
    public static class MarketingMerchantBuilder {
    }
}
