package com.freewayemi.merchant.commons.dto.refund;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.dto.TransactionV2Response;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class RefundResponse {

	private String orderId;
	private String paymentTxnId;
	private List<TransactionV2Response.RefundInfo> refunds;
	private Instant timestamp;

	@JsonCreator
	public RefundResponse(@JsonProperty("orderId") String orderId, @JsonProperty("paymentTxnId") String paymentTxnId,
			@JsonProperty("refunds") List<TransactionV2Response.RefundInfo> refunds,
			@JsonProperty("timestamp") Instant timestamp) {
		this.orderId = orderId;
		this.paymentTxnId = paymentTxnId;
		this.refunds = refunds;
		this.timestamp = timestamp;
	}

}
