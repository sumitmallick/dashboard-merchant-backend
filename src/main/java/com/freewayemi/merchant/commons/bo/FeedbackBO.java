package com.freewayemi.merchant.commons.bo;

import com.freewayemi.merchant.commons.dto.FeedbackRequest;
import com.freewayemi.merchant.commons.entity.Feedback;
import com.freewayemi.merchant.commons.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FeedbackBO {
    private final FeedbackRepository feedbackRepository;

    @Autowired
    public FeedbackBO(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public void save(String consumer, FeedbackRequest request) {
        Feedback feedback = new Feedback();
        feedback.setConsumerId(consumer);
        feedback.setMerchantRating(request.getMerchantRating());
        feedback.setTransactionId(request.getTransactionId());
        feedback.setMerchantId(request.getMerchantId());
        feedback.setPositives(request.getPositives());
        feedback.setPreferences(request.getPreferences());
        feedback.setComment(request.getComment());
        feedbackRepository.save(feedback);
    }

    public List<Feedback> findByTransactionId(String transactionId) {
          return feedbackRepository.findByTransactionId(transactionId).orElse(null);
    }
}
