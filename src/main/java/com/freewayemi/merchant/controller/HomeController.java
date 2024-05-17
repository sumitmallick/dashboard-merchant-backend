package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.bo.HomeBO;
import com.freewayemi.merchant.bo.MerchantUserBO;
import com.freewayemi.merchant.commons.bo.NotificationService;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.entity.MerchantConfigs;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.enums.AppType;
import com.freewayemi.merchant.repository.MerchantConfigsRepository;
import com.freewayemi.merchant.service.AuthCommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
public class HomeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);
    private final HomeBO homeBO;
    private final MerchantUserBO merchantUserBO;
    private final NotificationService notificationService;
    private final AuthCommonService authCommonService;
    private final MerchantConfigsRepository merchantConfigsRepository;
    @Autowired
    public HomeController(MerchantUserBO merchantUserBO, HomeBO homeBO,
                          NotificationService notificationService, AuthCommonService authCommonService,
                          MerchantConfigsRepository merchantConfigsRepository) {
        this.merchantUserBO = merchantUserBO;
        this.homeBO = homeBO;
        this.notificationService = notificationService;
        this.authCommonService = authCommonService;
        this.merchantConfigsRepository = merchantConfigsRepository;
    }

    @GetMapping("/api/v1/home")
    public ResponseEntity<?> home(@RequestHeader(value = "AppVersion", required = false) Integer appVersion,
                                  @RequestHeader(value = "AppType", required = false) String appType,
                                  @RequestParam(value = "partner", required = false) String partner,
                                  HttpServletRequest httpServletRequest) {
        LOGGER.info("Merchant home request received with params appType is : {}, appVersion : {} ", appType, appVersion);
        AppType appTypeEnum = AppType.getByType(appType);
        Optional<MerchantConfigs> prop = merchantConfigsRepository.findByLabel(appTypeEnum.getAppVersionLabel());
        int defaultMinVersion = AppType.IOS == appTypeEnum ? 5 : 69;
        if(prop.isPresent()){
            Integer value = prop.get().getVersion();
            if(Objects.nonNull(value)) {
                defaultMinVersion = value;
            }
        }
        if (appVersion < defaultMinVersion) {
            return ResponseEntity.ok(new HashMap<String, Boolean>() {{put("forceUpdate", true);}});
        }
        String merchant = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser user = merchantUserBO.getUserById(merchant);
        MerchantUser partnerUser = null;
        String userMobile = user.getMobile();
        List<String> partners = user.getPartners();
        if (Util.isNotNull(partner) && Util.isNotNull(partners)) {
            if (Util.isNotNull(partners) && partners.contains(partner)) {
                partnerUser = merchantUserBO.getUserByMobile(userMobile + "_" + partner);
                user = partnerUser;
            }
            else {
                throw new FreewayException(userMobile + " doesn't have any partner merchant with partner: " + partner);
            }
        }
        LOGGER.info("Received request for V1 home api for displayId: {}", user.getDisplayId());
        Map<String, String> credentials = authCommonService.getMerchantId(httpServletRequest).getCredentials();
        String storeUserId = null != credentials ? credentials.get("storeUserId") : "";
        try {
            return ResponseEntity.ok(homeBO.get(user, storeUserId));
        } catch (Exception e) {
            LOGGER.error("Exception home controller:: ", e);
            notificationService.sendException(
                    String.format("MerchantAPP: Home fetch issue with %s", user.getDisplayId()));
            throw new FreewayException("Something went wrong!", "merchant", merchant);
        }
    }


}