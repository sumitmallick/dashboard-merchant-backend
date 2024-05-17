package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "merchant_segment_mapping")
@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantSegmentMapping extends BaseEntity {

    private String merchantId;
    private String segmentId;
    private Instant validFrom;
    private Instant validTo;
    private Boolean isValid;
}
