package com.freewayemi.merchant.commons.bo.eligibility.provider;

import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.commons.bo.eligibility.EligibilityRequest;
import com.freewayemi.merchant.commons.bo.eligibility.EligibilityResponse;
import com.freewayemi.merchant.commons.ntbservice.bo.NtbService;
import com.freewayemi.merchant.commons.ntbservice.dto.NtbLoanDto;
import com.freewayemi.merchant.commons.ntbservice.dto.NtbLoanEligibilityResponse;
import com.freewayemi.merchant.commons.ntbservice.helper.NtbServiceConstants;
import com.freewayemi.merchant.commons.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class KotakNtbEligibilityProvider implements EligibilityProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(KotakNtbEligibilityProvider.class);

    private final NtbService ntbService;

    @Autowired
    public KotakNtbEligibilityProvider(NtbService ntbService) {
        this.ntbService = ntbService;
    }

    @Override
    public EligibilityResponse check(EligibilityRequest request) {
        LOGGER.info("Check eligibility for mobile number: {}", request.getMobile());
        boolean isEligible = false;
        try {
            if (StringUtils.hasText(request.getAppVersion())) {
                NtbLoanEligibilityResponse ntbLoanEligibilityResponse = ntbService.checkLoanEligibility(getNtbLoanDto(request), NtbServiceConstants.LOAN_ELIGIBILITY_ENDPOINT);
                if (Util.isNotNull(ntbLoanEligibilityResponse)) {
                    isEligible = NTBLoanStatuses.isApproved(ntbLoanEligibilityResponse.getLoanStatus(), NtbProviderEnum.KKBK.name()) &&
                            !NTBLoanStatuses.isLoanDisbursed(ntbLoanEligibilityResponse.getLoanStatus());
                    if (null != ntbLoanEligibilityResponse.getEligibleAmount()) {
                        LOGGER.info("Consumer: {} is not eligible for the kotak ntb transaction since the current credit limit: {} " +
                                "is less than the transaction limit: {}", request.getMobile(), ntbLoanEligibilityResponse.getEligibleAmount(), request.getAmount());
                        isEligible = isEligible && Float.parseFloat(ntbLoanEligibilityResponse.getEligibleAmount()) >= request.getAmount();
                    }
                    return EligibilityResponse.builder()
                            .eligible(isEligible)
                            .bankCode(BankEnum.KKBK.getCode())
                            .eligibleStatus(ntbLoanEligibilityResponse.getUserType())
                            .eligibilityUrl(ntbLoanEligibilityResponse.getRedirectionUrl())
                            .minEligibleTenure(Integer.parseInt(ntbLoanEligibilityResponse.getMinTenure()))
                            .maxEligibleTenure(Integer.parseInt(ntbLoanEligibilityResponse.getMaxTenure()))
                            .eligibleAmount(ntbLoanEligibilityResponse.getEligibleAmount())
                            .maxEligibleAmount(ntbLoanEligibilityResponse.getMaxCreditLimit())
                            .processingRate(ntbLoanEligibilityResponse.getProcessingFeeRate())
                            .maxProcessingFee(ntbLoanEligibilityResponse.getMaxProcessingFee())
                            .cardType(getCardType())
                            .loanId(ntbLoanEligibilityResponse.getLoanId())
                            .build();
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred while checking eligibility with kotak ntb preapproval for mobile: " + request.getMobile(), e);
        }
        LOGGER.info("Check eligibility for mobile number: {} is: {}", request.getMobile(), isEligible);
        return null;
    }

    @Override
    public PaymentProviderEnum getProvider() {
        return PaymentProviderEnum.kotakntb;
    }

    @Override
    public String getBankCode() {
        return BankEnum.KKBK.getCode();
    }

    @Override
    public int getScore() {
        return 7;
    }

    @Override
    public String getCardType() {
        return CardTypeEnum.NTB.getCardType();
    }

    private NtbLoanDto getNtbLoanDto(EligibilityRequest request) {
        return NtbLoanDto.builder()
                .mobileNumber(request.getMobile())
                .provider(NtbProviderEnum.KKBK.name())
                .amount(request.getAmount())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .ip(request.getIp())
                .build();
    }
}

