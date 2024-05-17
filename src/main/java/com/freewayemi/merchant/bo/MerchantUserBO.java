package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.commons.bo.*;
import com.freewayemi.merchant.commons.dto.*;
import com.freewayemi.merchant.commons.dto.karza.GstAuthReq;
import com.freewayemi.merchant.commons.dto.offer.DynamicOffer;
import com.freewayemi.merchant.commons.dto.offer.DynamicOfferResponse;
import com.freewayemi.merchant.commons.dto.qr.QRRequest;
import com.freewayemi.merchant.commons.dto.qr.QRResponse;
import com.freewayemi.merchant.commons.entity.*;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.exception.MerchantException;
import com.freewayemi.merchant.commons.exception.UserNotFoundException;
import com.freewayemi.merchant.commons.type.*;
import com.freewayemi.merchant.commons.utils.DateUtil;
import com.freewayemi.merchant.commons.utils.paymentConstants;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.commons.utils.ValidationUtil;
import com.freewayemi.merchant.dao.MerchantDAO;
import com.freewayemi.merchant.dto.*;
import com.freewayemi.merchant.dto.BankAccount.BankAccountAuthReq;
import com.freewayemi.merchant.dto.NotificationRequest;
import com.freewayemi.merchant.dto.BankAccount.BankAccountAuthResp;
import com.freewayemi.merchant.dto.request.ProfileRequest;
import com.freewayemi.merchant.dto.request.*;
import com.freewayemi.merchant.dto.response.*;
import com.freewayemi.merchant.dto.sales.PaymentConfigInfo;
import com.freewayemi.merchant.dto.sales.TransactionDataResponse;
import com.freewayemi.merchant.dto.sales.*;
import com.freewayemi.merchant.entity.AgreementDetails;
import com.freewayemi.merchant.entity.*;
import com.freewayemi.merchant.enums.*;
import com.freewayemi.merchant.pojos.APIResponse;
import com.freewayemi.merchant.pojos.GenerateAgreementResponse;
import com.freewayemi.merchant.pojos.pan.PanDetailsRequest;
import com.freewayemi.merchant.pojos.pan.PanDetailsResponse;
import com.freewayemi.merchant.repository.*;
import com.freewayemi.merchant.service.*;
import com.freewayemi.merchant.type.MerchantConstants;
import com.freewayemi.merchant.type.Source;
import com.freewayemi.merchant.utils.MerchantStatus;
import com.freewayemi.merchant.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.freewayemi.merchant.commons.utils.paymentConstants.*;
import static com.freewayemi.merchant.enums.Status.ONBOARDING;
import static com.freewayemi.merchant.enums.Status.PENDING;
import static com.freewayemi.merchant.enums.Status.SUCCESS;
import static com.freewayemi.merchant.utils.Constants.*;


