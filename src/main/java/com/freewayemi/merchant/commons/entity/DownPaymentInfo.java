package com.freewayemi.merchant.commons.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.freewayemi.merchant.commons.type.TransactionCode;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@JsonDeserialize(builder = DownPaymentInfo.DownPaymentInfoBuilder.class)
@Builder(builderClassName = "DownPaymentInfoBuilder", toBuilder = true)
@ToString
public class DownPaymentInfo {
	private Float amount;
	private String dpTransactionId;
	private String status;
	private Integer statusCode;
	private String statusMessage;
	private Integer tenure;
	private String bankCode;
	private String eligibility;
	private String cardType;
	private DpChargeInfo dpChargeInfo;
	private String refundTransactionId;
	private String dpRefundStatus;
	private Integer dpRefundStatusCode;
	private String dpRefundStatusMessage;

	@JsonPOJOBuilder(withPrefix = "")
	public static class DownPaymentInfoBuilder {
	}

	public void setTxnCode(TransactionCode transactionCode) {
		this.setStatus(transactionCode.getStatus());
		this.setStatusCode(transactionCode.getCode());
		this.setStatusMessage(transactionCode.getStatusMsg());
	}
}
