package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import com.freewayemi.merchant.dto.ReSubmissionValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "merchant_configs")
@Data
@EqualsAndHashCode (callSuper = true)
public class MerchantConfigsV2 extends BaseEntity {
    private String label;
    private List<ReSubmissionValue> values;
}
