package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "merchant_configs")
@Data
@EqualsAndHashCode (callSuper = true)
public class MerchantConfigs extends BaseEntity {
    private String label;
    private List<String> values;
    private Integer version;
    private Integer count;
}
