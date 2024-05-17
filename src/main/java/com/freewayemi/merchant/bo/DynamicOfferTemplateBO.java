package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.entity.DynamicOfferTemplate;
import com.freewayemi.merchant.repository.DynamicOfferTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DynamicOfferTemplateBO {
    private final DynamicOfferTemplateRepository dynamicOfferTemplateRepository;

    @Autowired
    public DynamicOfferTemplateBO(
            DynamicOfferTemplateRepository dynamicOfferTemplateRepository) {
        this.dynamicOfferTemplateRepository = dynamicOfferTemplateRepository;
    }

    public DynamicOfferTemplate find(String id) {
        return this.dynamicOfferTemplateRepository.findById(id).orElse(null);
    }

    public DynamicOfferTemplate findByName(String name) {
        return this.dynamicOfferTemplateRepository.findByName(name)
                .orElseThrow(() -> new FreewayException("Offer Template not found!"));
    }
}
