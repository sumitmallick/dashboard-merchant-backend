package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "merchant_products")
@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantProduct extends BaseEntity {
    private String uuid;
    private String merchantId;
    private String productName;
    private String productCategory;
    private Float productPrice;
    private String productId;
    private List<String> productImages;
    private Boolean active;
    private Boolean gstIncluded;

    public void addImage(String url) {
        if (null == productImages)
            productImages = new ArrayList<>();
        productImages.add(url);
    }
}
