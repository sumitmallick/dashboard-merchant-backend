package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.dto.karza.BankAccVerificationResponse;
import com.freewayemi.merchant.commons.entity.BaseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@Document("session_activities")
@EqualsAndHashCode(callSuper = true)
public class MerchantSessionActivity extends BaseEntity {
    private String type;
    private String sessionId;
    private String paymentLinkId;
    private Instant lastActivityDate;
}
