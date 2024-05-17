package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.bo.MerchantSessionBO;
import com.freewayemi.merchant.entity.MerchantSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class MerchantSessionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantSessionController.class);

    private final MerchantSessionBO merchantSessionBO;

    @Autowired
    public MerchantSessionController(MerchantSessionBO merchantSessionBO) {
        this.merchantSessionBO = merchantSessionBO;
    }

    @GetMapping("/internal/api/v1/getMerchantSession")
    public MerchantSession getMerchantSession(@RequestParam String token) {
        LOGGER.info("Request to get merchant session");
        return merchantSessionBO.getMerchantSession(token);
    }

    @PostMapping("/internal/api/v1/saveMerchantSession")
    public void saveMerchantSession(@RequestBody MerchantSession merchantSession) {
        LOGGER.info("Request to save merchant session");
        merchantSessionBO.saveMerchantSession(merchantSession);
    }

}
