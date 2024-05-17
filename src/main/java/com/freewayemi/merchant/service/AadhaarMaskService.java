package com.freewayemi.merchant.service;

import com.freewayemi.merchant.commons.bo.NtbCoreService;
import com.freewayemi.merchant.commons.dto.AadhaarMaskResponse;
import com.freewayemi.merchant.commons.type.TransactionSource;
import com.freewayemi.merchant.entity.MerchantUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AadhaarMaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AadhaarMaskService.class);

    private final NtbCoreService ntbCoreService;

    @Autowired
    public AadhaarMaskService(NtbCoreService ntbCoreService) {
        this.ntbCoreService = ntbCoreService;
    }

    @Async
    public void checkAadhaarMask(MerchantUser mu, String key, String type, String name) {
        if (("Aadhaar Number").equalsIgnoreCase(mu.getSigDocType())
                && (("signatoryIDProof").equalsIgnoreCase(type) || ("signatoryIDProof").equalsIgnoreCase(name)
                  || ("signatoryAddressProof").equalsIgnoreCase(type) || ("signatoryAddressProof").equalsIgnoreCase(name)
        )) {
            AadhaarMaskResponse aadhaarMaskResponse = ntbCoreService.aadhaarMask(mu.getId().toString(), key, TransactionSource.merchantApp.name());
            if (aadhaarMaskResponse != null && ("success").equalsIgnoreCase(aadhaarMaskResponse.getStatus())) {
                LOGGER.info("Success in masking aadhaar for merchant id:{}", mu.getId().toString());
            } else {
                LOGGER.error("Exception occurred in masking aadhaar for merchant id:{}", mu.getId().toString());
            }
        }
    }
}
