package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import com.freewayemi.merchant.commons.type.BankInterestTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "bank_interests")
@Data
@EqualsAndHashCode(callSuper = true)
public class BankInterest extends BaseEntity {

    // used to query for merchant associated txn
    private String merchantId;

    // use to query for brand associated txn
    private String brandId;

    List<InterestPerTenure> interestPerTenures;

    private Boolean isActive;

    private Instant validFrom;

    private Instant validTo;

    private String createdBy;

    private String updatedBy;

    private BankInterestTypeEnum bankInterestType;
}
