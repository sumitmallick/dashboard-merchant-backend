package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import com.freewayemi.merchant.enums.EarningState;
import com.freewayemi.merchant.enums.EarningType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "merchant_earnings")
@Data
@EqualsAndHashCode(callSuper = true)
public class Earning extends BaseEntity {
    private String scratchCardId;
    private String merchantId;
    private String referralMerchantId;
    private String txnId;
    private long amount;
    private EarningState earningState;
    private EarningType earningType;
    private Instant expiryDate;
    private Instant scratchedDate;
    private Instant redeemedDate;
    private Instant redeemableDate;
    private Boolean toBeNotified;
    private Instant notifiedAt;
    private String tag;
}
