package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.bo.StoreUserRewardsBO;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.entity.StoreUserReward;
import com.freewayemi.merchant.service.AuthCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
public class StoreUserRewardsController {
    private final StoreUserRewardsBO storeUserRewardsBO;
    private final AuthCommonService authCommonService;

    @Autowired
    public StoreUserRewardsController(StoreUserRewardsBO storeUserRewardsBO, AuthCommonService authCommonService) {

        this.storeUserRewardsBO = storeUserRewardsBO;
        this.authCommonService = authCommonService;
    }

    @GetMapping("/api/v1/storeUsers/rewards")
    public List<StoreUserReward> get(HttpServletRequest httpServletRequest) {
        Map<String, String> credentials = authCommonService.getMerchantId(httpServletRequest).getCredentials();
        String storeUserId = null != credentials ? credentials.get("storeUserId") : "";
        if (StringUtils.hasText(storeUserId)) {
            return storeUserRewardsBO.getRewards(storeUserId);
        } else {
            throw new FreewayException("Store User Not found");
        }
    }
}
