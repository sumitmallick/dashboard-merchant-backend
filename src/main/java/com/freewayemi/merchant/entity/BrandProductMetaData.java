package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "brand_product_meta_data")
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
public class BrandProductMetaData extends BaseEntity {

    public String key;

    public List<String> brandIds;

    public List<String> categories;
    public List<String> ccCards;
    public List<String> dcCards;

    public Boolean isValid;
}
