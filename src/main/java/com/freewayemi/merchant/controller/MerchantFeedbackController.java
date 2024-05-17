package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.commons.bo.FeedbackBO;
import com.freewayemi.merchant.commons.dto.FeedbackRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MerchantFeedbackController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantFeedbackController.class);
    private final FeedbackBO feedbackBO;

    @Autowired
    public MerchantFeedbackController(FeedbackBO feedbackBO) {
        this.feedbackBO = feedbackBO;
    }

    @PostMapping("/api/v1/feedback")
    public void feedback(@RequestBody FeedbackRequest request) {
        feedbackBO.save(null, request);
    }
}
