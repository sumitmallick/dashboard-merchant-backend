package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "kyc_link")
@Data
@EqualsAndHashCode(callSuper = true)
public class KYCLink extends BaseEntity {
    private String merchantId;
}
