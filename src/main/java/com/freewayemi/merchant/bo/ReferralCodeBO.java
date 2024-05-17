package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.dto.request.PartnerInfo;
import com.freewayemi.merchant.dto.request.PartnerInfoResponse;
import com.freewayemi.merchant.entity.ReferralCode;
import com.freewayemi.merchant.repository.ReferralCodeRepository;
import com.freewayemi.merchant.service.PaymentOpsService;
import com.freewayemi.merchant.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Component
public class ReferralCodeBO {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReferralCodeBO.class);

    @Autowired
    private ReferralCodeRepository referralCodeRepository;

    @Autowired
    private PaymentOpsService paymentOpsService;

    public Boolean partnerExistsByReferralCode(String code) {
        LOGGER.info("Find Partner exists for referral code: {}", code);
        ReferralCode referralCode = referralCodeRepository.findReferralCodeByReferralCodeAndIsActive(code, Boolean.TRUE).orElse(null);
        if(Objects.nonNull(referralCode)){
            return true;
        }
        return false;
    }

    public PartnerInfo findPartnerByReferralCode(String code) {
        LOGGER.info("Find Partner by ReferralCode: {}", code);
        ReferralCode referralCode = referralCodeRepository.findReferralCodeByReferralCodeAndIsActive(code, Boolean.TRUE).orElse(null);
        String partner = Constants.payment_PARTNER;
        if (Objects.nonNull(referralCode)) {
            partner = referralCode.getPartner();
        }
        return getPartnerInfo(partner);
    }

    public PartnerInfo getPartnerInfo(String partner){
        LOGGER.info("Find partner info by partner code: {}", partner);
        PartnerInfoResponse partnerInfoResponse = paymentOpsService.getPartnerInfo(partner);
        if (Objects.isNull(partnerInfoResponse) || Objects.isNull(partnerInfoResponse.getPartnerInfo()) || !StringUtils.hasText(partnerInfoResponse.getPartnerInfo().getCode())) {
            throw new FreewayException("No Partner found");
        }
        return partnerInfoResponse.getPartnerInfo();
    }

}
