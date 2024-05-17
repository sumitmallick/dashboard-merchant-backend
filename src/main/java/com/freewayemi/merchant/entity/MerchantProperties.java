package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.dto.Kyc;
import com.freewayemi.merchant.commons.entity.BaseEntity;
import com.freewayemi.merchant.dto.sales.Commercial;
import com.freewayemi.merchant.dto.sales.Merchandise;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "merchant_properties")
@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantProperties extends BaseEntity {
    private List<String> brandTags;
    private Merchandise merchandise;
    private String merchantId;
    private Commercial commercials;
    private Kyc kyc;
    private Map<String, List<String>> productCategoriesMap;
    private String noMerchandiseReason;
}
