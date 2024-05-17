package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.commons.bo.AuthUserBO;
import com.freewayemi.merchant.commons.dto.TokenRequest;
import com.freewayemi.merchant.bo.MerchantUserBO;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.service.AuthCommonService;
import com.freewayemi.merchant.utils.MerchantStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static com.freewayemi.merchant.commons.utils.paymentConstants.CLICKWRAP;

@RestController
public class MerchantClickWrapController {
    private final MerchantUserBO merchantUserBO;
    private final AuthUserBO authUserBO;
    private final AuthCommonService authCommonService;

    @Autowired
    public MerchantClickWrapController(MerchantUserBO merchantUserBO, AuthUserBO authUserBO, AuthCommonService authCommonService) {
        this.merchantUserBO = merchantUserBO;
        this.authUserBO = authUserBO;
        this.authCommonService = authCommonService;
    }

    @PostMapping("/api/v1/clickwrap/request")
    public ResponseEntity<?> trigger(HttpServletRequest httpServletRequest) {
        String user = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserById(user);
        authUserBO.createAuthUser(mu.getMobile(), user, CLICKWRAP, false, false, mu.getEmail(), true);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/v1/clickwrap/response")
    public ResponseEntity<?> verify(@RequestBody TokenRequest request,
                                    @RequestHeader(value = "X-Real-IP", required = false) String ip,
                                    @RequestHeader(value = "User-Agent", required = false) String ua, HttpServletRequest httpServletRequest) {
        String user = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        authUserBO.validateClickWrap(user, CLICKWRAP, request.getOtp());
        MerchantUser mu = merchantUserBO.getUserById(user);
//        if ("123456".equals(request.getOtp())) {
        Map<String, String> map = new HashMap<>();
        map.put("IP", ip);
        map.put("UA", ua);
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("Browser", getBrowser(ua));
        map.put("Platform", getPlatform(ua));
        map.put("Mobile", mu.getMobile());
        map.put("Name", mu.getFirstName() + " " + mu.getLastName());
        map.put("Application", user);

        if (!MerchantStatus.approved.name().equals(mu.getStatus())) {
            mu.setStatus(MerchantStatus.profiled.name());
            mu.setProfiledDate(Instant.now());
            mu.setDynamicOffers(true);
            mu.setDynamicOfferTemplate("Template2");
            merchantUserBO.save(mu);
        }

        return ResponseEntity.ok(map);
//        }
//        throw new FreewayException("OTP Not Found!");
    }

    public static String getBrowser(String ua) {
        try {
            /*UserAgentParser parser =
                    new UserAgentService().loadParser(Arrays.asList(BrowsCapField.BROWSER, BrowsCapField.BROWSER_TYPE,
                            BrowsCapField.BROWSER_MAJOR_VERSION,
                            BrowsCapField.DEVICE_TYPE, BrowsCapField.PLATFORM, BrowsCapField.PLATFORM_VERSION,
                            BrowsCapField.RENDERING_ENGINE_VERSION, BrowsCapField.RENDERING_ENGINE_NAME,
                            BrowsCapField.PLATFORM_MAKER, BrowsCapField.RENDERING_ENGINE_MAKER));
            final Capabilities capabilities = parser.parse(ua);
            return capabilities.getBrowser();*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getPlatform(String ua) {
        try {
            /*UserAgentParser parser =
                    new UserAgentService().loadParser(Arrays.asList(BrowsCapField.BROWSER, BrowsCapField.BROWSER_TYPE,
                            BrowsCapField.BROWSER_MAJOR_VERSION,
                            BrowsCapField.DEVICE_TYPE, BrowsCapField.PLATFORM, BrowsCapField.PLATFORM_VERSION,
                            BrowsCapField.RENDERING_ENGINE_VERSION, BrowsCapField.RENDERING_ENGINE_NAME,
                            BrowsCapField.PLATFORM_MAKER, BrowsCapField.RENDERING_ENGINE_MAKER));
            final Capabilities capabilities = parser.parse(ua);
            return capabilities.getPlatform();*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
