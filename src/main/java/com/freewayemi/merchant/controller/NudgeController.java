package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.bo.MerchantEarningsBO;
import com.freewayemi.merchant.bo.NudgeBO;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.response.NudgeResponse;
import com.freewayemi.merchant.service.AuthCommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class NudgeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(NudgeController.class);

    private final MerchantEarningsBO merchantEarningsBO;
    private final NudgeBO nudgeBO;
    private final AuthCommonService authCommonService;

    @Autowired
    public NudgeController(MerchantEarningsBO merchantEarningsBO,
                           NudgeBO nudgeBO, AuthCommonService authCommonService) {
        this.merchantEarningsBO = merchantEarningsBO;
        this.nudgeBO = nudgeBO;
        this.authCommonService = authCommonService;
    }

    @GetMapping("/api/v1/nudges")
    public List<NudgeResponse> get(@RequestParam(value = "type", required = false) String type,
                                   @RequestParam(value = "partner", required = false) String partner,
                                   HttpServletRequest httpServletRequest) {
        String merchant = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        if (Util.isNotNull(partner)) {
            return nudgeBO.getNudges(merchant, type, partner);
        }
        return nudgeBO.getNudges(merchant, type, null);
    }

    @PutMapping("/api/v1/nudges/{nudgeId}")
    public void update(@PathVariable("nudgeId") String nudgeId, HttpServletRequest httpServletRequest) {
        String merchant = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        merchantEarningsBO.earningsNotified(merchant);
        nudgeBO.updateReadStatus(nudgeId);
    }

}
