package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "referral_codes")
@Data
@EqualsAndHashCode(callSuper = true)
public class ReferralCode extends BaseEntity {
    public String referralCode;
    public String partner;
    public Boolean isActive;
}
