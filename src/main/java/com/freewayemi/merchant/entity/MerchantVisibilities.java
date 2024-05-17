package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document(collection = "visibilities")
@EqualsAndHashCode(callSuper = true)
public class MerchantVisibilities extends BaseEntity {
    private String name;
    private String status;
    private Instant activationDate;
    private String createdBy;
    private String merchantId;
    private String merchantDisplayId;
    private String samplePhotoUrl;
    private String merchantShopName;
}
