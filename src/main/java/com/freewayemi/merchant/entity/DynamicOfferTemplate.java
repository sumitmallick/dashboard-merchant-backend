package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.dto.offer.DynamicOffer;
import com.freewayemi.merchant.commons.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "dynamic_offer_templates")
@Data
@EqualsAndHashCode(callSuper = true)
public class DynamicOfferTemplate extends BaseEntity {
    private String name;
    private List<DynamicOffer> offers;
    private List<DynamicOffer> margins;
    private String type;
    private Float ccBaseRate;
    private Float dcBaseRate;
}
