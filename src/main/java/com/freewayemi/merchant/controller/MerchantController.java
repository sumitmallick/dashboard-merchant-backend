package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.bo.*;
import com.freewayemi.merchant.commons.bo.NotificationService;
import com.freewayemi.merchant.commons.dto.*;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.AddressResponse;
import com.freewayemi.merchant.dto.MerchantAuthDto;
import com.freewayemi.merchant.dto.MerchantLeadOwnerData;
import com.freewayemi.merchant.dto.NotificationRequest;
import com.freewayemi.merchant.dto.*;
import com.freewayemi.merchant.dto.request.*;
import com.freewayemi.merchant.dto.response.BasicResponse;
import com.freewayemi.merchant.dto.response.BrandMerchantResponse;
import com.freewayemi.merchant.dto.response.MerchantUserResponse;
import com.freewayemi.merchant.dto.response.ProductInfoResponse;
import com.freewayemi.merchant.dto.sales.*;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.entity.OnBoardingDocument;
import com.freewayemi.merchant.enums.MerchantAuthSource;
import com.freewayemi.merchant.service.*;
import com.freewayemi.merchant.type.MerchantConstants;
import com.freewayemi.merchant.utils.MerchantStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class MerchantController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantController.class);
    private static List<MarketingMerchant> marketingMerchants;
    private final MerchantUserBO merchantUserBO;
    private final OfferBO offerBO;
    private final BrandBO brandBO;
    private final BrandProductBO brandProductBO;
    private final MerchantDiscountRateBO merchantDiscountRateBO;
    private final ProductOfferBO productOfferBO;
    private final MerchantService merchantService;
    private final BankInterestBO bankInterestBO;
    private final MerchantInstantDiscountConfigurationBO merchantInstantDiscountConfigurationBO;
    private SalesAgentService salesAgentService;
    private AuthCommonService authCommonService;
    private DigitalIdentityService digitalIdentityService;
    private final RuleEngineHelperService ruleEngineHelperService;
    private final NotificationService notificationService;
    private final MerchantAuthService merchantAuthService;
    private final MerchantSessionActivityBO merchantSessionActivityBO;

    private final OpsDashboardService opsDashboardService;

    @Autowired
    public MerchantController(MerchantUserBO merchantUserBO, OfferBO offerBO, BrandBO brandBO,
                              BrandProductBO brandProductBO, MerchantDiscountRateBO merchantDiscountRateBO,
                              ProductOfferBO productOfferBO, BankInterestBO bankInterestBO,
                              MerchantInstantDiscountConfigurationBO merchantInstantDiscountConfigurationBO,
                              MerchantService merchantService, SalesAgentService salesAgentService,
                              AuthCommonService authCommonService, RuleEngineHelperService ruleEngineHelperService,
                              DigitalIdentityService digitalIdentityService, NotificationService notificationService,
                              MerchantAuthService merchantAuthService, MerchantSessionActivityBO merchantSessionActivityBO, OpsDashboardService opsDashboardService) {

        this.merchantUserBO = merchantUserBO;
        this.offerBO = offerBO;
        this.brandBO = brandBO;
        this.brandProductBO = brandProductBO;
        this.merchantDiscountRateBO = merchantDiscountRateBO;
        this.productOfferBO = productOfferBO;
        this.bankInterestBO = bankInterestBO;
        this.merchantInstantDiscountConfigurationBO = merchantInstantDiscountConfigurationBO;
        this.salesAgentService = salesAgentService;
        this.merchantService = merchantService;
        this.authCommonService = authCommonService;
        this.ruleEngineHelperService = ruleEngineHelperService;
        this.digitalIdentityService = digitalIdentityService;
        this.notificationService = notificationService;
        this.merchantAuthService = merchantAuthService;
        this.merchantSessionActivityBO = merchantSessionActivityBO;
        this.digitalIdentityService = digitalIdentityService;
        this.opsDashboardService = opsDashboardService;
    }

    @GetMapping("/api/v1/sms/{sid}")
    public MerchantResponse getMerchantBySms(@PathVariable("sid") String sid) {
        LOGGER.info("Request received to get merchant by sms on sid: {}", sid);
        MerchantUser merchant = merchantUserBO.getUserBySmsCode(sid);
        if (!MerchantStatus.approved.name().equals(merchant.getStatus())) {
            throw new FreewayException("merchant not approved.");
        }
        MerchantUserResponse mu =
                new MerchantUserResponse(merchant, null, null, brandBO.hasBrand(merchant.getParams()), null, null, null, null, null);
        List<OfferResponse> offerResponses = offerBO.getPgMerchantOffers(merchant.getId().toString());
        return MerchantResponse.builder()
                .merchantId(mu.getMerchantId())
                .shopName(mu.getShopName())
                .email(mu.getEmail())
                .mobile(mu.getMobile())
                .offers(offerResponses)
                .deviceToken(merchant.getDeviceToken())
                .params(merchant.getParams())
                .returnUrl(merchant.getReturnUrl())
                .webhookUrl(merchant.getWebhookUrl())
                .mdrs(merchant.getMdrs())
                .supportedDpProviders(merchant.getSupportedDpProviders())
                .downPaymentEnabled(merchant.getDownPaymentEnabled())
                .type(merchant.getType())
                .displayId(merchant.getDisplayId())
                .qr(merchant.getQrCode())
                .build();
    }

    @GetMapping("/api/v1/merchant/{mid}")
    public MerchantResponse getMerchant(@PathVariable("mid") String merchantId,
                                        @RequestParam(value = "brandProductId", required = false) String brandProductId,
                                        @RequestParam(value = "serialNumber", required = false) String serialNumber,
                                        @RequestParam(value = "modelNumber", required = false) String modelNumber,
                                        @RequestParam(value = "productSkuCode", required = false) String productSkuCode,
                                        @RequestParam(value = "brandId", required = false) String merchantProvidedBrandId,
                                        @RequestParam(value = "providerGroup", required = false) String providerGroup) {
        LOGGER.info("Request received to get merchant for merchant id: {} brandProductId: {} serialNumber: {}" +
                        " modelNumber: {} merchantProvidedBrandId: {}", merchantId, brandProductId, serialNumber,
                modelNumber,
                merchantProvidedBrandId);
        return merchantService.getMerchantDetails(merchantId, brandProductId, serialNumber, modelNumber,
                merchantProvidedBrandId, providerGroup, productSkuCode);
    }

    @PostMapping("/api/v1/merchant/{mid}")
    public void activate(@PathVariable("mid") String merchantId, @RequestBody ActivateMerchantRequest request) {
        LOGGER.info("Request received to activate merchant for merchant id: {} with params: {}", merchantId, request);
        merchantUserBO.activate(merchantId, request);
    }

    @GetMapping("/api/v1/recommendations")
    public List<MarketingMerchant> recommendations() {
        if (null == marketingMerchants) {
            marketingMerchants = merchantUserBO.marketing();
        }
        return marketingMerchants;
    }

    @GetMapping("/api/v1/stores")
    public MerchantResponse getShops(@RequestParam(value = "category", required = false) String[] category,
                                     @RequestParam(value = "brand", required = false) String brand,
                                     @RequestParam(value = "location", required = false) String location) {
        return merchantUserBO.getShops(category, brand, location);
    }

    @GetMapping(value = "/api/v1/display/{did}")
    public Map<String, String> qr(@PathVariable("did") String displayId, @RequestHeader Map<String, String> headers)
            throws IOException {
        MerchantUser mu = merchantUserBO.getMerchantUserByQR(displayId);
        ruleEngineHelperService.saveMobileData(headers, mu.getId().toString());
        if (MerchantStatus.approved.name().equals(mu.getStatus())) {
            String logo = null != mu.getParams() ? mu.getParams().getLogo() : "na.png";
            String merchantLocation =
                    null != mu.getAddress().getCoordinates() ? mu.getAddress().getCoordinates().toString() : "";
            String shopName = mu.getShopName();
            Map<String, String> map = new HashMap<>();
            map.put("logo", "https://paymentassets.s3.ap-south-1.amazonaws.com/logos/" + logo);
            map.put("shopName", shopName);
            map.put("merchantId", mu.getId().toString());
            map.put("rating", "4.1/5");
            map.put("category", mu.getCategory());
            map.put("address", mu.getAddress().getDisplayString());
            map.put("cashbackStatus", "true");
            map.put("productStatus", brandProductBO.checkBrandProductStatus(mu));

            map.put("store_url", MerchantConstants.STORE_LINK_BASE_URL + mu.getStoreCode());
            map.put("subCategory", mu.getSubCategory());
            map.put("location", merchantLocation);
            map.put("storeClassification", mu.getParams().getStoreClassification());
            map.put("transaction_type", mu.getType());
            map.put("displayId", mu.getDisplayId());
            map.put("isOfferFilterEnabled", productOfferBO.isOfferFilterEnabled(mu));
            map.put("skipNoCostEmiText", mu.getParams().getSkipNoCostEmiText());
            map.put("otpVerification", null != mu.getParams().getOtpVerificationRequired() ? String.valueOf(
                    mu.getParams().getOtpVerificationRequired()) : null);
            map.put("partner", null != mu.getPartner() ? mu.getPartner() : null);
            map.put("isOldCheckoutDesign", Objects.nonNull(mu.getParams().getIsOldCheckoutDesign()) ? String.valueOf(
                    mu.getParams().getIsOldCheckoutDesign()) : null);
            map.put("disableOthersOption", Objects.nonNull(mu.getParams().getDisableOthersOption()) ? String.valueOf(mu.getParams().getDisableOthersOption()) : "false");
            return map;
        }
        throw new FreewayException("QR not found!");
    }

    @PostMapping("/api/v1/merchants")
    public List<MerchantLocationResponse> getMerchantByCoordinate(
            @RequestBody NearByMerchantRequestDTO nearByMerchantRequestDTO) {
        LOGGER.info("Request received to get merchant with params : {} ", nearByMerchantRequestDTO);
        return merchantUserBO.getMerchantByCoordinate(nearByMerchantRequestDTO.getLatitude(),
                nearByMerchantRequestDTO.getLongitude(), nearByMerchantRequestDTO.getShopName(),
                nearByMerchantRequestDTO.getBrands(), nearByMerchantRequestDTO.getLimit(),
                nearByMerchantRequestDTO.getOffset(), nearByMerchantRequestDTO.getMaxDistance());
    }

    @GetMapping("/internal/api/v1/getMerchantLeads")
    public List<MerchantUser> getMerchantLeads(@ModelAttribute MerchantLeadsRequest merchantLeadsRequest) {
        return merchantUserBO.getMerchantLeads(merchantLeadsRequest);
    }

    @GetMapping("/internal/api/v1/getMerchantLead")
    public MerchantInfo getMerchantLead(@RequestParam("displayId") String displayId) throws ParseException {
        MerchantUser merchantUser = merchantUserBO.getMerchantLeadByDisplayId(displayId);
        return salesAgentService.getMerchant(merchantUser.getId().toString(), Boolean.TRUE);
    }

    @PostMapping("/internal/api/v1/createLead")
    public BasicResponse createLead(@RequestParam("leadOwnerId") String leadOwnerId,
                                    @RequestBody MerchantLeadRequest merchantLeadRequest) {
        LOGGER.info("Request received to createLead api: {} {}", leadOwnerId, merchantLeadRequest);
        return merchantUserBO.createLead(leadOwnerId, merchantLeadRequest);
    }

    @GetMapping("/internal/api/v1/merchantInfo/{mid}")
    public MerchantUser getMerchantInfo(@PathVariable("mid") String merchantId) {
        return merchantUserBO.getMerchantUserByIdOrDisplayIdOrMobile(merchantId);
    }

    @GetMapping("/internal/api/v1/merchantInfoByLid/{lid}")
    public List<MerchantUser> getMerchantInfoByLid(@PathVariable("lid") String leadownerId) {
        return merchantUserBO.getMerchantUsersByLid(leadownerId, 0, 25);
    }

    @PostMapping("/internal/api/v1/merchantUsers")
    public List<MerchantUser> getMerchantInfo(@RequestBody GetMerchants getMerchants) {
        return merchantUserBO.getMerchantUserByIdsOrDisplayIdOrMobile(getMerchants);
    }

    @GetMapping("/api/v1/merchantDisplay")
    public MerchantUser getMerchantByDisplayId(@RequestParam("merchantId") String merchantId) {
        return merchantUserBO.getUserByDisplayId(merchantId);
    }

    @GetMapping("/internal/api/v1/merchant/getStoreUserCount/{mid}")
    public MerchantDetailCount getStoreUserCount(@PathVariable("mid") String merchantId) {
        return merchantUserBO.getStoreUserCount(merchantId);
    }

    @PostMapping("/internal/api/v1/updateMerchant/{mid}")
    public BasicResponse updateMerchant(@PathVariable("mid") String merchantId,
                                        @RequestBody UpdateMerchantProperties updateMerchantProperties) {
        return merchantUserBO.updateMerchant(merchantId, updateMerchantProperties);
    }

    @PostMapping("/internal/api/v1/updateMerchantProperties/{mid}")
    public BasicResponse updateMerchantProperties(@PathVariable("mid") String merchantId,
                                                  UpdateMerchantProperties updateMerchantProperties) {
        return merchantUserBO.updateMerchantProperties(merchantId, updateMerchantProperties);
    }

    @PostMapping("/internal/api/v1/saveMerchant")
    public BasicResponse saveMerchant(@RequestBody MerchantUser merchantUser) {
        LOGGER.info("save merchant: {}", merchantUser);
        return merchantUserBO.checkAndSave(merchantUser);
    }

    @GetMapping("/internal/api/v1/merchant-configs")
    public ResponseEntity<?> merchantConfig(@RequestParam("label") String label) {
        return ResponseEntity.ok(merchantUserBO.getMerchantConfigs(label));
    }

    @PutMapping("/internal/api/v1/handleMerchantException")
    public void handleMerchantException(@RequestParam("merchantId") String merchantId) {
        merchantUserBO.handleMerchantException(merchantId, null);
    }

    @GetMapping("/internal/api/v1/getCommercials")
    public ResponseEntity<?> getCommercials(@RequestParam("merchantId") String merchantId) {
        return ResponseEntity.ok(merchantUserBO.getCommercials(merchantId));
    }

    @PostMapping("/internal/api/v1/updateMerchantDetails")
    public ResponseEntity<?> updateMerchantDetails(@RequestHeader(value = "AppVersion") String appVersion,
                                                   @RequestParam("merchantId") String merchantId,
                                                   @RequestBody UpdateMerchantRequest updateMerchantRequest,
                                                   HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(
                merchantUserBO.updateMerchantDetails(merchantId, updateMerchantRequest, httpServletRequest,
                        appVersion));
    }

    @PostMapping("/internal/api/v1/updateStoreUserByMerchant")
    public ResponseEntity<?> updateStoreUserByMerchant(@RequestParam("mid") String merchantId, @RequestBody
    UpdateStoreUserByMerchantRequest updateStoreUserByMerchantRequest, HttpServletRequest request) {
        return ResponseEntity.ok(
                merchantUserBO.updateStoreUserByMerchant(merchantId, updateStoreUserByMerchantRequest, request, Boolean.FALSE));
    }

    @GetMapping("/internal/api/v1/saveNotification")
    public void saveNotification(@RequestBody NotificationRequest notificationRequest) {
        merchantUserBO.saveNotification(notificationRequest);
    }

    @GetMapping("/api/v1/agreementAndCommercials")
    public ResponseEntity<?> getCommercials(HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(merchantUserBO.getMerchantOnboardingAgreement(httpServletRequest));
    }

    @PostMapping("/api/v1/saveAgreement")
    public ResponseEntity<?> saveAgreement(@RequestParam(value = "ntbAgreement", defaultValue = "false") Boolean ntbAgreement,
                                           @RequestBody AgreementDetails agreementDetails,
                                           @RequestHeader(value = "X-Real-IP", required = false) String ip,
                                           HttpServletRequest httpServletRequest) {
        String merchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        return ResponseEntity.ok(merchantUserBO.saveAgreement(merchantId, agreementDetails, ip, ntbAgreement));
    }

    @PostMapping("/internal/api/v1/transaction/notification")
    public void sendTransactionNotificationRequest(@RequestBody ConsumerTransactionNotificationRequest consumerTransactionNotificationRequest, HttpServletRequest httpServletRequest) {
        LOGGER.info("Request received to send SMS to sales agent for transaction by : {}", consumerTransactionNotificationRequest.getConsumerMobile());
        MerchantAuthDto merchantAuthDto = MerchantAuthDto.builder().request(httpServletRequest).build();
        merchantAuthService.doAuth(merchantAuthDto);
        if (Util.isNotNull(consumerTransactionNotificationRequest) && !StringUtils.isEmpty(consumerTransactionNotificationRequest.getConsumerMobile()) && !StringUtils.isEmpty(consumerTransactionNotificationRequest.getMerchantId())) {
            MerchantLeadOwnerData merchantLeadOwnerData = merchantUserBO.getMerchantUserAndLeadOwnerUser(consumerTransactionNotificationRequest.getMerchantId());
            LOGGER.info("merchantLeadOwnerData : {}", merchantLeadOwnerData);
            if (Util.isNotNull(merchantLeadOwnerData.getMerchantUser())) {
                MerchantUser merchantUser = merchantLeadOwnerData.getMerchantUser();
                String merchantLeadOwnerId = null;
                if (Util.isNotNull(merchantUser.getParams()) && !StringUtils.isEmpty(merchantUser.getParams().getLeadOwnerId())) {
                    merchantLeadOwnerId = merchantUser.getParams().getLeadOwnerId();
                }
                if (StringUtils.isEmpty(merchantLeadOwnerId)) {
                    throw new FreewayException("Merchant doesn't have any leadOwnerId");
                }
                if (Util.isNotNull(merchantLeadOwnerData.getSalesUserProfile())) {
                    SalesUserProfile salesUserProfile = merchantLeadOwnerData.getSalesUserProfile();
                    if (!StringUtils.isEmpty(salesUserProfile.getMobile())) {
                        LOGGER.info("sending notification data for consumer: {} for merchant: {} to sales User: {}", consumerTransactionNotificationRequest.getConsumerMobile(), merchantUser.getMobile(), salesUserProfile.getMobile());
                        if (StringUtils.isEmpty(salesUserProfile.getDeviceToken())) {
                            throw new FreewayException("Device Token missing for Sales User: " + merchantLeadOwnerId);
                        }
                        notificationService.sendTransactionPushNotification(consumerTransactionNotificationRequest.getConsumerMobile(), merchantUser.getShopName(), merchantUser.getDisplayId(), salesUserProfile.getDeviceToken());
                        return;
                    }
                }
            }
            throw new FreewayException("Unauthorized");
        }
    }

    @GetMapping("/api/v1/pincode/{pinCode}")
    public AddressResponse getPostalAddress(@PathVariable("pinCode") String pinCode) {
        return digitalIdentityService.getPostalAddress(pinCode);
    }

    @GetMapping("/internal/api/v1/merchantDetails")
    public ResponseEntity<?> getMerchantDetails(@RequestParam("merchantId") String merchantId) {
        return ResponseEntity.ok(merchantUserBO.getMerchantDetails(merchantId));
    }

    @GetMapping("/internal/api/v1/dynamicFields/{mid}")
    public ResponseEntity<?> getDynamicFields(@PathVariable("mid") String merchantId) {
        return ResponseEntity.ok(merchantUserBO.getDynamicFields(merchantId));
    }

    @PostMapping("/internal/api/v1/saveMerchantSessionActivities")
    public ResponseEntity<?> saveMerchantSessionActivities(@RequestBody MerchantSessionActivityRequest merchantSessionActivityRequest) {
        return ResponseEntity.ok(merchantSessionActivityBO.saveMerchantSessionActivities(merchantSessionActivityRequest));
    }

    @PostMapping("/internal/api/v1/createMerchantProduct")
    public ResponseEntity<?> createMerchantProduct(@RequestBody MerchantProductRequest merchantProductRequest) {
        return ResponseEntity.ok(merchantSessionActivityBO.createMerchantProduct(merchantProductRequest));
    }

    @GetMapping("/internal/api/v1/userInfoForBrandDashboard")
    public ResponseEntity<?> getUserInfoForBrandDashboard(@RequestParam String merchantId) {
        return ResponseEntity.ok(merchantUserBO.getUserInfoForBrandDashboard(merchantId));
    }

    @GetMapping("/internal/api/v1/products/info")
    public ProductInfoResponse getProductInfo(@RequestParam(value = "brandId") String brandId, HttpServletRequest httpServletRequest) {
        MerchantAuthDto merchantAuthDto = MerchantAuthDto.builder().request(httpServletRequest).source(MerchantAuthSource.INTERNAL).build();
        merchantAuthService.authenticate(merchantAuthDto);
        return opsDashboardService.getProductsInfo(brandId);
    }

    @GetMapping("/internal/api/v1/merchant/info")
    public BrandMerchantResponse getMerchantUserInfo(@RequestParam(value = "displayIdOrName") String displayIdOrName, HttpServletRequest httpServletRequest) {
        MerchantAuthDto merchantAuthDto = MerchantAuthDto.builder().request(httpServletRequest).source(MerchantAuthSource.INTERNAL).build();
        merchantAuthService.authenticate(merchantAuthDto);
        return opsDashboardService.getMerchantInfo(displayIdOrName);
    }

    @GetMapping("/api/v1/onBoardingDocuments")
    public List<OnBoardingDocument> getMerchantOnboardingDocuments(@RequestParam(value = "settlement") Boolean settlement,
                                                                   HttpServletRequest httpServletRequest) {
        String merchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        return merchantUserBO.getMerchantOnboardingDocuments(merchantId, settlement);
    }

    @PostMapping("/api/v1/saveSettlementConfig")
    public ResponseEntity<?> saveSettlementConfig(HttpServletRequest httpServletRequest) {
        String merchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        return ResponseEntity.ok(merchantUserBO.saveSettlementConfig(merchantId));
    }

    @GetMapping("/internal/api/v1/settlementConfig")
    public ResponseEntity<?> getSettlementConfig(@RequestParam("merchantId") String merchantId, HttpServletRequest httpServletRequest) {
        merchantAuthService.authenticate(
                MerchantAuthDto.builder()
                        .request(httpServletRequest)
                        .source(MerchantAuthSource.INTERNAL)
                        .merchantIdOrDisplayId(merchantId)
                        .build()
        );
        return ResponseEntity.ok(merchantUserBO.getSettlementConfig(merchantId));
    }

}
