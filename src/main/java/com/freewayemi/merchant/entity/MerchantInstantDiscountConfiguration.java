package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "merchant_instant_discount_configuration")
@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantInstantDiscountConfiguration extends BaseEntity {
    private String merchantId;
    private String brandId;
    private List<String> offerType;
    //  emiInstantDiscount,
//  additionalInstantDiscount,
//  merchantDiscount
    private Float additionalMdr;
    private String status;

}