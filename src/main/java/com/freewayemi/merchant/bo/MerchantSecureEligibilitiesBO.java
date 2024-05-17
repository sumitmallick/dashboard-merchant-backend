package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.commons.dto.CheckEligibilityRequest;
import com.freewayemi.merchant.commons.dto.SecureTransactionRequest;
import com.freewayemi.merchant.commons.dto.SecureTransactionResponse;
import com.freewayemi.merchant.commons.entity.SecurityCredentials;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.utils.EncryptionUtil;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.request.ValidateOtpRequest;
import com.freewayemi.merchant.dto.response.CheckEligibilityResponse;
import com.freewayemi.merchant.entity.Eligibilities;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.utils.MerchantStatus;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Component
public class MerchantSecureEligibilitiesBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantSecureEligibilitiesBO.class);

    private final MerchantEligibilitiesBO merchantEligibilitiesBO;

    private final CacheBO cacheBO;
    private final MerchantUserBO merchantUserBO;
    private final Gson gson = new Gson();

    @Autowired
    public MerchantSecureEligibilitiesBO(MerchantEligibilitiesBO merchantEligibilitiesBO, CacheBO cacheBO,
                                         MerchantUserBO merchantUserBO) {
        this.merchantEligibilitiesBO = merchantEligibilitiesBO;
        this.cacheBO = cacheBO;
        this.merchantUserBO = merchantUserBO;
    }

    public SecureTransactionResponse getEligibilities(MerchantUser mu, SecureTransactionRequest request,
                                                      String source, Boolean isWithOtp, String partner) {
        SecurityCredentials securityCredentials = mu.getSecurityCredentials();
        if (StringUtils.hasText(partner)){
            securityCredentials = merchantUserBO.updateSecurityCredentialsWithTenant(mu, partner);
        }
        String decryptedRequest;
        try {
            decryptedRequest = EncryptionUtil.decrypt(request.getEncryptedRequest(),
                    securityCredentials.getSecretKey(), securityCredentials.getIvKey(), request.getSecretKeyType(),
                    securityCredentials.getCipher());
            LOGGER.info("Plaint text check eligibility request is: {}", decryptedRequest);
        } catch (Exception e) {
            LOGGER.error("Exception occurred while decrypting request: ", e);
            throw new FreewayException("Unable to decrypt request");
        }

        CheckEligibilityRequest checkEligibilityRequest;
        try {
            checkEligibilityRequest = Util.convertToJsonObject(decryptedRequest, CheckEligibilityRequest.class);
            if(StringUtils.hasText(partner)){
                checkEligibilityRequest.setPartner(partner);
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred while converting request to pgTransactionRequest object: ", e);
            throw new FreewayException("Incorrect request body");
        }
        CheckEligibilityResponse checkEligibilityResponse;
        if (isWithOtp) {
            checkEligibilityResponse = merchantEligibilitiesBO.getCheckEligibilityWithOtp(mu, checkEligibilityRequest);
        } else {
            checkEligibilityResponse = getEligibilitiesFromCache(mu, checkEligibilityRequest, source);
        }
        LOGGER.info("checkEligibilityResponse: {}", checkEligibilityResponse);
        try {
            String encryptedResp = EncryptionUtil.encrypt(Util.convertToString(checkEligibilityResponse, false),
                    securityCredentials.getSecretKey(), securityCredentials.getIvKey(), request.getSecretKeyType(),
                    securityCredentials.getCipher());
            LOGGER.info("Encrypted check eligibility response is: {}", encryptedResp);
            return SecureTransactionResponse.builder().encryptedResponse(encryptedResp).build();
        } catch (Exception e) {
            LOGGER.error("Exception occurred while encrypting response: ", e);
            throw new FreewayException("Unable to encrypting response");
        }
    }

    private CheckEligibilityResponse getEligibilitiesFromCache(MerchantUser mu, CheckEligibilityRequest checkEligibilityRequest, String source){
        String eligibilities = "";
//        cacheBO.getDecryptedValueFromCache(RedisKeysConstants.ELIGIBILITY_DATA + mu.getId().toString() + "_" + checkEligibilityRequest.getMobile());
        if(StringUtils.hasText(eligibilities)){
            LOGGER.info("eligibilitues: {}", eligibilities);
            return gson.fromJson(eligibilities, CheckEligibilityResponse.class);
        }
        CheckEligibilityResponse checkEligibilityResponse = merchantEligibilitiesBO.checkEligibility(mu, checkEligibilityRequest, source);
//        cacheBO.putInCache(RedisKeysConstants.ELIGIBILITY_DATA + mu.getId().toString() + "_" + checkEligibilityRequest.getMobile(), gson.toJson(checkEligibilityResponse));
        return checkEligibilityResponse;
    }

    public SecureTransactionResponse validateOtpAndSendEligibilities(MerchantUser mu,
                                                                     SecureTransactionRequest request) {
        SecurityCredentials securityCredentials = mu.getSecurityCredentials();
        String decryptedRequest;
        try {
            decryptedRequest = EncryptionUtil.decrypt(request.getEncryptedRequest(),
                    securityCredentials.getSecretKey(), securityCredentials.getIvKey(), request.getSecretKeyType(),
                    securityCredentials.getCipher());
            LOGGER.info("Plaint text check eligibility with otp request is: {}", decryptedRequest);
        } catch (Exception e) {
            LOGGER.error("Exception occurred while decrypting request: ", e);
            throw new FreewayException("Unable to decrypt request");
        }

        ValidateOtpRequest validateOtpRequest;
        try {
            validateOtpRequest = Util.convertToJsonObject(decryptedRequest, ValidateOtpRequest.class);
        } catch (Exception e) {
            LOGGER.error("Exception occurred while converting request to pgTransactionRequest object: ", e);
            throw new FreewayException("Incorrect request body");
        }
        CheckEligibilityResponse checkEligibilityResponse = getEligibilitiesWithOtpFromCache(mu, validateOtpRequest);

        try {
            String encryptedResp = EncryptionUtil.encrypt(Util.convertToString(checkEligibilityResponse, false),
                    securityCredentials.getSecretKey(), securityCredentials.getIvKey(), request.getSecretKeyType(),
                    securityCredentials.getCipher());
            LOGGER.info("Encrypted check eligibility response is: {}", encryptedResp);
            return SecureTransactionResponse.builder().encryptedResponse(encryptedResp).build();
        } catch (Exception e) {
            LOGGER.error("Exception occurred while encrypting response: ", e);
            throw new FreewayException("Unable to encrypting response");
        }
    }

    private CheckEligibilityResponse getEligibilitiesWithOtpFromCache(MerchantUser mu, ValidateOtpRequest validateOtpRequest){
        Eligibilities dbEligibilities = merchantEligibilitiesBO.findById(validateOtpRequest.getpaymentRefNo());
        if(Objects.nonNull(dbEligibilities) && StringUtils.hasText(dbEligibilities.getMobile())) {
            String eligibilities = "";
//                    cacheBO.getDecryptedValueFromCache(RedisKeysConstants.ELIGIBILITY_VALIDATE_OTP_DATA + mu.getId().toString() + "_" + dbEligibilities.getMobile());
            if (StringUtils.hasText(eligibilities)) {
                return gson.fromJson(eligibilities, CheckEligibilityResponse.class);
            }
        }
        CheckEligibilityResponse checkEligibilityResponse = merchantEligibilitiesBO.validateOtpAndSendEligibilities(mu, validateOtpRequest);
//        if(Objects.nonNull(dbEligibilities) && StringUtils.hasText(dbEligibilities.getMobile())) {
//            cacheBO.putInCache(RedisKeysConstants.ELIGIBILITY_VALIDATE_OTP_DATA + mu.getId().toString() + "_" + dbEligibilities.getMobile(), gson.toJson(checkEligibilityResponse));
//        }
        return checkEligibilityResponse;
    }

    public SecureTransactionResponse getEligibilitiesWithCardDetails(MerchantUser mu, SecureTransactionRequest request,
                                                                     String source) {
        if (!MerchantStatus.approved.name().equals(mu.getStatus())) {
            throw new FreewayException("merchant not approved.");
        }
        SecurityCredentials securityCredentials = mu.getSecurityCredentials();
        String decryptedRequest;
        try {
            decryptedRequest = EncryptionUtil.decrypt(request.getEncryptedRequest(),
                    securityCredentials.getSecretKey(), securityCredentials.getIvKey(), request.getSecretKeyType(),
                    securityCredentials.getCipher());
        } catch (Exception e) {
            LOGGER.error("Exception occurred while decrypting request: ", e);
            throw new FreewayException("Unable to decrypt request");
        }

        CheckEligibilityRequest checkEligibilityRequest;
        try {
            checkEligibilityRequest = Util.convertToJsonObject(decryptedRequest, CheckEligibilityRequest.class);
        } catch (Exception e) {
            LOGGER.error("Exception occurred while converting request to pgTransactionRequest object: ", e);
            throw new FreewayException("Incorrect request body");
        }
        CheckEligibilityResponse checkEligibilityResponse = merchantEligibilitiesBO.checkEligibilityWithCardDetails(mu,
                checkEligibilityRequest, source);

        try {
            String encryptedResp = EncryptionUtil.encrypt(Util.convertToString(checkEligibilityResponse, false),
                    securityCredentials.getSecretKey(), securityCredentials.getIvKey(), request.getSecretKeyType(),
                    securityCredentials.getCipher());
            LOGGER.info("Encrypted check eligibility response is: {}", encryptedResp);
            return SecureTransactionResponse.builder().encryptedResponse(encryptedResp).build();
        } catch (Exception e) {
            LOGGER.error("Exception occurred while encrypting response: ", e);
            throw new FreewayException("Unable to encrypting response");
        }
    }
}
