package com.freewayemi.merchant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freewayemi.merchant.bo.MerchantUserBO;
import com.freewayemi.merchant.bo.OfferBO;
import com.freewayemi.merchant.commons.dto.AllOfferDetailsResponse;
import com.freewayemi.merchant.commons.dto.MerchantResponse;
import com.freewayemi.merchant.commons.dto.OfferResponse;
import com.freewayemi.merchant.dto.AdditionalOfferResponse;
import com.freewayemi.merchant.dto.request.AllApplicableOfferRequest;
import com.freewayemi.merchant.dto.request.OfferRequest;
import com.freewayemi.merchant.service.MerchantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class OfferController {
    private final OfferBO offerBO;
    private final MerchantUserBO merchantUserBO;
    private final MerchantService merchantService;
    private final Logger logger = LoggerFactory.getLogger(OfferController.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public OfferController(OfferBO offerBO, MerchantUserBO merchantUserBO, MerchantService merchantService) {
        this.offerBO = offerBO;
        this.merchantUserBO = merchantUserBO;
        this.merchantService = merchantService;
    }

    @GetMapping("/api/v1/offers")
    public List<OfferResponse> search() {
        return offerBO.getAll();
    }

    @PostMapping("/api/v1/offers")
    public void create(@Valid @RequestBody OfferRequest request) {
        offerBO.create(request);
    }

    @GetMapping("/api/v1/merchant/{mid}/offers")
    public List<OfferResponse> getMerchantOffers(@PathVariable("mid") String merchantId) {
        return merchantUserBO.getMerchantOffers(merchantId);
    }

    @GetMapping("/api/v1/merchant/additional/offers/{bid}/{pid}")
    public List<AdditionalOfferResponse> getAdditionalOffers(@PathVariable("bid") String brandId,
                                                             @PathVariable("pid") String productId,
                                                             @RequestParam("merchantId") String merchantId,
                                                             @RequestParam("amount") Float amount) {
        return offerBO.getAdditionalOffersForProduct(productId, brandId, merchantId, amount);
    }

//    @PostMapping("/api/v1/merchant/applicable/offers")
//    public List<OfferDetailsResponse> getApplicableOffers(@Valid @RequestBody ApplicableOfferRequest
//    applicableOfferRequest) {
//        return offerBO.getApplicableOffers(applicableOfferRequest);
//    }

    @PostMapping("/api/v1/merchant/applicable/offers")
    public AllOfferDetailsResponse getAllApplicableOffers(
            @Valid @RequestBody AllApplicableOfferRequest allApplicableOfferRequest) {

        if(StringUtils.hasText(allApplicableOfferRequest.getTransactionId())){
            logger.info("Request received to get all applicable offers for transactionId: {}",
                    allApplicableOfferRequest.getTransactionId());
            return offerBO.getAllApplicableOffersV2(allApplicableOfferRequest);
        }
        logger.info("Request received to get all applicable offers for merchantId: {}",
                allApplicableOfferRequest.getMerchantId());
        MerchantResponse merchantResponse = merchantService.getMerchantDetails(allApplicableOfferRequest.getMerchantId(),
                allApplicableOfferRequest.getBrandProductId(), null, null, allApplicableOfferRequest.getBrandId(), null, null);
        return offerBO.getAllApplicableOffersV3(allApplicableOfferRequest, merchantResponse);
    }

}
