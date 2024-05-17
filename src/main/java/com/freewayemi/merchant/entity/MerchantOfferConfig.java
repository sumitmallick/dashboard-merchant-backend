package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.dto.offer.DynamicOffer;
import com.freewayemi.merchant.commons.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "merchant_offer_configs")
@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantOfferConfig extends BaseEntity {
    private String merchantId;
    private String dynamicOfferTemplateId;
    private String type;
    private List<DynamicOffer> offers;
    private List<DynamicOffer> margins;
    private Float ccBaseRate;
    private Float dcBaseRate;
    private Boolean lowCostEmi;
    private Boolean activated;
    private Instant activationDate;
    private Boolean enableConvenienceFee;
}
