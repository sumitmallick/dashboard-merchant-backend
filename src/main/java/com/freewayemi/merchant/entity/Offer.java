package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "offers")
@Data
@EqualsAndHashCode(callSuper = true)
public class Offer extends BaseEntity {
    private String type;
    private String merchantId;
    private String category;
    private Integer tenure;
    private float subvention;
    private String cardType;
    private String productId;
    private List<String> productIds;
    private String bankCode;
    private String brandId;
    private Instant validFrom;
    private Instant validTo;
    private Boolean isValid;
    private String subventionType;
    private Float minAmount;
    private Float maxOfferAmount;
    private Float offerPercentage;
    private Float bankPercentShare;
    private Float bankShareAmt;
    private Float brandPercentShare;
    private Float brandShareAmt;
    private String offerDescription;
    private Integer velocity;
    private Integer effectiveTenure;
    private Float maxBankShare;
    private Float maxBrandShare;
    private Float minMarginDownPaymentAmount;
    private Float maxMarginDownPaymentAmount;

    private List<String> applicableStates;
    private List<String> exclusionStates;
    private String partner;

    /**
     * @Field offlineAdvanceEMITenure
     * This field is a place holder to store tenure value using which payment will calculate the downpayment amount
     * which will be collect by the merchant from customer offline.
     * e.g. amount = 1,00,000
     * tenure = 8
     * offlineAdvanceEMITenure = 2
     * downpayment = 2 * (1,00,000 / (8 + 2)) = 20,000 to be collected by merchant from customer as cash
     * remaining 80,000 will be loan booking amount for 8 months
     * This requirement was raised with Allen Integration LEN-988, LEN-1010
     * This field is temporary solution specific for allen integration request you to not use this for any other purpose
     */
    private Integer offlineAdvanceEmiTenure;

    private String segmentId;
    private Float maxAmount;
    // IMPORTANT NOTE:
    // If any field is added in this, you need add the same in admin repository -> merchant_service.py ->
    // update_merchant_offers()
}
