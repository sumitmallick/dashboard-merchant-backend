package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "brand_products_inventory")
@Data
@EqualsAndHashCode(callSuper = true)
public class BrandProductsInventory extends BaseEntity {
    private String brandId;
    private String productName;
}
