package com.freewayemi.merchant.commons.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "consumer_feedbacks")
public class Feedback extends BaseEntity {
	private String consumerId;
	private String merchantRating;
	private String transactionId;
	private String merchantId;
	private List<String> positives;
	private List<String> preferences;
	private String comment;
	private Boolean skipped;
}
