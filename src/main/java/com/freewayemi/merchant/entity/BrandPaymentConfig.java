package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "brand_payment_configs")
@Data
@EqualsAndHashCode(callSuper = true)
public class BrandPaymentConfig extends BaseEntity {

    private String brandId;
    private HdfcDcEmiConfig hdfcDcEmiConfig;
    private IsgPgConfig isgPgConfig;
    private CCAvenueEmiPgConfig ccavenueEmiPgConfig;
    // This will be the row id of brands_products table (optional)
    private String productId;

}