@Component
public class MerchantUserBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantUserBO.class);

    private static final long referalBonus = 200;

    private final MerchantUserRepository merchantUserRepository;

    private final MerchantPennydropDetailsRepository merchantPennydropDetailsRepository;

    private final OfferRepository offerRepository;

    private final ExceptionBrandsRepository exceptionBrandsRepository;

    private final OfferBO offerBO;
    private final NotificationService notificationService;
    private final S3UploadService s3UploadService;
    private final QRService qrService;
    private final MerchantOfferConfigBO merchantOfferConfigBO;
    private final DynamicPromotionalImageBO dynamicPromotionalImageBO;
    private final KarzaService karzaService;
    private final MerchantGstAuthInfoRepository merchantGstAuthInfoRepository;
    private final MerchantEarningsBO merchantEarningsBO;
    private final Boolean enableKarzaAPI;
    private final String paymentBaseUrl;
    private final BrandBO brandBO;
    private final AdminAuthUserRepository adminAuthUserRepository;
    private final AuthUserBO authUserBO;
    private final MerchantSessionBO merchantSessionBO;
    private final JwtTokenBO jwtTokenBO;
    private final AadhaarMaskService aadhaarMaskService;
    private final NtbCoreService ntbCoreService;
    private final GeoCoding geoCoding;
    private final MerchantConfigBO merchantConfigBO;
    private final MerchantProperitiesBO merchantProperitiesBO;
    private final AuthCommonService authCommonService;
    private final NotificationBO notificationBO;
    private final Boolean isProduction;
    private final ReportService reportService;

    private final HelperService helperService;
    private final PaymentOpsService paymentOpsService;

    private final DigitalIdentityService digitalIdentityService;
    private final MerchantTracesRepository merchantTracesRepository;
    private final GenerateAgreementService generateAgreementService;
    private final AgreementRepository agreementRepository;
    private final MerchantConfigsRepository merchantConfigsRepository;
    private final RuleEngineHelperService ruleEngineHelperService;

    private final SalesAgentBO salesAgentBO;
    private final CacheBO cacheBO;
    private final MerchantDAO merchantDAO;
    private final ReferralCodeBO referralCodeBO;
    private final OnBoardingDocumentRepository onBoardingDocumentRepository;

    private final EmailService emailService;

    @Autowired
    public MerchantUserBO(AdminAuthUserRepository adminAuthUserRepository,
                          MerchantUserRepository merchantUserRepository, OfferBO offerBO,
                          NotificationService notificationService, S3UploadService s3UploadService, QRService qrService,
                          MerchantOfferConfigBO merchantOfferConfigBO,
                          DynamicPromotionalImageBO dynamicPromotionalImageBO, KarzaService karzaService,
                          MerchantGstAuthInfoRepository merchantGstAuthInfoRepository,
                          MerchantEarningsBO merchantEarningsBO, @Value("${ENABLE_KARZA}") Boolean enableKarzaAPI,
                          @Value("${payment.base.url}") String paymentBaseUrl, BrandBO brandBO, AuthUserBO authUserBO,
                          MerchantSessionBO merchantSessionBO, JwtTokenBO jwtTokenBO,
                          AadhaarMaskService aadhaarMaskService, NtbCoreService ntbCoreService, GeoCoding geoCoding,
                          DigitalIdentityService digitalIdentityService,
                          MerchantTracesRepository merchantTracesRepository, OfferRepository offerRepository, MerchantConfigBO merchantConfigBO,
                          MerchantProperitiesBO merchantProperitiesBO,
                          AuthCommonService authCommonService, ReportService reportService, HelperService helperService,
                          NotificationBO notificationBO, @Value("${payment.deployment.env}") String env,
                          PaymentOpsService paymentOpsService, MerchantPennydropDetailsRepository merchantPennydropDetailsRepository,
                          GenerateAgreementService generateAgreementService, AgreementRepository agreementRepository,
                          MerchantConfigsRepository merchantConfigsRepository, ExceptionBrandsRepository exceptionBrandsRepository,
                          RuleEngineHelperService ruleEngineHelperService, SalesAgentBO salesAgentBO,
                          EmailService emailService, CacheBO cacheBO, MerchantDAO merchantDAO, ReferralCodeBO referralCodeBO,
                          OnBoardingDocumentRepository onBoardingDocumentRepository) {
        this.adminAuthUserRepository = adminAuthUserRepository;
        this.merchantUserRepository = merchantUserRepository;
        this.offerRepository = offerRepository;
        this.offerBO = offerBO;
        this.notificationService = notificationService;
        this.s3UploadService = s3UploadService;
        this.qrService = qrService;
        this.merchantOfferConfigBO = merchantOfferConfigBO;
        this.dynamicPromotionalImageBO = dynamicPromotionalImageBO;
        this.karzaService = karzaService;
        this.merchantGstAuthInfoRepository = merchantGstAuthInfoRepository;
        this.merchantEarningsBO = merchantEarningsBO;
        this.enableKarzaAPI = enableKarzaAPI;
        this.paymentBaseUrl = paymentBaseUrl;
        this.brandBO = brandBO;
        this.authUserBO = authUserBO;
        this.merchantSessionBO = merchantSessionBO;
        this.jwtTokenBO = jwtTokenBO;
        this.aadhaarMaskService = aadhaarMaskService;
        this.ntbCoreService = ntbCoreService;
        this.geoCoding = geoCoding;
        this.digitalIdentityService = digitalIdentityService;
        this.merchantTracesRepository = merchantTracesRepository;
        this.merchantConfigBO = merchantConfigBO;
        this.merchantProperitiesBO = merchantProperitiesBO;
        this.authCommonService = authCommonService;
        this.notificationBO = notificationBO;
        this.isProduction = paymentConstants.PRODENV.equals(env);
        this.reportService = reportService;
        this.helperService = helperService;
        this.paymentOpsService = paymentOpsService;
        this.merchantPennydropDetailsRepository = merchantPennydropDetailsRepository;
        this.exceptionBrandsRepository = exceptionBrandsRepository;
        this.generateAgreementService = generateAgreementService;
        this.agreementRepository = agreementRepository;
        this.merchantConfigsRepository = merchantConfigsRepository;
        this.ruleEngineHelperService = ruleEngineHelperService;
        this.salesAgentBO = salesAgentBO;
        this.emailService = emailService;
        this.cacheBO = cacheBO;
        this.merchantDAO = merchantDAO;
        this.referralCodeBO = referralCodeBO;
        this.onBoardingDocumentRepository = onBoardingDocumentRepository;
    }

    private static Float getRate(String cardType, Integer tenure, List<DynamicOffer> margins) {
        for (DynamicOffer d : margins) {
            if (tenure.equals(d.getTenure()) && cardType.equals(d.getCardType())) {
                return d.getRate();
            }
        }
        return 2.5f;
    }

    public void createAccountPennyDrop(MerchantPennydropDetails merchantPennydropDetails) {
        try {
            merchantPennydropDetailsRepository.save(merchantPennydropDetails);
        } catch (Exception e) {
            LOGGER.info("Unable to create Penny Drop Result");
        }
    }

    private static Float getRate(String cardType, Integer tenure, DynamicOfferResponse request, Float baseRate) {
        float maxRate = 0.0f;
        for (DynamicOffer offer : request.getDynamicOffers()) {
            if ("flat".equals(request.getType()) && offer.getSelected() && offer.getRate() > maxRate) {
                maxRate = offer.getRate();
            } else if ("complex".equals(request.getType()) && offer.getSelected() &&
                    cardType.equals(offer.getCardType()) && offer.getRate() > maxRate) {
                maxRate = offer.getRate();
            }
        }
        if (maxRate == 0.0f) {
            return baseRate;
        }
        for (DynamicOffer offer : request.getDynamicOffers()) {
            if (cardType.equals(offer.getCardType()) && tenure.equals(offer.getTenure()) && offer.getSelected()) {
                return offer.getRate();
            } else if (cardType.equals(offer.getCardType()) && tenure.equals(offer.getTenure())) {
                return request.getLowCostEmi() ? maxRate : baseRate;
            }
        }
        for (DynamicOffer offer : request.getDynamicOffers()) {
            if (null == offer.getCardType() && tenure.equals(offer.getTenure()) && offer.getSelected()) {
                return offer.getRate();
            } else if (null == offer.getCardType() && tenure.equals(offer.getTenure())) {
                return request.getLowCostEmi() ? maxRate : baseRate;
            }
        }
        throw new FreewayException("something went wrong!");
    }

    public MerchantUser createUser(CreateMerchantRequest request, String type) {
        Optional<MerchantUser> optional = this.merchantUserRepository.findByMobileAndIsDeleted(request.getMobile(), false);
        if (optional.isPresent()) {
            throw new UserNotFoundException("merchant already registered with " + request.getMobile() + ". please sign in");
        }
        Optional<AdminAuthUser> optionalAuthUser = this.adminAuthUserRepository.findByMobile(request.getMobile());
        if (optionalAuthUser.isPresent()) {
            throw new UserNotFoundException("merchant already registered with " + request.getMobile() + ". please sign in");
        }
        return create(request, type);

    }

    private MerchantUser create(CreateMerchantRequest request, String type) {
        com.freewayemi.merchant.dto.request.PartnerInfo partnerInfo = referralCodeBO.findPartnerByReferralCode(request.getReferralCode());
        MerchantUser user = new MerchantUser();
        user.setSource("MobileApp");
        user.setMobile(request.getMobile().trim());
        user.setEmail(request.getEmail().trim());
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmailVerified(false);
        user.setMobileVerified(false);
        user.setStatus(MerchantStatus.registered.name());
        user.setStage(!CollectionUtils.isEmpty(partnerInfo.getStages()) ? partnerInfo.getStages().get(0) : ONBOARDING_STAGE_0);
        user.setDeleted(false);
        user.setDisplayId(this.createDisplayId());
        user.setReferralCode(user.getDisplayId());
        if (!StringUtils.isEmpty(request.getReferralCode())) {
            user.setReferralCode(request.getReferralCode());
        }
        user.setReferredBy(request.getReferredBy());
        user.setType(type);
        user.setMerchantRefundConfig(MerchantRefundConfig.builder().doRefundFromSaleOnly(true).build());
        Params params = new Params();
        MerchantConfigs merchantConfigs = merchantConfigsRepository.findByLabel(Constants.exclusionCreditBanks).orElse(null);
        if (Objects.nonNull(merchantConfigs)) {
            params.setExclusionCreditBanks(String.join(",", merchantConfigs.getValues()));
        } else {
            params.setExclusionCreditBanks("SBIN,HSBC,AUFB,ONECARD");
        }
        params.setShowHdfcCardless("true");
        params.setShowKotakCardless("false");
        params.setEnableDownpaymentByDebitCard(false);

        params.setShowKotakCardless("false");
        Agreements agreements = new Agreements();
        agreements.setServiceAgreement(PENDING.getStatus());
        agreements.setCommercialsAgreement(PENDING.getStatus());
        agreements.setNtbAgreement(PENDING.getStatus());
        user.setAgreements(agreements);
        user.setParams(params);
        user.setRiskCategory(1);
        user.setSettlementConfig(SettlementConfig.builder()
                .settlementCycle(SettlementCycleEnum.STANDARD)
                .excludeMdrAndGstCharges(false)
                .settlementType(SettlementTypeEnum.SELF.name())
                .build());
        user = merchantUserRepository.save(user);
        // parent and child merchant logic
        if (referralCodeBO.partnerExistsByReferralCode(request.getReferralCode())) {
            user.setPartners((Collections.singletonList(partnerInfo.getCode())));
            MerchantUser childMerchantUser = createChildMerchantUser(request, user, type, partnerInfo.getCode());
            updateParentMerchantUser(user, childMerchantUser);
            merchantUserRepository.save(user);
        }
        notificationService.sendRegistrationNotification(user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getMobile());
        if (!StringUtils.isEmpty(user.getReferredBy()) && !SALES_REFERRAL_CODE.contains(user.getReferredBy())) {
            Optional<MerchantUser> optional = merchantUserRepository.findByReferralCode(user.getReferredBy());
            if (optional.isPresent()) {
                MerchantUser referee = optional.get();
                merchantEarningsBO.createMerchantEarnings(referee.getId().toString(), user.getId().toString(),
                        EarningType.referredBy.name(), MerchantEarningsBO.REFERREDBY);
                merchantEarningsBO.createMerchantEarnings(user.getId().toString(), referee.getId().toString(),
                        EarningType.referred.name(), MerchantEarningsBO.REFERRED);
            }
        }
        return user;
    }


    public MerchantUser createChildMerchantUser(CreateMerchantRequest request, MerchantUser parentMerchantUser,
                                                String type, String partner) {
        String mobile = request.getMobile() + "_" + partner;
        Optional<MerchantUser> optional = this.merchantUserRepository.findByMobileAndIsDeleted(mobile, false);
        if (optional.isPresent()) {
            throw new UserNotFoundException(
                    "merchant already registered with " + request.getMobile() + ". please sign in");
        }
        return createChildMerchant(request, type, parentMerchantUser, partner);
    }

    private MerchantUser createChildMerchant(CreateMerchantRequest request, String type,
                                             MerchantUser parentMerchantUser, String partner) {
        LOGGER.info("received request to create childMerchant: {}", request);
        com.freewayemi.merchant.dto.request.PartnerInfo partnerInfo = referralCodeBO.getPartnerInfo(partner);
        MerchantUser user = new MerchantUser();
        user.setSource("MobileApp");
        user.setMobile(request.getMobile() + "_" + partner);
        user.setStatus(MerchantStatus.leadcreated.name());
        user.setStage(!CollectionUtils.isEmpty(partnerInfo.getStages()) ? partnerInfo.getStages().get(0) : ONBOARDING_STAGE_0);
        user.setDeleted(false);
        user.setDisplayId(this.createDisplayId());
        user.setReferralCode(user.getDisplayId());
        user.setPartner(partner);
        if (Util.isNotNull(parentMerchantUser)) {
            user.setParentMerchant(parentMerchantUser.getId().toString());
        }
        user.setType(type);
        user.setMerchantRefundConfig(MerchantRefundConfig.builder().doRefundFromSaleOnly(true).build());
        Params params = new Params();
        MerchantConfigs merchantConfigs = merchantConfigsRepository.findByLabel(Constants.exclusionCreditBanks).orElse(null);
        if (Objects.nonNull(merchantConfigs)) {
            params.setExclusionCreditBanks(String.join(",", merchantConfigs.getValues()));
        } else {
            params.setExclusionCreditBanks("SBIN,HSBC,AUFB,ONECARD");
        }
        params.setShowHdfcCardless("true");
        params.setShowKotakCardless("false");
        params.setEnableDownpaymentByDebitCard(false);
        params.setShowKotakCardless("false");
        Agreements agreements = new Agreements();
        agreements.setServiceAgreement(PENDING.getStatus());
        agreements.setCommercialsAgreement(PENDING.getStatus());
        agreements.setNtbAgreement(PENDING.getStatus());
        user.setAgreements(agreements);
        user.setParams(params);
        user.setSettlementConfig(SettlementConfig.builder()
                .settlementCycle(SettlementCycleEnum.STANDARD)
                .excludeMdrAndGstCharges(false)
                .build());
        merchantUserRepository.save(user);
        notificationService.sendRegistrationNotification(user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getMobile());
        if (!StringUtils.isEmpty(user.getReferredBy()) && !SALES_REFERRAL_CODE.contains(user.getReferredBy())) {
            Optional<MerchantUser> optional = merchantUserRepository.findByReferralCode(user.getReferredBy());
            if (optional.isPresent()) {
                MerchantUser referee = optional.get();
                merchantEarningsBO.createMerchantEarnings(referee.getId().toString(), user.getId().toString(),
                        EarningType.referredBy.name(), MerchantEarningsBO.REFERREDBY);
                merchantEarningsBO.createMerchantEarnings(user.getId().toString(), referee.getId().toString(),
                        EarningType.referred.name(), MerchantEarningsBO.REFERRED);
            }
        }
        return user;
    }

    public String createDisplayId() {
        String displayId = Util.generateRandomDisplayId();
        return this.merchantUserRepository.findByDisplayId(displayId).map(mu -> createDisplayId()).orElse(displayId);
    }

    public String getUserOrStoreUser(String mobile) {
        MerchantUser mu = this.merchantUserRepository.findByMobileAndIsDeleted(mobile, false).orElse(null);
        if (null == mu) {
            AdminAuthUser user = adminAuthUserRepository.findByMobile(mobile).orElse(null);
            if (null == user) {
                throw new UserNotFoundException("merchant not registered with " + mobile + ". please sign up");
            } else if (user.getStatus().equals(StoreUserStatus.INACTIVE.toString().toLowerCase())) {
                throw new FreewayException("Inactive User");
            } else {
                return user.getId().toString();
            }
        } else {
            return mu.getId().toString();
        }
    }

    public MerchantUser getUser(String mobile) {
        return this.merchantUserRepository.findByMobileAndIsDeleted(mobile, false).orElseThrow(() -> new UserNotFoundException("merchant not registered with " + mobile + ". please sign up"));
    }

    public MerchantUser getUserByMobile(String mobile) {
        return this.merchantUserRepository.findByMobileAndIsDeleted(mobile, false).orElse(null);
    }

    public MerchantUser getUserByIdOrDisplayId(String id) {
        return this.merchantUserRepository.findById(id).orElseGet(() -> this.getUserByDisplayId(id));
    }

    public MerchantUser getUserBySmsCode(String sid) {
        return this.merchantUserRepository.findBySmsCode(sid).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public MerchantUser getUserById(String merchantId) {
        LOGGER.info("merchantId : {}", merchantId);
        return this.merchantUserRepository.findById(merchantId).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public MerchantUser getUserByDisplayId(String displayId) {
        return this.merchantUserRepository.findByDisplayId(displayId).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public MerchantUser getMerchantUserByIdOrDisplayIdOrMobile(String id) {
        return this.merchantUserRepository.findById(id).orElseGet(() -> this.getMerchantUserByDisplayIdOrMobile(id));
    }

    public List<MerchantUser> getMerchantUserByIdsOrDisplayIdOrMobile(GetMerchants getMerchants) {
        List<String> ids = Objects.nonNull(getMerchants) ? getMerchants.getIds() : new ArrayList<>();
        if (!CollectionUtils.isEmpty(ids)) {
            String[] merchantIds = new String[ids.size()];
            int i = 0;
            for (String id : ids) {
                merchantIds[i++] = id;
            }
            return this.merchantUserRepository.findByIdsOrDisplayIdOrMobile(merchantIds).orElseGet(ArrayList::new);
        }
        return new ArrayList<>();
    }

    public MerchantUser getMerchantUserByDisplayIdOrMobile(String id) {
        return this.merchantUserRepository.findByDisplayId(id).orElseGet(() -> this.getMerchantUserByMobile(id));
    }

    public MerchantUser getUserByMerchantIdOrDisplayId(String displayIdOrMerchantId) {
        Optional<MerchantUser> optional = this.merchantUserRepository.findByDisplayId(displayIdOrMerchantId);
        return optional.orElseGet(() -> this.merchantUserRepository.findById(displayIdOrMerchantId)
                .orElseThrow(() -> new UserNotFoundException("User not found")));
    }

    public void mobileVerified(MerchantUser user) {
        user.setMobileVerified(true);
        this.merchantUserRepository.save(user);
    }

    public void emailVerified(MerchantUser user) {
        user.setEmailVerified(true);
        this.merchantUserRepository.save(user);
    }

    public VerifyDetailsResponse verifyDetails(VerifyMerchantDetails request, MerchantUser merchantUser) {
        if (StringUtils.hasText(request.getType())) {
            switch (request.getType()) {
                case "pan":
                    PanDetailsResponse panResp = digitalIdentityService.getPANDetails(PanDetailsRequest.builder()
                            .panNumber(request.getPan())
                            .source(Source.MERCHANTMS)
                            .build());
                    return VerifyDetailsResponse.builder()
                            .status("success".equals(panResp.getStatus()) ? "SUCCESS" : "FAILED")
                            .businessName("success".equals(panResp.getStatus()) ? panResp.getFullName() : "unverified")
                            .build();
                case "gst":
                    com.freewayemi.merchant.dto.gst.GstAuthResp gstAuthResp = digitalIdentityService.verifyGst(GstAuthReq.builder().gstin(request.getGst()).provider(MerchantConstants.KARZA).source(Source.MERCHANTMS).build());
                    if (Objects.nonNull(gstAuthResp)) {
                        MerchantGSTDetails merchantGSTDetails = new MerchantGSTDetails();
                        merchantGSTDetails.setMerchantId(merchantUser.getId().toString());
                        merchantGSTDetails.setGst(request.getGst());
                        merchantGSTDetails.setGstAuthResp(gstAuthResp);
                        merchantGSTDetails.setCreatedDate(Instant.now());
                        merchantGSTDetails.setLastModifiedDate(Instant.now());
                        merchantGstAuthInfoRepository.save(merchantGSTDetails);
                    }
                    if (Objects.nonNull(gstAuthResp.getAddressesOfPrincipalPlaceOfBusiness())) {
                        String address =
                                Objects.equals(gstAuthResp.getAddressesOfPrincipalPlaceOfBusiness().getCompleteAddress(), "NA")
                                        ? "" : gstAuthResp.getAddressesOfPrincipalPlaceOfBusiness().getCompleteAddress();
                        if (!StringUtils.hasText(address)) {
                            address =
                                    Objects.equals(gstAuthResp.getAddressesOfPrincipalPlaceOfBusiness().getNatureOfBusinessAtAddress(), "NA")
                                            ? "" : gstAuthResp.getAddressesOfPrincipalPlaceOfBusiness().getNatureOfBusinessAtAddress();
                        }
                        String[] addressList = address.split(",");
                        String pincode = addressList.length > 0 ? addressList[addressList.length - 1].trim() : "";
                        String line1 = addressList.length > 4 ? String.join(",",
                                Arrays.copyOfRange(addressList, 0, addressList.length - 4)) : address;
                        String line2 = addressList.length > 3 ? addressList[addressList.length - 4].trim() : "";
                        String city = addressList.length > 2 ? addressList[addressList.length - 3].trim() : "";
                        String state = addressList.length > 1 ? addressList[addressList.length - 2].trim() : "";
                        return VerifyDetailsResponse.builder()
                                .status("success".equals(gstAuthResp.getStatus()) ? "SUCCESS" : "FAILED")
                                .address(new Address(pincode, city, line1, line2, state, "", null, "", true, null))
                                .build();
                    }
                    return VerifyDetailsResponse.builder()
                            .status("success".equals(gstAuthResp.getStatus()) ? "SUCCESS" : "FAILED")
                            .build();
                case "account":
                    BankAccountAuthResp bankAccountAuthResp = digitalIdentityService.verifyAccount(BankAccountAuthReq.builder()
                            .accountNumber(request.getAccountNumber()).ifsc(request.getIfscCode())
                            .provider(MerchantConstants.KARZA).source(Source.MERCHANTMS).build());
                    if (Objects.nonNull(bankAccountAuthResp) && "success".equals(bankAccountAuthResp.getStatus()) && Boolean.TRUE.equals(bankAccountAuthResp.getBankTxnStatus())) {
                        Account account = new Account(bankAccountAuthResp.getIfsc(), bankAccountAuthResp.getAccountNumber(), bankAccountAuthResp.getAccountName());
                        LOGGER.info("Adding account info for merchantId: {} ", merchantUser.getId().toString());
                        merchantUser.setAccount(account);
                        merchantUserRepository.save(merchantUser);
                        MerchantPennydropDetails merchantPennydropDetails = MerchantPennydropDetails.builder()
                                .merchantId(merchantUser.getId().toString())
                                .bankAccountAuthResp(bankAccountAuthResp)
                                .acc(request.getAccountNumber())
                                .ifsc(request.getIfscCode()).build();
                        merchantPennydropDetails.setCreatedDate(Instant.now());
                        createAccountPennyDrop(merchantPennydropDetails);
                    }

                    return Objects.nonNull(bankAccountAuthResp) && "success".equals(bankAccountAuthResp.getStatus()) ?
                            VerifyDetailsResponse.builder()
                                    .status("SUCCESS")
                                    .accountName(bankAccountAuthResp.getAccountName())
                                    .build() :
                            VerifyDetailsResponse.builder()
                                    .status("FAILED")
                                    .accountName("unverified")
                                    .build();
//                    return VerifyDetailsResponse.builder()
//                            .status("success".equals(bankAccountAuthResp.getStatus()) ? "SUCCESS" : "FAILED")
//                            .accountName("success".equals(bankAccountAuthResp.getStatus()) ? bankAccountAuthResp.getAccountName() : "unverified")
//                            .build();
                default:
                    break;
            }
        }
        return VerifyDetailsResponse.builder().status("FAILED").build();
    }

    public APIResponse update(MerchantUser mu, ProfileRequest request) {
        String nextOnboardingStage = "";
        MerchantUser childMerchant = null;
        String partnerValue = payment_PARTNER;
        if (MerchantStatus.registered.name().equals(mu.getStatus()) && !CollectionUtils.isEmpty(mu.getPartners())) {
            childMerchant = merchantUserRepository.findByMobileAndIsDeleted(mu.getMobile() + "_" + mu.getPartners().get(0), false).orElseThrow(() -> new FreewayException("Merchant user is not found"));
            partnerValue = mu.getPartners().get(0);
        }
        com.freewayemi.merchant.dto.request.PartnerInfo partner = referralCodeBO.getPartnerInfo(partnerValue);
        String lastStage = Util.getLastOnboardingStage(partner);

        if ((Objects.nonNull(childMerchant) && MerchantStatus.registered.name().equals(childMerchant.getStatus())) || MerchantStatus.registered.name().equals(mu.getStatus())) {
            if (StringUtils.hasText(request.getBusinessName())) {
                mu.setBusinessName(request.getBusinessName());
            }
            if (StringUtils.hasText(request.getShopName())) {
                mu.setShopName(request.getShopName());
                mu.setStoreCode(String.format("%s-%s", mu.getDisplayId(),
                        mu.getShopName().replace(" ", "-").toLowerCase()));
            }
            if (StringUtils.hasText(request.getFirstName())) {
                mu.setFirstName(request.getFirstName());
            }
            if (StringUtils.hasText(request.getLastName())) {
                mu.setLastName(request.getLastName());
            }
            if (StringUtils.hasText(request.getEmail())) {
                mu.setEmail(request.getEmail());
            }
            if (StringUtils.hasText(request.getReferralCode())) {
                mu.setReferralCode(request.getReferralCode());
            }
            if (StringUtils.hasText(request.getCategory())) {
                mu.setCategory(request.getCategory());
            }
            if (StringUtils.hasText(request.getDisplayName())) {
                mu.setShopName(request.getDisplayName());
            }
            if (StringUtils.hasText(request.getUrl())) {
                mu.setWebsiteUrl(request.getUrl());
            }
            if (StringUtils.hasText(request.getSubCategory())) {
                mu.setSubCategory(request.getSubCategory());
            }
            if (StringUtils.hasText(request.getMccCode())) {
                mu.setMccCode(request.getMccCode());
            }
            if (StringUtils.hasText(request.getGst())) {
                mu.setGst(request.getGst());
            }
            if (StringUtils.hasText(request.getOwnership())) {
                mu.setOwnership(request.getOwnership());
            }
            if (StringUtils.hasText(request.getPan())) {
                mu.setPan(request.getPan());
            }
            if (null != request.getAddress()) {
                Address merchantAddress = Util.isNotNull(mu.getAddress()) ? mu.getAddress() : new Address();
                if (!StringUtils.isEmpty(request.getAddress().getLine1())) {
                    merchantAddress.setLine1(request.getAddress().getLine1());
                }
                if (!StringUtils.isEmpty(request.getAddress().getLine2())) {
                    merchantAddress.setLine2(request.getAddress().getLine2());
                }
                if (!StringUtils.isEmpty(request.getAddress().getPincode())) {
                    merchantAddress.setPincode(request.getAddress().getPincode());
                }
                if (Util.isNotNull(request.getAddress().getCity())) {
                    merchantAddress.setCity(request.getAddress().getCity());
                }
                if (Util.isNotNull(request.getAddress().getState())) {
                    merchantAddress.setState(request.getAddress().getState());
                }
                if (null != request.getAddress().getCoordinates() &&
                        request.getAddress().getCoordinates().size() == 2) {
                    List<Double> coordinates = request.getAddress().getCoordinates();
                    merchantAddress.setCoordinates(coordinates);
                    List<Double> reverseCoordinates = Arrays.asList(coordinates.get(1), coordinates.get(0));
                    merchantAddress.setReverseCoordinates(reverseCoordinates);
                    if (null == request.getAddress().getGoogleInfo()) {
                        request.getAddress().setGoogleInfo(new GoogleInfo("", "", "", ""));
                    }
                    request.getAddress()
                            .getGoogleInfo()
                            .setPincode(geoCoding.getPincode(request.getAddress().getCoordinates()));
                    merchantAddress.setGoogleInfo(request.getAddress().getGoogleInfo());
                }
                mu.setAddress(merchantAddress);
            }
            if (null != request.getAccount()) {
                if (StringUtils.hasText(request.getAccount().getNumber()))
                    if (!request.getAccount().getNumber().contains("XXXX")) mu.setAccount(request.getAccount());
            }
            if (StringUtils.hasText(request.getSigDocType())) {
                mu.setSigDocType(request.getSigDocType());
            }
            if (StringUtils.hasText(request.getSigDocNumber())) {
                mu.setSigDocNumber(request.getSigDocNumber());
            }
            if (Util.isNotNull(mu.getStage()) && mu.getStage().equals(lastStage)) {
                mu.setStatus(getStatus(mu));
            }
            if (Util.isNotNull(request.getDocsSubmitted()) && request.getDocsSubmitted() && ONBOARDING_STAGE_4.equals(request.getStage())) {
                if (Objects.nonNull(childMerchant)) {
                    childMerchant.setStage(paymentConstants.ONBOARDING_STAGE_4);
                } else {
                    mu.setStage(paymentConstants.ONBOARDING_STAGE_4);
                }
            } else if (Util.isNotNull(request.getAccountSubmitted()) && request.getAccountSubmitted() && ONBOARDING_STAGE_5.equals(request.getStage())) {
                if (Objects.nonNull(childMerchant)) {
                    childMerchant.setStage(paymentConstants.ONBOARDING_STAGE_5);
                } else {
                    mu.setStage(paymentConstants.ONBOARDING_STAGE_5);
                }
            } else {
                if (Objects.nonNull(childMerchant)) {
                    childMerchant.setStage(request.getStage());
                } else {
                    mu.setStage(request.getStage());
                }
            }
        }
        if ((Objects.nonNull(childMerchant) && MerchantStatus.approved.name().equals(childMerchant.getStatus())) || MerchantStatus.approved.name().equals(mu.getStatus()) || ONBOARDING_STAGE_8.equals(request.getStage())) {
            if (Objects.nonNull(childMerchant)) {
                if (updateQR(childMerchant, request.getQrCode())) {
                    copyMerchantDetailsFromParentToChild(mu, childMerchant);
                    childMerchant.setStatus(MerchantStatus.profiled.name());
                    SettlementConfig settlementConfig = childMerchant.getSettlementConfig();
                    if (Objects.isNull(settlementConfig)) {
                        settlementConfig = SettlementConfig.builder().build();
                    }
                    settlementConfig.setSettlementDocumentsStatus("open");
                    this.merchantUserRepository.save(childMerchant);
                    notificationService.qrActivation(childMerchant.getEmail(), childMerchant.getMobile(), childMerchant.getDeviceToken(),
                            childMerchant.getShopName(), childMerchant.getFirstName());
                }
            } else {
                if (updateQR(mu, request.getQrCode())) {
                    this.merchantUserRepository.save(mu);
                    notificationService.qrActivation(mu.getEmail(), mu.getMobile(), mu.getDeviceToken(),
                            mu.getShopName(), mu.getFirstName());
                }
            }
        }
        if ((Objects.nonNull(childMerchant) && MerchantStatus.approved.name().equals(childMerchant.getStatus())) || MerchantStatus.approved.name().equals(mu.getStatus())) {
            if (Objects.nonNull(childMerchant)) {
                if (updateOffers(childMerchant, request.getOffers())) {
                    this.merchantUserRepository.save(childMerchant);
                    notificationService.updatedOffersNotification(mu.getEmail(), mu.getMobile(), mu.getDeviceToken(),
                            mu.getShopName(), getMaxRate(mu, request.getOffers()));
                }
            } else {
                if (updateOffers(mu, request.getOffers())) {
                    this.merchantUserRepository.save(mu);
                    notificationService.updatedOffersNotification(mu.getEmail(), mu.getMobile(), mu.getDeviceToken(),
                            mu.getShopName(), getMaxRate(mu, request.getOffers()));
                }
            }
            if (null != request.getConsent() && Boolean.TRUE.equals(request.getConsent())) {
                mu.setConsent(true);
                mu.setConsentAcceptDate(Instant.now());
                this.merchantUserRepository.save(mu);
            }
            if (null != request.getActivateInvoice() && Boolean.TRUE.equals(request.getActivateInvoice())) {
                mu.setIsInvoiceEnabled(true);
                mu.setInvoiceEnabledDate(Instant.now());
                this.merchantUserRepository.save(mu);
            }
            if (Objects.nonNull(childMerchant)) {
                if (null != request.getAddress()) {
                    childMerchant.setAddress(request.getAddress());
                    this.merchantUserRepository.save(childMerchant);
                }
            } else {
                if (null != request.getAddress()) {
                    mu.setAddress(request.getAddress());
                    this.merchantUserRepository.save(mu);
                }
            }
        }
        this.merchantUserRepository.save(mu);
        if (Objects.nonNull(childMerchant)) {
            this.merchantUserRepository.save(childMerchant);
        }
        nextOnboardingStage = Util.getNextOnboardingStage(partner, request.getStage());
        MerchantUserResponse merchantUserResponse = new MerchantUserResponse(mu, null, null, null, null, null, null, null, null);
        if (Objects.nonNull(nextOnboardingStage)) {
            merchantUserResponse.setNextOnboardingStage(nextOnboardingStage);
        }
        return new APIResponse(200, "SUCCESS", "Successfully updated details for merchant: " + mu.getDisplayId(), merchantUserResponse);
    }

    public void copyMerchantDetailsFromParentToChild(MerchantUser parentMerchant, MerchantUser childMerchant) {
        childMerchant.setAgreements(parentMerchant.getAgreements());
        childMerchant.setAddress(parentMerchant.getAddress());
        childMerchant.setParams(parentMerchant.getParams());
        childMerchant.setReferralCode(parentMerchant.getReferralCode());
        childMerchant.setConsent(parentMerchant.getConsent());
        childMerchant.setDocsSubmit(parentMerchant.isDocsSubmit());
        childMerchant.setGst(parentMerchant.getGst());
        childMerchant.setLastName(parentMerchant.getLastName());
        childMerchant.setFirstName(parentMerchant.getFirstName());
        childMerchant.setShopName(parentMerchant.getShopName());
        childMerchant.setAccount(parentMerchant.getAccount());
        childMerchant.setPan(parentMerchant.getPan());
        childMerchant.setOwnership(parentMerchant.getOwnership());
    }

    private boolean updateQR(MerchantUser mu, String qrCode) {
        if (StringUtils.isEmpty(qrCode)) return false;
//        if (!StringUtils.isEmpty(mu.getQrCode()))
//            throw new FreewayException("QR error");
        Optional<MerchantUser> existing = merchantUserRepository.findByQrCodeOrSoftQrCodeOrStoreCode(qrCode, qrCode, qrCode);
        if (existing.isPresent()) {
            LOGGER.error("QR {} already mapped to {}", qrCode, existing.get().getDisplayId());
            throw new FreewayException("Error in QR Activation, please report to payment support.");
        } else {
            String partner = null;
            if (!StringUtils.isEmpty(mu.getPartner())) {
                partner = mu.getPartner();
            }
            QRResponse response = qrService.getQrMetadata(qrCode, partner);
            LOGGER.info("qrResponse: {}", response);
            if (null != response && "merchant".equals(response.getType())) {
                mu.setQrActivationDate(Instant.now());
                mu.setQrCode(qrCode);
                mu.setIsOnboarded(Boolean.TRUE);
                mu.setSmsCode(response.getSmsCode());
                return true;
            } else {
                LOGGER.error(" Incorrect QR {} scanned with partner {}.", qrCode, partner);
                String errorMsg = String.format("Error in QR Activation, Scan %s QR Code!",
                        StringUtils.hasText(partner) ? partner : "payment");
                throw new FreewayException(errorMsg);
            }
        }
    }

    private String getMaxRate(MerchantUser mu, List<OfferResponse> offers) {
        final float[] max = {0.0f};
        if (null != offers && null != mu.getOffers()) {
            offers.forEach(req -> {
                mu.getOffers().stream().filter(o -> o.getId().toString().equals(req.getId())).forEach(o -> {
                    if (max[0] < o.getSubvention()) {
                        max[0] = o.getSubvention();
                    }
                });
            });
        }
        return String.valueOf(max[0]);
    }

    private boolean updateOffers(MerchantUser mu, List<OfferResponse> offers) {
        if (null != offers && null != mu.getMdrs()) {
            offers.forEach(req -> {
                mu.getMdrs()
                        .stream()
                        .filter(o -> o.getUuid().equals(req.getId()))
                        .forEach(o -> o.setActive(req.getActive()));
            });
            return true;
        }
        return false;
    }

    private String getStatus(MerchantUser mu) {
        if ((MerchantStatus.registered.name().equals(mu.getStatus()) ||
                MerchantStatus.resubmission.name().equals(mu.getStatus())) && mu.isMobileVerified() &&
                StringUtils.hasText(mu.getShopName()) && StringUtils.hasText(mu.getCategory()) &&
                StringUtils.hasText(mu.getGst()) && null != mu.getAddress() && null != mu.getAccount() &&
                mu.isDocsSubmit()) {
            if (Boolean.TRUE.equals(enableKarzaAPI)) {
                Boolean isGSTVerified = verifyGst(mu);
                mu.setIsGSTVerified(isGSTVerified);
            }
            notificationService.sendMerchantDocumentSubmitSuccess(mu.getShopName(), mu.getEmail(), mu.getMobile(),
                    mu.getDeviceToken());
//            notificationService.sendSlackMessage("merchant-onboarding",
//                    "All documents submitted for merchant " + mu.getShopName() + ". Proceed with approval process");
            mu.setDynamicOffers(true);
            mu.setDynamicOfferTemplate("Template2");
            mu.setProfiledDate(Instant.now());
            return MerchantStatus.profiled.name();
        }
        return mu.getStatus();
    }

    private Boolean verifyGst(MerchantUser mu) {
        Pageable pageable = new OffsetBasedPageRequest(1, 0, new Sort(Sort.Direction.DESC, "_id"));
        List<MerchantGSTDetails> merchantGSTDetails = merchantGstAuthInfoRepository.findByMerchantId(mu.getId().toString(), pageable).orElse(null);
        if (!CollectionUtils.isEmpty(merchantGSTDetails) && Objects.nonNull(merchantGSTDetails.get(0).getGstAuthResp())) {
            return merchantGSTDetails.get(0).getGstAuthResp().getCode() == 101;
        }
        com.freewayemi.merchant.dto.gst.GstAuthResp gstAuthResp = digitalIdentityService.verifyGst(GstAuthReq.builder().gstin(mu.getGst()).provider(MerchantConstants.KARZA).source(Source.MERCHANTMS).build());
        if (Objects.nonNull(gstAuthResp)) {
            MerchantGSTDetails gstDetails = new MerchantGSTDetails();
            gstDetails.setMerchantId(mu.getId().toString());
            gstDetails.setCreatedDate(Instant.now());
            gstDetails.setLastModifiedDate(Instant.now());
            gstDetails.setGstAuthResp(gstAuthResp);
            merchantGstAuthInfoRepository.save(gstDetails);
            return gstAuthResp.getCode() == 101;
        }
        return Boolean.FALSE;
    }

    public void activate(String merchantId, ActivateMerchantRequest request) {
        Optional<MerchantUser> optional = merchantUserRepository.findById(merchantId);
        if (optional.isPresent()) {
            if (!MerchantStatus.approved.name().equals(request.getStatus()) &&
                    !MerchantStatus.rejected.name().equals(request.getStatus())) {
                throw new FreewayException("Not valid status.");
            }
            if (MerchantStatus.approved.name().equals(request.getStatus()) &&
                    (null == request.getOffers() || request.getOffers().size() < 1)) {
                throw new FreewayException("Provide at least one offer");
            }
            MerchantUser user = optional.get();
            String oldStatus = user.getStatus();
            List<MerchantOffer> offers = new ArrayList<>();
            request.getOffers().stream().forEach(o -> {
                Offer offer = offerBO.get(o);
                MerchantOffer merchantOffer = new MerchantOffer();
                merchantOffer.setId(offer.getId());
                merchantOffer.setTenure(offer.getTenure());
                merchantOffer.setSubvention(offer.getSubvention());
                merchantOffer.setValidFrom(offer.getValidFrom());
                merchantOffer.setValidTo(offer.getValidTo());
                merchantOffer.setActive(false);
                offers.add(merchantOffer);
            });
            user.setOffers(offers);
            user.setStatus(request.getStatus());
            merchantUserRepository.save(user);
            sendActivationNotification(oldStatus, user);
        } else {
            throw new UserNotFoundException("Not a valid merchant");
        }
    }

    private void sendActivationNotification(String status, MerchantUser user) {
        if (MerchantStatus.profiled.name().equals(status) || MerchantStatus.resubmission.name().equals(status)) {
            if (MerchantStatus.approved.name().equals(user.getStatus())) {
                notificationService.sendMerchantActivatedNotification(user.getEmail(), user.getMobile(),
                        user.getDeviceToken(), user.getShopName(), user.getAddress());
            } else {
                notificationService.sendMerchantRejectedNotification(user.getEmail(), user.getMobile(),
                        user.getDeviceToken(), user.getFirstName());
            }
        }
    }

    public List<OfferResponse> getMerchantOffers(String merchantId) {
        List<OfferResponse> offers = new ArrayList<>();
        MerchantUser user = this.getUserById(merchantId);
        if (null != user.getOffers()) {
            offers = user.getOffers().stream().map(offer -> new OfferResponse(offer.getId().toString(), offer.getTenure(), offer.getSubvention(), offer.isActive(), null, null, null, null, offer.getValidFrom(), offer.getValidTo(), offer.getMinAmount(), null, null, null, null, null, null, null, null, null, offer.getEffectiveTenure(), offer.getOfflineAdvanceEmiTenure(), offer.getMinMarginDownPaymentAmount(), offer.getMaxMarginDownPaymentAmount(), offer.getApplicableStates(), offer.getExclusionStates(), offer.getMaxAmount())).collect(Collectors.toList());
        }
        return offers;
    }

    public MerchantUserResponse getMerchantByQR(String qr) {
        LOGGER.info("getMerchantByQr: {}", qr);
        return merchantUserRepository.findByQrCode(qr)
                .map(mu -> new MerchantUserResponse(mu, null, null, brandBO.hasBrand(mu.getParams()), null, null, null, null, null))
                .orElseThrow(() -> new UserNotFoundException("Not a valid merchant for QR"));
    }

//    public MerchantUserResponse getMerchantById(String merchantId) {
//        LOGGER.info("getMerchantById: {}", merchantId);
//        return merchantUserRepository.findById(merchantId)
//                .map(mu -> new MerchantUserResponse(mu, null, brandBO.hasBrand(mu.getParams()), null))
//                .orElseThrow(() -> new UserNotFoundException("Not a valid merchant for QR"));
//    }

    public MerchantUser getMerchantUserByQR(String qr) {
        LOGGER.info("getMerchantUserByQr: {}", qr);
        return merchantUserRepository.findByQrCodeOrSoftQrCodeOrStoreCode(qr, qr, qr)

                .orElseGet(() -> this.getUserByIdOrDisplayId(qr));
    }

    public MerchantUser getMerchantUserByQR_back(String qr) {
        LOGGER.info("getMerchantUserByQr: {}", qr);
        Optional<MerchantUser> optional = merchantUserRepository.findByQrCode(qr);
        return optional.orElseGet(() -> merchantUserRepository.findByStoreCode(qr)
                .orElseThrow(() -> new UserNotFoundException("Not a valid merchant for QR")));
    }

    public void verifyReferralId(String referralId) {
        if (SALES_REFERRAL_CODE.contains(referralId.toUpperCase())) {
            return;
        }
        this.merchantUserRepository.findByReferralCode(referralId)
                .orElseThrow(() -> new UserNotFoundException("Referral not found"));
    }

    public APIResponse upload(String merchantId, MultipartFile file, String name, String type, String documentOwnerType) throws IOException {
        if (!file.isEmpty()) {
            String path = "/tmp/" + file.getOriginalFilename();
            Path filepath = Paths.get(path);
            try (OutputStream os = Files.newOutputStream(filepath)) {
                os.write(file.getBytes());
            }
            LOGGER.info("Document temporary saved on path: {}", path);
            String key = "merchants/" + merchantId + "/" + file.getOriginalFilename();
            s3UploadService.upload(key, new File(path), file.getContentType());
            List<String> retValues = s3UploadService.getPreSignedURL(key);
            String url = retValues.get(0);
            String expiry = retValues.get(1);
            LOGGER.info("Document saved on s3 url: {} with expiry: {}", url, expiry);
            Optional<MerchantUser> optional = merchantUserRepository.findById(merchantId);
            if (optional.isPresent()) {
                MerchantUser mu = optional.get();
                try {
                    DocumentInfo di =
                            new DocumentInfo(url, file.getOriginalFilename(), name, type, expiry, key,
                                    "UPLOADED", "", "", documentOwnerType);
                    List<DocumentInfo> documents = null == mu.getDocuments() ? new ArrayList<>() : mu.getDocuments();
                    documents.add(di);
                    mu.setDocuments(documents);
                    if (!"merchantCommercialsAgreement".equals(type)) {
                        this.merchantUserRepository.save(mu);
                    }
                    aadhaarMaskService.checkAadhaarMask(mu, key, type, name);
                    MerchantUserResponse merchantUserResponse = new MerchantUserResponse(mu, null, null,
                            null, null, null, null, null, null);
                    return new APIResponse(200, "SUCCESS",
                            "Successfully uploaded document of type: " + type + " for merchant: " +
                                    mu.getDisplayId(), merchantUserResponse);
                } catch (Exception e) {
                    LOGGER.error("Exception in upload document for merchant {}", merchantId);
                    return new APIResponse(231, "ERROR",
                            "Error occurred while uploading file for: " +
                                    mu.getDisplayId(), new MerchantUserResponse(mu, null, null,
                            null, null, null, null, null, null));
                }
            }
        } else {
            throw new FreewayException("Uploaded document is empty");
        }
        return null;
    }

    public void save(MerchantUser mu) {
        this.merchantUserRepository.save(mu);
    }

    private boolean allDocumentsSubmitted(MerchantUser merchantUser, List<DocumentInfo> documents) {
        if (Util.isNotNull(merchantUser.getOwnership()) && !merchantUser.getOwnership().equals("")) {
            if (merchantUser.getOwnership().equals("Proprietorship")) {
                return documents.stream().anyMatch(d -> "Bank_Account_Details".equals(d.getName())) &&
                        documents.stream().anyMatch(d -> "POI".equals(d.getName())) &&
                        documents.stream().anyMatch(d -> "POA".equals(d.getName())) &&
                        documents.stream().anyMatch(d -> "Government_Certificate".equals(d.getName())) &&
                        documents.stream().anyMatch(d -> "Company_Address_Proof".equals(d.getName()));
            } else if (merchantUser.getOwnership().equals("HUF") || merchantUser.getOwnership().equals("Partnership") || merchantUser.getOwnership().equals("LLP")) {
                return documents.stream().anyMatch(d -> "Bank_Account_Details".equals(d.getName())) &&
                        documents.stream().anyMatch(d -> "POI".equals(d.getName())) &&
                        documents.stream().anyMatch(d -> "POA".equals(d.getName())) &&
                        documents.stream().anyMatch(d -> "Government_Certificate".equals(d.getName())) &&
                        documents.stream().anyMatch(d -> "Company_Address_Proof".equals(d.getName()));
            } else if (merchantUser.getOwnership().equals("Pvt Ltd") || merchantUser.getOwnership().equals("Public limited") || merchantUser.getOwnership().equals("Private limited")) {
                return documents.stream().anyMatch(d -> "Bank_Account_Details".equals(d.getName())) &&
                        documents.stream().anyMatch(d -> "POI".equals(d.getName())) &&
                        documents.stream().anyMatch(d -> "POA".equals(d.getName())) &&
                        documents.stream().anyMatch(d -> "Government_Certificate".equals(d.getName())) &&
                        documents.stream().anyMatch(d -> "Company_Address_Proof".equals(d.getName())) &&
                        documents.stream().anyMatch(d -> "Company_Document".equals(d.getName()));
            } else if (merchantUser.getOwnership().equals("Partnership")) {
                return documents.stream().anyMatch(d -> "Bank_Account_Details".equals(d.getName())) &&
                        documents.stream().anyMatch(d -> "POI".equals(d.getName())) &&
                        documents.stream().anyMatch(d -> "POA".equals(d.getName())) &&
                        documents.stream().anyMatch(d -> "Government_Certificate".equals(d.getName())) &&
                        documents.stream().anyMatch(d -> "Company_Address_Proof".equals(d.getName())) &&
                        documents.stream().anyMatch(d -> "Donations".equals(d.getName())) &&
                        documents.stream().anyMatch(d -> "Company_Document".equals(d.getName()));
            } else if (merchantUser.getOwnership().equals("Trust") || merchantUser.getOwnership().equals("NGO")) {
                return documents.stream().anyMatch(d -> "Bank_Account_Details".equals(d.getName())) &&
                        documents.stream().anyMatch(d -> "POI".equals(d.getName())) &&
                        documents.stream().anyMatch(d -> "POA".equals(d.getName())) &&
                        documents.stream().anyMatch(d -> "Government_Certificate".equals(d.getName())) &&
                        documents.stream().anyMatch(d -> "Company_Address_Proof".equals(d.getName())) &&
                        documents.stream().anyMatch(d -> "Donations".equals(d.getName()));
            } else if (merchantUser.getOwnership().equals("Society")) {
                return documents.stream().anyMatch(d -> "Bank_Account_Details".equals(d.getName())) &&
                        documents.stream().anyMatch(d -> "POI".equals(d.getName())) &&
                        documents.stream().anyMatch(d -> "POA".equals(d.getName())) &&
                        documents.stream().anyMatch(d -> "Company_Address_Proof".equals(d.getName()));
            } else if (merchantUser.getOwnership().equals("Goverment Services") || merchantUser.getOwnership().equals("Goverment Undertakings")) {
                return documents.stream().anyMatch(d -> "Bank_Account_Details".equals(d.getName())) &&
                        documents.stream().anyMatch(d -> "POI".equals(d.getName())) &&
                        documents.stream().anyMatch(d -> "POA".equals(d.getName())) &&
                        documents.stream().anyMatch(d -> "Government_Certificate".equals(d.getName()));
            } else {
                return documents.stream().anyMatch(d -> "Bank_Account_Details".equals(d.getName())) &&
                        documents.stream().anyMatch(d -> "POI".equals(d.getName())) &&
                        documents.stream().anyMatch(d -> "POA".equals(d.getName()));
            }
        }
        return documents.stream().anyMatch(d -> "Bank_Account_Details".equals(d.getName())) &&
                documents.stream().anyMatch(d -> "POI".equals(d.getName())) &&
                documents.stream().anyMatch(d -> "POA".equals(d.getName()));
    }

    public void configurePricing(String merchantId, DynamicOfferResponse request, String ip, String source) {
        if (null != request && null != request.getDynamicOffers() && request.getDynamicOffers().size() == 0) {
            LOGGER.error("error in setting offers for merchant {} with request {}", merchantId, request);
            throw new FreewayException("Something went wrong!", "merchant", merchantId);
        }
        MerchantOfferConfig config = merchantOfferConfigBO.get(merchantId);
        config.setOffers(Objects.requireNonNull(request).getDynamicOffers());
        config.setLowCostEmi(request.getLowCostEmi());
        config.setActivated(true);
        config.setActivationDate(Instant.now());
        config.setEnableConvenienceFee(request.getEnableConvenienceFee());
        merchantOfferConfigBO.save(config);

        List<Mdr> mdrs = new ArrayList<>();
        List<Offer> offers = new ArrayList<>();

        if (null != request.getEnableConvenienceFee() && request.getEnableConvenienceFee()) {
            Mdr mdr1 = Mdr.builder().build();
            mdr1.setCardType("DEBIT");
            mdr1.setBankCode(null);
            mdr1.setTenure(-1);
            mdr1.setRate(0.0f);
            Mdr mdr2 = Mdr.builder().build();
            mdr2.setCardType("CREDIT");
            mdr2.setBankCode(null);
            mdr2.setTenure(-1);
            mdr2.setRate(0.0f);
            mdrs.add(mdr1);
            mdrs.add(mdr2);
            Offer offer11 = new Offer();
            offer11.setCardType("DEBIT");
            offer11.setBankCode(null);
            offer11.setTenure(-1);
            offer11.setSubvention(0.0f);
            Offer offer12 = new Offer();
            offer12.setCardType("CREDIT");
            offer12.setBankCode(null);
            offer12.setTenure(-1);
            offer12.setSubvention(0.0f);
            offers.add(offer11);
            offers.add(offer12);
        } else {
            GetMdrsAndOffers getMdrsAndOffers = new GetMdrsAndOffers(merchantId, request, config).invoke();
            mdrs = addCardLessMdrs(getMdrsAndOffers.getMdrs());
            offers = addCardLessOffers(getMdrsAndOffers.getOffers());
        }
        Optional<MerchantUser> optional = merchantUserRepository.findById(merchantId);
        if (optional.isPresent()) {
            MerchantUser mu = optional.get();
            mu.setMdrs(mdrs);
            mu.setIsConvFee(request.getEnableConvenienceFee());
            if (null == mu.getConsent() || !mu.getConsent()) {
                mu.setConsent(true);
                mu.setConsentAcceptDate(Instant.now());
            }
            merchantUserRepository.save(mu);
            offerBO.updateOffers(merchantId, offers);
            if ("App".equals(source))
                notificationService.sendUpdateOfferEmail(mu.getMobile(), mu.getEmail(), mu.getShopName(), ip,
                        "Merchant Business App");
        }
    }

    private List<Offer> addCardLessOffers(List<Offer> offers) {
        List<Offer> cardLessOffers = new ArrayList<>();
        for (Offer offer : offers) {
            if ("DEBIT".equalsIgnoreCase(offer.getCardType())) {
                Offer cardLessOffer = new Offer();
                cardLessOffer.setCardType("CARDLESS");
                cardLessOffer.setTenure(offer.getTenure());
                cardLessOffer.setSubvention(offer.getSubvention());
                cardLessOffer.setMerchantId(offer.getMerchantId());
                cardLessOffer.setValidFrom(offer.getValidFrom());
                cardLessOffer.setValidTo(offer.getValidTo());
                cardLessOffers.add(cardLessOffer);
            }
        }
        offers.addAll(cardLessOffers);
        return offers;
    }

    private List<Mdr> addCardLessMdrs(List<Mdr> mdrs) {
        List<Mdr> cardLessMdrs = new ArrayList<>();
        for (Mdr mdr : mdrs) {
            if ("DEBIT".equalsIgnoreCase(mdr.getCardType())) {
                Mdr cardLess = Mdr.builder().build();
                cardLess.setTenure(mdr.getTenure());
                cardLess.setRate(mdr.getRate());
                cardLess.setCardType("CARDLESS");
                cardLessMdrs.add(cardLess);
            }
        }
        mdrs.addAll(cardLessMdrs);
        return mdrs;
    }

    public List<MarketingMerchant> marketing() {
//        for now, this api is not in use. so blocking the same.
        return new ArrayList<>();
//        return merchantUserRepository.findByStatusAndType(MerchantStatus.approved.name(), paymentConstants.OFFLINE,
//                Sort.by("approvedDate").descending())
//                .map(merchantUsers -> merchantUsers.stream()
//                        .filter(mu -> !mu.getShopName().toLowerCase().contains("payment"))
//                        .map(mu -> MarketingMerchant.builder()
//                                .shopName(mu.getShopName())
//                                .category(mu.getCategory())
//                                .address(mu.getAddress())
//                                .brand(null != mu.getParams() ? mu.getParams().getOrDefault("brand", null) : null)
//                                .build()).collect(Collectors.toLowerCaseoList())).orElse(null);
    }

    @Async
    public void uploadPromotionalContent(MerchantUser user, String filename) {
        List<String> images = dynamicPromotionalImageBO.getSharableUrls(user, filename);
        user.setShareImages(images);
        merchantUserRepository.save(user);
    }

    //    @Async
    public void generateMerchantSoftQRCode(MerchantUser user) {
        String softQRCode = UUID.randomUUID().toString();
        qrService.populateQrCode(new QRRequest("merchant", softQRCode));
        user.setSoftQrCode(softQRCode);
        merchantUserRepository.save(user);
    }

    public MerchantResponse getShops(String[] categories, String brand, String location) {

        Boolean isEmptyList = false;
        Optional<List<MerchantUser>> resp;
        List<MerchantUser> merchantUsers;
        resp = getResult(categories, brand, location);

        if (!resp.isPresent() || resp.get().size() == 0) {
            resp = merchantUserRepository.findByStatus("approved");
            merchantUsers = resp.get().subList(0, 10);
            isEmptyList = true;

        } else {
            merchantUsers = resp.get();
        }

        List<MarketingMerchant> marketingMerchants = populateMerchantResponse(merchantUsers, isEmptyList);

        MerchantResponse merchantResponse = MerchantResponse.builder().marketingMerchants(marketingMerchants).build();
        return merchantResponse;
    }

    private List<MarketingMerchant> populateMerchantResponse(List<MerchantUser> merchantUsers, Boolean isEmptyList) {

        List<MarketingMerchant> marketingMerchants = new ArrayList<>();
        for (MerchantUser merchantUser : merchantUsers) {
            Address merchantAddress;
            String merchantCategory;
            //Map<String, String> offers = merchantOfferConfigBO.getMerchantOffersTenuresText(merchantUser);

            //if address is null
            if (merchantUser.getAddress() == null || merchantUser.getAddress().getCity() == null || merchantUser.getAddress().getCity().equals("") || merchantUser.getAddress().getCity().equals(" ")) {
                merchantAddress = new Address("411038", "Pune", "ABC apartments", "Kothrud", "Maharashtra", "India", null, null, null, null);
            } else {
                merchantAddress = merchantUser.getAddress();
            }


            //if category is null
            if (merchantUser.getConsent() == null || merchantUser.getCategory().equals("")) {
                merchantCategory = "Others";
            } else {
                merchantCategory = merchantUser.getCategory();
            }

            MarketingMerchant merchant = MarketingMerchant.builder()
                    .merchantId(merchantUser.getId().toString())
                    .address(merchantAddress)
                    .brand(merchantUser.getParams().getBrand())
                    .logo(paymentBaseUrl + "/web/payment/resources/images/" + merchantUser.getParams().getLogo())
                    .category(merchantCategory)
                    .shopName(merchantUser.getShopName())
                    .listEmpty(isEmptyList)
                    .EMIOption("Low cost EMI")
                    .build();
            marketingMerchants.add(merchant);
        }

        return marketingMerchants;
    }

    private Optional<List<MerchantUser>> getResult(String[] categories, String brand, String location) {

        Optional<List<MerchantUser>> resp = Optional.empty();
        if (categories != null) {
            if (!brand.equals("null") && !location.equals("null")) {
                resp = merchantUserRepository.findByStatusAndCategoryAndBrandAndCity("approved", categories, brand,
                        location);
            } else if (!brand.equals("null")) {
                resp = merchantUserRepository.findByCategoryAndBrandAndStatus(categories, brand, "approved");
            } else if (!location.equals("null")) {
                resp = merchantUserRepository.findByCategoryAndCityAndStatus(categories, location, "approved");
            } else {
                resp = merchantUserRepository.findByCategoryAndStatus(categories, "approved");
            }
        } else if (!brand.equals("null")) {
            if (!location.equals("null")) {
                resp = merchantUserRepository.findByBrandAndCityAndStatus(brand, location, "approved");
            } else {
                resp = merchantUserRepository.findByBrandAndStatus(brand, "approved");
            }
        } else if (!location.equals("null") && categories == null) {
            resp = merchantUserRepository.findByCityAndStatus(location, "approved");

        }
        return resp;
    }

    public Map<String, String> verifyExternalStoreCode(String storeCode) {
        Map<String, String> map = new HashMap<>();
        LOGGER.info("verifyExternalStoreCode: {}", storeCode);
        Optional<MerchantUser> optional = merchantUserRepository.findByExternalStoreCode(storeCode);
        if (optional.isPresent()) {
            MerchantUser mu = optional.get();
            map.put("email", Util.maskedEmail(mu.getEmail()));
            map.put("mobile", Util.maskedMobile(mu.getMobile()));
        } else {
            throw new UserNotFoundException("Merchant Not Found");
        }
        return map;
    }

    public MerchantUser getTTkMerchant(TTKMerchantRequest request) {
        Optional<MerchantUser> optional =
                !"generic".equalsIgnoreCase(request.getStoreCode()) ? merchantUserRepository.findByExternalStoreCode(
                        request.getStoreCode())
                        : StringUtils.hasText(request.getEmail()) ? merchantUserRepository.findByEmailAndIsDeleted(
                        request.getEmail(), false)
                        : merchantUserRepository.findByMobileAndIsDeleted(request.getMobile(), false);
        if (optional.isPresent()) {
            MerchantUser mu = optional.get();
            if (StringUtils.hasText(mu.getEmail()) && mu.getEmail().equals(request.getEmail())) return mu;
            if (StringUtils.hasText(mu.getMobile()) && mu.getMobile().equals(request.getMobile())) return mu;
        }
        throw new FreewayException("Merchant not found!");
    }

    public TokenResponse allocateToken(TokenRequest request, Boolean newUser, String appType, String appVersion,
                                       String deviceToken, Map<String, String> headers) throws IOException {
        MerchantUser mu = this.merchantUserRepository.findByMobileAndIsDeleted(request.getMobile(), false).orElse(null);
        if (null == mu) {
            AdminAuthUser user = adminAuthUserRepository.findByMobile(request.getMobile()).orElse(null);
            if (null == user) {
                throw new UserNotFoundException(
                        "merchant not registered with " + request.getMobile() + ". please sign up");
            } else {
                authUserBO.validate(user.getId().toString(), request.getOtp());
                if (StringUtils.isEmpty(user.getDeviceToken()) && StringUtils.isEmpty(user.getAppInstalledDate()) &&
                        null != deviceToken) {
                    user.setAppInstalledDate(Instant.now());
                }
                user.setDeviceToken(deviceToken);
                user.setAppType(appType);
                user.setAppVersion(appVersion);
                adminAuthUserRepository.save(user);
                List<String> authorities = Arrays.asList("MERCHANT", "STORE_USER");
                if (PARTNER_SALES.equals(user.getRole())) {
                    authorities = Arrays.asList("MERCHANT", PARTNER_SALES);
                }
                if (user.getRole().equals(SALES)) {
                    authorities = Arrays.asList(SALES);
                }
                LOGGER.info("authorities {}", authorities);
                TokenResponse resp = new TokenResponse(jwtTokenBO.generateToken(user.getMerchantId(), deviceToken, authorities, user.getId().toString()));
                merchantSessionBO.saveSession(user.getMerchantId(), user.getMobile(), resp.getToken(), user.getId().toString());
                return resp;
            }
        } else {
            if ("1234512345".equals(request.getMobile()) && "321321".equals(request.getOtp())) {
                LOGGER.info("dummy user login happened!");
            } else {
                authUserBO.validate(mu.getId().toString(), request.getOtp());
            }
            if (StringUtils.isEmpty(mu.getDeviceToken()) && StringUtils.isEmpty(mu.getAppInstalledDate()) &&
                    null != deviceToken) {
                mu.setAppInstalledDate(Instant.now());
            }
            mu.setDeviceToken(deviceToken);
            mu.setAppType(appType);
            mu.setAppVersion(appVersion);
            this.mobileVerified(mu);
            if (null != newUser && newUser) {
                notificationService.sendSignUpMerchant(request.getMobile());
            }
            ruleEngineHelperService.saveMobileData(headers, mu.getId().toString());
            TokenResponse resp = new TokenResponse(jwtTokenBO.generateToken(mu.getId().toString(), deviceToken,
                    Collections.singletonList("MERCHANT")));
            merchantSessionBO.saveSession(mu.getId().toString(), mu.getMobile(), resp.getToken());
            return resp;
        }
    }

    public List<MerchantUser> getBrandStore(String brand, String location, Integer limit, Integer offset) {
        LOGGER.info("brand {},  location {}", brand, location);
        Optional<List<MerchantUser>> resp = Optional.empty();
        if (!StringUtils.isEmpty(brand)) {
            Pageable pageable = new OffsetBasedPageRequest(limit, offset, new Sort(Sort.Direction.ASC, "id"));
            if (!StringUtils.isEmpty(location)) {
                resp = merchantUserRepository.findByBrandIdAndBrandIdsAndCityAndStatus(brand, brand, location,
                        "approved", pageable);
            } else {
                resp = merchantUserRepository.findByBrandIdAndStatus(brand, brand, "approved", pageable);
            }
        }
        return resp.get();
    }

    public List<MerchantUser> getBrandStore(String brand, String location) {
        LOGGER.info("brand {},  location {}", brand, location);
        Optional<List<MerchantUser>> resp = Optional.empty();
        if (!StringUtils.isEmpty(brand)) {
            if (!StringUtils.isEmpty(location)) {
                resp = merchantUserRepository.findByBrandIdAndBrandIdsAndCityAndStatus(brand, brand, location,
                        "approved");
            } else {
                resp = merchantUserRepository.findByBrandIdAndStatus(brand, brand, "approved");
            }
        }
        return resp.get();
    }

    public List<MerchantLocationResponse> getMerchantByCoordinate(String latitude, String longitude, String shopName,
                                                                  String[] brandIds, Integer limit, Integer offset,
                                                                  Double maxDistance) {
        List<MerchantLocationResponse> merchantLocationResponseList = new ArrayList<MerchantLocationResponse>();
        Optional<List<MerchantUser>> totalStoreList;
        Optional<List<MerchantUser>> merchantUserListOptional;
        Pageable pageable;
        Double lat = 0.0;
        Double lng = 0.0;
        if (StringUtils.hasText(latitude) && StringUtils.hasText(longitude)) {
            pageable = new OffsetBasedPageRequest(limit, offset, Sort.unsorted());
            maxDistance = maxDistance * 1000;
            lat = convertValue(latitude);
            lng = convertValue(longitude);
            if (StringUtils.hasText(shopName) && null != brandIds && brandIds.length > 0) {
                totalStoreList = merchantUserRepository.findByStatusAndAddressCoordinatesAndShopNameAndBrands(lat, lng,
                        "approved", shopName, brandIds, brandIds, maxDistance);
                merchantUserListOptional =
                        merchantUserRepository.findByStatusAndAddressCoordinatesAndShopNameAndBrands(lat, lng,
                                "approved", shopName, brandIds, brandIds, maxDistance, pageable);
            } else if (StringUtils.hasText(shopName)) {
                totalStoreList =
                        merchantUserRepository.findByStatusAndAddressCoordinatesAndShopName(lat, lng, "approved",
                                shopName, maxDistance);
                merchantUserListOptional =
                        merchantUserRepository.findByStatusAndAddressCoordinatesAndShopName(lat, lng, "approved",
                                shopName, maxDistance, pageable);
            } else if (null != brandIds && brandIds.length > 0) {
                totalStoreList = merchantUserRepository.findByStatusAndAddressCoordinatesAndBrands(lat, lng, "approved",
                        brandIds, brandIds, maxDistance);
                merchantUserListOptional =
                        merchantUserRepository.findByStatusAndAddressCoordinatesAndBrands(lat, lng, "approved",
                                brandIds, brandIds, maxDistance, pageable);
            } else {
                totalStoreList =
                        merchantUserRepository.findByStatusAndAddressCoordinates(lat, lng, "approved", maxDistance);
                merchantUserListOptional =
                        merchantUserRepository.findByStatusAndAddressCoordinates(lat, lng, "approved", maxDistance,
                                pageable);
            }
        } else {
            pageable = new OffsetBasedPageRequest(limit, offset, new Sort(Sort.Direction.ASC, "id"));
            if (StringUtils.hasText(shopName) && null != brandIds && brandIds.length > 0) {
                totalStoreList = merchantUserRepository.findByStatusAndShopNameAndBrands("approved", shopName, brandIds,
                        brandIds);
                merchantUserListOptional =
                        merchantUserRepository.findByStatusAndShopNameAndBrands("approved", shopName, brandIds,
                                brandIds, pageable);
            } else if (StringUtils.hasText(shopName)) {
                totalStoreList = merchantUserRepository.findByStatusAndShopName("approved", shopName);
                merchantUserListOptional =
                        merchantUserRepository.findByStatusAndShopName("approved", shopName, pageable);
            } else if (null != brandIds && brandIds.length > 0) {
                totalStoreList = merchantUserRepository.findByStatusAndBrands("approved", brandIds, brandIds);
                merchantUserListOptional =
                        merchantUserRepository.findByStatusAndBrands("approved", brandIds, brandIds, pageable);
            } else {
                totalStoreList = merchantUserRepository.findByStatus("approved");
                merchantUserListOptional = merchantUserRepository.findByStatus("approved", pageable);
            }
        }
        if (totalStoreList.isPresent() && !totalStoreList.get().isEmpty() && merchantUserListOptional.isPresent() &&
                !merchantUserListOptional.get().isEmpty()) {
            for (MerchantUser merchantUser : merchantUserListOptional.get()) {
                MerchantLocationResponse merchantLocationResponse = new MerchantLocationResponse();
                merchantLocationResponse.setAddress(merchantUser.getAddress());
                merchantLocationResponse.setShopName(merchantUser.getShopName());
                merchantLocationResponse.setMobile(merchantUser.getMobile());
                merchantLocationResponse.setTotalCount(totalStoreList.get().size());
                merchantLocationResponse.setEmiOption("No-Cost EMI");
                merchantLocationResponse.setRating("4.1");
                if (StringUtils.hasText(latitude) && StringUtils.hasText(longitude) && Objects.nonNull(merchantUser.getAddress()) &&
                        Objects.nonNull(merchantUser.getAddress().getCoordinates()) && !CollectionUtils.isEmpty(merchantUser.getAddress().getCoordinates()) &&
                        Objects.nonNull(merchantUser.getAddress().getCoordinates().get(0)) && Objects.nonNull(merchantUser.getAddress().getCoordinates().get(1))) {
                    merchantLocationResponse.calculateDistance(lat, lng,
                            merchantUser.getAddress().getCoordinates().get(0),
                            merchantUser.getAddress().getCoordinates().get(1));
                }

                merchantLocationResponseList.add(
                        calculateMerchantBrandsAndCategories(merchantUser, merchantLocationResponse));
            }
        }

        return merchantLocationResponseList;
    }

    public MerchantLocationResponse calculateMerchantBrandsAndCategories(MerchantUser merchantUser,
                                                                         MerchantLocationResponse merchantLocationResponse) {
        if (!StringUtils.isEmpty(merchantUser)) {
            Params params = merchantUser.getParams();
            List<String> combinedBrandIds = Util.getCombinedBrandIds(params.getBrandId(), params.getBrandIds());
            if (!CollectionUtils.isEmpty(combinedBrandIds)) {
                String[] brandIds = new String[combinedBrandIds.size()];
                int counter = 0;
                for (String brandId : combinedBrandIds) {
                    brandIds[counter] = brandId;
                    counter++;
                }
                List<Brand> brandList = brandBO.findByBrandId(brandIds);
                if (!CollectionUtils.isEmpty(brandList)) {
                    List<BrandResponseDTO> brandResponseDTOList = new ArrayList<>();
                    Map<String, String> categoryMap = new HashMap<>();
                    brandList.forEach(brand -> {
                        if (null != brand.getHasProducts() && brand.getHasProducts()) {
                            categoryMap.put(brand.getCategory(), brand.getCategory());
                            brandResponseDTOList.add(new BrandResponseDTO(brand.getId().toString(), brand.getName(), brand.getEmiOption(), brand.getIcon(), brand.getSideBanner(), brand.getDisplayHeader(), brand.getDisplaySubHeader(), brand.getHideOtherProducts(), brand.getHideSerialNumber()));
                        }
                    });
                    merchantLocationResponse.setAvailableBrands(brandResponseDTOList);
                    merchantLocationResponse.setAvailableCategories(new ArrayList<>(categoryMap.keySet()));
                }
            }
        }
        return merchantLocationResponse;
    }


    Double convertValue(String value) {
        try {
            return Double.valueOf(value);
        } catch (Exception e) {
            throw new FreewayException("Invalid input!");
        }
    }

    public EmiPricingResponse getEmiPricing() {
        List<EmiOffer> pricingOffers = new ArrayList<>();
        pricingOffers.add(new EmiOffer("3_nce_template2", 5f, Arrays.asList(3), Arrays.asList(6, 9, 12, 18, 24)));
        pricingOffers.add(new EmiOffer("6_nce_template2", 7f, Arrays.asList(3, 6), Arrays.asList(9, 12, 18, 24)));
        pricingOffers.add(new EmiOffer("9_nce_template2", 9f, Arrays.asList(3, 6, 9), Arrays.asList(12, 18, 24)));

        return new EmiPricingResponse(pricingOffers, HIDE_PRICING_PROPOSAL);
    }

    //    public APIResponse setEmiOptions(EmiOptionsRequest request, MerchantUser merchantUser) {
//        if ((Util.isNotNull(request.getLat()) && !request.getLat().equals("")) &&
//                (Util.isNotNull(request.getLng()) && !request.getLng().equals(""))) {
//            merchantUser.getParams().setMerchantProfiledCoordinates(Arrays.asList(request.getLat(), request.getLng()));
//        }
//        if (request.getId().equals("custom")) {
//            String merchantUrl = paymentBaseUrl + "/#/merchants/" + merchantUser.getId().toString();
//            notificationService.sendMerReqCustomPricing("care@getpayment.com", "Merchant has " +
//                            "requested for the customised pricing. please get in touch with the merchant for the " +
//                            "same.", "Merchant requested for custom pricing", merchantUser.getId().toString(), merchantUrl,
//                    merchantUser.getShopName(),
//                    "Mobile: " + merchantUser.getMobile() + ", Email: " + merchantUser.getEmail(),
//                    null != merchantUser.getAddress() ? merchantUser.getAddress().toString() : "");
//        } else {
//            merchantUser.setStatus(getStatus(merchantUser));
//            MerchantOfferConfig config = merchantOfferConfigBO.global(merchantUser.getId().toString(), "Template2");
//            config.setLowCostEmi(true);
//            config.setOffers(
//                    setBasedOnMaxTenure(Integer.valueOf(request.getId().split("_")[0]), config.getOffers()));
//            config.setMargins(
//                    setBasedOnMaxTenure(Integer.valueOf(request.getId().split("_")[0]), config.getMargins()));
//            DynamicOfferResponse dynamicOffer = DynamicOfferResponse.builder()
//                    .dynamicOffers(config.getOffers())
//                    .lowCostEmi(true)
//                    .type("complex")
//                    .build();
//            GetMdrsAndOffers getMdrsAndOffers = new GetMdrsAndOffers(merchantUser.getId().toString(), dynamicOffer, config).invoke();
//            List<Mdr> mdrs = addCardLessMdrs(getMdrsAndOffers.getMdrs());
//            List<Offer> offers = addCardLessOffers(getMdrsAndOffers.getOffers());
//            merchantUser.setMdrs(mdrs);
//            offerBO.updateOffers(merchantUser.getId().toString(), offers);
//        }
//        if (merchantUser.getStage().equals(paymentConstants.ONBOARDING_STAGE_6) || Util.isNotNull(merchantUser.getStage())) {
//            merchantUser.setStage(paymentConstants.ONBOARDING_STAGE_7);
//            merchantUser.setStatus(MerchantStatus.profiled.name());
//        }
//        if (merchantUser.getStage().equals(paymentConstants.ONBOARDING_STAGE_6) || Util.isNull(merchantUser.getStage())) {
//            merchantUser.setStatus(MerchantStatus.profiled.name());
//        }
//        merchantUserRepository.save(merchantUser);
//        return new APIResponse(200, "SUCCESS", "Store details for merchant: " + merchantUser.getDisplayId(), new MerchantUserResponse(merchantUser, null, null, null, null, null, null, null, null));
//    }
//
    private List<DynamicOffer> setBasedOnMaxTenure(Integer maxTenure, List<DynamicOffer> offers) {
        if (null == offers) return offers;
        Float ccMaxRate = 0.0f;
        Float dcMaxRate = 0.0f;
        for (DynamicOffer offer : offers) {
            offer.setSelected(true);
            if (offer.getTenure() > maxTenure) {
                if ("DEBIT".equals(offer.getCardType())) {
                    offer.setRate(dcMaxRate);
                }
                if ("CREDIT".equals(offer.getCardType())) {
                    offer.setRate(ccMaxRate);
                }
            } else {
                if ("DEBIT".equals(offer.getCardType()) && offer.getRate() > dcMaxRate) {
                    dcMaxRate = offer.getRate();
                }
                if ("CREDIT".equals(offer.getCardType()) && offer.getRate() > ccMaxRate) {
                    ccMaxRate = offer.getRate();
                }
            }
        }
        return offers;
    }

    public List<MerchantUser> findByBrandIdAndStatus(String brandId, String status) {
        return merchantUserRepository.findByBrandIdAndStatus(brandId, brandId, status).orElse(null);
    }

    static class GetMdrsAndOffers {
        private String merchantId;
        private DynamicOfferResponse request;
        private MerchantOfferConfig config;
        private List<Mdr> mdrs;
        private List<Offer> offers;

        public GetMdrsAndOffers(String merchantId, DynamicOfferResponse request, MerchantOfferConfig config) {
            this.merchantId = merchantId;
            this.request = request;
            this.config = config;
        }

        public List<Mdr> getMdrs() {
            return mdrs;
        }

        public List<Offer> getOffers() {
            return offers;
        }

        public GetMdrsAndOffers invoke() {
            mdrs = new ArrayList<>();
            offers = new ArrayList<>();

            for (String cardType : new String[]{"DEBIT", "CREDIT"}) {
                Float baseRate = "DEBIT".equals(cardType) ? config.getDcBaseRate() : config.getCcBaseRate();
                for (Integer tenure : TENURES) {
                    Mdr mdr = Mdr.builder().build();
                    mdr.setTenure(tenure);
                    mdr.setRate(getRate(cardType, tenure, request, baseRate));

                    if ("flat".equals(request.getType()) && "DEBIT".equals(cardType)) {

                    } else {
                        mdr.setCardType(cardType);
                    }

                    if ("flat".equals(request.getType()) && "CREDIT".equals(cardType)) {
                    } else {
                        mdrs.add(mdr);
                    }

                    Offer offer = new Offer();
                    offer.setCardType(cardType);
                    offer.setTenure(tenure);
                    if (mdr.getRate().floatValue() == baseRate) {
                        offer.setSubvention(0.0f);
                    } else {
                        float margin = getRate(cardType, tenure, config.getMargins());
                        offer.setSubvention(config.getLowCostEmi() ? mdr.getRate() - margin
                                : mdr.getRate().floatValue() == baseRate ? 0.0f : mdr.getRate() - margin);
                    }
                    offer.setMerchantId(merchantId);
                    offers.add(offer);

                    if (mdr.getRate() < baseRate) {
                        throw new FreewayException("Oops, something went wrong!");
                    }
                }
            }
            return this;
        }
    }

    public MerchantUser findByExternalStoreCode(String storeCode, String status) {
        return this.merchantUserRepository.findByExternalStoreCodeAndStatus(storeCode, status).orElseThrow(() -> new UserNotFoundException("Merchant not found"));
    }

    public long getMerchantsCountMonthly(String leadOwnerId, Instant fromDate, Instant endDate) {
        return merchantUserRepository.findByLeadOwnerIdAndCreatedDateTillMtd(leadOwnerId, fromDate, endDate);
    }

    public long getMerchantsCount(String leadOwnerId, Instant fromDate, Instant endDate) {
        return merchantUserRepository.findByLeadOwnerIdAndCreatedDate(leadOwnerId, fromDate, endDate);
    }

    public List<MerchantUser> getMerchantUsers(String leadOwnerId, int skip, int limit) {
        Pageable pageable = new OffsetBasedPageRequest(limit, skip, new Sort(Sort.Direction.DESC, "id"));
        return merchantUserRepository.findByLeadOwnerId(leadOwnerId, pageable).orElse(new ArrayList<>());
    }

    public List<MerchantUser> getMerchantUsersByLid(String leadOwnerId, int skip, int limit) {
        Pageable pageable = new OffsetBasedPageRequest(limit, skip, new Sort(Sort.Direction.DESC, "id"));
        return merchantUserRepository.findByLeadId(leadOwnerId, pageable).orElse(new ArrayList<>());
    }

    public long getMerchantsOnBoardedCount(String leadOwnerId, Instant fromDate, Instant endDate, Boolean isOnBoarded) {
        return merchantUserRepository.findMerchantOnBoardedCount(leadOwnerId, fromDate, endDate, isOnBoarded);
    }

    public long getOnBoardingMerchantsCount(String leadOwnerId, String status, Boolean isOnBoarded) {
        return merchantUserRepository.findOnBoardingMerchantsCount(leadOwnerId, status, isOnBoarded);
    }

    public long getMerchantActivationCounts(String leadOwnerId, Boolean isOnBoarded, Boolean isActivated) {
        return merchantUserRepository.findMerchantActivationCounts(leadOwnerId, isOnBoarded, isActivated);
    }

    public long getMerchantActivationCountTillMTD(String leadOwnerId, Instant fromDate, Instant endDate, Boolean isActivated) {
        return merchantUserRepository.findMerchantActivatedCountTillMTD(leadOwnerId, fromDate, endDate, isActivated);
    }

    public long getMerchantOnBoardingCountTillMTD(String leadOwnerId, Instant fromDate, Instant endDate, Boolean isOnBoarded) {
        return merchantUserRepository.findMerchantOnBoardedCountTillMTD(leadOwnerId, fromDate, endDate, isOnBoarded);
    }

    public long getMerchantsOnBoardedCountTillNow(String leadOwnerId, Boolean isOnBoarded) {
        return merchantUserRepository.findMerchantsOnBoardedCountTillNow(leadOwnerId, "approved", isOnBoarded);
    }

    public List<MerchantUser> getMerchantUsersByMobile(String mobile) {
        Pageable pageable = new OffsetBasedPageRequest(1000000, 0, new Sort(Sort.Direction.DESC, "createdDate"));
        return merchantUserRepository.findByMobile(mobile, pageable).orElse(new ArrayList<>());
    }

    public List<MerchantUser> getMerchantUsersByGST(String gst) {
        Pageable pageable = new OffsetBasedPageRequest(1000000, 0, new Sort(Sort.Direction.DESC, "createdDate"));
        return merchantUserRepository.findByGst(gst, pageable).orElse(new ArrayList<>());
    }

    public long findMerchantsCountNotActivated(String merchantId, String status, Boolean isQrActivationDate, Boolean isDeviceToken) {
        return merchantUserRepository.findMerchantsCountNotActivated(merchantId, status, isQrActivationDate, isDeviceToken);
    }

    public MerchantUser getMerchantUserByMobile(String mobile) {
        return merchantUserRepository.findByMobileAndIsDeleted(mobile, Boolean.FALSE).orElse(null);
    }

    public MerchantDetailCount getStoreUserCount(String merchantId) {
        long storeUserCount = adminAuthUserRepository.findStoreUserCount(merchantId, "STORE_USER", "inactive");
        return MerchantDetailCount.builder().merchantStoreUserCount(storeUserCount).build();
    }

    public BasicResponse updateMerchant(String merchantId, UpdateMerchantProperties updateMerchantProperties) {
        MerchantUser merchantUser = merchantUserRepository.findById(merchantId).orElseThrow(() -> new FreewayException("Merchant not found"));
        if (Objects.nonNull(merchantUser)) {
            merchantUser.setStage(updateMerchantProperties.getStage());
            MerchantKycInfo merchantKycInfo = MerchantKycInfo.builder().isKycDone(updateMerchantProperties.getKycProperties().getIsKycDone()).successDate(updateMerchantProperties.getKycProperties().getSuccessDate()).build();
            if (Objects.nonNull(merchantUser.getKyc())) {
                merchantKycInfo.setDocNumber(merchantUser.getKyc().getDocNumber());
                merchantKycInfo.setName(merchantUser.getKyc().getName());
                merchantKycInfo.setWhyNotAadhaarKyc(merchantUser.getKyc().getWhyNotAadhaarKyc());
            }
            merchantUser.setKyc(merchantKycInfo);
            merchantUserRepository.save(merchantUser);
            return BasicResponse.builder().status(Status.SUCCESS).build();
        }
        return BasicResponse.builder().status(Status.FAILED).build();
    }

    public BasicResponse updateMerchantProperties(String merchantId, UpdateMerchantProperties updateMerchantProperties) {
        MerchantProperties merchantProperties = new MerchantProperties();
        merchantProperties.setKyc(updateMerchantProperties.getKyc());
        merchantProperties.setMerchantId(merchantId);
        merchantProperitiesBO.saveMerchantProperities(merchantProperties);
        return BasicResponse.builder().status(Status.SUCCESS).statusMsg("successful").build();
    }

    public BasicResponse checkAndSave(MerchantUser merchantUser) {
        MerchantUser user = merchantUserRepository.findById(merchantUser.getId().toString()).orElseThrow(() -> new FreewayException("User not Found"));
        merchantUserRepository.save(merchantUser);
        return BasicResponse.builder().status(Status.SUCCESS).build();
    }

    public Map<String, String> getMerchantConfigs(String label) {
        return merchantConfigBO.findMerchantConfigByLabel(label);
    }

    private Map<String, List<Mdr>> getMdrAndSubvention(String brand) {
        if ("daikin".equals(brand)) {
            return DAIKIN_MDRS_SUBVENTIONS;
        }
        if ("realme".equals(brand)) {
            return REALME_MDRS_SUBVENTIONS;
        }
        if ("bluestar".equals(brand)) {
            return BLUESTAR_MDRS_SUBVENTIONS;
        }
        if ("asus".equals(brand)) {
            return ASUS_MDRS_SUBVENTIONS;
        }
        if ("unitdeals".equals(brand)) {
            return UNITDEALS_MDRS_SUBVENTIONS;
        }
        if ("utl".equals(brand)) {
            return UTL_MDRS_SUBVENTIONS;
        }
        if ("lakme".equals(brand)) {
            return LAKME_MDRS_SUBVENTIONS;
        }
        if ("voltas".equals(brand)) {
            return VOLTAS_MDRS_SUBVENTIONS;
        }
        if ("whirlpool".equals(brand)) {
            return WHIRPOOL_MDRS_SUBVENTIONS;
        }
        if ("mitsubishi electric".equals(brand)) {
            return MITSUBISHI_MDRS_SUBVENTIONS;
        }
        if ("bosch siemens brand store".equals(brand)) {
            return BOSCH_MDRS_SUBVENTIONS;
        }
        if ("bosch seimens other stores".equals(brand)) {
            return BOSCH_OTHER_STORES_MDRS_SUBVENTIONS;
        }
        if ("top 10".equals(brand)) {
            return TOP10_MDRS_SUBVENTIONS;
        }
        if ("mccoy india".equals(brand)) {
            return MCCOY_INDIA_MDRS_SUBVENTIONS;
        }
        if ("lifestyle".equals(brand)) {
            return LIFE_STYLE_MDRS_SUBVENTIONS;
        }
        if ("hero ev".equals(brand)) {
            return HERO_EV_MDRS_SUBVENTIONS;
        }
        if ("non brand ( mobile / electronics /home appliances)".equals(brand)) {
            return NON_BRAND_MDRS_SUBVENTIONS;
        }
        if ("non brand - health and wellness, beauty and fragrance, furniture".equals(brand)) {
            return NON_BRAND_HEALTH_CARE_MDRS_SUBVENTIONS;
        }
        if ("khaitan solar".equals(brand)) {
            return KHAITAN_SOLAR_MDRS_SUBVENTIONS;
        }
        if ("franke faber".equals(brand)) {
            return FRANKE_FABER_MDRS_SUBVENTIONS;
        }
        if ("motovolt".equals(brand)) {
            return MOTO_VOLT_MDRS_SUBVENTIONS;
        }
        if ("ti india".equals(brand)) {
            return TI_INDIA_MDRS_SUBVENTIONS;
        }
        if ("hercules fitness".equals(brand)) {
            return HERCULES_MDRS_SUBVENTIONS;
        }
        return DEFAULT_MDRS_SUBVENTIONS;
    }

    public Map<String, List<Mdr>> getMdrSubventionV2(String brandId) {
        Map<String, List<Mdr>> mdrs = new HashMap<>();
        List<ExceptionBrand> exceptionBrands = exceptionBrandsRepository.findByBrandIdOrType(brandId, "default").orElse(new ArrayList<>());
        for (ExceptionBrand exceptionBrand : exceptionBrands) {
            if (brandId.equals(exceptionBrand.getBrandId())) {
                if (Objects.nonNull(exceptionBrand.getMdrs())) {
                    mdrs.put("mdrs", exceptionBrand.getMdrs());
                } else {
                    mdrs.put("mdrs", new ArrayList<>());
                }
                if (Objects.nonNull(exceptionBrand.getSubventions())) {
                    mdrs.put("subventions", exceptionBrand.getSubventions());
                } else {
                    mdrs.put("subventions", new ArrayList<>());
                }
            }
        }
        if (CollectionUtils.isEmpty(mdrs) && !CollectionUtils.isEmpty(exceptionBrands)) {
            if (Objects.nonNull(exceptionBrands.get(0).getMdrs())) {
                mdrs.put("mdrs", exceptionBrands.get(0).getMdrs());
            } else {
                mdrs.put("mdrs", new ArrayList<>());
            }
            if (Objects.nonNull(exceptionBrands.get(0).getSubventions())) {
                mdrs.put("subventions", exceptionBrands.get(0).getSubventions());
            } else {
                mdrs.put("subventions", new ArrayList<>());
            }
        }
        return mdrs;
    }

    public Boolean isExceptionBrand(String brandId) {
        Long count = exceptionBrandsRepository.findBrandIdCount(brandId);
        return count > 0;
    }

    public void handleMerchantException(String merchantId, MerchantUser merchantUser) {
        if (Objects.isNull(merchantUser)) {
            merchantUser = merchantUserRepository.findById(merchantId).orElseThrow(() -> new FreewayException("User not found"));
        }
        Map<String, List<Mdr>> mdrSubventionMap = getMdrs(merchantUser, merchantId);
        LOGGER.info("mdrSubventionMap1: {}", mdrSubventionMap);
        if (StringUtils.hasText(merchantUser.getPartner())) {
            com.freewayemi.merchant.dto.request.PartnerInfo partnerInfo = referralCodeBO.getPartnerInfo(merchantUser.getPartner());
            LOGGER.info("handleMerchantException partnerInfo: {}", partnerInfo);
            if (Objects.nonNull(partnerInfo)) {
                if (!CollectionUtils.isEmpty(partnerInfo.getMdrs())) {
                    mdrSubventionMap.put("mdrs", partnerInfo.getMdrs());
                }
                if (!CollectionUtils.isEmpty(partnerInfo.getSubventions())) {
                    mdrSubventionMap.put("subventions", partnerInfo.getSubventions());
                }
            }
        }
        if (Objects.nonNull(mdrSubventionMap.get("mdrs"))) {
            merchantUser.setMdrs(mdrSubventionMap.get("mdrs"));
        }
        LOGGER.info("mdrSubventionMap: {}", mdrSubventionMap);
        Optional<List<Offer>> offers = offerRepository.findByMerchantId(merchantId);
        offers.ifPresent(offerRepository::deleteAll);
        cacheBO.removeFromCache(RedisKeyUtil.getMerchantOffersKey(merchantId));
        if ((Objects.nonNull(mdrSubventionMap.get("subventions")))) {
            for (int i = 0; i < mdrSubventionMap.get("subventions").size(); i++) {
                Offer offer = new Offer();
                offer.setCardType(mdrSubventionMap.get("subventions").get(i).getCardType());
                offer.setBankCode(mdrSubventionMap.get("subventions").get(i).getBankCode());
                offer.setTenure(mdrSubventionMap.get("subventions").get(i).getTenure());
                offer.setSubvention(mdrSubventionMap.get("subventions").get(i).getSubvention());
                offer.setMerchantId(merchantId);
                offerRepository.save(offer);
            }
        }
        PaymentConfigInfo paymentConfigInfo = getDefaultPaymentConfig(merchantId, "");
        paymentConfigInfo.setLastModifiedDate(Instant.now());
        paymentOpsService.updatePaymentConfig(paymentConfigInfo);
        merchantUserRepository.save(merchantUser);
    }

    public Map<String, List<Mdr>> getMdrs(MerchantUser merchantUser, String merchantId) {
        if (Objects.isNull(merchantUser)) {
            merchantUser = merchantUserRepository.findById(merchantId).orElseThrow(() -> new FreewayException("User not found"));
        }
        String brandId = "";
        if (Objects.nonNull(merchantUser.getParams()) && StringUtils.hasText(merchantUser.getParams().getBrandId())) {
            // String exceptionBrand = EXCEPTION_BRANDS_MAP.get(merchantUser.getParams().getBrandId());
            brandId = merchantUser.getParams().getBrandId();
            //Map<String, List<Mdr>> mdrSubventionMap = getMdrAndSubvention(exceptionBrand.toLowerCase());
        }
        return getMdrSubventionV2(brandId);
    }

    private List<BrandCommercialMdr> formatMdrs(List<Mdr> mdrs) {
        List<BrandCommercialMdr> mdrList = new ArrayList<>();
        for (Mdr mdr : mdrs) {
            String cardType = mdr.getCardType();
            Integer tenure = mdr.getTenure();
            BrandCommercialMdr finalMdr = null;
            for (BrandCommercialMdr mdrData : mdrList) {
                if (Util.isNotNull(mdrData.getCardType()) && Util.isNotNull(mdrData.getTenure()) && cardType.equals(mdrData.getCardType()) && tenure.equals(mdrData.getTenure())) {
                    finalMdr = mdrData;
                    if (StringUtils.hasText(mdrData.getRate())) {
                        if (Float.parseFloat(mdrData.getRate()) > mdr.getRate()) {
                            Float temp = Float.parseFloat(mdrData.getRate());
                            mdrData.setMaxRate(temp);
                            mdrData.setRate(String.valueOf(mdr.getRate()));
                        }
                        if (mdr.getRate() > Float.parseFloat(mdrData.getRate())) {
                            mdrData.setMaxRate(mdr.getRate());
                        }
                    }
                }
            }
            if (Objects.isNull(finalMdr)) {
                BrandCommercialMdr brandCommercialMdr = BrandCommercialMdr.builder().bankCode(mdr.getBankCode()).cardType(mdr.getCardType()).tenure(mdr.getTenure()).rate(String.valueOf(mdr.getRate())).build();
                finalMdr = brandCommercialMdr;
                mdrList.add(brandCommercialMdr);
            }
        }
        for (BrandCommercialMdr mdr : mdrList) {
            if (Objects.nonNull(mdr.getMaxRate())) {
                mdr.setRate(mdr.getRate() + "-" + mdr.getMaxRate());
            }
            mdr.setBankCode(null);
            mdr.setMaxRate(null);
        }
        return mdrList;
    }

    private List<CommercialDetails> getCommercialDetails(CommercialPojo commercialPojo) {
        if (commercialPojo.getTitle().contains("Standard")) {
            return Arrays.asList(CommercialDetails.builder().cardType("Debit Card").charge("1.50%").tenure("Any").build(), CommercialDetails.builder().cardType("Credit Card").charge("2.25%").tenure("Any").build());
        }
        if (commercialPojo.getTitle().contains("Convenience")) {
            return Arrays.asList(CommercialDetails.builder().cardType("Debit Card").charge("0%").tenure("Any").build(), CommercialDetails.builder().cardType("Credit Card").charge("0%").tenure("Any").build());
        } else {
            List<CommercialDetails> commercialDetails = new ArrayList<>();
            Integer tenure = commercialPojo.getTitle().contains("9") ? 9 : commercialPojo.getTitle().contains("6") ? 6 : 3;
            Map<Integer, Integer> mdrs = new HashMap<Integer, Integer>() {{
                put(3, 5);
                put(6, 7);
                put(9, 9);
            }};
            for (Integer item : TENURES) {
                if (tenure >= item) {
                    commercialDetails.add(CommercialDetails.builder().emiType("No Cost").cardType("Any").mdr(String.valueOf(mdrs.get(item))).tenure(String.valueOf(item)).build());
                } else {
                    commercialDetails.add(CommercialDetails.builder().emiType("Low Cost").cardType("Any").mdr(String.valueOf(mdrs.get(tenure))).tenure(String.valueOf(item)).build());
                }
            }
            return commercialDetails;
        }
    }

    private void getMdsFromBrandId(MerchantUser merchantUser, CommercialResponse commercialResponse) {
        List<String> brandIds = new ArrayList<>();
        if (Objects.nonNull(merchantUser.getParams()) && !CollectionUtils.isEmpty(merchantUser.getParams().getBrandIds())) {
            brandIds = merchantUser.getParams().getBrandIds();
        }
        if (Objects.nonNull(merchantUser.getParams()) && StringUtils.hasText(merchantUser.getParams().getBrandId()) &&
                !brandIds.contains(merchantUser.getParams().getBrandId())) {
            brandIds.add(merchantUser.getParams().getBrandId());
        }
        Map<String, Brand> brandMap = brandBO.getBrandMap(brandIds);
        Map<String, List<BrandCommercialMdr>> brandCommercials = new LinkedHashMap<>();
        for (String brandId : brandIds) {
            Map<String, List<Mdr>> mdrSubventionMap = getMdrSubventionV2(brandId);
            if (brandMap.containsKey(brandId)) {
                brandCommercials.put(brandMap.get(brandId).getName(), formatMdrs(mdrSubventionMap.get("mdrs")));
            }
        }
        if (!CollectionUtils.isEmpty(brandCommercials)) {
            commercialResponse.setBrandCommercials(brandCommercials);
        } else {
            Map<String, List<Mdr>> mdrSubventionMap = getMdrSubventionV2("default");
            if ((Objects.nonNull(mdrSubventionMap) && Objects.nonNull(mdrSubventionMap.get("mdrs")))) {
                brandCommercials.put("defaultMdrs", formatMdrs(mdrSubventionMap.get("mdrs")));
            }
            commercialResponse.setBrandCommercials(brandCommercials);
        }
    }

    public CommercialResponse getCommercials(String merchantId) {
        CommercialResponse commercialResponse = CommercialResponse.builder().build();
        if (StringUtils.hasText(merchantId)) {
            MerchantUser merchantUser = merchantUserRepository.findById(merchantId).orElseThrow(() -> new FreewayException("User not found"));
            MerchantUser childMerchant = null;
            String partnerValue = payment_PARTNER;
            if (MerchantStatus.registered.name().equals(merchantUser.getStatus()) && !CollectionUtils.isEmpty(merchantUser.getPartners())) {
                childMerchant = merchantUserRepository.findByMobileAndIsDeleted(merchantUser.getMobile() + "_" + merchantUser.getPartners().get(0), false).orElseThrow(() -> new FreewayException("Merchant user is not found"));
                partnerValue = merchantUser.getPartners().get(0);
            }
            if (!payment_PARTNER.equals(partnerValue)) {
                com.freewayemi.merchant.dto.request.PartnerInfo partnerInfo = referralCodeBO.getPartnerInfo(partnerValue);
                if (Objects.nonNull(partnerInfo) && !CollectionUtils.isEmpty(partnerInfo.getMdrs())) {
                    Map<String, List<BrandCommercialMdr>> brandCommercials = new LinkedHashMap<>();
                    brandCommercials.put("defaultMdrs", formatMdrs(partnerInfo.getMdrs()));
                    commercialResponse.setBrandCommercials(brandCommercials);
                } else {
                    getMdsFromBrandId(merchantUser, commercialResponse);
                }
            } else {
                getMdsFromBrandId(merchantUser, commercialResponse);
            }

            List<CommercialPojo> commercialPojos = new ArrayList<>();
            commercialPojos.add(CommercialPojo.builder().title("No-Cost : 3 Months").body("Low cost : 6, 9, 12, 18 & 24 Months").heading("3 Month No Cost EMI").build());
            commercialPojos.add(CommercialPojo.builder().title("No-Cost : 3 & 6 Months").body("Low cost : 9, 12, 18 & 24 Months").heading("3 & 6 Months No Cost EMI").bestSeller(Boolean.TRUE).build());
            commercialPojos.add(CommercialPojo.builder().title("No-Cost : 3, 6 & 9 Months").body("Low cost : 12, 18 & 24 Months").heading("3, 6 & 9 Months No Cost EMI").build());
            if (Objects.nonNull(merchantUser.getCategory())) {
                if (Arrays.asList("Vehicle sales", "Mobile & electronics").contains(merchantUser.getCategory())) {
                    commercialPojos.add(CommercialPojo.builder()
                            .title("Standard Emi : all tenures")
                            .body("Customer will bear complete interest as per bank.")
                            .heading("Standard EMI for all tenures")
                            .build());
                }
                if (Arrays.asList("Vehicle sales").contains(merchantUser.getCategory())) {
                    commercialPojos.add(CommercialPojo.builder().title("Convenience fee : all tenures").body("Customer will bear additional MDR & interest as per bank.").heading("Convenience fee for all tenures").build());
                }
            }
            commercialResponse.setCommercials(commercialPojos);
            for (CommercialPojo commercialPojo : commercialPojos) {
                commercialPojo.setDetails(getCommercialDetails(commercialPojo));
            }
        }
        LOGGER.info("commercialResponse: {}", commercialResponse);
        return commercialResponse;
    }

    public PaymentConfigInfo getDefaultPaymentConfig(String merchantId, String category) {
        PaymentProviderInfo axis = PaymentProviderInfo.builder().provider(PaymentProviderEnum.axis).bank(BankEnum.UTIB).type(PaymentModeEnum.DEBIT).disabled(Boolean.FALSE).build();
        PaymentProviderInfo icici = PaymentProviderInfo.builder().provider(PaymentProviderEnum.icici).bank(BankEnum.ICIC).type(PaymentModeEnum.DEBIT).disabled(Boolean.FALSE).build();
        PaymentProviderInfo cashfreeemipg = PaymentProviderInfo.builder().provider(PaymentProviderEnum.cashfreeemipg).bank(null).type(PaymentModeEnum.CREDIT).disabled(Boolean.FALSE).build();
        PaymentProviderInfo hdfc = PaymentProviderInfo.builder().provider(PaymentProviderEnum.hdfc).bank(BankEnum.HDFC).type(PaymentModeEnum.DEBIT).disabled(Boolean.FALSE).build();
        List<PaymentProviderInfo> pgSettings = Arrays.asList(axis, icici, cashfreeemipg, hdfc);
        if (StringUtils.hasText(category) && "Vehicle Sales".equals(category)) {
            pgSettings = Arrays.asList(axis, icici, cashfreeemipg);
        }
        PaymentConfigInfo paymentConfigInfo = new PaymentConfigInfo();
        paymentConfigInfo.setMerchantId(merchantId);
        paymentConfigInfo.setMerchantResponseKey("");
        paymentConfigInfo.setHdfcDcEmiConfig(HdfcDcEmiConfig.builder().storeId("2920").storeName("Newbazaar Technologies Pvt Ltd").build());
        paymentConfigInfo.setIsgPgConfig(IsgPgConfig.builder().merchantCode("").bankId("").secureSecret("").accessCode("").encryptionKey("").mccCode("").terminalId("").build());
        paymentConfigInfo.setPgSettings(pgSettings);
        return paymentConfigInfo;
    }

    private MerchantUser getMerchantDataToUpdate(UpdateMerchantRequest updateMerchantRequest, String merchantId, String stage, HttpServletRequest httpServletRequest) {
        MerchantUser merchantUser = merchantUserRepository.findById(merchantId).orElseThrow(() -> new FreewayException("User not found"));
        String user = authCommonService.getMerchantId(httpServletRequest).getCredentials().get("storeUserId");
        if ("businessv1".equals(stage)) {
            if (StringUtils.hasText(updateMerchantRequest.getShopName())) {
                merchantUser.setShopName(updateMerchantRequest.getShopName());
            }
            if (StringUtils.hasText(updateMerchantRequest.getBusinessName())) {
                merchantUser.setBusinessName(updateMerchantRequest.getBusinessName());
            }
            merchantUser.setType(updateMerchantRequest.getType());
            merchantUser.setCategory(updateMerchantRequest.getCategory());
            merchantUser.setSubCategory(updateMerchantRequest.getSubCategory());
            merchantUser.setMccCode(updateMerchantRequest.getMccCode());
            merchantUser.setPan(updateMerchantRequest.getPan());
            merchantUser.setOwnership(updateMerchantRequest.getOwnership());
            if (StringUtils.hasText(updateMerchantRequest.getExternalStoreCode())) {
                merchantUser.getParams().setExternalStoreCode(updateMerchantRequest.getExternalStoreCode());
            }
            reportService.createEvent(CreateEventRequest.builder().eventName("BUSINESS_DETAILS").createdBy(user).createdByType("sales").merchantId(merchantId).build());
        } else if ("businessDetails".equals(stage)) {
            merchantUser.setEmail(updateMerchantRequest.getEmail());
            merchantUser.setFirstName(updateMerchantRequest.getFirstName());
            merchantUser.setLastName(updateMerchantRequest.getLastName());
            merchantUser.setOwnerDob(updateMerchantRequest.getOwnerDob());
            reportService.createEvent(CreateEventRequest.builder().eventName("BUSINESS_DETAILS").createdBy(user).createdByType("sales").merchantId(merchantId).build());
        } else if ("storeDetails".equals(stage)) {
            if (StringUtils.hasText(updateMerchantRequest.getShopName())) {
                merchantUser.setShopName(updateMerchantRequest.getShopName());
            }
            if (StringUtils.hasText(updateMerchantRequest.getBusinessName())) {
                merchantUser.setBusinessName(updateMerchantRequest.getBusinessName());
            }
            merchantUser.setType(updateMerchantRequest.getType());
            merchantUser.setCategory(updateMerchantRequest.getCategory());
            merchantUser.setSubCategory(updateMerchantRequest.getSubCategory());
            merchantUser.setMccCode(updateMerchantRequest.getMccCode());
            merchantUser.setWebsiteUrl(updateMerchantRequest.getWebsiteUrl());
            Params params = new Params();
            if (Objects.nonNull(merchantUser.getParams())) {
                params = merchantUser.getParams();
            }
            if (StringUtils.hasText(updateMerchantRequest.getExternalStoreCode())) {
                params.setExternalStoreCode(updateMerchantRequest.getExternalStoreCode());
            }
            merchantUser.setParams(params);
        } else if ("created".equals(stage)) {
            if (Objects.nonNull(updateMerchantRequest.getLatitude()) && Objects.nonNull(updateMerchantRequest.getLongitude())) {
                Address address = new Address();
                if (Objects.nonNull(merchantUser.getAddress())) {
                    address = merchantUser.getAddress();
                }
                List<Double> coOrdinates = Arrays.asList(updateMerchantRequest.getLatitude(), updateMerchantRequest.getLongitude());
                address.setCoordinates(coOrdinates);
                address.setReverseUserCoordinates(coOrdinates);
                merchantUser.setAddress(address);
            }
            if (StringUtils.hasText(updateMerchantRequest.getEmail())) {
                merchantUser.setEmail(updateMerchantRequest.getEmail());
            }
            if (StringUtils.hasText(updateMerchantRequest.getFirstName())) {
                merchantUser.setFirstName(updateMerchantRequest.getFirstName());
            }
            if (StringUtils.hasText(updateMerchantRequest.getEmail())) {
                merchantUser.setLastName(updateMerchantRequest.getLastName());
            }
            Params params = merchantUser.getParams();
            if (Objects.isNull(params)) {
                params = new Params();
            }
            if (StringUtils.hasText(updateMerchantRequest.getBrandId())) {
                params.setBrandId(updateMerchantRequest.getBrandId());
            }
            if (Objects.nonNull(updateMerchantRequest.getBrandIds()) && !CollectionUtils.isEmpty(updateMerchantRequest.getBrandIds())) {
                params.setBrandIds(new ArrayList<>(new HashSet<>(updateMerchantRequest.getBrandIds())));
                params.setBrandId(updateMerchantRequest.getBrandIds().get(0));
            }
            merchantUser.setParams(params);
            if (!merchantUser.isMobileVerified()) {
                if (StringUtils.hasText(updateMerchantRequest.getMobile())) {
                    merchantUser.setMobile(updateMerchantRequest.getMobile());
                }
            }
        } else if ("address".equals(stage)) {
            String pinCode = updateMerchantRequest.getPincode();
            String city = "";
            String state = "";
            AddressResponse response = digitalIdentityService.getPostalAddress(pinCode);
            if (Util.isNotNull(response) && Util.isNotNull(response.getCity()) && Util.isNotNull(response.getState())) {
                city = response.getCity();
                state = response.getState();
            }
            Address address = new Address(updateMerchantRequest.getPincode(), city, updateMerchantRequest.getLine1(), updateMerchantRequest.getLine2(), state, merchantUser.getAddress().getCountry(), merchantUser.getAddress().getCoordinates(), merchantUser.getAddress().getSource(), Objects.nonNull(updateMerchantRequest.getIsCurrentSame()) ? updateMerchantRequest.getIsCurrentSame() : Boolean.FALSE, merchantUser.getAddress().getGoogleInfo());
            merchantUser.setStage(stage);
            merchantUser.setAddress(address);
            reportService.createEvent(CreateEventRequest.builder().eventName("BUSINESS_ADDRESS").createdBy(user).createdByType("sales").merchantId(merchantId).build());
            Merchandise merchandise = Merchandise.builder().deployedMerchandise(updateMerchantRequest.getDeployedMerchandise()).isExclusive(updateMerchantRequest.getIsExclusive()).noDeployedReason(updateMerchantRequest.getNoDeployedReason()).selectedCollateralOptions(updateMerchantRequest.getCollateralIntent()).build();
            MerchantProperties merchantProperties = new MerchantProperties();
            merchantProperties.setMerchantId(merchantId);
            merchantProperties.setMerchandise(merchandise);
            merchantProperties.setBrandTags(updateMerchantRequest.getSelectedBrandTags());
            merchantProperties.setProductCategoriesMap(updateMerchantRequest.getProductCategoriesMap());
            merchantProperitiesBO.saveMerchantProperities(merchantProperties);
        } else if ("commercials".equals(stage)) {
            Map<String, List<Mdr>> mdrSubventionConvFeeRateMap = getMdrSubventionConvFeeRateMap(updateMerchantRequest.getType(), updateMerchantRequest.getMaxTenure(), updateMerchantRequest.getLowCostEmi());
            if (Objects.nonNull(mdrSubventionConvFeeRateMap.get("mdrs"))) {
                merchantUser.setMdrs(mdrSubventionConvFeeRateMap.get("mdrs"));
            }
            merchantUser.setIsConvFee("convenienceFee".equals(updateMerchantRequest.getType()));
            if (Objects.nonNull(mdrSubventionConvFeeRateMap.get("convFeeRates"))) {
                List<ConvFeeRate> convFeeRates = new ArrayList<>();
                for (Mdr mdr : mdrSubventionConvFeeRateMap.get("convFeeRates")) {
                    convFeeRates.add(new ConvFeeRate(mdr.getTenure(), mdr.getRate(), mdr.getCardType(), mdr.getBankCode(), null));
                }
                merchantUser.setConvFeeRates(convFeeRates);
            }
            merchantUser.setStage(stage);
            merchantUser.setDynamicOffers(Objects.nonNull(updateMerchantRequest.getProvideMdr()) ? updateMerchantRequest.getProvideMdr() : Boolean.FALSE);
            merchantUser.setDynamicOfferTemplate("Template2");
            merchantUser.setStatus("profiled");
            merchantUser.setProfiledDate(Instant.now());
            MerchantProperties merchantProperties = new MerchantProperties();
            merchantProperties.setMerchantId(merchantId);
            merchantProperties.setCommercials(Commercial.builder().type(updateMerchantRequest.getType()).lowCostEmi(updateMerchantRequest.getLowCostEmi()).maxTenure(updateMerchantRequest.getMaxTenure()).build());
            reportService.createEvent(CreateEventRequest.builder().eventName("PROFILED").createdBy(user).createdByType("sales").merchantId(merchantId).build());
            merchantProperitiesBO.saveMerchantProperities(merchantProperties);
            Optional<List<Offer>> offers = offerRepository.findByMerchantId(merchantId);
            offers.ifPresent(offerRepository::deleteAll);
            cacheBO.removeFromCache(RedisKeyUtil.getMerchantOffersKey(merchantId));
            for (int i = 0; i < mdrSubventionConvFeeRateMap.get("subventions").size(); i++) {
                mdrSubventionConvFeeRateMap.get("subventions").get(i).setMerchantId(merchantId);
                Offer offer = new Offer();
                offer.setCardType(mdrSubventionConvFeeRateMap.get("subventions").get(i).getCardType());
                offer.setBankCode(mdrSubventionConvFeeRateMap.get("subventions").get(i).getBankCode());
                offer.setTenure(mdrSubventionConvFeeRateMap.get("subventions").get(i).getTenure());
                offer.setSubvention(mdrSubventionConvFeeRateMap.get("subventions").get(i).getRate());
                offer.setMerchantId(mdrSubventionConvFeeRateMap.get("subventions").get(i).getMerchantId());
                offerRepository.save(offer);
            }
            PaymentConfigInfo paymentConfigInfo = getDefaultPaymentConfig(merchantId, merchantUser.getCategory());
            paymentConfigInfo.setLastModifiedDate(Instant.now());
            paymentOpsService.updatePaymentConfig(paymentConfigInfo);
            merchantUserRepository.save(merchantUser);
        } else if ("commercialsv1".equals(stage)) {
            if (Objects.isNull(updateMerchantRequest.getBrandCommercial())) {
                reportService.createEvent(CreateEventRequest.builder().eventName("MANUAL_COMMERCIAL").value(updateMerchantRequest.getCommercialTitle()).createdBy(user).createdByType("sales").merchantId(merchantId).build());
                Map<String, String> commercialMap = getCommercialsDetails(updateMerchantRequest.getCommercialTitle());
                Integer maxTenure = null;
                try {
                    maxTenure = Integer.valueOf(commercialMap.get("maxTenure"));
                } catch (Exception ex) {
                    LOGGER.error("MaxTenure coming as null: " + ex);
                }
                Map<String, List<Mdr>> mdrSubventionConvFeeRateMap = getMdrSubventionConvFeeRateMap(commercialMap.get("emiType"), maxTenure, Boolean.TRUE);
                if (Objects.nonNull(mdrSubventionConvFeeRateMap.get("mdrs"))) {
                    merchantUser.setMdrs(mdrSubventionConvFeeRateMap.get("mdrs"));
                }
                merchantUser.setIsConvFee("convenienceFee".equals(commercialMap.get("emiType")));
                if (Objects.nonNull(mdrSubventionConvFeeRateMap.get("convFeeRates"))) {
                    List<ConvFeeRate> convFeeRates = new ArrayList<>();
                    for (Mdr mdr : mdrSubventionConvFeeRateMap.get("convFeeRates")) {
                        convFeeRates.add(new ConvFeeRate(mdr.getTenure(), mdr.getRate(), mdr.getCardType(), mdr.getBankCode(), null));
                    }
                    merchantUser.setConvFeeRates(convFeeRates);
                }
                merchantUser.setStage(stage);
                merchantUser.setDynamicOffers(Boolean.TRUE);
                merchantUser.setDynamicOfferTemplate("Template2");
                MerchantProperties merchantProperties = new MerchantProperties();
                merchantProperties.setMerchantId(merchantId);
                merchantProperties.setCommercials(Commercial.builder().type(commercialMap.get("emiType")).lowCostEmi(Boolean.TRUE).maxTenure(updateMerchantRequest.getMaxTenure()).build());
                merchantUser.setIsConvFee("convenienceFee".equals(commercialMap.get("emiType")));
                merchantProperitiesBO.saveMerchantProperities(merchantProperties);
                Optional<List<Offer>> offers = offerRepository.findByMerchantId(merchantId);
                offers.ifPresent(offerRepository::deleteAll);
                cacheBO.removeFromCache(RedisKeyUtil.getMerchantOffersKey(merchantId));
                for (int i = 0; i < mdrSubventionConvFeeRateMap.get("subventions").size(); i++) {
                    mdrSubventionConvFeeRateMap.get("subventions").get(i).setMerchantId(merchantId);
                    Offer offer = new Offer();
                    offer.setCardType(mdrSubventionConvFeeRateMap.get("subventions").get(i).getCardType());
                    offer.setBankCode(mdrSubventionConvFeeRateMap.get("subventions").get(i).getBankCode());
                    offer.setTenure(mdrSubventionConvFeeRateMap.get("subventions").get(i).getTenure());
                    offer.setSubvention(mdrSubventionConvFeeRateMap.get("subventions").get(i).getRate());
                    offer.setMerchantId(mdrSubventionConvFeeRateMap.get("subventions").get(i).getMerchantId());
                    offerRepository.save(offer);
                }
                PaymentConfigInfo paymentConfigInfo = getDefaultPaymentConfig(merchantId, merchantUser.getCategory());
                paymentConfigInfo.setLastModifiedDate(Instant.now());
                paymentOpsService.updatePaymentConfig(paymentConfigInfo);
                merchantUserRepository.save(merchantUser);
            } else {
                reportService.createEvent(CreateEventRequest.builder().eventName("BRAND_COMMERCIAL").createdBy(user).createdByType("sales").merchantId(merchantId).build());
                merchantUser.setStage(stage);
            }
        } else if ("account".equals(stage)) {
            LOGGER.info("account: {}", updateMerchantRequest);
            Account account = new Account(updateMerchantRequest.getIfsc(), updateMerchantRequest.getAccountNumber(), updateMerchantRequest.getName());
            merchantUser.setAccount(account);
            merchantUser.setStage(stage);
            if (Objects.nonNull(updateMerchantRequest.getIsResubmission()) && updateMerchantRequest.getIsResubmission()) {
                updateMerchantResubmission("accountNo", merchantUser);
            }
            reportService.createEvent(CreateEventRequest.builder().eventName("BUSINESS_ACCOUNT").createdBy(user).createdByType("sales").merchantId(merchantId).build());
        } else if ("dropped".equals(stage)) {
            merchantUser.setStatus("dropped");
            merchantUser.setDroppedReason(updateMerchantRequest.getDroppedReason());
        } else if ("uploads".equals(stage)) {
            if (Objects.nonNull(updateMerchantRequest.getNoMerchandiseReason())) {
                MerchantProperties merchantProperties = new MerchantProperties();
                merchantProperties.setMerchantId(merchantId);
                merchantProperties.setNoMerchandiseReason(updateMerchantRequest.getNoMerchandiseReason());
                merchantProperitiesBO.saveMerchantProperities(merchantProperties);
            }
        } else if ("geoTagging".equals(stage)) {
            String pincode = "";
            if (Objects.nonNull(updateMerchantRequest.getCoordinates())) {
                Map<String, String> coordinates = new HashMap<>();
                coordinates.put("lat", updateMerchantRequest.getCoordinates().get(0).toString());
                coordinates.put("lon", updateMerchantRequest.getCoordinates().get(1).toString());
                pincode = helperService.getPincodeCity(coordinates).getPincCode();
            }
            merchantUser.setSource("salesapp");
            Address address = merchantUser.getAddress();
            if (Objects.isNull(address)) {
                address = new Address();
            }
            GoogleInfo googleInfo = updateMerchantRequest.getGoogleInfo();
            if (Objects.isNull(googleInfo)) {
                googleInfo = new GoogleInfo(null, null, null, null);
            }
            googleInfo.setPincode(pincode);
            address.setGoogleInfo(googleInfo);
            address.setCoordinates(updateMerchantRequest.getCoordinates());
            address.setReverseUserCoordinates(updateMerchantRequest.getCoordinates());
            merchantUser.setAddress(address);
            merchantUser.setStage(stage);
        } else if ("createStoreUser".equals(stage)) {
            updateStoreUserByMerchant(merchantId, updateMerchantRequest.getStoreUser(), httpServletRequest, Boolean.TRUE);
            merchantUser.setStatus("profiled");
            merchantUser.setProfiledDate(Instant.now());
            Params params = merchantUser.getParams();
            if (Objects.isNull(params)) {
                params = new Params();
            }
            params.setLeadOwnerIds(Arrays.asList(authCommonService.getMerchantId(httpServletRequest).getCredentials().get("storeUserId")));
            merchantUser.setParams(params);
            reportService.createEvent(CreateEventRequest.builder().eventName("PROFILED").createdBy(user).createdByType("sales").merchantId(merchantId).build());
        } else if ("merchandise".equals(stage)) {
            Params params = merchantUser.getParams();
            if (Objects.isNull(params)) {
                params = new Params();
            }
            params.setMerchandiseNotes(updateMerchantRequest.getNotes());
            merchantUser.setParams(params);
            handleMerchantException(merchantId, merchantUser);
        } else if ("kyc".equals(stage)) {
            MerchantKycInfo merchantKycInfo = MerchantKycInfo.builder().build();
            if (Objects.nonNull(merchantUser.getKyc())) {
                merchantKycInfo = merchantUser.getKyc();
            }
            if (Objects.nonNull(updateMerchantRequest.getWhyNotAadhaarKyc())) {
                merchantKycInfo.setWhyNotAadhaarKyc(updateMerchantRequest.getWhyNotAadhaarKyc());
            }
            if (Objects.nonNull(updateMerchantRequest.getDocNumber())) {
                merchantKycInfo.setDocNumber(updateMerchantRequest.getDocNumber());
            }
            if (Objects.nonNull(updateMerchantRequest.getName())) {
                merchantKycInfo.setName(updateMerchantRequest.getName());
            }
            merchantUser.setKyc(merchantKycInfo);
        }
        return merchantUser;
    }

    public Double getMdrRate(String cardType, Integer tenure, Integer maxTenure, Boolean lowCostEmi) {
        if (tenure > maxTenure && lowCostEmi) {
            return MDR_RATES_MAP.get(maxTenure);
        }
        if (tenure > maxTenure) {
            if ("CREDIT".equals(cardType)) {
                return 2.25;
            } else {
                return 1.5;
            }
        }
        return MDR_RATES_MAP.get(tenure);
    }

    public Double getSubventionRate(String cardType, Integer tenure, Integer maxTenure, Boolean lowCostEmi) {
        if (tenure > maxTenure && lowCostEmi) {
            if ("CREDIT".equals(cardType)) {
                return CC_SUBVENTIONS_RATES_MAP.get(maxTenure);
            } else {
                return DC_SUBVENTIONS_RATES_MAP.get(maxTenure);
            }
        }
        if (tenure > maxTenure) {
            return 0.0;
        }
        if ("CREDIT".equals(cardType)) {
            return CC_SUBVENTIONS_RATES_MAP.get(tenure);
        } else {
            return DC_SUBVENTIONS_RATES_MAP.get(tenure);
        }
    }

    public Map<String, List<Mdr>> getMdrSubventionConvFeeRateMap(String type, Integer maxTenure, Boolean lowCostEmi) {
        Map<String, List<Mdr>> mdrSubventionConvFeeRateMap = new HashMap<>();
        List<Mdr> mdrs = new ArrayList<>();
        List<Mdr> subventions = new ArrayList<>();
        List<Mdr> convFeeRates = new ArrayList<>();
        if ("noCostEmi".equals(type)) {
            for (String cardType : CARD_TYPES) {
                for (Integer tenure : TENURES) {
                    mdrs.add(Mdr.builder().cardType(cardType).rate(getMdrRate(cardType, tenure, maxTenure, lowCostEmi).floatValue()).bankCode(null).tenure(tenure).build());
                    subventions.add(Mdr.builder().cardType(cardType).rate(getSubventionRate(cardType, tenure, maxTenure, lowCostEmi).floatValue()).bankCode(null).tenure(tenure).build());
                }
            }
        } else if ("standardEmi".equals(type)) {
            mdrs.addAll(Arrays.asList(Mdr.builder().cardType("DEBIT").rate(1.5f).bankCode(null).tenure(-1).build(), Mdr.builder().cardType("CARDLESS").rate(1.5f).bankCode(null).tenure(-1).build(), Mdr.builder().cardType("CREDIT").rate(2.25f).bankCode(null).tenure(-1).build()));
            subventions.addAll(Arrays.asList(Mdr.builder().cardType("DEBIT").subvention(0f).bankCode(null).tenure(-1).build(), Mdr.builder().cardType("CARDLESS").subvention(0f).bankCode(null).tenure(-1).build(), Mdr.builder().cardType("CREDIT").subvention(0f).bankCode(null).tenure(-1).build()));
        } else if ("convenienceFee".equals(type)) {
            mdrs.addAll(Arrays.asList(Mdr.builder().cardType("DEBIT").rate(0f).bankCode(null).tenure(-1).build(), Mdr.builder().cardType("CARDLESS").rate(0f).bankCode(null).tenure(-1).build(), Mdr.builder().cardType("CREDIT").rate(0f).bankCode(null).tenure(-1).build()));
            subventions.addAll(Arrays.asList(Mdr.builder().cardType("DEBIT").subvention(0f).bankCode(null).tenure(-1).build(), Mdr.builder().cardType("CARDLESS").subvention(0f).bankCode(null).tenure(-1).build(), Mdr.builder().cardType("CREDIT").subvention(0f).bankCode(null).tenure(-1).build()));
            convFeeRates.addAll(Arrays.asList(Mdr.builder().cardType("DEBIT").rate(1.5f).bankCode(null).tenure(-1).build(), Mdr.builder().cardType("CARDLESS").rate(1.5f).bankCode(null).tenure(-1).build(), Mdr.builder().cardType("CREDIT").rate(2.25f).bankCode(null).tenure(-1).build()));
        }
        mdrSubventionConvFeeRateMap.put("mdrs", mdrs);
        mdrSubventionConvFeeRateMap.put("subventions", subventions);
        mdrSubventionConvFeeRateMap.put("convFeeRates", convFeeRates);
        return mdrSubventionConvFeeRateMap;
    }

    public List<AdminAuthUser> findByIdOrMerchantId(String id, String role, List<String> statuses) {
        LOGGER.info("findByIdOrMerchantId: {} {} {}", id, role, statuses);
        if (MerchantCommonUtil.isNotEmptyString(role) && !CollectionUtils.isEmpty(statuses) && !statuses.contains("null")) {
            return adminAuthUserRepository.findByIdOrMerchantIdAndRoleAndStatus(id, role, statuses).orElse(new ArrayList<>());
        }
        if (!Objects.equals(role, "null") && MerchantCommonUtil.isNotEmptyString(role)) {
            return adminAuthUserRepository.findByMerchantIdAndRole(id, role).orElse(new ArrayList<>());
        }
        if (!CollectionUtils.isEmpty(statuses) && !statuses.contains("null")) {
            return adminAuthUserRepository.findByIdOrMerchantIdAndStatus(id, statuses).orElse(new ArrayList<>());
        }
        return adminAuthUserRepository.findByIdOrMerchantId(id).orElse(new ArrayList<>());
    }

    public AdminAuthUser findAdminAuthUser(String merchantId, String mobile, String role, String status) {
        return adminAuthUserRepository.findByIdAndMerchantIdAndRoleAndStatus(merchantId, mobile, role, status).orElse(null);
    }

    public MerchantBasicInfo updateStoreUserByMerchant(String merchantId, UpdateStoreUserByMerchantRequest updateStoreUserByMerchantRequest, HttpServletRequest httpServletRequest, Boolean ignoreError) {
        if (StringUtils.isEmpty(updateStoreUserByMerchantRequest.getEmail()) || StringUtils.isEmpty(updateStoreUserByMerchantRequest.getMobile()) || StringUtils.isEmpty(updateStoreUserByMerchantRequest.getName())) {
            if (Boolean.FALSE.equals(ignoreError)) {
                throw new FreewayException("Missing mandatory parameters [mobile,name,email] to create a store_user");
            }
        }
        List<AdminAuthUser> adminAuthUsers = findByIdOrMerchantId(updateStoreUserByMerchantRequest.getMobile(), "STORE_USER", Arrays.asList("approval_pending", "active"));
        AdminAuthUser adminAuthUser = null;
        if (!CollectionUtils.isEmpty(adminAuthUsers)) {
            adminAuthUser = adminAuthUsers.get(0);
        }
        if (Objects.nonNull(adminAuthUser)) {
            if (Boolean.FALSE.equals(ignoreError)) {
                throw new FreewayException("User mobile already exists.");
            }
            return null;
        }
        adminAuthUsers = findByIdOrMerchantId(updateStoreUserByMerchantRequest.getEmail(), null, null);
        adminAuthUser = null;
        if (!CollectionUtils.isEmpty(adminAuthUsers)) {
            adminAuthUser = adminAuthUsers.get(0);
        }
        if (Objects.nonNull(adminAuthUser)) {
            if (Boolean.FALSE.equals(ignoreError)) {
                throw new FreewayException("User with this email already exists.");
            }
            return null;
        }
        MerchantUser merchantUser = getMerchantUserByIdOrDisplayIdOrMobile(updateStoreUserByMerchantRequest.getMobile());
        if (Objects.nonNull(merchantUser)) {
            if (Boolean.FALSE.equals(ignoreError)) {
                throw new FreewayException("User already exists as merchant.");
            }
            return null;
        }
        merchantUser = getMerchantUserByIdOrDisplayIdOrMobile(merchantId);
        if (Objects.isNull(merchantUser)) {
            if (Boolean.FALSE.equals(ignoreError)) {
                throw new FreewayException("Not a valid merchant.");
            }
            return null;
        }
        createAuthUser(updateStoreUserByMerchantRequest.getName(), updateStoreUserByMerchantRequest.getEmail(), "Password@123#", updateStoreUserByMerchantRequest.getMobile(), merchantId, "STORE_USER", Arrays.asList("STORE_USER"), Boolean.FALSE, "approval_pending", authCommonService.getMerchantId(httpServletRequest).getCredentials().get("storeUserId"));
        if ("approved".equals(merchantUser.getStatus())) {
            adminAuthUsers = findByIdOrMerchantId(authCommonService.getMerchantId(httpServletRequest).getCredentials().get("storeUserId"), null, null);
            if (!CollectionUtils.isEmpty(adminAuthUsers)) {
                AdminAuthUser loggedInUser = adminAuthUsers.get(0);
                adminAuthUser = findAdminAuthUser(loggedInUser.getId().toString(), updateStoreUserByMerchantRequest.getMobile(), "STORE_USER", "approval_pending");
                if (Objects.nonNull(adminAuthUser)) {
                    notificationService.sendStoreUserAddedNotification(merchantUser.getMobile(), adminAuthUser.getName(), loggedInUser.getName(), adminAuthUser.getId().toString(), merchantId);
                }
            }
        }
        return MerchantBasicInfo.builder().merchantFirstName(merchantUser.getFirstName()).merchantLastName(merchantUser.getLastName()).mobile(merchantUser.getMobile()).status("Success").build();
    }

    public MerchantUser createParentUser(MerchantLeadRequest request, String type, AdminAuthUser adminAuthUser,
                                         com.freewayemi.merchant.dto.gst.GstAuthResp gstAuthResp) {
        Optional<MerchantUser> optional =
                this.merchantUserRepository.findByMobileAndIsDeleted(request.getMobile(), false);
        if (optional.isPresent()) {
            throw new UserNotFoundException(
                    "merchant already registered with " + request.getMobile() + ". please sign in");
        }
        Optional<AdminAuthUser> optionalAuthUser = this.adminAuthUserRepository.findByMobile(request.getMobile());
        if (optionalAuthUser.isPresent()) {
            throw new UserNotFoundException(
                    "merchant already registered with " + request.getMobile() + ". please sign in");
        }
        return createParentMerchant(request, type, adminAuthUser, gstAuthResp);
    }

    private MerchantUser createParentMerchant(MerchantLeadRequest request, String type, AdminAuthUser adminAuthUser,
                                              com.freewayemi.merchant.dto.gst.GstAuthResp gstAuthResp) {
        LOGGER.info("received request to create parentMerchant: {}", request);
        MerchantUser user = new MerchantUser();
        user.setGst(gstAuthResp.getGst());
        user.setSource("MobileApp");
        user.setMobile(request.getMobile().trim());
        user.setEmail(request.getEmail().trim());
        user.setEmailVerified(false);
        user.setMobileVerified(false);
        user.setStatus(MerchantStatus.leadcreated.name());
        user.setStage(ONBOARDING_STAGE_0);
        user.setDeleted(false);
        user.setDisplayId(this.createDisplayId());
        user.setReferralCode(user.getDisplayId());
        if (Util.isNotNull(adminAuthUser.getPartner())) {
            user.setPartners((Arrays.asList(adminAuthUser.getPartner())));
        } else {
            user.setPartners((Arrays.asList(PartnerName.payment.getName())));
        }
        user.setType(type);
        user.setMerchantRefundConfig(MerchantRefundConfig.builder().doRefundFromSaleOnly(true).build());
        Params params = new Params();
        MerchantConfigs merchantConfigs = merchantConfigsRepository.findByLabel(Constants.exclusionCreditBanks).orElse(null);
        if (Objects.nonNull(merchantConfigs)) {
            params.setExclusionCreditBanks(String.join(",", merchantConfigs.getValues()));
        } else {
            params.setExclusionCreditBanks("SBIN,HSBC,AUFB,ONECARD");
        }
        params.setShowHdfcCardless("true");
        params.setShowKotakCardless("false");
        params.setCallVaultForAxisCreditCard(false);
        params.setCallVaultForAufbCreditCard(false);
        params.setCallVaultForAmexCreditCard(false);
        params.setCallVaultForBobCreditCard(false);
        params.setCallVaultForCitiCreditCard(false);
        params.setCallVaultForHdfcCreditCard(false);
        params.setCallVaultForHsbcCreditCard(false);
        params.setCallVaultForIciciCreditCard(false);
        params.setCallVaultForIndusIndCreditCard(false);
        params.setCallVaultForKotakCreditCard(false);
        params.setCallVaultForRblCreditCard(false);
        params.setCallVaultForSbiCreditCard(false);
        params.setCallVaultForScbCreditCard(false);
        params.setCallVaultForYesCreditCard(false);
        params.setCallVaultForAxisDebitCard(false);
        params.setCallVaultForIciciDebitCard(false);
        params.setCallVaultForOneCardCreditCard(false);
        params.setEnableDownpaymentByDebitCard(false);
        params.setShowKotakCardless("false");
        params.setLeadOwnerIds((Arrays.asList(adminAuthUser.getId().toString())));
        params.setLeadOwnerId(adminAuthUser.getId().toString());
        Agreements agreements = new Agreements();
        agreements.setServiceAgreement(PENDING.getStatus());
        agreements.setCommercialsAgreement(PENDING.getStatus());
        agreements.setNtbAgreement(PENDING.getStatus());
        user.setAgreements(agreements);
        user.setParams(params);
        user.setSettlementConfig(SettlementConfig.builder()
                .settlementCycle(SettlementCycleEnum.STANDARD)
                .excludeMdrAndGstCharges(false)
                .build());
        user.setBusinessName(gstAuthResp.getLegalNameOfBusiness());
        user.setShopName(gstAuthResp.getTradeName());
        merchantUserRepository.save(user);
        notificationService.sendRegistrationNotification(user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getMobile());
        if (!StringUtils.isEmpty(user.getReferredBy()) && !SALES_REFERRAL_CODE.contains(user.getReferredBy())) {
            Optional<MerchantUser> optional = merchantUserRepository.findByReferralCode(user.getReferredBy());
            if (optional.isPresent()) {
                MerchantUser referee = optional.get();
                merchantEarningsBO.createMerchantEarnings(referee.getId().toString(), user.getId().toString(),
                        EarningType.referredBy.name(), MerchantEarningsBO.REFERREDBY);
                merchantEarningsBO.createMerchantEarnings(user.getId().toString(), referee.getId().toString(),
                        EarningType.referred.name(), MerchantEarningsBO.REFERRED);
            }
        }
        return user;
    }

    public MerchantUser createChildMerchantUser(MerchantLeadRequest request, MerchantUser parentMerchantUser,
                                                String type, AdminAuthUser adminAuthUser) {
        String mobile = request.getMobile() + "_" + adminAuthUser.getPartner();
        Optional<MerchantUser> optional = this.merchantUserRepository.findByMobileAndIsDeleted(mobile, false);
        if (optional.isPresent()) {
            throw new UserNotFoundException(
                    "merchant already registered with " + request.getMobile() + ". please sign in");
        }
        return createChildMerchant(request, type, adminAuthUser, parentMerchantUser);
    }

    private MerchantUser createChildMerchant(MerchantLeadRequest request, String type, AdminAuthUser adminAuthUser,
                                             MerchantUser parentMerchantUser) {
        LOGGER.info("received request to create childMerchant: {} by {}", request, adminAuthUser.getId());
        MerchantUser user = new MerchantUser();
        user.setSource("MobileApp");
        user.setMobile(request.getMobile() + "_" + adminAuthUser.getPartner());
        user.setStatus(MerchantStatus.leadcreated.name());
        user.setStage(ONBOARDING_STAGE_0);
        user.setDeleted(false);
        user.setDisplayId(this.createDisplayId());
        user.setReferralCode(user.getDisplayId());
        user.setMeCode(request.getMeCode());
        if (Util.isNotNull(adminAuthUser.getPartner())) {
            user.setPartner(adminAuthUser.getPartner());
        } else {
            user.setPartner(PartnerName.payment.getName());
        }
        if (Util.isNotNull(parentMerchantUser)) {
            user.setParentMerchant(parentMerchantUser.getId().toString());
        }
        user.setType(type);
        user.setMerchantRefundConfig(MerchantRefundConfig.builder().doRefundFromSaleOnly(true).build());
        Params params = new Params();
        MerchantConfigs merchantConfigs = merchantConfigsRepository.findByLabel(Constants.exclusionCreditBanks).orElse(null);
        if (Objects.nonNull(merchantConfigs)) {
            params.setExclusionCreditBanks(String.join(",", merchantConfigs.getValues()));
        } else {
            params.setExclusionCreditBanks("SBIN,HSBC,AUFB,ONECARD");
        }
        params.setShowHdfcCardless("true");
        params.setShowKotakCardless("false");
        params.setCallVaultForAxisCreditCard(false);
        params.setCallVaultForAufbCreditCard(false);
        params.setCallVaultForAmexCreditCard(false);
        params.setCallVaultForBobCreditCard(false);
        params.setCallVaultForCitiCreditCard(false);
        params.setCallVaultForHdfcCreditCard(false);
        params.setCallVaultForHsbcCreditCard(false);
        params.setCallVaultForIciciCreditCard(false);
        params.setCallVaultForIndusIndCreditCard(false);
        params.setCallVaultForKotakCreditCard(false);
        params.setCallVaultForRblCreditCard(false);
        params.setCallVaultForSbiCreditCard(false);
        params.setCallVaultForScbCreditCard(false);
        params.setCallVaultForYesCreditCard(false);
        params.setCallVaultForAxisDebitCard(false);
        params.setCallVaultForIciciDebitCard(false);
        params.setCallVaultForOneCardCreditCard(false);
        params.setEnableDownpaymentByDebitCard(false);
        params.setShowKotakCardless("false");
        params.setLeadOwnerIds((Arrays.asList(adminAuthUser.getId().toString())));
        params.setLeadOwnerId(adminAuthUser.getId().toString());
        Agreements agreements = new Agreements();
        agreements.setServiceAgreement(PENDING.getStatus());
        agreements.setCommercialsAgreement(PENDING.getStatus());
        agreements.setNtbAgreement(PENDING.getStatus());
        user.setAgreements(agreements);
        user.setParams(params);
        user.setSettlementConfig(SettlementConfig.builder()
                .settlementCycle(SettlementCycleEnum.STANDARD)
                .excludeMdrAndGstCharges(false)
                .build());
        merchantUserRepository.save(user);
        notificationService.sendRegistrationNotification(user.getFirstName(), user.getLastName(), user.getEmail(),
                user.getMobile());
        if (!StringUtils.isEmpty(user.getReferredBy()) && !SALES_REFERRAL_CODE.contains(user.getReferredBy())) {
            Optional<MerchantUser> optional = merchantUserRepository.findByReferralCode(user.getReferredBy());
            if (optional.isPresent()) {
                MerchantUser referee = optional.get();
                merchantEarningsBO.createMerchantEarnings(referee.getId().toString(), user.getId().toString(),
                        EarningType.referredBy.name(), MerchantEarningsBO.REFERREDBY);
                merchantEarningsBO.createMerchantEarnings(user.getId().toString(), referee.getId().toString(),
                        EarningType.referred.name(), MerchantEarningsBO.REFERRED);
            }
        }
        return user;
    }

    public MerchantUser updateParentMerchantUser(MerchantUser parentMerchantUser,
                                                 MerchantUser childMerchantUser) {
        if (Util.isNotNull(parentMerchantUser) && Util.isNotNull(childMerchantUser)) {
            if (Util.isNotNull(parentMerchantUser.getPartnerMerchants())) {
                List partnerMerchants = parentMerchantUser.getPartnerMerchants();
                partnerMerchants.add(childMerchantUser.getId().toString());
                parentMerchantUser.setPartnerMerchants(partnerMerchants);
            } else {
                parentMerchantUser.setPartnerMerchants(Arrays.asList(childMerchantUser.getId().toString()));
            }
        }
        return parentMerchantUser;
    }

    public List<MerchantUser> getMerchantLeads(MerchantLeadsRequest merchantLeadsRequest) {
        LOGGER.info("MerchantLeadsRequest: {}", merchantLeadsRequest);
        int skip = 0;
        if (Objects.nonNull(merchantLeadsRequest.getSkip())) {
            skip = merchantLeadsRequest.getSkip();
        }
        Pageable pageable = new OffsetBasedPageRequest(DEFAULT_LIMIT, skip, new Sort(Sort.Direction.DESC, "id"));
        String statuses = "";
        String status = null;
        if (!Objects.equals(merchantLeadsRequest.getStatus(), "null") && Objects.nonNull(merchantLeadsRequest.getStatus()) && StringUtils.hasText(merchantLeadsRequest.getStatus())) {
            status = merchantLeadsRequest.getStatus();
        }
        if (Objects.nonNull(status)) {
            if (MerchantStatus.resubmission.name().equals(status)) {
                statuses = MerchantStatus.resubmission.name();
            }
            if (MerchantStatus.rejected.name().equals(status)) {
                statuses = MerchantStatus.rejected.name();
            }
            if (MerchantStatus.approved.name().equals(status)) {
                statuses = MerchantStatus.approved.name();
            }
            if ("onboarding".equals(status)) {
                statuses = MerchantStatus.approved.name();
            }
        }
        if (!Objects.equals(merchantLeadsRequest.getName(), "null") && Objects.nonNull(merchantLeadsRequest.getName()) &&
                !StringUtils.isEmpty(merchantLeadsRequest.getName()) && Objects.nonNull(status)) {
            if ("onboarding".equals(status)) {
                return merchantUserRepository.findByStatusAndBussinessNameForOnBoarding(
                                merchantLeadsRequest.getLeadOwnerId(), statuses, merchantLeadsRequest.getName(), Boolean.TRUE, pageable)
                        .orElse(new ArrayList<>());
            } else if (MerchantStatus.approved.name().equals(status)) {
                return merchantUserRepository.findByStatusAndBussinessNameForApproved(
                                merchantLeadsRequest.getLeadOwnerId(), statuses, merchantLeadsRequest.getName(), Boolean.TRUE, pageable)
                        .orElse(new ArrayList<>());
            }
            return merchantUserRepository.findByStatusAndBussinessName(merchantLeadsRequest.getLeadOwnerId(), statuses,
                    merchantLeadsRequest.getName(), pageable).orElse(new ArrayList<>());
        }
        if (Objects.nonNull(status)) {
            if ("onboarding".equals(status)) {
                return merchantUserRepository.findByLeadOwnerIdsAndStatusOnBoarding(
                        merchantLeadsRequest.getLeadOwnerId(), statuses, Boolean.TRUE).orElse(new ArrayList<>());
            } else if (MerchantStatus.approved.name().equals(status)) {
                return merchantUserRepository.findByLeadOwnerIdsAndStatusApproved(
                                merchantLeadsRequest.getLeadOwnerId(), statuses, Boolean.TRUE, pageable)
                        .orElse(new ArrayList<>());
            }
            return merchantUserRepository.findByLeadOwnerIdsAndStatus(merchantLeadsRequest.getLeadOwnerId(), statuses, pageable)
                    .orElse(new ArrayList<>());
        }
        if (!Objects.equals(merchantLeadsRequest.getName(), "null") && Objects.nonNull(merchantLeadsRequest.getName()) &&
                !StringUtils.isEmpty(merchantLeadsRequest.getName())) {
            return merchantUserRepository.findByLeadOwnerIdsAndBussinessName(merchantLeadsRequest.getLeadOwnerId(),
                    merchantLeadsRequest.getName()).orElse(new ArrayList<>());
        }
        return merchantUserRepository.findByLeadOwnerIds(merchantLeadsRequest.getLeadOwnerId(), pageable)
                .orElse(new ArrayList<>());
    }

    public MerchantUser getMerchantLeadByDisplayId(String displayId) {
        MerchantUser merchantUser = merchantUserRepository.findByDisplayId(displayId)
                .orElseThrow(() -> new FreewayException("Merchant lead is not exists"));

        return merchantUser;
    }

    private void leadRequestValidation(MerchantLeadRequest merchantLeadRequest) {
        if (Objects.isNull(merchantLeadRequest)) {
            throw new FreewayException("Request should not be null");
        }
        if (StringUtils.isEmpty(merchantLeadRequest.getMobile())) {
            throw new FreewayException("Mobile Number should not be empty");
        }
        if (StringUtils.isEmpty(merchantLeadRequest.getEmail())) {
            throw new FreewayException("Email should not be empty");
        }
        if (StringUtils.isEmpty(merchantLeadRequest.getGst())) {
            throw new FreewayException("GST should not be empty");
        }
        if (StringUtils.isEmpty(merchantLeadRequest.getMeCode())) {
            throw new FreewayException("ME Code should not be empty");
        }
        if (StringUtils.isEmpty(merchantLeadRequest.getPinCode())) {
            throw new FreewayException("Pincode should not be empty");
        }
        if (!ValidationUtil.validateMobileNumber(merchantLeadRequest.getMobile())) {
            throw new FreewayException("Invalid Mobile Number");
        }
    }

    public MerchantUser getMerchantLeadByMobileAndPartner(String mobile, String partner) {
        if (Util.isNotNull(partner) && !partner.equals("")) {
            mobile = mobile.toString() + "_" + partner;
        }
        return merchantUserRepository.findByMobileAndPartner(mobile, partner).orElse(null);
    }

    public BasicResponse createLead(String leadOwnerId, MerchantLeadRequest merchantLeadRequest) {
        leadRequestValidation(merchantLeadRequest);
        LOGGER.info("Request recieved to createLead method: {}", merchantLeadRequest);
        String mobileNumber = merchantLeadRequest.getMobile();
        String gst = merchantLeadRequest.getGst();
        MerchantUser merchantUser = getUserByMobile(mobileNumber);
        AdminAuthUser adminAuthUser = adminAuthUserRepository.findById(leadOwnerId).orElse(null);
        if (Objects.isNull(adminAuthUser)) {
            throw new FreewayException("leadOwnerId is not exists");
        }
        MerchantUser merchantLead =
                getMerchantLeadByMobileAndPartner(merchantLeadRequest.getMobile(), adminAuthUser.getPartner());
        LOGGER.info("merchantUser : {} and merchantLead : {}", merchantUser, merchantLead);
        if (Objects.nonNull(merchantLead)) {
            return BasicResponse.builder()
                    .statusMsg(ResponseCode.LEAD_ALREADY_CREATED.getStatusMsg())
                    .status(ResponseCode.LEAD_ALREADY_CREATED.getStatus())
                    .statusCode(ResponseCode.LEAD_ALREADY_CREATED.getCode())
                    .build();
        }
        com.freewayemi.merchant.dto.gst.GstAuthResp gstAuthResp =
                digitalIdentityService.verifyGst(GstAuthReq.builder().gstin(gst).provider(MerchantConstants.KARZA).source(Source.MERCHANTMS).build());
        LOGGER.info("gstDetailsResponse : {}", gstAuthResp);
        if (Objects.nonNull(gstAuthResp) && "success".equals(gstAuthResp.getStatus())) {
            MerchantUser parentMerchantUser = null;
            if (Objects.isNull(merchantUser)) {
                parentMerchantUser = createParentUser(merchantLeadRequest, paymentConstants.ONLINE, adminAuthUser,
                        gstAuthResp);
            } else {
                parentMerchantUser = merchantUser;
                parentMerchantUser.setIspaymentMerchant(true);
                List<String> partners = new ArrayList<>();
                if (Util.isNotNull(parentMerchantUser.getPartners()) && !parentMerchantUser.getPartners().isEmpty()) {
                    partners = parentMerchantUser.getPartners();
                }
                if (Util.isNotNull(adminAuthUser.getPartner())) {
                    partners.add(adminAuthUser.getPartner());
                } else {
                    partners.add(PartnerName.payment.getName());
                }
                parentMerchantUser.setPartners(partners);
                merchantUserRepository.save(parentMerchantUser);
            }
            AdminAuthUser newAdminAuthUser = new AdminAuthUser();
            if (Util.isNotNull(adminAuthUser.getPartner())) {
                newAdminAuthUser.setPartner(adminAuthUser.getPartner());
            }
            newAdminAuthUser.setMobile(merchantLeadRequest.getMobile());
            newAdminAuthUser.setLogin(merchantLeadRequest.getEmail());
            newAdminAuthUser.setMerchantId(parentMerchantUser.getId().toString());
            newAdminAuthUser.setRole(AUTHUSER_MERCHANT);
            newAdminAuthUser.setStatus(paymentConstants.STATUS_ACTIVE);
            adminAuthUserRepository.save(newAdminAuthUser);
            if (Util.isNotNull(adminAuthUser.getPartner())) {
                MerchantUser childMerchantUser =
                        createChildMerchantUser(merchantLeadRequest, parentMerchantUser, paymentConstants.ONLINE,
                                adminAuthUser);
                updateParentMerchantUser(parentMerchantUser, childMerchantUser);
                MerchantTraces merchantTraces = MerchantTraces.builder()
                        .merchantId(childMerchantUser.getId().toString())
                        .eventName(ONBOARDING.name())
                        .partner(adminAuthUser.getPartner())
                        .onBoardingStages(getOnBoardingStages())
                        .build();
                merchantTracesRepository.save(merchantTraces);
            }
            notificationService.sendLeadCreationNotification("9502803991", "varaprasad");
            return BasicResponse.builder()
                    .statusMsg(ResponseCode.LEAD_CREATED.getStatusMsg())
                    .status(ResponseCode.LEAD_CREATED.getStatus())
                    .statusCode(ResponseCode.LEAD_CREATED.getCode())
                    .header("Congratulations!")
                    .build();
        } else {
            return BasicResponse.builder()
                    .statusMsg(ResponseCode.GST_VALIDAION_FAILED.getStatusMsg())
                    .status(ResponseCode.GST_VALIDAION_FAILED.getStatus())
                    .statusCode(ResponseCode.GST_VALIDAION_FAILED.getCode())
                    .build();
        }
    }

    private OnBoardingStage getStage(String step, Status status) {
        return OnBoardingStage.builder()
                .name(step)
                .status(status)
                .createdDate(Instant.now())
                .lastModifiedDate(Instant.now())
                .build();
    }

    private List<OnBoardingStage> getOnBoardingStages() {
        List<OnBoardingStage> onBoardingStages = new ArrayList<>();
        onBoardingStages.add(getStage(ONBOARDING_STEP1, SUCCESS));
        onBoardingStages.add(getStage(ONBOARDING_STEP2, PENDING));
        onBoardingStages.add(getStage(ONBOARDING_STEP3, PENDING));
        onBoardingStages.add(getStage(ONBOARDING_STEP4, PENDING));
        onBoardingStages.add(getStage(ONBOARDING_STEP5, PENDING));
        onBoardingStages.add(getStage(ONBOARDING_STEP6, PENDING));
        onBoardingStages.add(getStage(ONBOARDING_STEP7, PENDING));
        onBoardingStages.add(getStage(ONBOARDING_STEP8, PENDING));
        return onBoardingStages;
    }

    public AdminAuthUser findLeadOwner(String leadOwnerId) {
        return adminAuthUserRepository.findById(leadOwnerId)
                .orElseThrow(() -> new FreewayException("User not exists."));
    }

    public long getMerchantCreatedLeadsCountToday(String leadOwnerId, Instant fromDate, Instant endDate) {
        return merchantUserRepository.findByLeadOwnerIdAndCreatedDate(leadOwnerId, fromDate, endDate);
    }

    public long getMerchantLeadsOnboardedCountMonthly(String leadOwnerId, Instant fromDate, Instant endDate, Boolean isOnboarded) {
        return merchantUserRepository.findByLeadOwnerIdAndApprovedDateAndOnBoardedTilMtd(leadOwnerId, fromDate, endDate,
                isOnboarded);
    }

    public long getMerchantLeadsActivatedCountMonthly(String leadOwnerId, Instant fromDate, Instant endDate, Boolean isActivated) {
        return merchantUserRepository.findByLeadOwnerIdAndApprovedDateAndActivatedTilMtd(leadOwnerId, fromDate, endDate,
                isActivated);
    }

    public long getMerchantCreatedLeadsCountMonthly(String leadOwnerId, Instant fromDate, Instant endDate) {
        return merchantUserRepository.findByLeadOwnerIdAndCreatedDateTillMtd(leadOwnerId, fromDate, endDate);
    }


    public long getStatusLeadsCount(String leadOwnerId, String status) {
        return merchantUserRepository.findByLeadOwnerIdAndStatusLeadsCount(leadOwnerId, status);
    }

    public long getOnBoardingLeadsCount(String leadOwnerId, String status) {
        return merchantUserRepository.findByLeadOwnerIdAndOnBoardingLeadsCount(leadOwnerId, status, Boolean.TRUE);
    }

    public long getAllLeadsCount(String leadOwnerId) {
        return merchantUserRepository.findAllLeadsByLeadOwnerId(leadOwnerId);
    }

    public BasicResponse saveAdminAuthUser(AdminAuthUser adminAuthUser, Boolean sendPasswordLink) {
        LOGGER.info("saveAdminAuthUser: {} {}", adminAuthUser, sendPasswordLink);
        try {
            adminAuthUserRepository.save(adminAuthUser);
            if (Boolean.TRUE.equals(sendPasswordLink)) {
                String otp = Util.generateOtp(isProduction);
                adminAuthUser = adminAuthUserRepository.findByLogin(adminAuthUser.getLogin()).orElse(null);
                if (Objects.nonNull(adminAuthUser)) {
                    Instant now = Instant.now();
                    adminAuthUser.setOtp(otp);
                    adminAuthUser.setLastModifiedDate(now);
                    adminAuthUser.setPasswordResetLinkGeneratedAt(now);
                    adminAuthUserRepository.save(adminAuthUser);
                }
            }
            return BasicResponse.builder().status(Status.SUCCESS).build();
        } catch (Exception ex) {
            return BasicResponse.builder().status(Status.FAILED).statusMsg("Something went wrong").build();
        }
    }

    private void createAuthUser(String name, String email, String password, String mobile, String merchantId, String role, List<String> permissions, Boolean sendPasswordLink, String status, String createdBy) {
        String salt = Util.getRandomString(5);
        String dbPassword = password + salt;
        String hashed = Util.getMd5(dbPassword);
        String userType = Util.getUserTypeByRole(role);
        if (StringUtils.hasText(name)) {
            name = name.toLowerCase();
        }

        AdminAuthUser adminAuthUser = new AdminAuthUser();
        adminAuthUser.setLogin(email);
        adminAuthUser.setCreatedDate(Instant.now());
        adminAuthUser.setSalt(salt);
        adminAuthUser.setPassword(hashed);
        adminAuthUser.setUserType(userType);
        adminAuthUser.setStatus("active");
        adminAuthUser.setName(name);
        adminAuthUser.setRole(role);
        if (StringUtils.hasText(createdBy)) {
            adminAuthUser.setCreatedBy(createdBy);
        }
        if (StringUtils.hasText(mobile)) {
            adminAuthUser.setMobile(mobile);
        }
        if (!CollectionUtils.isEmpty(permissions)) {
            adminAuthUser.setPermissions(permissions);
        }
        if (StringUtils.hasText(status)) {
            adminAuthUser.setStatus(status);
        }
        if (StringUtils.hasText(merchantId)) {
            adminAuthUser.setMerchantId(merchantId);
        }
        if (sendPasswordLink) {
            String otp = Util.generateOtp(isProduction);
            Instant now = Instant.now();
            adminAuthUser.setOtp(otp);
            adminAuthUser.setLastModifiedDate(now);
            adminAuthUser.setPasswordResetLinkGeneratedAt(now);
            notificationService.sendForgotPasswordEmail(adminAuthUser.getLogin(), otp);
        }
        saveAdminAuthUser(adminAuthUser, sendPasswordLink);
    }

    private Map<String, String> getCommercialsDetails(String commercialTitle) {
        Map<String, String> commercialMap = new HashMap<>();
        if (StringUtils.hasText(commercialTitle)) {
            if (commercialTitle.contains("Standard")) {
                commercialMap.put("emiType", "standardEmi");
                commercialMap.put("maxTenure", null);
            } else if (commercialTitle.contains("'Convenience'")) {
                commercialMap.put("emiType", "'Convenience'");
                commercialMap.put("maxTenure", null);
            } else if (commercialTitle.contains("9")) {
                commercialMap.put("emiType", "noCostEmi");
                commercialMap.put("maxTenure", "9");
            } else if (commercialTitle.contains("6")) {
                commercialMap.put("emiType", "noCostEmi");
                commercialMap.put("maxTenure", "6");
            } else if (commercialTitle.contains("3")) {
                commercialMap.put("emiType", "noCostEmi");
                commercialMap.put("maxTenure", "3");
            } else {
                commercialMap.put("emiType", "standardEmi");
                commercialMap.put("maxTenure", null);
            }
        }
        return commercialMap;
    }

    private void updateMerchantResubmission(String type, MerchantUser merchantUser) {
        if (StringUtils.hasText(merchantUser.getResubmissionReason())) {
            List<String> resubmissionReasons = Arrays.stream(merchantUser.getResubmissionReason().split(",")).map(String::trim).collect(Collectors.toList());
            String action = "";
            if (SIGNATORY_ID_PROOF.equals(type)) {
                action = REUPLOAD_KYC;
            }
            if (ACCOUNT_PROOF.equals(type)) {
                action = REUPLOAD_ACCOUNT;
            }
            if (ADDRESS_PROOF.equals(type)) {
                action = UPLOAD_ADDRESS_PROOF;
            }
            if (MERCHANDISE.equals(type)) {
                action = REUPLOAD_MERCHANDISE;
            }
            if (SHOP.equals(type)) {
                action = REUPLOAD_STORE_PHOTO;
            }
            if (ACCOUNT_NO.equals(type)) {
                action = ACCOUNT_NO_CHANGE;
            }
            if ("".equals(type)) {
                return;
            }
            Map<String, String> merchantConfigs = getMerchantConfigs("resubmission_reasons");
            for (Map.Entry<String, String> entry : merchantConfigs.entrySet()) {
                if (entry.getValue().equals(action)) {
                    resubmissionReasons.remove(entry.getKey().trim());
                }
            }
            int count = 0;
            for (String key : merchantConfigs.keySet()) {
                if (resubmissionReasons.contains(key.trim())) {
                    count += 1;
                }
            }
            if (count == 0) {
                merchantUser.setStatus("profiled");
                merchantUser.setResubmissionReason("");
            } else {
                merchantUser.setResubmissionReason(String.join(",", resubmissionReasons));
            }
            merchantUserRepository.save(merchantUser);
        }
    }

    public MerchantInfo getDecorateMerchant(MerchantUser merchantUser, Boolean viewInfo) {
        MerchantProperties merchantProperties = merchantProperitiesBO.getMerchantProperties(merchantUser.getId().toString());
        MerchantInfo merchantInfo = new MerchantInfo();
        if (Objects.nonNull(merchantProperties)) {
            if (Objects.nonNull(merchantProperties.getId())) {
                merchantInfo.set_id(merchantProperties.getId().toString());
            }
            merchantInfo.setMerchantProperties(merchantProperties);
        }
        if (Boolean.TRUE.equals(viewInfo)) {
            merchantInfo.setMid(merchantUser.getId().toString());
            merchantInfo.set_id(merchantUser.getId().toString());
            PaymentConfigInfo paymentConfigInfo = null;
            PaymentConfigResponse paymentConfigResponse = paymentOpsService
                    .getPgSettings(TransactionOpsRequest.builder().merchantId(merchantUser.getId().toString()).build());
            if (Objects.nonNull(paymentConfigResponse)) {
                paymentConfigInfo = paymentConfigResponse.getPaymentConfigInfo();
            }
            if (Objects.nonNull(paymentConfigInfo)) {
                merchantInfo.setPgSettings(paymentConfigInfo.getPgSettings());
            }
        }
        if (Objects.nonNull(merchantUser.getStatus()) && (!merchantUser.getStatus().contains("profiled") && !merchantUser.getStatus().contains("approved"))) {
            merchantInfo.setMid(merchantUser.getId().toString());
            merchantInfo.set_id(merchantUser.getId().toString());
            List<DocumentInfo> documentInfos = new ArrayList<>();
            if (Objects.nonNull(merchantUser.getDocuments()) && !CollectionUtils.isEmpty(merchantUser.getDocuments())) {
                if (Objects.nonNull(merchantInfo.getDocuments())) {
                    for (DocumentInfo documentInfo : merchantInfo.getDocuments()) {
                        if (Objects.nonNull(documentInfo.getDocStatus()) && "rejected".equals(documentInfo.getDocStatus())) {
                            documentInfos.add(documentInfo);
                        }
                    }
                }
            }

            merchantInfo.setDocuments(documentInfos);
            if (Objects.isNull(merchantInfo.getAddress())) {
                Address address = new Address();
                address.setCity("");
                address.setPincode("");
                address.setLine1("");
                address.setLine2("");
            }
            if (Objects.isNull(merchantUser.getShopName())) {
                merchantInfo.setShopName("");
            }
        }
        Pageable pageable = new OffsetBasedPageRequest(1, 0, new Sort(Sort.Direction.DESC, "_id"));
        List<MerchantGSTDetails> merchantGSTDetails = merchantGstAuthInfoRepository.findByMerchantId(merchantUser.getId().toString(), pageable).orElse(new ArrayList<>());
        if (!CollectionUtils.isEmpty(merchantGSTDetails) && Objects.nonNull(merchantGSTDetails.get(0).getGstAuthResp())) {
            merchantInfo.setGstData(merchantGSTDetails.get(0).getGstAuthResp());
        }
        merchantInfo.setMid(merchantUser.getId().toString());
        merchantInfo.setDisplayId(merchantUser.getDisplayId());
        merchantInfo.setShopName(merchantUser.getShopName());
        merchantInfo.setBusinessName(merchantUser.getBusinessName());
        merchantInfo.setMobile(merchantUser.getMobile());
        merchantInfo.setEmail(merchantUser.getEmail());
        merchantInfo.setGst(merchantUser.getGst());
        merchantInfo.setAddress(merchantUser.getAddress());
        merchantInfo.setStage(merchantUser.getStage());
        merchantInfo.setStatus(merchantUser.getStatus());
        merchantInfo.setIsOnboarded(merchantUser.getIsOnboarded());
        merchantInfo.setCreatedDate(DateUtil.getInstantDateInISTWithPattern("dd MMMM yyyy", merchantUser.getCreatedDate()));
        merchantInfo.setCategory(merchantUser.getCategory());
        merchantInfo.setSubCategory(merchantUser.getSubCategory());
        merchantInfo.setApprovedDate(merchantUser.getApprovedDate());
        merchantInfo.setAccount(merchantUser.getAccount());
        merchantInfo.setFirstName(merchantUser.getFirstName());
        merchantInfo.setLastName((merchantUser.getLastName()));
        merchantInfo.setParams(merchantUser.getParams());
        merchantInfo.setPan(merchantUser.getPan());
        merchantInfo.setSource(merchantUser.getSource());
        merchantInfo.setType(merchantUser.getType());
        merchantInfo.setMccCode(merchantUser.getMccCode());
        merchantInfo.setOwnership(merchantUser.getOwnership());
        merchantInfo.setSettlementConfig(merchantUser.getSettlementConfig());
        merchantInfo.setRiskCategory(merchantUser.getRiskCategory());
        return merchantInfo;
    }


    public MerchantInfo updateMerchantDetails(String merchantId, UpdateMerchantRequest updateMerchantRequest, HttpServletRequest httpServletRequest, String appVersion) {
        String stage = updateMerchantRequest.getStage();
        LOGGER.info("merchant state to be updated: {}", stage);
        MerchantUser merchantUser = getMerchantDataToUpdate(updateMerchantRequest, merchantId, stage, httpServletRequest);
        Params params = merchantUser.getParams();
        if (Objects.isNull(params)) {
            params = new Params();
        }
        params.setSalesAppVersion(appVersion);
        merchantUser.setSource("SalesApp");
        if ("account".equals(stage) && Objects.nonNull(updateMerchantRequest.getVerified()) && Boolean.FALSE.equals(updateMerchantRequest.getVerified())) {
            stage = "storePhoto";
        } else if ("kyc".equals(stage)) {
            stage = "businessDetails";
        }
        merchantUser.setStage(stage);
        params.setLeadOwnerIds(Arrays.asList(authCommonService.getMerchantId(httpServletRequest).getCredentials().get("storeUserId")));
        merchantUser.setParams(params);
        merchantUserRepository.save(merchantUser);
        return getDecorateMerchant(merchantUser, false);
    }

    public Boolean brandGstCheckRequired(String user) {
        try {
            AdminAuthUser adminAuthUser = adminAuthUserRepository.findById(user).orElse(null);
            if (Objects.nonNull(adminAuthUser)) {
                if (Objects.nonNull(adminAuthUser.getPermissions()) && !CollectionUtils.isEmpty(adminAuthUser.getPermissions()) && adminAuthUser.getPermissions().contains("IGNORE_BRAND_GST_CHECK")) {
                    return Boolean.FALSE;
                }
            }
        } catch (Exception e) {
            throw new FreewayException("Something went wrong: " + e);
        }
        return Boolean.TRUE;
    }

    public void saveNotification(NotificationRequest notificationRequest) {
        if (Objects.nonNull(notificationRequest)) {
            Notification notification = Notification.builder().title(notificationRequest.getTitle()).readStatus(notificationRequest.getReadStatus())
                    .body(notificationRequest.getBody()).leadOwnerId(notificationRequest.leadOwnerId)
                    .merchantId(notificationRequest.getMerchantId()).source(notificationRequest.source).build();
            notificationBO.saveNotification(notification);
        }
    }

    public MerchantUserAndCountResponse getUniqueMerchantsByLeadOwner(String leadOwnerId, String monthStartDate, Boolean transacting, List<String> ids) {
        TransactionDataResponse transactionDataResponse = paymentOpsService.getMerchantList(TransactionOpsRequest.builder().leadOwnerId(leadOwnerId)
                .fromDate(monthStartDate).toDate(DateUtil.getFormattedDate(Date.from(Instant.now()), "yyyy-MM-dd")).transactionStatus("success").build());
        List<String> merchantIds;
        if (Objects.nonNull(transactionDataResponse)) {
            merchantIds = transactionDataResponse.getMerchants();
        } else {
            merchantIds = new ArrayList<>();
        }
        if (Boolean.TRUE.equals(transacting)) {
            List<MerchantUser> merchantUsers = merchantUserRepository.findByIdAndOnBoarded(merchantIds, "approved", Boolean.TRUE).orElse(new ArrayList<>());
            return MerchantUserAndCountResponse.builder().merchantCount((long) merchantUsers.size())
                    .merchantUsers(merchantUsers).build();
        } else {
            List<String> uniqueMerchantIds = ids.stream().filter(id -> !merchantIds.contains(id)).collect(Collectors.toList());
            return MerchantUserAndCountResponse.builder().merchantCount((long) uniqueMerchantIds.size())
                    .merchantUsers(merchantUserRepository.findByIdAndOnBoarded(uniqueMerchantIds, "approved", Boolean.TRUE).orElse(new ArrayList<>())).build();
        }
    }

    public List<MerchantSearchResponse> getSearchMerchants(String leadOwnerId) {
        List<MerchantUser> merchantUsers = merchantUserRepository.findByMerchantNameAndLeadOwnerId(leadOwnerId).orElse(new ArrayList<>());
        List<MerchantSearchResponse> merchantSearchResponses = new ArrayList<>();
        for (MerchantUser merchantUser : merchantUsers) {
            MerchantSearchResponse merchantSearchResponse = MerchantSearchResponse.builder().shopName(merchantUser.getShopName()).displayId(merchantUser.getDisplayId()).merchantId(merchantUser.getId().toString()).build();
            merchantSearchResponses.add(merchantSearchResponse);
        }
        return merchantSearchResponses;
    }

    public Long getPartnerMerchantStatusCount(String partner, String mobile) {
        if (!StringUtils.isEmpty(mobile) && mobile.contains("_")) {
            mobile = mobile.split("_")[0];
        }
        LOGGER.info("getPartnerMerchantStatusCount: {} {}", mobile, partner);
        return merchantUserRepository.getPartnerMerchantStatusCount(mobile + "_" + partner, "registered");
    }

    public GenerateAgreementResponse getMerchantOnboardingAgreement(HttpServletRequest request) {
        String merchantId = authCommonService.getMerchantId(request).getMerchantIdOrDisplayId();
        GenerateAgreementResponse generateAgreementResponse =
                new GenerateAgreementResponse(20,
                        "FAILED", "No agreement is available", null, null,
                        null);
        try {
            MerchantUser merchantUser = merchantUserRepository.findById(merchantId).orElseThrow(() -> new FreewayException("No merchant found"));
//            if (StringUtils.hasText(merchantUser.getParentMerchant())) {
//                LOGGER.info("Got child Merchant so, shifting to parent merchant : {}", merchantUser);
//                merchantUser = merchantUserRepository.findById(merchantUser.getParentMerchant()).orElseThrow(() -> new FreewayException("No merchant found"));
//                merchantId = merchantUser.getId().toString();
//            }
            LOGGER.info("aggrement: {}", merchantUser.getAgreements());
            Pageable pageable = new OffsetBasedPageRequest(1, 0, new Sort(Sort.Direction.DESC, "_id"));
            List<MerchantPennydropDetails> pennydropDetails = merchantPennydropDetailsRepository.findMerchantPennydropDetailsByMerchantId(merchantId, pageable)
                    .orElse(new ArrayList<>());
            if (CollectionUtils.isEmpty(pennydropDetails)) {
                throw new FreewayException("Please verify the account details");
            }

            List<MerchantGSTDetails> merchantGSTDetails = merchantGstAuthInfoRepository.findByMerchantId(merchantId, pageable).orElse(new ArrayList<>());
            if (CollectionUtils.isEmpty(merchantGSTDetails)) {
                throw new FreewayException("Please verify the gst details");
            }

            generateAgreementResponse = generateAgreementService.generateMerchantOnboardingAgreement(merchantUser, pennydropDetails.get(0), merchantGSTDetails.get(0), "ONBOARDING");
            CommercialResponse commercialResponse = getCommercials(merchantId);
            if (generateAgreementResponse.getCode() == 20) {
                generateAgreementResponse = new GenerateAgreementResponse(0,
                        "SUCCESS", "", null, null, null);
            }
            generateAgreementResponse.setCommercialResponse(commercialResponse);
        } catch (Exception e) {
            LOGGER.error("Exception occurred while generating agreement: " + e);
            throw new FreewayException(StringUtils.hasText(e.getMessage()) ? e.getMessage() : "something went wrong");
        }

        return generateAgreementResponse;
    }

    public APIResponse saveAgreement(String merchantId, com.freewayemi.merchant.dto.request.AgreementDetails agreementDetails, String ip, Boolean ntbAgreement) {
        authUserBO.validate(merchantId, agreementDetails.getOtp());
        MerchantUser merchantUser = merchantUserRepository.findById(merchantId).orElseThrow(() -> new FreewayException("Merchant does not exist"));
        MerchantUser childMerchant = null;
        String partnerValue = payment_PARTNER;
        if (MerchantStatus.registered.name().equals(merchantUser.getStatus()) && !CollectionUtils.isEmpty(merchantUser.getPartners())) {
            childMerchant = merchantUserRepository.findByMobileAndIsDeleted(merchantUser.getMobile() + "_" + merchantUser.getPartners().get(0), false).orElseThrow(() -> new FreewayException("Merchant user is not found"));
            partnerValue = merchantUser.getPartners().get(0);
        }
        com.freewayemi.merchant.dto.request.PartnerInfo partner = referralCodeBO.getPartnerInfo(partnerValue);
        String lastStage = Util.getLastOnboardingStage(partner);

        List<DocumentInfo> documentInfos = new ArrayList<>();
        if (Objects.nonNull(merchantUser.getDocuments())) {
            documentInfos = merchantUser.getDocuments();
        }
        Agreements agreements = merchantUser.getAgreements();
        if (Objects.isNull(agreements)) {
            agreements = new Agreements();
        }

        if (Boolean.TRUE.equals(ntbAgreement)) {
            agreements.setNtbAgreement(SUCCESS.getStatus());
            documentInfos.add(new DocumentInfo(agreementDetails.getMerchantNtbAgreementUrl(), "Merchant NTB Agreement", "Merchant NTB AgreementDetails", "merchantNTBAgreement", null, null, "UPLOADED", null, null, null));
        } else {
            if (StringUtils.hasText(agreementDetails.getMerchantServiceAgreementUrl())) {
                documentInfos.add(new DocumentInfo(agreementDetails.getMerchantServiceAgreementUrl(), "MerchantServiceAgreement", "Merchant Service AgreementDetails", "merchantServiceAgreement", null, null, "UPLOADED", null, null, null));
            }
            if (StringUtils.hasText(agreementDetails.getMerchantCommercialsAgreementUrl())) {
                documentInfos.add(new DocumentInfo(agreementDetails.getMerchantCommercialsAgreementUrl(), "MerchantCommercialsAgreement", "Merchant Commercials AgreementDetails", "merchantCommercialsAgreement", null, null, "UPLOADED", null, null, null));
            }
            if (Objects.nonNull(childMerchant)) {
                if (!MerchantStatus.approved.name().equals(childMerchant.getStatus())) {
                    handleMerchantException(childMerchant.getId().toString(), childMerchant);
                    childMerchant.setStage(agreementDetails.getStage());
                    if (lastStage.equals(agreementDetails.getStage())) {
                        childMerchant.setStatus(MerchantStatus.profiled.name());
                    }
                }
            } else {
                if (!MerchantStatus.approved.name().equals(merchantUser.getStatus())) {
                    handleMerchantException(merchantId, merchantUser);
                    merchantUser.setStage(agreementDetails.getStage());
                    if (lastStage.equals(agreementDetails.getStage())) {
                        merchantUser.setStatus(MerchantStatus.profiled.name());
                    }
                }
            }
            if (Status.PENDING.getStatus().equals(merchantUser.getAgreements().getCommercialsAgreement())) {
                agreements.setCommercialsAgreement(SUCCESS.getStatus());
            }
            if (Status.PENDING.getStatus().equals(merchantUser.getAgreements().getServiceAgreement())) {
                agreements.setServiceAgreement(SUCCESS.getStatus());
            }
        }
        merchantUser.setDocuments(documentInfos);
        merchantUser.setAgreements(agreements);
        merchantUserRepository.save(merchantUser);
        if (Objects.nonNull(childMerchant)) {
            merchantUserRepository.save(childMerchant);
        }
        AgreementDetails agreement = new AgreementDetails();
        Map<String, List<Mdr>> mdrSubventionMap = getMdrs(merchantUser, merchantId);
        if (Boolean.FALSE.equals(ntbAgreement)) {
            if (Objects.nonNull(childMerchant)) {
                com.freewayemi.merchant.dto.request.PartnerInfo partnerInfo = referralCodeBO.getPartnerInfo(childMerchant.getPartner());
                if (Objects.nonNull(partnerInfo) && !CollectionUtils.isEmpty(partnerInfo.getMdrs())) {
                    mdrSubventionMap.put("mdrs", partnerInfo.getMdrs());
                }
            }
            if (Objects.nonNull(mdrSubventionMap.get("mdrs"))) {
                agreement.setMdrs(mdrSubventionMap.get("mdrs"));
            }
        }
        if (Objects.nonNull(childMerchant)) {
            agreement.setMerchantId(childMerchant.getId().toString());
            agreement.setCreatedBy(childMerchant.getId().toString());
        } else {
            agreement.setMerchantId(merchantId);
            agreement.setCreatedBy(merchantId);
        }
        agreement.setMerchantServiceAgreementUrl(agreementDetails.getMerchantServiceAgreementUrl());
        agreement.setMerchantCommercialsAgreementUrl(agreementDetails.getMerchantCommercialsAgreementUrl());
        agreement.setMerchantNtbAgreementUrl(agreementDetails.getMerchantNtbAgreementUrl());
        agreement.setOtp(agreementDetails.getOtp());
        agreement.setLatitude(agreementDetails.getLatitude());
        agreement.setLongitude(agreementDetails.getLongitude());
        agreement.setIpAddress(ip);
        agreement.setCreatedDate(Instant.now());
        agreement.setLastModifiedDate(Instant.now());
        agreementRepository.save(agreement);
        if (Boolean.TRUE.equals(ntbAgreement)) {
            String subject = "NTB Agreement and Commercial - Merchant Consent received";
            if (!isProduction) {
                subject = "DEV: NTB Agreement and Commercial - Merchant Consent received";
            }
            String displayId = merchantUser.getDisplayId();
            if (Objects.nonNull(childMerchant.getDisplayId())) {
                displayId = childMerchant.getDisplayId();
            }
            String content = "Following merchant has provided his interest and consent for payment Digital Finance:" + " <br></br>" +
                    "Merchant Name: " + merchantUser.getFirstName() + " " + merchantUser.getLastName() + " <br></br>" +
                    "DisplayId: " + displayId + " <br></br>" +
                    "Request date: " + Date.from(Instant.now()) + " <br></br>";

            MerchantConfigs merchantConfigs = merchantConfigsRepository.findByLabel("operations_email_list").orElse(null);

            String emailIds = "";
            List<String> emails = new ArrayList<>();
            if (Objects.nonNull(merchantConfigs.getValues())) {
                emails = merchantConfigs.getValues();
            }
            for (String email : emails) {
                if (StringUtils.hasText(emailIds)) {
                    emailIds = emailIds + "," + email;
                } else {
                    emailIds = email;
                }
            }

            EmailRequest emailRequest = EmailRequest.builder().toEmailIds(emailIds).subject(subject).htmlBody(content).build();
            emailService.sendEmail(emailRequest);
        }
        String nextStage = "";
        if (Objects.nonNull(childMerchant)) {
            nextStage = Util.getNextOnboardingStage(partner, childMerchant.getStage());
        } else {
            nextStage = Util.getNextOnboardingStage(partner, merchantUser.getStage());
        }
        MerchantUserResponse merchantUserResponse = new MerchantUserResponse(merchantUser, null, null, null, null, null, null, null, null);
        merchantUserResponse.setNextOnboardingStage(nextStage);
        return new APIResponse(200, "SUCCESS", "AgreementDetails Stored Successfully: " + merchantUser.getDisplayId(), merchantUserResponse);
    }

    public MerchantLeadOwnerData getMerchantUserAndLeadOwnerUser(String id) {
        MerchantUser merchantUser = getMerchantUserByIdOrDisplayIdOrMobile(id);
        MerchantLeadOwnerData merchantLeadOwnerData = new MerchantLeadOwnerData(null, null);
        if (Util.isNotNull(merchantUser) && Util.isNotNull(merchantUser.getParams()) && !StringUtils.isEmpty(merchantUser.getParams().getLeadOwnerId())) {
            merchantLeadOwnerData.setMerchantUser(merchantUser);
            SalesUserProfile salesUserProfile = salesAgentBO.getSalesUserProfile(merchantUser.getParams().getLeadOwnerId());
            if (Util.isNotNull(salesUserProfile)) {
                merchantLeadOwnerData.setSalesUserProfile(salesUserProfile);
            }
        }
        return merchantLeadOwnerData;
    }

    public MerchantUser getMerchantUser(String displayMerchantId, String partner) {
        MerchantUser mu = getUserByMerchantIdOrDisplayId(displayMerchantId);
        String userMobile = mu.getMobile();
        MerchantUser partnerUser = null;
        List<String> partners = mu.getPartners();
        if (Util.isNotNull(partner) && Util.isNotNull(partners)) {
            if (Util.isNotNull(partners) && partners.contains(partner)) {
                partnerUser = getUserByMobile(userMobile + "_" + partner);
                mu = partnerUser;
                displayMerchantId = mu.getId().toString();
            } else {
                throw new FreewayException(userMobile + " doesn't have any partner merchant with partner: " + partner);
            }
        }
        return mu;
    }

    public String getMerchantApiKey(String merchantIdOrDisplayId) {
        if (StringUtils.isEmpty(merchantIdOrDisplayId)) {
            return null;
        }
        MerchantUser mu = getUserByMerchantIdOrDisplayId(merchantIdOrDisplayId);
        return Optional.of(mu)
                .map(MerchantUser::getSecurityCredentials)
                .map(SecurityCredentials::getXApiKey)
                .orElse(null);
    }

    public String getCachedMerchantApiKey(String merchantIdOrDisplayId) {
        if (StringUtils.isEmpty(merchantIdOrDisplayId)) {
            return null;
        }
        String cacheKey = RedisKeyUtil.getMerchantApiKey(merchantIdOrDisplayId);
        String fromCache = cacheBO.getFromCache(cacheKey);
        if (StringUtils.hasText(fromCache)) {
            return fromCache;
        }
        String fromDB = getMerchantApiKey(merchantIdOrDisplayId);
        cacheBO.putInCache(cacheKey, fromDB, false);
        return fromDB;
    }


    public MerchantBasicInfo getMerchantDetails(String merchantId) {
//        MerchantUser merchantUser = merchantUserRepository.findByEmailAndIsDeleted(email, Boolean.FALSE).orElseThrow(()-> new FreewayException("Merchant not found"));
        MerchantUser merchantUser = merchantUserRepository.findById(merchantId).orElseThrow(() -> new FreewayException("Merchant not found"));
        return MerchantBasicInfo.builder().login(merchantUser.getEmail())
                .mobile(merchantUser.getMobile())
                .name(merchantUser.getShopName())
                .merchantLastName(merchantUser.getLastName())
                .merchantFirstName(merchantUser.getFirstName())
                .status(merchantUser.getStatus())
                .merchantId(merchantUser.getId().toString())
                .checkOutVersion(merchantUser.getParams().getCheckoutVersion())
                .category(merchantUser.getCategory())
                .build();
    }

    public PaymentLinkInfo getDynamicFields(String merchantId) {
        MerchantUser merchantUser = merchantUserRepository.findById(merchantId).orElseThrow(() -> new FreewayException("Merchant Not Found"));
        PaymentLinkInfo paymentLinkInfo = merchantUser.getPaymentLinkInfo();
        if (Objects.isNull(paymentLinkInfo)) {
            paymentLinkInfo = PaymentLinkInfo.builder().build();
        }
        if (!CollectionUtils.isEmpty(brandBO.getBrands(merchantUser))) {
            paymentLinkInfo.setHasBrandProducts(Boolean.TRUE);
        } else {
            paymentLinkInfo.setHasBrandProducts(Boolean.FALSE);
        }
        return paymentLinkInfo;
    }

    public List<String> getMerchantIdsByBrandId(String brandId) {
        List<MerchantUser> merchantUsers = merchantUserRepository.findByBrandId(brandId).orElse(new ArrayList<>());
        return merchantUsers.stream().map(merchantUser -> merchantUser.getId().toString()).collect(Collectors.toList());
    }

    public BrandDashboardResponse getUserInfoForBrandDashboard(String merchantId) {
        MerchantUser merchantUser = merchantUserRepository.findById(merchantId).orElseThrow(() -> new FreewayException("Merchant not found"));
        BrandDashboardResponse brandDashboardResponse = new BrandDashboardResponse();
        brandDashboardResponse.setMerchantId(merchantUser.getId().toString());
        brandDashboardResponse.setMerchantDetails(merchantUser);
        brandDashboardResponse.setStoreCount(1);
        if (Objects.nonNull(merchantUser.getParams()) && !CollectionUtils.isEmpty(merchantUser.getParams().getBrandIds())) {
            String brandId = merchantUser.getParams().getBrandIds().get(0);
            brandDashboardResponse.setBrand(brandBO.findById(brandId));
            brandDashboardResponse.setBrandId(brandId);
        }
        return brandDashboardResponse;
    }


    @Cacheable(value = "cacheManagerForAnyExpiry", key = "'MERCHANT_INFO' + #merchantNameOrDisplayId")
    public HashMap<String, String> getUserByNameOrDisplayId(String merchantNameOrDisplayId) {
        MerchantUser merchantUser = merchantUserRepository.findByDisplayId(merchantNameOrDisplayId).orElse(null);

        HashMap<String, String> merchantUserInfo = new HashMap<>();
        if (Objects.isNull(merchantUser)) {
            List<MerchantUser> merchantUsers = merchantDAO.getMerchantsByShopNameWithRegex(merchantNameOrDisplayId);
            if (CollectionUtils.isEmpty(merchantUsers)) {
                throw new MerchantException(MerchantResponseCode.INVALID_MERCHANT);
            }
            merchantUsers.forEach(mu -> merchantUserInfo.put(mu.getShopName(), mu.getId().toString()));
            return merchantUserInfo;
        }

        if (StringUtils.hasText(merchantUser.getShopName())) {
            merchantUserInfo.put(merchantUser.getShopName(), merchantUser.getId().toString());
            return merchantUserInfo;
        } else {
            throw new MerchantException(MerchantResponseCode.INVALID_MERCHANT);
        }
    }

    public PartnerInfoResponse getPartnerInfo(String partner) {
        return paymentOpsService.getPartnerInfo(partner);
    }

    public PartnerInfoResponse validatePartner(String partner, MerchantUser mu) {
        PartnerInfoResponse paymentOpsResponse = getPartnerInfo(partner);
        LOGGER.info("response partnerInfo: {}", paymentOpsResponse);
        if ((Util.isNotNull(mu.getPartner()) && Util.isNotNull(paymentOpsResponse))) {
            if (Util.isNotNull(paymentOpsResponse.getPartnerInfo()) && StringUtils.hasText(paymentOpsResponse.getPartnerInfo().getCode())) {
                if (mu.getPartner().equals(paymentOpsResponse.getPartnerInfo().getCode())) {
                    return paymentOpsResponse;
                }
            }
        }
        return null;
    }

    public SecurityCredentials updateSecurityCredentialsWithTenant(MerchantUser mu, String partner) {
        PartnerInfoResponse partnerInfoResponse = validatePartner(partner, mu);
        if (Util.isNotNull(partnerInfoResponse)) {
            if (Objects.isNull(partnerInfoResponse.getPartnerInfo()) || Objects.isNull(partnerInfoResponse.getPartnerInfo().getConfigs()) ||
                    Objects.isNull(partnerInfoResponse.getPartnerInfo().getConfigs().getCredentials()) || !StringUtils.hasText(partnerInfoResponse.getPartnerInfo().getConfigs().getCredentials().getSecretKey())
                    || !StringUtils.hasText(partnerInfoResponse.getPartnerInfo().getConfigs().getCredentials().getIvKey())) {
                throw new FreewayException("Credentials are missing.");
            }
            return new SecurityCredentials(partnerInfoResponse.getPartnerInfo().getConfigs().getCredentials().getSecretKey(), partnerInfoResponse.getPartnerInfo().getConfigs().getCredentials().getIvKey(), null, null, null, null);
        } else {
            LOGGER.error("merchant partner mismatch with the requested tenant api");
            throw new MerchantException(MerchantResponseCode.INVALID_PARTNER);
        }
    }

    public MerchantUser getMerchantUserByKey(String key) {
        return merchantUserRepository.findByXApiKey(key).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public String getNextStage(MerchantUser merchantUser) {
        String partnerValue = payment_PARTNER;
        String mobile = merchantUser.getMobile();
        if (MerchantStatus.registered.name().equals(merchantUser.getStatus()) && !CollectionUtils.isEmpty(merchantUser.getPartners())) {
            partnerValue = merchantUser.getPartners().get(0);
        } else {
            if (StringUtils.hasText(merchantUser.getPartner())) {
                partnerValue = merchantUser.getPartner();
                mobile = mobile.split("_")[0];
            }
        }
        com.freewayemi.merchant.dto.request.PartnerInfo partner = referralCodeBO.getPartnerInfo(partnerValue);
        if (!payment_PARTNER.equals(partnerValue)) {
            MerchantUser childMerchant = merchantUserRepository.findByMobileAndIsDeleted(mobile + "_" + partner.getCode(), false).orElseThrow(() -> new FreewayException("Merchant user is not found"));
            if (Objects.nonNull(childMerchant)) {
                return Util.getNextOnboardingStage(partner, childMerchant.getStage());
            }
        }
        return Util.getNextOnboardingStage(partner, merchantUser.getStage());
    }

    public List<OnBoardingDocument> getMerchantOnboardingDocuments(String merchantId, Boolean settlement) {
        MerchantUser merchantUser = getUserByMerchantIdOrDisplayId(merchantId);
        String partnerValue = payment_PARTNER;
        if (MerchantStatus.registered.name().equals(merchantUser.getStatus()) && !CollectionUtils.isEmpty(merchantUser.getPartners())) {
            partnerValue = merchantUser.getPartners().get(0);
            MerchantUser childMerchant = merchantUserRepository.findByMobileAndIsDeleted(merchantUser.getMobile() + "_" + partnerValue, false).orElseThrow(() -> new FreewayException("Merchant user is not found"));
        }
        com.freewayemi.merchant.dto.request.PartnerInfo partner = referralCodeBO.getPartnerInfo(partnerValue);
        if (StringUtils.hasText(merchantUser.getOwnership())) {
            if (Boolean.TRUE.equals(settlement)) {
                return onBoardingDocumentRepository.findByPartnerAndBusinessTYpeForSettlement(partner.getCode(), merchantUser.getOwnership(), settlement)
                        .orElse(new ArrayList<>());
            }
            return onBoardingDocumentRepository.findByPartnerAndBusinessTYpeForOnboarding(partner.getCode(), merchantUser.getOwnership(), Boolean.TRUE)
                    .orElse(new ArrayList<>());
        }
        return new ArrayList<>();
    }

    public List<PartnerData> getPartnersView(MerchantUser merchantUser) {
        List<PartnerData> partnerDataList = new ArrayList<>();
        if (Objects.nonNull(merchantUser.getPartners()) && !CollectionUtils.isEmpty(merchantUser.getPartners())) {
            PartnerData partnerData = PartnerData.builder().partner(payment_PARTNER).status(merchantUser.getStatus()).display(STATUS_LIST.contains(merchantUser.getStatus())).build();
            if (Objects.nonNull(merchantUser.getSettlementConfig()) && StringUtils.hasText(merchantUser.getSettlementConfig().getSettlementDocumentsStatus())) {
                partnerData.setSettlementDocumentsStatus(merchantUser.getSettlementConfig().getSettlementDocumentsStatus());
            }
            partnerDataList.add(partnerData);
            for (String partner : merchantUser.getPartners()) {
                MerchantUser childMerchant = getUserByMobile(merchantUser.getMobile() + "_" + partner);
                if (Objects.nonNull(childMerchant)) {
                    PartnerData childPartnerData = PartnerData.builder().partner(partner).status(childMerchant.getStatus()).display(STATUS_LIST.contains(childMerchant.getStatus())).build();
                    if (Objects.nonNull(childMerchant.getSettlementConfig()) && StringUtils.hasText(childMerchant.getSettlementConfig().getSettlementDocumentsStatus())) {
                        childPartnerData.setSettlementDocumentsStatus(childMerchant.getSettlementConfig().getSettlementDocumentsStatus());
                    }
                    partnerDataList.add(childPartnerData);
                }
            }
        } else if(StringUtils.hasText(merchantUser.getParentMerchant())) {
            MerchantUser parentMerchant = merchantUserRepository.findById(merchantUser.getParentMerchant()).orElse(null);
            if (Objects.nonNull(parentMerchant)) {
                PartnerData partnerData = PartnerData.builder().partner(payment_PARTNER).status(parentMerchant.getStatus()).display(STATUS_LIST.contains(parentMerchant.getStatus())).build();
                if (Objects.nonNull(parentMerchant.getSettlementConfig()) && StringUtils.hasText(parentMerchant.getSettlementConfig().getSettlementDocumentsStatus())) {
                    partnerData.setSettlementDocumentsStatus(parentMerchant.getSettlementConfig().getSettlementDocumentsStatus());
                }
                partnerDataList.add(partnerData);
            }
            PartnerData partnerData = PartnerData.builder().partner(merchantUser.getPartner()).status(merchantUser.getStatus()).display(STATUS_LIST.contains(merchantUser.getStatus())).build();
            if (Objects.nonNull(merchantUser.getSettlementConfig()) && StringUtils.hasText(merchantUser.getSettlementConfig().getSettlementDocumentsStatus())) {
                partnerData.setSettlementDocumentsStatus(merchantUser.getSettlementConfig().getSettlementDocumentsStatus());
            }
            partnerDataList.add(partnerData);
        }else{
            PartnerData partnerData = PartnerData.builder().partner(payment_PARTNER).status(merchantUser.getStatus()).display(STATUS_LIST.contains(merchantUser.getStatus())).build();
            if (Objects.nonNull(merchantUser.getSettlementConfig()) && StringUtils.hasText(merchantUser.getSettlementConfig().getSettlementDocumentsStatus())) {
                partnerData.setSettlementDocumentsStatus(merchantUser.getSettlementConfig().getSettlementDocumentsStatus());
            }
            partnerDataList.add(partnerData);
        }
        return partnerDataList;
    }

    public BasicResponse saveSettlementConfig(String merchantId) {
        MerchantUser merchantUser = getUserByMerchantIdOrDisplayId(merchantId);
        if (Util.isNotNull(merchantUser.getPartners())) {
            if (!merchantUser.getPartners().isEmpty()) {
                MerchantUser childMerchant = getUserByMobile(merchantUser.getMobile() + "_" + merchantUser.getPartners().get(0));
                return updateSettlementConfg(childMerchant);
            }
        }
        return updateSettlementConfg(merchantUser);
    }

    private BasicResponse updateSettlementConfg(MerchantUser merchantUser) {
        SettlementConfig settlementConfig = merchantUser.getSettlementConfig();
        if (Objects.isNull(settlementConfig)) {
            settlementConfig = SettlementConfig.builder().build();
        }
        settlementConfig.setSettlementDocumentsStatus("processing");
        merchantUser.setSettlementConfig(settlementConfig);
        triggerNotificationForOps(merchantUser);
        merchantUserRepository.save(merchantUser);
        return new BasicResponse("Successfully updated settlement status", SUCCESS, 0, "");
    }

    private void triggerNotificationForOps(MerchantUser merchantUser) {
        String subject = "Merchant settlement documents update";
        if (!isProduction) {
            subject = "DEV: Merchant settlement documents update";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
                .withZone(ZoneId.systemDefault());

        String submissionDate = formatter.format(Instant.now());
        String content = "The following merchant has submitted the documents for settlement<br><br/>" +
                "Display Id: " + merchantUser.getDisplayId() + " <br></br>" +
                "Merchant Name: " + merchantUser.getFirstName() + " " + merchantUser.getLastName() + " <br></br>" +
                "partner Code: " + merchantUser.getPartner() + " <br></br>" +
                "Submission Date:  " + submissionDate + " <br></br>";

        MerchantConfigs merchantConfigs = merchantConfigsRepository.findByLabel("operations_email_list").orElse(null);

        String emailIds = "";
        List<String> emails = new ArrayList<>();
        if (Objects.nonNull(merchantConfigs) && Objects.nonNull(merchantConfigs.getValues())) {
            emails = merchantConfigs.getValues();
        }
        for (String email : emails) {
            if (StringUtils.hasText(emailIds)) {
                emailIds = emailIds + "," + email;
            } else {
                emailIds = email;
            }
        }

        EmailRequest emailRequest = EmailRequest.builder().toEmailIds(emailIds).subject(subject).htmlBody(content).build();
        emailService.sendEmail(emailRequest);
    }


    public SettlementConfig getSettlementConfig(String merchantId) {
        MerchantUser merchantUser = getUserByMerchantIdOrDisplayId(merchantId);
        if (Util.isNotNull(merchantUser.getPartners())) {
            if (!merchantUser.getPartners().isEmpty()) {
                MerchantUser childMerchant = getUserByMobile(merchantUser.getMobile() + "_" + merchantUser.getPartners().get(0));
                return getSettlementConfigOrDefaultConfig(childMerchant);
            }
        }
        return getSettlementConfigOrDefaultConfig(merchantUser);
    }

    private SettlementConfig getSettlementConfigOrDefaultConfig(MerchantUser merchantUser) {
        SettlementConfig settlementConfig = merchantUser.getSettlementConfig();
        if (Objects.nonNull(settlementConfig) && !StringUtils.hasText(settlementConfig.getSettlementDocumentsStatus())) {
            settlementConfig.setSettlementDocumentsStatus("approved");
        }
        return settlementConfig;
    }
}

