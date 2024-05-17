package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.bo.*;
import com.freewayemi.merchant.commons.bo.AuthUserBO;
import com.freewayemi.merchant.commons.bo.FcmService;
import com.freewayemi.merchant.commons.bo.JwtTokenBO;
import com.freewayemi.merchant.commons.bo.NotificationService;
import com.freewayemi.merchant.commons.dto.PartnerInfo;
import com.freewayemi.merchant.commons.dto.PgTokenRequest;
import com.freewayemi.merchant.commons.dto.TokenRequest;
import com.freewayemi.merchant.commons.dto.TokenResponse;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.utils.paymentConstants;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.StoreUserInfo;
import com.freewayemi.merchant.dto.request.*;
import com.freewayemi.merchant.dto.response.MerchantUserResponse;
import com.freewayemi.merchant.dto.response.PgMerchantUserResponse;
import com.freewayemi.merchant.entity.AdminAuthUser;
import com.freewayemi.merchant.entity.Brand;
import com.freewayemi.merchant.entity.BrandGst;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.enums.PgTokenRequestType;
import com.freewayemi.merchant.repository.BrandGstRepository;
import com.freewayemi.merchant.repository.BrandRepository;
import com.freewayemi.merchant.repository.PartnerRepository;
import com.freewayemi.merchant.service.AuthCommonService;
import com.freewayemi.merchant.service.RuleEngineHelperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

import static com.freewayemi.merchant.commons.utils.paymentConstants.*;

