package com.freewayemi.merchant.commons.entity;

import com.freewayemi.merchant.commons.juspay.CardInfo;
import com.freewayemi.merchant.commons.juspay.CardToken;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "consumer_cards")
@Data
@EqualsAndHashCode(callSuper = true)
public class ConsumerCard extends BaseEntity {
	private String consumerId;
	private String maskedNumber;
	private String bankCode;
	private Boolean emiEnabled;
	private Boolean pendingEligibility;
	private CardInfo cardInfo;
	private CardToken cardToken;
	private String sms;
	private Boolean isVaultToken;
	private Boolean isSaveCard;
	private String deviceToken;
}
