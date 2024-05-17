package com.freewayemi.merchant.commons.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "merchant_discount_rates")
@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantDiscountRate extends BaseEntity {
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
}
