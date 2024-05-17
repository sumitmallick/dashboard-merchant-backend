package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "brand_gst")
@Data
@EqualsAndHashCode(callSuper = true)
public class BrandMerchantData extends BaseEntity {
    private String brandId;
    private String gst;
    private String storeCode;
    private String distributorCode;
    private String merchantId;
}