@RestController
public class LoginController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    private final String paymentBaseUrl;
    private final AuthUserBO authUserBO;
    private final MerchantUserBO merchantUserBO;
    private final AdminAuthUserBO adminAuthUserBO;
    private final List<String> admins;
    private final NotificationService notificationService;
    private final FcmService fcmService;
    private final OfferBO offerBO;
    private final MerchantSessionBO merchantSessionBO;
    private final JwtTokenBO jwtTokenBO;
    private final BrandBO brandBO;
    private final AuthCommonService authCommonService;
    private final PartnerRepository partnerRepository;
    private final BrandGstRepository brandGstRepository;
    private final BrandRepository brandRepository;
    private final RuleEngineHelperService ruleEngineHelperService;

    @Autowired
    public LoginController(JwtTokenBO jwtTokenBO, AuthUserBO authUserBO, MerchantUserBO merchantUserBO,
                           AdminAuthUserBO adminAuthUserBO, @Value("${freewayemi.admins}") List<String> admins,
                           NotificationService notificationService,
                           FcmService fcmService, OfferBO offerBO, MerchantSessionBO merchantSessionBO,
                           @Value("${payment.base.url}") String paymentBaseUrl, BrandBO brandBO,
                           AuthCommonService authCommonService,
                           PartnerRepository partnerRepository,
                           BrandGstRepository brandGstRepository,
                           BrandRepository brandRepository, RuleEngineHelperService ruleEngineHelperService) {
        this.jwtTokenBO = jwtTokenBO;
        this.authUserBO = authUserBO;
        this.merchantUserBO = merchantUserBO;
        this.adminAuthUserBO = adminAuthUserBO;
        this.admins = admins;
        this.notificationService = notificationService;
        this.fcmService = fcmService;
        this.offerBO = offerBO;
        this.merchantSessionBO = merchantSessionBO;
        this.paymentBaseUrl = paymentBaseUrl;
        this.brandBO = brandBO;
        this.authCommonService = authCommonService;
        this.partnerRepository = partnerRepository;
        this.brandGstRepository = brandGstRepository;
        this.brandRepository = brandRepository;
        this.ruleEngineHelperService = ruleEngineHelperService;
    }

    @GetMapping("/api/v1/referral/{rid}")
    public void verifyReferral(@PathVariable("rid") String referralId) {
        merchantUserBO.verifyReferralId(referralId);
    }

    @GetMapping("/api/v1/storeCode/{sid}")
    public ResponseEntity<?> verifyStoreCode(@PathVariable("sid") String storeCode) {
        return ResponseEntity.ok(merchantUserBO.verifyExternalStoreCode(storeCode));
    }

    @PostMapping("/sendOnboardingLink")
    public ResponseEntity<?> smsOnboardingLink(
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "mobile", required = true) String mobile) {

        final String onboardingLink = paymentBaseUrl + "/mweb/#/onboarding/signup";
        notificationService.sendSmsOnboardLink(mobile, onboardingLink);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/reachOut")
    public ResponseEntity<?> reachOut(
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "pageUrl", required = false) String pageUrl, HttpServletRequest httpServletRequest) {

        String merchant = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        LOGGER.info("merchant user identified is:  {}", merchant);
        MerchantUser user = merchantUserBO.getUserById(merchant);

        String name = user.getFirstName() + " " + user.getLastName();
        String address = user.getAddress() != null ? user.getAddress().toString() : "";
        String mobile = user.getMobile();
        String email = user.getEmail();
        String businessName = user.getBusinessName();

        notificationService.reachOut(name, address, mobile, email, pageUrl, businessName);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/v1/signup")
    public ResponseEntity<?> createMerchant(@Valid @RequestBody CreateMerchantRequest request,
                                            @RequestHeader("DeviceToken") String deviceToken,
                                            @RequestHeader(value = "AppVersion", required = false) String appVersion,
                                            @RequestHeader(value = "AppType", required = false) String appType,
                                            @RequestHeader Map<String, String> headers) throws IOException {
        try {
            fcmService.validate(deviceToken);
        } catch (Exception e) {
            notificationService.sendSlackMessage("exceptions",
                    "Error 101 for merchant signup = " + request.getMobile());
        }
        MerchantUser user = merchantUserBO.createUser(request, paymentConstants.OFFLINE);
        authUserBO.createAuthUser(request.getMobile(), user.getId().toString(), AUTHUSER_MERCHANT,
                !StringUtils.isEmpty(appVersion) && "android".equals(appType), false);
        ruleEngineHelperService.saveMobileData(headers, user.getId().toString());
        MerchantUserResponse merchantUserResponse = new MerchantUserResponse(user, null, null, brandBO.hasBrand(user.getParams()), null, null, null, null, null);
        String nextOnboardingStage = merchantUserBO.getNextStage(user);
        if (StringUtils.hasText(nextOnboardingStage)) {
            merchantUserResponse.setNextOnboardingStage(nextOnboardingStage);
        }
        return ResponseEntity.ok(merchantUserResponse);
    }

    @PostMapping("/api/v1/pgsignup")
    public ResponseEntity<?> createPgMerchant(@Valid @RequestBody CreateMerchantRequest request) {
        MerchantUser user = merchantUserBO.createUser(request, paymentConstants.ONLINE);
        String apiKey = UUID.randomUUID().toString();
        authUserBO.createPgApiKey(user.getDisplayId(), apiKey, AUTHUSER_PG_MERCHANT);
        return ResponseEntity.ok(new PgMerchantUserResponse(user.getDisplayId(), apiKey));
    }

    @PostMapping("/api/v1/brandSignup")
    public ResponseEntity<?> brandSignup(@Valid @RequestBody CreateBrandRequest request) {
        LOGGER.info("Received request for brand signup :{} ", request);
        Brand brand = brandBO.getBrandByBrandDisplayId(request.getBrandDisplayId());
        String apiKey = authUserBO.checkAndCreatePgApiKey(brand.getBrandDisplayId(), AUTHUSER_BRAND_MERCHANT);
        return ResponseEntity.ok(new PgMerchantUserResponse(brand.getBrandDisplayId(), apiKey));
    }

    @PostMapping("/api/v1/ttkSignin")
    public void ttkRequest(@RequestBody TTKMerchantRequest request) {
        LOGGER.info("TTk User login as {}", request);
        MerchantUser user = merchantUserBO.getTTkMerchant(request);
        authUserBO.createAuthUser(
                user.getMobile(),
                user.getId().toString(),
                AUTHUSER_MERCHANT,
                false, false,
                user.getEmail(),
                true
        );
    }

    @PostMapping("/api/v1/ttkToken")
    public ResponseEntity<?> ttkToken(@RequestBody TTKMerchantRequest request) {
        MerchantUser user = merchantUserBO.getTTkMerchant(request);
        authUserBO.validate(user.getId().toString(), request.getOtp());
        if (StringUtils.hasText(request.getMobile())) {
            merchantUserBO.mobileVerified(user);
        } else {
            merchantUserBO.emailVerified(user);
        }
        List<String> authorities = this.admins.contains(request.getMobile()) ? Arrays.asList("MERCHANT", "ADMIN")
                : Arrays.asList("MERCHANT");
        TokenResponse resp =
                new TokenResponse(jwtTokenBO.generateToken(user.getId().toString(), "na", authorities));
        merchantSessionBO.saveSession(user.getId().toString(), user.getMobile(), resp.getToken());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/api/v1/signin")
    public void requestOtp(@RequestBody LoginMerchantRequest request,
                           @RequestHeader("DeviceToken") String deviceToken,
                           @RequestHeader(value = "AppVersion", required = false) String appVersion,
                           @RequestHeader(value = "AppType", required = false) String appType,
                           @RequestHeader(value = "resend_otp", required = false) Boolean resendOtp) {
        LOGGER.info("Received v1 signin request with params: {} appVersion: {} appType: {} and resendOtp: {}", request,
                appVersion, appType, resendOtp);
        try {
            fcmService.validate(deviceToken);
        } catch (Exception e) {
            notificationService.sendSlackMessage("exceptions",
                    "Error 101 for merchant signin = " + request.getMobile());
        }
        String userId = merchantUserBO.getUserOrStoreUser(request.getMobile());
        if (StringUtils.hasText(userId)) {
            authUserBO.createAuthUser(request.getMobile(), userId, AUTHUSER_MERCHANT,
                    !StringUtils.isEmpty(appVersion) && "android".equals(appType), null != resendOtp && resendOtp);
        }
    }

    @GetMapping("/api/v1/me")
    public ResponseEntity<?> me(HttpServletRequest httpServletRequest,
                                @RequestParam(value = "partner", required = false) String partner) {
        String merchant = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        LOGGER.info("merchant user identified is:  {}", merchant);
        MerchantUser user = merchantUserBO.getUserById(merchant);

        String userMobile = user.getMobile();
        MerchantUser partnerUser = null;
        List<String> partners = user.getPartners();
        LOGGER.info("UserInfo partners1: {}", partners);
        List<PartnerInfo> partnerInfos = new ArrayList<>();
        MerchantUser childMerchant = null;
        if (Util.isNotNull(user.getPartners())) {
            if (!user.getPartners().isEmpty()) {
                childMerchant = merchantUserBO.getUserByMobile(userMobile + "_" + user.getPartners().get(0));
                for (String partnerName : partners) {
                    PartnerInfoResponse partnerInfoResponse = merchantUserBO.getPartnerInfo(partnerName);
                    if (Objects.nonNull(partnerInfoResponse)) {
                        com.freewayemi.merchant.dto.request.PartnerInfo partnerData = partnerInfoResponse.getPartnerInfo();
                        PartnerInfo partnerInfo = new PartnerInfo(partnerData.getCode(), partnerData.getPartnerLogoUrl(), partnerData.getMerchantAppBannerUrl());
                        partnerInfos.add(partnerInfo);
                    }
                }
            }
        }

        if (Util.isNotNull(partner) && Util.isNotNull(partners)) {
            if (partners.contains(partner)) {
                partnerUser = merchantUserBO.getUserByMobile(userMobile + "_" + partner);
                user = partnerUser;
            } else {
                throw new FreewayException(userMobile + " doesn't have any partner merchant with partner: " + partner);
            }
        }

        Map<String, String> credentials = authCommonService.getMerchantId(httpServletRequest).getCredentials();
        String storeUserId = null != credentials ? credentials.get("storeUserId") : "";

        StoreUserInfo storeUserInfo = new StoreUserInfo();
        if (StringUtils.hasText(storeUserId)) {
            LOGGER.info("Store user identification is: {}", storeUserId);
            AdminAuthUser adminAuthUser = adminAuthUserBO.findById(storeUserId);
            if (null != adminAuthUser) {
                storeUserInfo.setConsent(adminAuthUser.getConsent());
                storeUserInfo.setName(adminAuthUser.getName());
                storeUserInfo.setEmail(adminAuthUser.getLogin());
                storeUserInfo.setMobile(adminAuthUser.getMobile());
                storeUserInfo.setAccountDetails(adminAuthUser.getAccountDetails());
            }
        }
        if(Objects.nonNull(childMerchant)){
            if (StringUtils.isEmpty(childMerchant.getQrCode())) {
                merchantUserBO.generateMerchantSoftQRCode(childMerchant);
            }
            user.setSoftQrCode(childMerchant.getSoftQrCode());
        }else{
            if (StringUtils.isEmpty(user.getQrCode())) {
                merchantUserBO.generateMerchantSoftQRCode(user);
            }
        }

        List<Brand> brandInfos = new ArrayList<>();
        if (Util.isNotNull(user.getGst()) && !"".equals(user.getGst())) {
            Optional<List<BrandGst>> brandGSTResponse = brandGstRepository.findByGst(user.getGst());
            List<String> brandsIds = new ArrayList<>();
            for (BrandGst brandGst : brandGSTResponse.get()) {
                if (Util.isNotNull(brandGst.getBrandId()) && !brandGst.getBrandId().equals("")) {
                    brandsIds.add(brandGst.getBrandId());
                }
            }
            if (Util.isNotNull(brandsIds)) {
                Optional<List<Brand>> infoOfBrands = brandRepository.findByBrandId(brandsIds.toArray(new String[brandsIds.size()]));
                if (infoOfBrands.isPresent()) {
                    brandInfos = infoOfBrands.get();
                }
            }
        }
        MerchantUser parentMerchant = null;
        if (Util.isNull(user.getPartners()) && Util.isNotNull(user.getParentMerchant())) {
            parentMerchant = merchantUserBO.getUserById(user.getParentMerchant());
        }
        if (Objects.nonNull(parentMerchant)) {
            if (Util.isNotNull(parentMerchant.getPartners())) {
                partners = parentMerchant.getPartners();
            }
        }
//        if (Util.isNotNull(partners)) {
//            List<String> validPartners = new ArrayList<>();
//            for (String p : partners) {
//                long count = merchantUserBO.getPartnerMerchantStatusCount(p, user.getMobile());
//                if (count > 0) {
//                    validPartners.add(p);
//                }
//            }
//            user.setPartners(validPartners);
//        }
        if (Util.isNotNull(user.getPartner())) {
            user.setAgreements(null);
        }
        LOGGER.info("UserInfo partner2: {}", user.getPartners());
        String nextStage = merchantUserBO.getNextStage(user);
        MerchantUserResponse merchantUserResponse = new MerchantUserResponse(user, parentMerchant, offerBO.getPgMerchantOffers(user.getId().toString()),
                brandBO.hasBrand(user.getParams()), storeUserInfo, null, partnerInfos, brandInfos, user.getMobile());
        merchantUserResponse.setNextOnboardingStage(nextStage);
        merchantUserResponse.setPartnerData(merchantUserBO.getPartnersView(user));
        return ResponseEntity.ok(merchantUserResponse);
    }

    @PostMapping("/api/v1/logout")
    public ResponseEntity<?> logout(HttpServletRequest httpServletRequest) {
        String merchant = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        Map<String, String> credentials = authCommonService.getMerchantId(httpServletRequest).getCredentials();
        String storeUserId = null != credentials ? credentials.get("storeUserId") : "";
        if (StringUtils.hasText(storeUserId)) {
            LOGGER.info("Store user identified is:  {}", storeUserId);
            merchantSessionBO.logoutStoreUser(storeUserId);
        } else {
            LOGGER.info("merchant user identified is:  {}", merchant);
            MerchantUser user = merchantUserBO.getUserById(merchant);
            merchantSessionBO.logoutUser(user.getId().toString());
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/v1/token")
    public ResponseEntity<?> token(@RequestBody TokenRequest request, @RequestHeader("DeviceToken") String deviceToken,
                                   @RequestParam(value = "newuser", required = false) Boolean newUser,
                                   @RequestHeader(value = "AppType", required = false) String appType,
                                   @RequestHeader(value = "AppVersion", required = false) String appVersion,
                                   HttpServletRequest httpServletRequest, @RequestHeader Map<String, String> headers) throws IOException {
        LOGGER.info("Device token: {}, AppType: {}, AppVersion: {}, Mobile: {}", deviceToken, appType, appVersion,
                request.getMobile());
        return ResponseEntity.ok(merchantUserBO.allocateToken(request, newUser, appType, appVersion, deviceToken, headers));
    }

    @PostMapping("/api/v2/token")
    public ResponseEntity<?> token(@RequestBody PgTokenRequest request) {
        authUserBO.validatePg(request.getClientId(), request.getClientSecret());
        if (StringUtils.hasText(request.getType()) &&
                PgTokenRequestType.BRAND.name().equalsIgnoreCase(request.getType())) {
            Brand brand = brandBO.getBrandByBrandDisplayId(request.getClientId());
            List<String> authorityList = Collections.singletonList("BRAND");
            String token = jwtTokenBO.generateToken(brand.getBrandDisplayId(), "", authorityList);
            merchantSessionBO.saveSession(brand.getBrandDisplayId(), token);
            return ResponseEntity.ok(new TokenResponse(token));
        } else {
            MerchantUser merchantUser = merchantUserBO.getUserByDisplayId(request.getClientId());
            List<String> authorityList =
                    this.admins.contains(merchantUser.getMobile()) ? Arrays.asList("MERCHANT", "ADMIN")
                            : Arrays.asList("MERCHANT");
            String token = jwtTokenBO.generateToken(merchantUser.getId().toString(), "", authorityList);
            merchantSessionBO.saveSession(String.valueOf(merchantUser.getId()), merchantUser.getMobile(), token);
            return ResponseEntity.ok(new TokenResponse(token));
        }
    }

    @PostMapping("/api/v1/requestMobileChange")
    public void requestMobileChange(@RequestBody LoginMerchantRequest request, HttpServletRequest httpServletRequest) {
        String merchant = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser user = merchantUserBO.getUserById(merchant);
        authUserBO.createAuthUser(
                request.getMobile(),
                user.getId().toString(),
                MOBILE_CHANGE,
                false, false,
                null,
                false
        );
    }

    @PostMapping("/api/v1/verifyMobileChange")
    public ResponseEntity<?> verifyMobileChange(@RequestBody TokenRequest request,
                                                HttpServletRequest httpServletRequest) {
        String merchant = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser user = merchantUserBO.getUserById(merchant);
        authUserBO.validate(user.getId().toString(), request.getOtp());
        if (StringUtils.hasText(request.getMobile())) {
            user.setAlteredMobileNumber(true);
            user.setMobile(request.getMobile());
            merchantUserBO.mobileVerified(user);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/v1/sendOtp")
    public ResponseEntity<?> sendOtp(@RequestBody LoginMerchantRequest request,
                                     @RequestHeader("DeviceToken") String deviceToken,
                                     @RequestHeader(value = "AppVersion", required = false) String appVersion,
                                     @RequestHeader(value = "AppType", required = false) String appType,
                                     @RequestHeader(value = "resend_otp", required = false) Boolean resendOtp,
                                     HttpServletRequest httpServletRequest) {

        return ResponseEntity.ok(authUserBO.sendOTP(request, resendOtp, appVersion, appType, httpServletRequest));
    }
}
