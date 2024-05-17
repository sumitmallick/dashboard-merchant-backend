package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.bo.MerchantEarningsBO;
import com.freewayemi.merchant.bo.StoreUserRewardsBO;
import com.freewayemi.merchant.commons.bo.NotificationService;
import com.freewayemi.merchant.commons.dto.StoreUserRewardInfo;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.response.MerchantEarningsResponse;
import com.freewayemi.merchant.dto.response.ScratchCardResponse;
import com.freewayemi.merchant.service.AuthCommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class MerchantEarningsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantEarningsController.class);

    private final MerchantEarningsBO merchantEarningsBO;
    private final NotificationService notificationService;
    private final StoreUserRewardsBO storeUserRewardsBO;
    private final AuthCommonService authCommonService;

    @Autowired
    public MerchantEarningsController(MerchantEarningsBO merchantEarningsBO,
                                      NotificationService notificationService,
                                      StoreUserRewardsBO storeUserRewardsBO, AuthCommonService authCommonService) {
        this.merchantEarningsBO = merchantEarningsBO;
        this.notificationService = notificationService;
        this.storeUserRewardsBO = storeUserRewardsBO;
        this.authCommonService = authCommonService;
    }

    @GetMapping("/api/v1/earnings")
    public MerchantEarningsResponse getEarnings(@RequestParam(value = "partner", required = false) String partner,
                                                HttpServletRequest httpServletRequest) {
        String merchant = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        LOGGER.info("Fetching earnings for merchant {}", merchant);
        if (Util.isNotNull(partner)) {
            return merchantEarningsBO.getMerchantEarnings(merchant, partner);
        }
        return merchantEarningsBO.getMerchantEarnings(merchant, null);
    }

    @PutMapping("/api/v1/earnings/{mid}")
    public void createEarnings(@PathVariable("mid") String merchantId,
                               @RequestParam(value = "transactionId", required = false) String transactionId,
                               @RequestBody StoreUserRewardInfo storeUserRewardInfo,
                               HttpServletRequest httpServletRequest) {
        LOGGER.info("Update earnings for merchant {}", merchantId);
        try {
            merchantEarningsBO.updateDailyEarnings(merchantId, transactionId);
            merchantEarningsBO.updateEarnings(merchantId, transactionId);
            storeUserRewardsBO.updateStoreUserReward(storeUserRewardInfo, merchantId);
        } catch (Exception e) {
            LOGGER.info("Issue while updating earnings for {}", merchantId);
            notificationService.sendException("Issue while updating earnings for " + merchantId);
        }
    }

    @PutMapping("/api/v1/scratchCards/{sid}")
    public ScratchCardResponse scratchCard(@PathVariable("sid") String scratchCardId,
                                           @RequestParam(value = "partner", required = false) String partner,
                                           HttpServletRequest httpServletRequest) {
        String merchant = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        LOGGER.info("Update earnings for merchant {} with scratchId {}", merchant, scratchCardId);
        if (Util.isNotNull(partner)) {
            return merchantEarningsBO.scratchCard(merchant, scratchCardId, partner);
        }
        return merchantEarningsBO.scratchCard(merchant, scratchCardId, null);
    }
}
