package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.bo.MerchantSecureEligibilitiesBO;
import com.freewayemi.merchant.bo.MerchantUserBO;
import com.freewayemi.merchant.commons.dto.PartnerInfo;
import com.freewayemi.merchant.commons.dto.SecureTransactionRequest;
import com.freewayemi.merchant.commons.dto.SecureTransactionResponse;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.type.EligibilityApiType;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.entity.Partner;
import com.freewayemi.merchant.repository.PartnerRepository;
import com.freewayemi.merchant.service.AuthCommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class MerchantSecureEligibilityController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantSecureEligibilityController.class);

    private final MerchantUserBO merchantUserBO;
    private final MerchantSecureEligibilitiesBO merchantSecureEligibilitiesBO;
    private final AuthCommonService authCommonService;
    private final PartnerRepository partnerRepository;

    @Autowired
    public MerchantSecureEligibilityController(MerchantUserBO merchantUserBO,
                                               MerchantSecureEligibilitiesBO merchantSecureEligibilitiesBO,
                                               AuthCommonService authCommonService,
                                               PartnerRepository partnerRepository) {
        this.merchantUserBO = merchantUserBO;
        this.merchantSecureEligibilitiesBO = merchantSecureEligibilitiesBO;
        this.authCommonService = authCommonService;
        this.partnerRepository = partnerRepository;
    }

    @PostMapping("/secure/api/v1/checkEligibility")
    public SecureTransactionResponse checkEligibility(@RequestBody SecureTransactionRequest request,
                                                      @RequestParam(value = "partner", required = false) String partner,
                                                      HttpServletRequest httpServletRequest) {
        LOGGER.info("Secure request received to check eligibility with params: {}", request);
        try {
            String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
            MerchantUser mu = merchantUserBO.getUserByMerchantIdOrDisplayId(displayMerchantId);

            String userMobile = mu.getMobile();
            MerchantUser partnerUser = null;
            List<String> partners = mu.getPartners();
            if (Util.isNotNull(partner) && Util.isNotNull(partners)) {
                if (Util.isNotNull(partners) && partners.contains(partner)) {
                    partnerUser = merchantUserBO.getUserByMobile(userMobile + "_" + partner);
                    mu = partnerUser;
                }
                else {
                    throw new FreewayException(userMobile + " doesn't have any partner merchant with partner: " + partner);
                }
            }

            LOGGER.info("Request is for display id: {} merchant: {}", mu.getDisplayId(), mu.getShopName());
            SecureTransactionResponse secureTransactionResponse =  merchantSecureEligibilitiesBO.getEligibilities(mu, request,
                    EligibilityApiType.MERCHANT_SECURE_ELIGIBILITY_API.name(), false, null);
            LOGGER.info("secureTransactionResponse: {}", secureTransactionResponse);
            return secureTransactionResponse;
        } catch (FreewayException e) {
            LOGGER.error("FreewayException while checking eligibility: ", e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Exception while checking eligibility: ", e);
            throw new FreewayException("Something went wrong!");
        }
    }

    @PostMapping("/secure/api/v1/eligibility/check")
    public SecureTransactionResponse checkEligibilityWithOtp(@RequestBody SecureTransactionRequest request,
                                                             @RequestParam(value = "partner", required = false) String partner,
                                                             HttpServletRequest httpServletRequest) {
        LOGGER.info("Secure request received to check eligibility with params: {}", request);
        try {
            String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
            MerchantUser mu = merchantUserBO.getUserByMerchantIdOrDisplayId(displayMerchantId);

            String userMobile = mu.getMobile();
            MerchantUser partnerUser = null;
            List<String> partners = mu.getPartners();
            if (Util.isNotNull(partner) && Util.isNotNull(partners)) {
                if (Util.isNotNull(partners) && partners.contains(partner)) {
                    partnerUser = merchantUserBO.getUserByMobile(userMobile + "_" + partner);
                    mu = partnerUser;
                }
                else {
                    throw new FreewayException(userMobile + " doesn't have any partner merchant with partner: " + partner);
                }
            }

            LOGGER.info("Request is for display id: {} merchant: {}", mu.getDisplayId(), mu.getShopName());
            return merchantSecureEligibilitiesBO.getEligibilities(mu, request,
                    EligibilityApiType.MERCHANT_SECURE_ELIGIBILITY_API_WITH_OTP.name(), true, null);
        } catch (Exception e) {
            LOGGER.error("Exception while checking eligibility: ", e);
            throw new FreewayException("Something went wrong!");
        }
    }

    @PostMapping("/secure/api/v1/eligibility/validateOtp")
    public SecureTransactionResponse validateOtp(@RequestBody SecureTransactionRequest request,
                                                 HttpServletRequest httpServletRequest) {
        LOGGER.info("Request received to validate OTP with params: {}", request);
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserByMerchantIdOrDisplayId(displayMerchantId);
        LOGGER.info("Request is for display id: {} merchant: {}", mu.getDisplayId(), mu.getShopName());
        return merchantSecureEligibilitiesBO.validateOtpAndSendEligibilities(mu, request);
    }

    @PostMapping("/secure/api/v1/checkEligibilityWithCardDetails")
    public SecureTransactionResponse checkEligibilityWithCardDetails(@RequestBody SecureTransactionRequest request,
                                                                     HttpServletRequest httpServletRequest) {
        LOGGER.info("Request received to validate OTP with params: {}", request);
        String displayMerchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserByMerchantIdOrDisplayId(displayMerchantId);
        LOGGER.info("Request is for display id: {} merchant: {}", mu.getDisplayId(), mu.getShopName());
        return merchantSecureEligibilitiesBO.getEligibilitiesWithCardDetails(mu, request,
                EligibilityApiType.MERCHANT_SECURE_ELIGIBILITY_API_WITH_CARD_DETAILS.name());
    }

}
