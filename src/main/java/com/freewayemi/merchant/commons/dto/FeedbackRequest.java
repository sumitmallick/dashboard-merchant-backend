package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;

import java.util.List;

@Data
public class FeedbackRequest {
    private final String transactionId;
    private final String merchantId;
    private final String merchantRating;
    private final List<String> positives;
    private final List<String> preferences;
    private final String comment;
    private final boolean skipped;

    @JsonCreator
    public FeedbackRequest(String transactionId, String merchantId, String merchantRating, List<String> positives,
                           List<String> preferences, String comment, boolean skipped) {
        this.transactionId = transactionId;
        this.merchantId = merchantId;
        this.merchantRating = merchantRating;
        this.positives = positives;
        this.preferences = preferences;
        this.comment = comment;
        this.skipped = skipped;
    }
}
