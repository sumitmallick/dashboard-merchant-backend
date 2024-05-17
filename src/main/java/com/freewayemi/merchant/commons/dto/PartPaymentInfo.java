package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.type.PaymentTypeEnum;
import lombok.Data;

@Data
public class PartPaymentInfo {

	private final PaymentTypeEnum paymentType;
	private final Float amount;

	@JsonCreator
	public PartPaymentInfo(@JsonProperty("paymentType") PaymentTypeEnum paymentType,
                           @JsonProperty("amount") Float amount) {
		this.paymentType = paymentType;
		this.amount = amount;
	}

}
