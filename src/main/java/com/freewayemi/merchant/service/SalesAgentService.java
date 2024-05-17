package com.freewayemi.merchant.service;

import com.freewayemi.merchant.bo.*;
import com.freewayemi.merchant.commons.bo.NtbCoreService;
import com.freewayemi.merchant.commons.dto.Address;
import com.freewayemi.merchant.commons.dto.BrandMerchantDataResponse;
import com.freewayemi.merchant.commons.dto.karza.BankAccVerificationDto;
import com.freewayemi.merchant.commons.dto.karza.GstAuthReq;
import com.freewayemi.merchant.commons.entity.Params;
import com.freewayemi.merchant.commons.entity.SettlementConfig;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.type.CardTypeEnum;
import com.freewayemi.merchant.commons.type.SettlementCycleEnum;
import com.freewayemi.merchant.commons.type.SettlementTypeEnum;
import com.freewayemi.merchant.commons.utils.DateUtil;
import com.freewayemi.merchant.commons.utils.paymentConstants;
import com.freewayemi.merchant.commons.utils.ValidationUtil;
import com.freewayemi.merchant.dao.MerchantDAO;
import com.freewayemi.merchant.dto.BankAccount.BankAccountAuthReq;
import com.freewayemi.merchant.dto.BankAccount.BankAccountAuthResp;
import com.freewayemi.merchant.dto.SecurityDetails;
import com.freewayemi.merchant.dto.request.AddMerchantRequest;
import com.freewayemi.merchant.dto.request.CreateEventRequest;
import com.freewayemi.merchant.dto.response.BasicResponse;
import com.freewayemi.merchant.dto.response.ProviderMasterConfigResponse;
import com.freewayemi.merchant.dto.response.SchemeConfigResponse;
import com.freewayemi.merchant.dto.sales.*;
import com.freewayemi.merchant.entity.MerchantIncentive;
import com.freewayemi.merchant.entity.*;
import com.freewayemi.merchant.enums.Status;
import com.freewayemi.merchant.pojos.pan.PanDetailsRequest;
import com.freewayemi.merchant.pojos.pan.PanDetailsResponse;
import com.freewayemi.merchant.repository.BrandGstRepository;
import com.freewayemi.merchant.repository.MerchantConfigsRepository;
import com.freewayemi.merchant.repository.MerchantGstAuthInfoRepository;
import com.freewayemi.merchant.type.MerchantConstants;
import com.freewayemi.merchant.type.Source;
import com.freewayemi.merchant.utils.Constants;
import com.freewayemi.merchant.utils.MerchantCommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.freewayemi.merchant.commons.utils.paymentConstants.*;
import static com.freewayemi.merchant.utils.Constants.*;

@Service
public class SalesAgentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SalesAgentService.class);

    private final AdminAuthUserBO adminAuthUserBO;
    private final MerchantUserBO merchantUserBO;
    private final MerchantIncentiveBO merchantIncentiveBO;
    private final IncentiveBO incentiveBO;
    private final NotificationBO notificationBO;
    private final PaymentOpsService paymentOpsService;

    private MerchantTransactionBO merchantTransactionBO;

    private final MerchantLocationBO merchantLocationBO;

    private final BrandGstRepository brandGstRepository;

    private final MerchantConfigsRepository merchantConfigsRepository;
    private final NtbCoreService ntbCoreService;
    private final BrandBO brandBO;
    private final AuthCommonService authCommonService;
    private final MerchantGstAuthInfoRepository merchantGstAuthInfoRepository;
    private final MerchantDAO merchantDAO;
    private final MerchantConfigBO merchantConfigBO;

    private final MerchantVisibilitiesBO merchantVisibilitiesBO;

    private final String ACCOUNT_VERIFICATION_REGEX = "^[A-Z]{4}0[A-Z0-9]{6}$";
    private final boolean isProduction;
    private final BrandMerchantDataBO brandMerchantDataBO;
    private final KYCLinkBO kycLinkBo;
    private final ReportService reportService;
    private final HelperService helperService;

    private final DigitalIdentityService digitalIdentityService;

    @Autowired
    public SalesAgentService(AdminAuthUserBO adminAuthUserBO, MerchantUserBO merchantUserBO,
                             MerchantIncentiveBO merchantIncentiveBO, IncentiveBO incentiveBO,
                             NotificationBO notificationBO, PaymentOpsService paymentOpsService,
                             SalesAgentBO salesAgentBO, MerchantLocationBO merchantLocationBO,
                             RestTemplate restTemplate, BrandGstRepository brandGstRepository,
                             MerchantConfigsRepository merchantConfigsRepository, NtbCoreService ntbCoreService,
                             MerchantProperitiesBO merchantProperitiesBO,
                             BrandBO brandBO, AuthCommonService authCommonService,
                             MerchantGstAuthInfoRepository merchantGstAuthInfoRepository, MerchantDAO merchantDAO,
                             MerchantConfigBO merchantConfigBO, @Value("${payment.deployment.env}") String env,
                             BrandMerchantDataBO brandMerchantDataBO,
                             KYCLinkBO kycLinkBo,
                             ReportService reportService, HelperService helperService, MerchantTransactionBO merchantTransactionBO,
                             MerchantVisibilitiesBO merchantVisibilitiesBO, DigitalIdentityService digitalIdentityService) {
        this.adminAuthUserBO = adminAuthUserBO;
        this.merchantUserBO = merchantUserBO;
        this.merchantIncentiveBO = merchantIncentiveBO;
        this.incentiveBO = incentiveBO;
        this.notificationBO = notificationBO;
        this.paymentOpsService = paymentOpsService;
        this.merchantLocationBO = merchantLocationBO;
        this.brandGstRepository = brandGstRepository;
        this.merchantConfigsRepository = merchantConfigsRepository;
        this.ntbCoreService = ntbCoreService;
        this.brandBO = brandBO;
        this.authCommonService = authCommonService;
        this.merchantGstAuthInfoRepository = merchantGstAuthInfoRepository;
        this.merchantDAO = merchantDAO;
        this.merchantConfigBO = merchantConfigBO;
        this.isProduction = paymentConstants.PRODENV.equals(env);
        this.brandMerchantDataBO = brandMerchantDataBO;
        this.kycLinkBo = kycLinkBo;
        this.reportService = reportService;
        this.helperService = helperService;
        this.merchantTransactionBO = merchantTransactionBO;
        this.merchantVisibilitiesBO = merchantVisibilitiesBO;
        this.digitalIdentityService = digitalIdentityService;
    }

    public SalesAnalytics getAnalaytics(String deviceToken, String leadOwnerId, String type,
                                        HttpServletRequest httpServletRequest) {
        AdminAuthUser adminAuthUser = adminAuthUserBO.findById(leadOwnerId);
        if (Objects.isNull(adminAuthUser)) {
            throw new FreewayException("User not found");
        }
        adminAuthUser.setDeviceToken(deviceToken);
        adminAuthUser.setLastModifiedDate(Instant.now());
        adminAuthUserBO.createAdminUser(adminAuthUser);
        Instant fromDate = DateUtil.startTimeCurrentDay(Instant.now());
        Instant endDate = DateUtil.endTimeOfTheDay(fromDate);
        Instant date = Instant.now();
        String from = DateUtil.getToday("yyyy-MM-dd");
        TransactionOpsRequest transactionOpsRequest = TransactionOpsRequest.builder().leadOwnerId(leadOwnerId).fromDate(from).toDate(from).transactionStatus("success").build();
        TransactionVolumeInfo dailyTransactionVolumeInfo = merchantTransactionBO.getTransactionVolume(transactionOpsRequest);
        from = DateUtil.getFormattedDate(Date.from(DateUtil.getFirstDayOfMonth(date)), "yyyy-MM-dd");
        String to = DateUtil.getFormattedDate(Date.from(DateUtil.getLastDayOfMonth(date)), "yyyy-MM-dd");
        transactionOpsRequest = TransactionOpsRequest.builder().leadOwnerId(leadOwnerId).fromDate(from).toDate(to).transactionStatus("success").build();
        TransactionVolumeInfo monthlyTransactionVolumeInfo = merchantTransactionBO.getTransactionVolume(transactionOpsRequest);
        List<TransactionModel> transactionModels = new ArrayList<>();
        TransactionDataResponse recentTransactions = paymentOpsService.getTransactions(TransactionOpsRequest.builder().leadOwnerId(leadOwnerId).pageNo(0).pageSize(5).build());
        if (Objects.nonNull(recentTransactions)) {
            transactionModels = recentTransactions.getTransactions();
        }
        switch (type) {
            case "merchant":
                MerchantStatus merchantStatus = MerchantStatus.builder()
                        .approved(merchantUserBO.getStatusLeadsCount(leadOwnerId, "approved"))
                        .reSubmission(merchantUserBO.getStatusLeadsCount(leadOwnerId, "resubmission"))
                        .profiled(merchantUserBO.getStatusLeadsCount(leadOwnerId, "profiled"))
                        .registered(merchantUserBO.getStatusLeadsCount(leadOwnerId, "registered"))
                        .build();
                return SalesAnalytics.builder().merchantStatus(merchantStatus).build();
            case "home":
                List<MerchantUser> merchantUserList = merchantUserBO.getMerchantUsers(leadOwnerId, 0, 5);
                List<MerchantInfo> recentMerchants = new ArrayList<>();
                date = Instant.now();
                from = DateUtil.getToday("yyyy-MM-dd");
                transactionOpsRequest = TransactionOpsRequest.builder().leadOwnerId(leadOwnerId).fromDate(from).toDate(from).build();
                SalesDataResponse dailyTransactionData =
                        paymentOpsService.salesDataByLeadOwnerId(deviceToken, transactionOpsRequest);
                HomeAnalytics homeAnalytics = HomeAnalytics.builder()
                        .name(adminAuthUser.getName())
                        .recentTxns(transactionModels)
                        .latestAppVer(17)
                        .nonActivatedMerchants(0)
                        .merchants(merchantUserBO.getMerchantsCountMonthly(leadOwnerId, DateUtil.getFirstDayOfMonth(date), DateUtil.getLastDayOfMonth(date)))
                        .merchantsToday(merchantUserBO.getMerchantsCount(leadOwnerId, fromDate, endDate))
                        .build();
                if (Objects.nonNull(dailyTransactionData) && Objects.nonNull(dailyTransactionData.getSalesData())) {
                    SalesData transactionCountAndVolumeResponse = dailyTransactionData.getSalesData();
                    homeAnalytics.setVolumeToday(transactionCountAndVolumeResponse.getVolume());
                    homeAnalytics.setTxnsToday(transactionCountAndVolumeResponse.getCount());
                }
                from = DateUtil.getFormattedDate(Date.from(DateUtil.getFirstDayOfMonth(date)), "yyyy-MM-dd");
                to = DateUtil.getFormattedDate(Date.from(DateUtil.getLastDayOfMonth(date)), "yyyy-MM-dd");
                transactionOpsRequest = TransactionOpsRequest.builder().leadOwnerId(leadOwnerId).fromDate(from).toDate(to).build();
                SalesDataResponse monthlyTransactionData =
                        paymentOpsService.salesDataByLeadOwnerId(deviceToken, transactionOpsRequest);
                if (Objects.nonNull(monthlyTransactionData) && Objects.nonNull(monthlyTransactionData.getSalesData())) {
                    SalesData transactionCountAndVolumeResponse = monthlyTransactionData.getSalesData();
                    homeAnalytics.setVolume(transactionCountAndVolumeResponse.getVolume());
                    homeAnalytics.setTxns(transactionCountAndVolumeResponse.getCount());
                }

                TransactionDataResponse transactionDataResponseMonthly = paymentOpsService.getMerchantList(
                        TransactionOpsRequest.builder().leadOwnerId(leadOwnerId).fromDate(from).
                                toDate(to)
                                .transactionStatus("success")
                                .build());
                if (Objects.nonNull(transactionDataResponseMonthly) && Objects.nonNull(transactionDataResponseMonthly.getMerchants())) {
                    homeAnalytics.setTransactingMerchants(transactionDataResponseMonthly.getMerchants().size());
                }
                TransactionDataResponse transactionDataResponseDaily = paymentOpsService.getMerchantList(
                        TransactionOpsRequest.builder().leadOwnerId(leadOwnerId).fromDate(from).
                                toDate(from)
                                .transactionStatus("success")
                                .build());
                if (Objects.nonNull(transactionDataResponseDaily) && Objects.nonNull(transactionDataResponseDaily.getMerchants())) {
                    homeAnalytics.setTransactingMerchantsToday(transactionDataResponseDaily.getMerchants().size());
                }
                for (MerchantUser merchantUser : merchantUserList) {
                    recentMerchants.add(merchantUserBO.getDecorateMerchant(merchantUser, null));
                }
                homeAnalytics.setRecentMerchants(recentMerchants);
                return SalesAnalytics.builder().homeAnalytics(homeAnalytics).build();
            case "homev1":
                int transactingMerchantMonthly = 0;
                long onboarded = merchantUserBO.getMerchantsOnBoardedCountTillNow(leadOwnerId, Boolean.TRUE);
                TransactionDataResponse transactionDataResponseMonthlyForv1 = paymentOpsService.getMerchantList(
                        TransactionOpsRequest.builder().leadOwnerId(leadOwnerId).fromDate(from).
                                toDate(to)
                                .transactionStatus("success")
                                .build());
                if (Objects.nonNull(transactionDataResponseMonthlyForv1) && Objects.nonNull(transactionDataResponseMonthlyForv1.getMerchants())) {
                    transactingMerchantMonthly = transactionDataResponseMonthlyForv1.getMerchants().size();
                }
                HomeV1Analytics homeV1Analytics = HomeV1Analytics.builder()
                        .name(adminAuthUser.getName())
                        .targetOnboardingPerDay(4L)
                        .achievedOnboardingPerDay(
                                merchantUserBO.getMerchantsOnBoardedCount(leadOwnerId, fromDate, endDate, Boolean.TRUE))
                        .pending(Pending.builder()
                                .reSubmission(merchantUserBO.getStatusLeadsCount(leadOwnerId, "resubmission"))
                                .onboarding(merchantUserBO.getOnBoardingMerchantsCount(leadOwnerId, "approved",
                                        Boolean.TRUE))
                                .activations(merchantUserBO.getMerchantActivationCounts(leadOwnerId, Boolean.TRUE,
                                        Boolean.TRUE))
                                .build())
                        .monthly(Monthly.builder()
                                .onboarded(merchantUserBO.getMerchantOnBoardingCountTillMTD(leadOwnerId, DateUtil.getFirstDayOfMonth(date), DateUtil.getLastDayOfMonth(date),
                                        Boolean.TRUE))
                                .activated(merchantUserBO.getMerchantActivationCountTillMTD(leadOwnerId, DateUtil.getFirstDayOfMonth(date), DateUtil.getLastDayOfMonth(date),
                                        Boolean.TRUE))
                                .build())
                        .displayRewards(Boolean.TRUE)
                        .teamMemberCount(adminAuthUserBO.getCountByReporter(leadOwnerId))
                        .transactionVolume(TransactionVolume.builder()
                                .dailyTarget(Objects.nonNull(adminAuthUser.getTarget()) ? adminAuthUser.getTarget()
                                        .getVolTargetFTD() : 150000)
                                .monthlyTarget(Objects.nonNull(adminAuthUser.getTarget()) ? adminAuthUser.getTarget()
                                        .getVolTargetMTD() : 3600000)
                                .dailyAchieved(Objects.nonNull(dailyTransactionVolumeInfo.getVolume()) ? dailyTransactionVolumeInfo.getVolume().getAchieved() : "")
                                .monthlyAchieved(Objects.nonNull(monthlyTransactionVolumeInfo.getVolume()) ? monthlyTransactionVolumeInfo.getVolume().getAchieved() : "")
                                .onboardedMerchants(onboarded)
                                .transactingMerchants(transactingMerchantMonthly)
                                .nonTransactingMerchants(onboarded - transactingMerchantMonthly)
                                .build())
                        .build();
                return SalesAnalytics.builder().homeV1Analytics(homeV1Analytics).build();
        }
        throw new FreewayException("Something went wrong");
    }

    public List<Brand> getBrands() {
        return brandBO.getBrands();
    }

    public List<MerchantInfo> getMerchantInfos(String mobile, String gst, String top, int skip, int limit,
                                               HttpServletRequest httpServletRequest) {
        List<MerchantUser> merchantUsers = new ArrayList<>();
        if (!Objects.equals(mobile, "null") && StringUtils.hasText(mobile)) {
            merchantUsers = merchantUserBO.getMerchantUsersByMobile(mobile);
        } else if (!Objects.equals(gst, "null") && StringUtils.hasText(gst)) {
            merchantUsers = merchantUserBO.getMerchantUsersByGST(gst);
        } else if (!Objects.equals(top, "null") && StringUtils.hasText(top)) {
            SecurityDetails securityDetails = authCommonService.getMerchantId(httpServletRequest);
            merchantUsers =
                    merchantUserBO.getMerchantUsers(securityDetails.getCredentials().get("storeUserId"), skip, limit);
        }
        List<MerchantInfo> merchantInfos = new ArrayList<>();
        for (MerchantUser merchantUser : merchantUsers) {
            MerchantInfo merchantInfo = merchantUserBO.getDecorateMerchant(merchantUser, null);
            if ((!Objects.equals(mobile, "null") && StringUtils.hasText(mobile)) ||
                    !Objects.equals(gst, "null") && StringUtils.hasText(gst)) {
                if (Objects.nonNull(merchantInfo) && "approved".equals(merchantInfo.getStatus()) && Boolean.TRUE.equals(merchantInfo.getIsOnboarded())) {
                    merchantInfo.setStatus("onboarded");
                }
                if (Objects.nonNull(merchantInfo) && STATUS_MAP.containsKey(merchantInfo.getStatus())) {
                    merchantInfo.setStatus(STATUS_MAP.get(merchantInfo.getStatus()));
                }
            }
            merchantInfos.add(merchantInfo);
        }
        LOGGER.info("merchantIno:{}", merchantInfos);
        return merchantInfos;
    }

    public List<MerchantInfo> getMerchantInfosv2(String text, String status, String searchType, int skip, int limit,
                                                 HttpServletRequest httpServletRequest) {
        List<MerchantInfo> merchantInfos = new ArrayList<>();
        if (!Objects.equals(searchType, "null") && searchType.equals("nonActivatedMerchants")) {
            return merchantInfos;
        }
        List<MerchantUser> merchantUsers;
        SecurityDetails securityDetails = authCommonService.getMerchantId(httpServletRequest);
        merchantUsers =
                merchantDAO.getMerchantUsersWithFilter(securityDetails.getCredentials().get("storeUserId"), text,
                        status, skip, limit);
        for (MerchantUser merchantUser : merchantUsers) {
            merchantInfos.add(merchantUserBO.getDecorateMerchant(merchantUser, null));
        }
        return merchantInfos;
    }

    public MerchantInfoAndCountResponse getMerchantInfosv3(String text, String status, String searchType,
                                                           String fieldNe, String transacting, int skip, int limit,
                                                           HttpServletRequest httpServletRequest) {
        if (MerchantCommonUtil.isNotEmptyString(searchType) && searchType.equals("nonActivatedMerchants")) {
            return MerchantInfoAndCountResponse.builder().build();
        }
        MerchantUserAndCountResponse merchantUserAndCountResponse = null;
        SecurityDetails securityDetails = authCommonService.getMerchantId(httpServletRequest);
        Instant monthStartDate = DateUtil.getFirstDayOfMonth(Instant.now());
        String date = DateUtil.getFormattedDate(Date.from(monthStartDate), "yyyy-MM-dd");
        String leadOwnerId = securityDetails.getCredentials().get("storeUserId");
        if (StringUtils.hasText(transacting) && !Objects.equals(transacting, "null")) {
            if ("true".equals(transacting)) {
                merchantUserAndCountResponse = merchantUserBO.getUniqueMerchantsByLeadOwner(leadOwnerId, date, Boolean.TRUE, new ArrayList<>());
            } else {
                List<String> ids = merchantDAO.getUniqueMerchantIds(leadOwnerId);
                merchantUserAndCountResponse = merchantUserBO.getUniqueMerchantsByLeadOwner(leadOwnerId, date, Boolean.FALSE, ids);
            }
        } else {
            merchantUserAndCountResponse =
                    merchantDAO.getMerchantInfosv3(leadOwnerId, text, status,
                            searchType, fieldNe, transacting, skip, limit);
        }
        List<MerchantInfo> merchantInfos = new ArrayList<>();
        for (MerchantUser merchantUser : merchantUserAndCountResponse.getMerchantUsers()) {
            MerchantInfo merchantInfo = merchantUserBO.getDecorateMerchant(merchantUser, null);
            if (Objects.nonNull(merchantInfo) && Boolean.TRUE.equals(merchantInfo.getIsOnboarded())) {
                merchantInfo.setStatus("onboarded");
            }
            merchantInfos.add(merchantInfo);
        }
        return MerchantInfoAndCountResponse.builder()
                .merchantInfos(merchantInfos)
                .merchantCount(merchantUserAndCountResponse.getMerchantCount())
                .build();
    }

    public List<MerchantInfo> searchMerchants(String params, HttpServletRequest httpServletRequest) {
        SecurityDetails securityDetails = authCommonService.getMerchantId(httpServletRequest);
        LOGGER.info(params);
        List<MerchantUser> merchantUsers =
                merchantDAO.searchMerchants(securityDetails.getCredentials().get("storeUserId"), params);
        LOGGER.info("merchant Users: {}", merchantUsers);
        List<MerchantInfo> merchantInfos = new ArrayList<>();
        for (MerchantUser merchantUser : merchantUsers) {
            merchantInfos.add(merchantUserBO.getDecorateMerchant(merchantUser, null));
        }
        return merchantInfos;
    }

    private double deductAmount(int amount, String cardType) {
        double debitCardPercentage = 0.17;
        double creditCardPercentage = 0.21;
        if ("debit".equals(cardType)) {
            return debitCardPercentage * amount;
        } else if ("credit".equals(cardType)) {
            return creditCardPercentage * amount;
        }
        return 0;
    }

    public EmiCalculator emiCalculator(int amount, String cardType) {
        double interest = 0.5;
        MerchantSettlement merchantSettlement = MerchantSettlement.builder()
                .transactionAmount(amount)
                .settlementAmount(deductAmount(amount, cardType))
                .chargeDeduction(amount - deductAmount(amount, cardType))
                .build();
        List<Integer> emiMonths = Arrays.asList(3, 6, 9, 12, 18, 24);
        List<CustomerEMI> customerEMIS = new ArrayList<>();
        for (Integer month : emiMonths) {
            customerEMIS.add(
                    CustomerEMI.builder().emiAmount(amount / month).intAmount(interest * amount).tenure(month).build());
        }
        return EmiCalculator.builder().merchantSettlement(merchantSettlement).customerEMIs(customerEMIS).build();
    }

    public BasicResponse merchantOnboardingStatus(TransactionOpsRequest transactionCountReq) throws ParseException {
        MerchantUser merchantUser = merchantUserBO.getMerchantUserByIdOrDisplayIdOrMobile(transactionCountReq.getMerchantId());
        if(Objects.isNull(merchantUser)){
            return BasicResponse.builder()
                    .status(Status.ONBOARDING_PENDING)
                    .statusMsg("Either QR is not activated or mobile app is not installed")
                    .build();
        }
        long count = 0;
        String monthStart = DateUtil.getFormattedDate(Date.from(DateUtil.getFirstDayOfMonth(Instant.now())), "yyyy-MM-dd");
        String monthEnd = DateUtil.getFormattedDate(Date.from(DateUtil.getLastDayOfMonth(Instant.now())), "yyyy-MM-dd");
        do {
            LOGGER.info("monthStart: {}", monthStart);
            transactionCountReq.setFromDate(monthStart);
            transactionCountReq.setToDate(monthEnd);
            transactionCountReq.setTransactionStatus("success");
            TransactionDataResponse transactionDataResponse = paymentOpsService.getTransactionCount(transactionCountReq);
            if (Objects.nonNull(transactionDataResponse)) {
                count = transactionDataResponse.getTransactionCount();
            }
            if (DateUtil.getDate(monthStart, "yyyy-MM-dd").before(Date.from(merchantUser.getCreatedDate()))) {
                break;
            }
            monthEnd = DateUtil.getFormattedDate(Date.from(DateUtil.getLastDayOfPreviousMonth(DateUtil.getDate(monthStart, "yyyy-MM-dd").toInstant())), "yyyy-MM-dd");
            monthStart = DateUtil.getFormattedDate(Date.from(DateUtil.getFirstDayOfPreviousMonth(DateUtil.getDate(monthStart, "yyyy-MM-dd").toInstant())), "yyyy-MM-dd");
        } while (count == 0);
        if (count > 0) {
            count = merchantUserBO.findMerchantsCountNotActivated(transactionCountReq.getMerchantId(), "approved", Boolean.TRUE, Boolean.TRUE);
            if (count > 0) {
                merchantDAO.updateOnboardingStatus(transactionCountReq.getMerchantId(), Boolean.TRUE);
                return BasicResponse.builder().status(Status.SUCCESS).build();
            }
        }
        return BasicResponse.builder()
                .status(Status.ONBOARDING_PENDING)
                .statusMsg("Either QR is not activated or mobile app is not installed")
                .build();
    }

    private Map<String, Map<String, Object>> reSubmissionAction(String reSubmissionReason) {
        Map<String, Map<String, Object>> formatReasons = new HashMap<>();
        if (MerchantCommonUtil.isNotEmptyString(reSubmissionReason)) {
            for (String reason : reSubmissionReason.split(",")) {
                Map<String, String> reSubmissionMap =
                        merchantConfigBO.findMerchantConfigByLabel("resubmission_reasons");
                if (reSubmissionMap.containsKey(reason)) {
                    Map<String, Object> data = RESUBMISSION_ACTIONS_DETAILS.get(reSubmissionMap.get(reason));
                    data.put("action", reSubmissionMap.get(reason));
                    formatReasons.put(reSubmissionMap.get(reason), data);
                }
            }
        }
        return formatReasons;
    }

    public MerchantInfo getMerchant(String merchantId, Boolean isCoBranding) throws ParseException {
        TransactionOpsRequest transactionCountReq = TransactionOpsRequest.builder().merchantId(merchantId).build();
        LOGGER.info("transactionCountReq: {}: {}", transactionCountReq, merchantId);
        merchantOnboardingStatus(transactionCountReq);
        MerchantUser merchantUser = merchantUserBO.getUserById(merchantId);
        MerchantInfo merchantInfo = merchantUserBO.getDecorateMerchant(merchantUser, Boolean.TRUE);
        if (com.freewayemi.merchant.utils.MerchantStatus.resubmission.name().equals(merchantInfo.getStatus())) {
            merchantInfo.setResubmissionReason(reSubmissionAction(merchantUser.getResubmissionReason()));
        }
        if (Objects.nonNull(merchantUser.getParams()) && Objects.nonNull(merchantUser.getParams().getBrandIds()) &&
                !CollectionUtils.isEmpty(merchantUser.getParams().getBrandIds())) {
            List<Brand> brands = brandBO.findByBrandId(merchantUser.getParams().getBrandIds().toArray(new String[0]));
            List<BrandBasicInfo> brandBasicInfos = new ArrayList<>();
            for (Brand brand : brands) {
                brandBasicInfos.add(BrandBasicInfo.builder()
                        .name(brand.getName())
                        .icon(brand.getIcon())
                        .id(brand.getId().toString())
                        .build());
            }
            merchantInfo.setBrandBasicInfos(brandBasicInfos);
        }
        long count = 0;
        long todaySuccess = 0;
        long todayAttempted = 0;
        TransactionDataByMerchant transactionDataByMerchant = new TransactionDataByMerchant();
        transactionCountReq.setFromDate(DateUtil.getFormattedDate(Date.from(Instant.now()), "yyyy-MM-dd"));
        transactionCountReq.setToDate(DateUtil.getFormattedDate(Date.from(Instant.now()), "yyyy-MM-dd"));
        transactionCountReq.setTransactionStatus(null);
        TransactionDataResponse transactionDataResponse = paymentOpsService.getTransactionCount(transactionCountReq);
        if (Objects.nonNull(transactionDataResponse)) {
            transactionDataByMerchant.setTodayAttempted(transactionDataResponse.getTransactionCount());
            todayAttempted = transactionDataResponse.getTransactionCount();
        }
        LOGGER.info("attem: {}", todayAttempted);
        transactionCountReq.setTransactionStatus("success");
        transactionDataResponse = paymentOpsService.getTransactionCount(transactionCountReq);
        if (Objects.nonNull(transactionDataResponse)) {
            transactionDataByMerchant.setTodaySuccess(transactionDataResponse.getTransactionCount());
            todaySuccess = transactionDataResponse.getTransactionCount();
        }
        LOGGER.info("success: {}", todayAttempted);
        String monthStart = DateUtil.getFormattedDate(Date.from(DateUtil.getFirstDayOfMonth(Instant.now())), "yyyy-MM-dd");
        String monthEnd = DateUtil.getFormattedDate(Date.from(DateUtil.getLastDayOfMonth(Instant.now())), "yyyy-MM-dd");
        do {
            transactionCountReq.setFromDate(monthStart);
            transactionCountReq.setToDate(monthEnd);
            transactionCountReq.setTransactionStatus("success");
            transactionDataResponse = paymentOpsService.getTransactionCount(transactionCountReq);
            if (Objects.nonNull(transactionDataResponse)) {
                count = transactionDataResponse.getTransactionCount();
                transactionDataByMerchant.setTotalSuccessTransactions(transactionDataResponse.getTransactionCount());
            }
            if (DateUtil.getDate(monthStart, "yyyy-MM-dd").before(Date.from(merchantUser.getCreatedDate()))) {
                break;
            }
            monthEnd = DateUtil.getFormattedDate(Date.from(DateUtil.getLastDayOfPreviousMonth(DateUtil.getDate(monthStart, "yyyy-MM-dd").toInstant())), "yyyy-MM-dd");
            monthStart = DateUtil.getFormattedDate(Date.from(DateUtil.getFirstDayOfPreviousMonth(DateUtil.getDate(monthStart, "yyyy-MM-dd").toInstant())), "yyyy-MM-dd");
        } while (count == 0);

        transactionCountReq.setFromDate(DateUtil.getFormattedDate(Date.from(DateUtil.getFirstDayOfMonth(Instant.now())), "yyyy-MM-dd"));
        transactionCountReq.setToDate(DateUtil.getFormattedDate(Date.from(DateUtil.getLastDayOfMonth(Instant.now())), "yyyy-MM-dd"));
        transactionDataResponse = paymentOpsService.getTransactionCount(transactionCountReq);
        Long thisMonthSuccess = 0L;
        if (Objects.nonNull(transactionDataResponse)) {
            transactionDataByMerchant.setMonthlySuccess(transactionDataResponse.getTransactionCount());
            thisMonthSuccess = transactionDataResponse.getTransactionCount();
        }
        List<WeeklyTransaction> weeklyTransactionList = new ArrayList<>();
        LocalDateTime endDate = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime startDate = endDate.toLocalDate().atStartOfDay().minusHours(5).minusMinutes(40);
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        int i = 1;
        while (i < 8) {
            WeeklyTransaction weeklyTransaction = new WeeklyTransaction();
            long success = 0;
            long attempted = 0;
            if (i == 1) {
                success = todaySuccess;
                attempted = todayAttempted;
            } else {
                String from = startDate.format(formatters);
                String to = endDate.format(formatters);
                transactionCountReq = TransactionOpsRequest.builder().merchantId(merchantId).fromDate(from).toDate(to).build();
                transactionDataResponse = paymentOpsService.getTransactionCount(transactionCountReq);
                if (Objects.nonNull(transactionDataResponse)) {
                    success = transactionDataResponse.getTransactionCount();
                }
                transactionCountReq = TransactionOpsRequest.builder().merchantId(merchantId).fromDate(from).toDate(to).transactionStatus("success").build();
                transactionDataResponse = paymentOpsService.getTransactionCount(transactionCountReq);
                if (Objects.nonNull(transactionDataResponse)) {
                    attempted = transactionDataResponse.getTransactionCount();
                }
            }
            weeklyTransaction.setSuccess(success);
            weeklyTransaction.setAttempt(attempted);
            weeklyTransaction.setDate(endDate);
            endDate = startDate;
            startDate = startDate.minusDays(1);
            weeklyTransactionList.add(weeklyTransaction);
            i++;
        }
        transactionDataByMerchant.setWeeklyTransactions(weeklyTransactionList);
        transactionCountReq.setFromDate(DateUtil.getFormattedDate(Date.from(DateUtil.getFirstDayOfLastMonth(Instant.now())), "yyyy-MM-dd"));
        transactionCountReq.setToDate(DateUtil.getFormattedDate(Date.from(DateUtil.getFirstDayOfLastMonth(Instant.now())), "yyyy-MM-dd"));
        transactionDataResponse = paymentOpsService.getTransactionCount(transactionCountReq);
        Long lastMonthSuccess = 0L;
        if (Objects.nonNull(transactionDataResponse)) {
            lastMonthSuccess = transactionDataResponse.getTransactionCount();
        }
        Long growthPercentage = lastMonthSuccess > 0 ? (thisMonthSuccess - lastMonthSuccess) * 100 / lastMonthSuccess : 0L;
        boolean growthSign = false;
        String growth = "Progress not available";
        if (lastMonthSuccess == 0 && thisMonthSuccess > 0) {
            growth = thisMonthSuccess + "more transactions compare to last month 0 transactions";
            growthSign = true;
        } else if (lastMonthSuccess == 0 && thisMonthSuccess == 0) {
            growth = "zero transactions since last two months";
        } else if (thisMonthSuccess > lastMonthSuccess) {
            growth = growthPercentage + "% above previous month";
            growthSign = true;
        } else if (thisMonthSuccess < lastMonthSuccess) {
            growth = -growthPercentage + "% below previous month";
        }
        transactionDataByMerchant.setCurrMonthGrowth(growth);
        transactionDataByMerchant.setCurrMonthGrownSign(growthSign);

        merchantInfo.setTestTransaction(count > 0 ? Status.SUCCESS : Status.PENDING);
        if ("approved".equals(merchantInfo.getStatus()) && Boolean.TRUE.equals(merchantInfo.getIsOnboarded())) {
            merchantInfo.setStatus("onboarded");
        }
        if (STATUS_MAP.containsKey(merchantInfo.getStatus())) {
            merchantInfo.setStatus(STATUS_MAP.get(merchantInfo.getStatus()));
        }
        merchantInfo.setRegisteredStage(getStages(merchantUser.getStage(), merchantInfo.getStatus(), isCoBranding));
        merchantInfo.setMobileApp(
                MerchantCommonUtil.isNotEmptyString(merchantUser.getDeviceToken()) ? Status.SUCCESS : Status.PENDING);
        merchantInfo.setQrActivation(
                Objects.nonNull(merchantUser.getQrActivationDate()) ? Status.SUCCESS : Status.PENDING);
        merchantInfo.setIsActivated(
                Objects.nonNull(merchantUser.getIsActivated()) && merchantUser.getIsActivated() ? Boolean.TRUE
                        : Boolean.FALSE);
        merchantInfo.setTransactionsInfo(transactionDataByMerchant);
        merchantInfo.setMerchandiseFileUpload(Boolean.FALSE);
        if (Objects.nonNull(merchantUser.getParams()) && Objects.nonNull(merchantUser.getParams().getLeadOwnerIds()) &&
                !CollectionUtils.isEmpty(merchantUser.getParams().getLeadOwnerIds())) {
            AdminAuthUser adminAuthUser = adminAuthUserBO.findById(merchantUser.getParams().getLeadOwnerIds().get(0));
            if (Objects.nonNull(adminAuthUser)) {
                merchantInfo.setLeadOwnerEmail(adminAuthUser.getLogin());
                merchantInfo.setLeadOwnerName(adminAuthUser.getName());
            }
        }
        return merchantInfo;
    }

    private Map<String, Status> getStages(String stage, String status, Boolean isCoBranding) {
        LOGGER.info("stage: {} {}", stage, status);
        Map<String, Status> stageMap = new HashMap<>();
        String currentStage = "";
        if ("businessDetails".equals(stage) || "kyc".equals(stage) || "created".equals(stage) ||
                "dropped".equals(stage)) {
            currentStage = "ownerKyc";
        } else if ("address".equals(stage) || "geoTagging".equals(stage) || "storeDetails".equals(stage) ||
                "storePhoto".equals(stage)) {
            currentStage = "businessKyc";
        } else if ("account".equals(stage)) {
            currentStage = "accountSetup";
        } else if ("uploads".equals(stage) || "commercials".equals(stage) || "commercialsv1".equals(stage)) {
            currentStage = "commercialSetup";
        } else if ("merchandise".equals(stage)) {
            currentStage = "merchandise";
        } else if ("createStoreUser".equals(stage)) {
            currentStage = "createStoreUser";
        } else if ("".equals(stage)) {
            currentStage = "ownerKyc";
        }
        List<String> stages = STAGES;
        if (isCoBranding) {
            stages = CO_BRADNING_STAGES;
        }
        if ("leadcreated".equals(status) || "Registered".equals(status) || "Dropped".equals(status)) {
            for (String s : stages) {
                if (stages.indexOf(s) <= stages.indexOf(currentStage)) {
                    stageMap.put(s, Status.SUCCESS);
                } else {
                    stageMap.put(s, Status.PENDING);
                }
            }
        } else {
            for (String s : stages) {
                stageMap.put(s, Status.SUCCESS);
            }
        }
        return stageMap;
    }

    public String autoBrandId(String gst) {
        List<BrandMerchantData> brandMerchantDatas = brandMerchantDataBO.getBrandMerchantDatas(gst);
        if (!CollectionUtils.isEmpty(brandMerchantDatas)) {
            return brandMerchantDatas.get(0).getBrandId();
        }
        return "";
    }

    public MerchantUser createMerchantUser(String leadOwnerId, String version, AddMerchantRequest addMerchantRequest) {
        Params params = new Params();
        params.setLeadOwnerIds(Arrays.asList(leadOwnerId));
        MerchantConfigs merchantConfigs = merchantConfigsRepository.findByLabel(Constants.exclusionCreditBanks).orElse(null);
        if(Objects.nonNull(merchantConfigs)){
            params.setExclusionCreditBanks(String.join(",", merchantConfigs.getValues()));
        }else{
            params.setExclusionCreditBanks("SBIN,HSBC,AUFB,ONECARD");
        }
        params.setSalesAppVersion(version);
        Address address = new Address();
        address.setCoordinates(Arrays.asList(addMerchantRequest.getLatitude(), addMerchantRequest.getLongitude()));
        String displayId = merchantUserBO.createDisplayId();
        String gst = addMerchantRequest.getGst();
        String bussinessName = "", displayName = "", ownerShip = "", pan = "";
        com.freewayemi.merchant.dto.gst.GstAuthResp gstAuthResp = null;
        if (StringUtils.hasText(gst)) {
            gstAuthResp = digitalIdentityService.verifyGst(GstAuthReq.builder().gstin(gst).provider(MerchantConstants.KARZA).source(Source.MERCHANTMS).build());
            if (Objects.nonNull(gstAuthResp)) {
                bussinessName = gstAuthResp.getLegalNameOfBusiness();
                displayName = gstAuthResp.getTradeName();
                ownerShip = gstAuthResp.getConstitutionOfBusiness();
                pan = "";
                if (gst.length() == 15) {
                    pan = gst.substring(2, 12);
                }
            }
        }
        String brandId = addMerchantRequest.getBrandId();
        List<String> brandIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(addMerchantRequest.getBrandIds())) {
            brandId = addMerchantRequest.getBrandIds().get(0);
            brandIds = addMerchantRequest.getBrandIds();
        }
        String category = "";
        String subCategory = "";
        if (StringUtils.hasText(brandId)) {
            Brand brand = brandBO.findByIdOrBrandDisplayId(brandId);
            if (Objects.nonNull(brand)) {
                category = brand.getCategory();
                subCategory = brand.getSubcategory();
            }
        }
        String autoBrandId = autoBrandId(gst);
        if (StringUtils.hasText(autoBrandId)) {
            brandIds.add(autoBrandId);
            if (StringUtils.isEmpty(brandId)) {
                brandId = autoBrandId;
            }
        }

        LinkedHashSet<String> uniqueBrandIds= new LinkedHashSet<String>();
        uniqueBrandIds.add(brandId);
        for(String brand:brandIds){
            uniqueBrandIds.add(brand);
        }

        List<String> uniqeBrands = new ArrayList<>();
        for(String uniqueBrand:uniqueBrandIds){
            uniqeBrands.add(uniqueBrand);
        }


        params.setBrandId(brandId);
        params.setBrandIds(uniqeBrands);
        params.setShowHdfcCardless("true");
        params.setShowKotakCardless("true");
        params.setCheckoutVersion("v2");
        params.setIsWebhookResponseAsJson(Boolean.TRUE);
        params.setUseTokenizationForAxisDebitCard(Boolean.TRUE);
        params.setUseTokenizationForIciciDebitCard(Boolean.TRUE);
        params.setCallVaultForAxisCreditCard(Boolean.TRUE);
        params.setCallVaultForAufbCreditCard(Boolean.FALSE);
        params.setCallVaultForAmexCreditCard(Boolean.FALSE);
        params.setCallVaultForBobCreditCard(Boolean.FALSE);
        params.setCallVaultForCitiCreditCard(Boolean.FALSE);
        params.setCallVaultForHdfcCreditCard(Boolean.FALSE);
        params.setCallVaultForHsbcCreditCard(Boolean.FALSE);
        params.setCallVaultForIciciCreditCard(Boolean.FALSE);
        params.setCallVaultForIndusIndCreditCard(Boolean.FALSE);
        params.setCallVaultForKotakCreditCard(Boolean.FALSE);
        params.setCallVaultForRblCreditCard(Boolean.FALSE);
        params.setCallVaultForSbiCreditCard(Boolean.FALSE);
        params.setCallVaultForScbCreditCard(Boolean.FALSE);
        params.setCallVaultForYesCreditCard(Boolean.FALSE);
        params.setCallVaultForIciciDebitCard(Boolean.FALSE);
        params.setCallVaultForAxisDebitCard(Boolean.FALSE);
        params.setCallVaultForOneCardCreditCard(Boolean.FALSE);
        params.setEnableDownpaymentByDebitCard(Boolean.FALSE);

        MerchantUser merchantUser = new MerchantUser();
        merchantUser.setParams(params);
        merchantUser.setMobile(addMerchantRequest.getMobile());
        merchantUser.setEmail(addMerchantRequest.getEmail());
        merchantUser.setFirstName(addMerchantRequest.getFirstName());
        merchantUser.setLastName(addMerchantRequest.getLastName());
        merchantUser.setGst(gst);
        merchantUser.setPan(pan);
        merchantUser.setAddress(address);
        merchantUser.setCategory(category);
        merchantUser.setSubCategory(subCategory);
        merchantUser.setBusinessName(bussinessName);
        merchantUser.setShopName(displayName);
        merchantUser.setOwnership(ownerShip);
        merchantUser.setCreatedDate(Instant.now());
        merchantUser.setLastModifiedDate(Instant.now());
        merchantUser.setStatus(com.freewayemi.merchant.utils.MerchantStatus.registered.name());
        merchantUser.setSource("SalesApp");
        merchantUser.setDisplayId(displayId);
        merchantUser.setDeleted(Boolean.FALSE);
        merchantUser.setReturnUrl(
                isProduction ? "https://api.getpayment.com/web/pay.html" : "https://staging.getpayment.com/web/pay.html");
        merchantUser.setReferralCode(displayId);
        merchantUser.setStage("created");
        merchantUser.setEmailVerified(Boolean.FALSE);
        merchantUser.setMobileVerified(Boolean.FALSE);
        merchantUser.setSettlementConfig(
                SettlementConfig.builder().settlementCycle(SettlementCycleEnum.STANDARD).settlementType(SettlementTypeEnum.SELF.name()).build());
        merchantUser.setRiskCategory(1);
        merchantUserBO.save(merchantUser);
        if (Objects.nonNull(gstAuthResp)) {
            MerchantUser merchant = merchantUserBO.getMerchantUserByMobile(merchantUser.getMobile());
            MerchantGSTDetails merchantGSTDetails = new MerchantGSTDetails();
            merchantGSTDetails.setMerchantId(merchant.getId().toString());
            merchantGSTDetails.setGst(gst);
            merchantGSTDetails.setGstAuthResp(gstAuthResp);
            merchantGSTDetails.setCreatedDate(Instant.now());
            merchantGSTDetails.setLastModifiedDate(Instant.now());
            merchantGstAuthInfoRepository.save(merchantGSTDetails);
        }
        return merchantUser;
    }

    public MerchantInfo createMerchant(String appVersion, AddMerchantRequest addMerchantRequest,
                                       HttpServletRequest httpServletRequest) {
        if (!ValidationUtil.validateMobileNumber(addMerchantRequest.getMobile())) {
            throw new FreewayException("Mobile number is not valid");
        }
        String leadOwnerId = authCommonService.getMerchantId(httpServletRequest).getCredentials().get("storeUserId");
        MerchantUser merchantUser = merchantUserBO.getMerchantUserByMobile(addMerchantRequest.getMobile());
        if (Objects.nonNull(merchantUser)) {
            Params params = merchantUser.getParams();
            if (!Objects.nonNull(params)) {
                params = new Params();
            }
            params.setLeadOwnerIds(Collections.singletonList(leadOwnerId));
            params.setSalesAppVersion(appVersion);
            params.setCheckoutVersion("v2");
            merchantUserBO.save(merchantUser);
            MerchantUser merchant = merchantUserBO.getUserById(merchantUser.getId().toString());
            return merchantUserBO.getDecorateMerchant(merchant, null);
        } else {
            MerchantUser user = createMerchantUser(leadOwnerId, appVersion, addMerchantRequest);
            MerchantUser merchant = merchantUserBO.getMerchantUserByMobile(user.getMobile());
            CreateEventRequest createEventRequest = CreateEventRequest.builder()
                    .eventName("SIGNUP")
                    .createdBy(leadOwnerId)
                    .createdByType("SALES")
                    .merchantId(merchant.getId().toString())
                    .build();
            reportService.createEvent(createEventRequest);
            return merchantUserBO.getDecorateMerchant(merchant, null);
        }
    }

    private ProposalRates getProposals(String type) {
        List<Integer> tenures = Arrays.asList(3, 6, 9, 12, 18, 24);
        ProposalRates proposalRates = ProposalRates.builder().build();
        if (NO_COST_EMI.equals(type)) {
            List<MerchantMdrs> merchantMdrs = new ArrayList();
            for (Integer tenure : tenures) {
                merchantMdrs.add(MerchantMdrs.builder()
                        .tenure(tenure)
                        .cardTypeEnum(null)
                        .charge(MDR_RATES_MAP.get(tenure))
                        .build());
            }
            proposalRates.setMerchantMdrs(merchantMdrs);
        } else if (STANDARD_EMI.equals(type)) {
            List<MerchantMdrs> merchantMdrs = Arrays.asList(
                    MerchantMdrs.builder().charge(1.5).cardTypeEnum(CardTypeEnum.DEBIT).tenure(-1).build(),
                    MerchantMdrs.builder().charge(2.25).cardTypeEnum(CardTypeEnum.CREDIT).tenure(-1).build());
            List<BankInterestRates> bankInterestRates = Arrays.asList(
                    BankInterestRates.builder().interest(16.0).cardTypeEnum(CardTypeEnum.DEBIT).tenure(-1).build(),
                    BankInterestRates.builder().interest(15.0).cardTypeEnum(CardTypeEnum.CREDIT).tenure(-1).build());
            proposalRates.setMerchantMdrs(merchantMdrs);
            proposalRates.setBankInterestRates(bankInterestRates);
        } else if (CONVENIENCE_FEE.equals(type)) {
            List<MerchantMdrs> merchantMdrs = Arrays.asList(
                    MerchantMdrs.builder().charge(0.0).cardTypeEnum(CardTypeEnum.DEBIT).tenure(-1).build(),
                    MerchantMdrs.builder().charge(0.0).cardTypeEnum(CardTypeEnum.CREDIT).tenure(-1).build());
            List<BankInterestRates> bankInterestRates = Arrays.asList(
                    BankInterestRates.builder().interest(16.0).cardTypeEnum(CardTypeEnum.DEBIT).tenure(-1).build(),
                    BankInterestRates.builder().interest(15.0).cardTypeEnum(CardTypeEnum.CREDIT).tenure(-1).build());
            List<CustomerInterestRates> customerInterestRates = Arrays.asList(
                    CustomerInterestRates.builder().interest(1.5).cardTypeEnum(CardTypeEnum.DEBIT).tenure(-1).build(),
                    CustomerInterestRates.builder()
                            .interest(2.25)
                            .cardTypeEnum(CardTypeEnum.CREDIT)
                            .tenure(-1)
                            .build());
            proposalRates.setMerchantMdrs(merchantMdrs);
            proposalRates.setBankInterestRates(bankInterestRates);
            proposalRates.setCustomerInterestRates(customerInterestRates);
        }
        return proposalRates;
    }

    private Boolean showConvFee(MerchantUser merchantUser) {
        if (Objects.nonNull(merchantUser.getCategory()) &&
                Arrays.asList("Vehicle sales").contains(merchantUser.getCategory())) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private Boolean showCustomerBearingSettings(MerchantUser merchantUser) {
        if (Objects.nonNull(merchantUser.getCategory()) &&
                Arrays.asList("Vehicle sales", "Mobile & electronics").contains(merchantUser.getCategory())) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private Boolean hidePricingProposals(MerchantUser merchantUser) {
        if (Objects.nonNull(merchantUser.getParams()) && Objects.nonNull(merchantUser.getParams().getBrandId()) &&
                merchantUserBO.isExceptionBrand(merchantUser.getParams().getBrandId())) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public PricingProposal getPricingProposal(String merchantId) {
        MerchantUser merchantUser = merchantUserBO.getUserById(merchantId);
        return PricingProposal.builder()
                .noCostEmi(getProposals(NO_COST_EMI))
                .standardEmi(getProposals(STANDARD_EMI))
                .convenienceFee(getProposals(CONVENIENCE_FEE))
                .showConvFeeSettings(showCustomerBearingSettings(merchantUser))
                .showCustomerBearingSettings(showCustomerBearingSettings(merchantUser))
                .hidePricingProposal(hidePricingProposals(merchantUser))
                .build();
    }

    public void createMerchantGST(MerchantGSTDetails gstDict) {
        try {
            gstDict.setLastModifiedDate(Instant.now());
            merchantGstAuthInfoRepository.save(gstDict);
        } catch (Exception e) {
            LOGGER.info("Could not create merchant", e);
        }
    }

    public void updateGstDetails(GstReq gstReq, com.freewayemi.merchant.dto.gst.GstAuthResp gstAuthResp) {
        MerchantGSTDetails merchantGSTDetails = new MerchantGSTDetails();
        merchantGSTDetails.setMerchantId(gstReq.getMid());
        merchantGSTDetails.setGst(gstReq.getGst());
        merchantGSTDetails.setGstAuthResp(gstAuthResp);
        merchantGSTDetails.setCreatedDate(Instant.now());
        createMerchantGST(merchantGSTDetails);

    }

    public BasicResponse brandGst(BrandGstRequest brandGstRequest) {
        if (Objects.nonNull(brandGstRequest.getBrandId()) &&
                Arrays.asList("60e9aad03419796c42203fa7").contains(brandGstRequest.getBrandId())) {
            BrandMerchantDataResponse brandMerchantDataResponse =
                    brandMerchantDataBO.searchBrandData(brandGstRequest.getBrandId(), brandGstRequest.getGst(), "");
            if (Objects.nonNull(brandMerchantDataResponse.getBrandId())) {
                return BasicResponse.builder()
                        .status(Status.FAILED)
                        .statusMsg("Brand does not exist for gst")
                        .statusCode(125)
                        .build();
            }
        }
        return BasicResponse.builder().status(Status.SUCCESS).statusMsg("successful").statusCode(0).build();
    }

    public List<KYCLink> getDigilockerLink(String merchantId) {
        return kycLinkBo.getKycLinks(merchantId);
    }

    public IncentiveDetails getMerchantIncentivesDetails(String merchantId) {
        List<MerchantIncentive> merchantIncentives =
                merchantIncentiveBO.getMerchantIncentivesDetails(merchantId).orElse(new ArrayList<>());
        LOGGER.info("data: {}", merchantIncentives.size());
        List<com.freewayemi.merchant.dto.sales.MerchantIncentive> incentives = new ArrayList<>();
        for (MerchantIncentive merchantIncentive : merchantIncentives) {
            LOGGER.info("loop start");
            Optional<Item> item = incentiveBO.getMerchantIncentives(merchantIncentive.getIncentiveId());
            LOGGER.info("item: {}", item);
            if (item.isPresent()) {
                Item item1 = item.get();
                if ("live".equals(item1.getStatus()) && merchantIncentiveBO.checkIfIncentivesExistInMerchant(merchantId,
                        merchantIncentive.getIncentiveId())) {
                    LOGGER.info("live data");
                    com.freewayemi.merchant.dto.sales.MerchantIncentive incentive =
                            com.freewayemi.merchant.dto.sales.MerchantIncentive.builder()
                                    .merchantId(String.valueOf(item1.getId()))
                                    .endDate(item1.getEndDate().toString())
                                    .startDate(item1.getStartDate().toString())
                                    .construct(item1.getConstruct())
                                    .constructType(item1.getConstructType())
                                    .name(item1.getName())
                                    .build();

                    LOGGER.info("incentive:{}", incentive);
                    incentives.add(incentive);
                }
            }
        }
        return IncentiveDetails.builder().incentives(incentives).build();
    }

    public List<MerchantNotifications> getNotification(String leadOwnerId) {
        Optional<List<Notification>> notifications = notificationBO.getNotification(leadOwnerId);
        List<MerchantNotifications> notifications2 = new ArrayList<>();
        if (notifications.isPresent()) {
            List<Notification> notifications1 = notifications.get();
            for (int i = 0; i < notifications1.size(); i++) {
                Notification notification = notifications1.get(i);

                MerchantNotifications notif = MerchantNotifications.builder()
                        .leadOwnerId(notification.getLeadOwnerId())
                        .title(notification.getTitle())
                        .body(notification.getBody())
                        .readStatus(notification.getReadStatus())
                        .createdDate(notification.getCreatedDate())
                        .lastModifiedDate(notification.getLastModifiedDate())
                        .build();

                notifications2.add(notif);
            }
        }
        return notifications2;
    }

    public BasicResponse saveKycLink(KYCLink kycLink) {
        kycLinkBo.saveKycLink(kycLink);
        return BasicResponse.builder().status(Status.SUCCESS).build();
    }

    public void updateAccount(GstReq gstReq, AccountBody accountBody,
                              BankAccountAuthResp bankAccountAuthResp) {
        MerchantPennydropDetails merchantPennydropDetails = MerchantPennydropDetails.builder()
                .merchantId(gstReq.getMid())
                .bankAccountAuthResp(bankAccountAuthResp)
                .acc(accountBody.getAccountNumber())
                .ifsc(accountBody.getIfsc()).build();
        merchantPennydropDetails.setCreatedDate(Instant.now());

        merchantUserBO.createAccountPennyDrop(merchantPennydropDetails);
    }

    public PanDict getPanVerifable(GstReq gstReq) {
        PanDict panDict;
        PanDetailsResponse panResp = digitalIdentityService.getPANDetails(PanDetailsRequest.builder()
                .panNumber(gstReq.getPan())
                .source(Source.MERCHANTMS)
                .build());
        if (Objects.nonNull(panResp)) {
            if ("success".equals(panResp.getStatus())) {
                String name = panResp.getFullName();
                if (!StringUtils.hasText(name)) {
                    name = "Not Available";
                }
                panDict = PanDict.builder().status(Boolean.TRUE).businessName(name).build();
            } else {
                panDict = PanDict.builder().status(Boolean.FALSE).build();
            }
            return panDict;
        }
        return null;
    }

    public PanDict getGstVerifable(GstReq gstReq, HttpServletRequest httpServletRequest) {
        if (isProduction) {
            ZonedDateTime currTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
            if (currTime.getHour() >= 18) {
                return PanDict.builder()
                        .status(Boolean.FALSE)
                        .statusMsg(
                                "Merchant onboarding not allowed post 6pm,please complete your merchant visit tasks " +
                                        "for volume")
                        .build();
            }
        }
        String user = authCommonService.getMerchantId(httpServletRequest).getCredentials().get("storeUserId");
        reportService.createEvent(CreateEventRequest.builder()
                .eventName("GST_VERIFY")
                .value(gstReq.getGst())
                .createdBy(user)
                .createdByType("sales")
                .merchantId(gstReq.getMid())
                .build());
        Boolean brandGstCheck = merchantUserBO.brandGstCheckRequired(user);
        if (Objects.isNull(gstReq.getGst())) {
            return PanDict.builder()
                    .status(Boolean.FALSE)
                    .statusMsg("Invalid GST, please enter the correct GST number")
                    .build();
        }
        List<BrandGst> bg = brandGstRepository.findByGst(gstReq.getGst().toUpperCase()).orElse(new ArrayList<>());
        if (bg.size() == 0 && brandGstCheck) {
            PanDict gstDict = PanDict.builder()
                    .status(Boolean.FALSE)
                    .statusHeader("GST doesn’t belong to any brand!")
                    .statusMsg("Please onboard brand merchants only,ask your manager for brand merchants list.")
                    .build();
            reportService.createEvent(CreateEventRequest.builder()
                    .eventName("GST_FAILED")
                    .reason("GST doesn’t belong to any brand!")
                    .value(gstReq.getGst())
                    .createdBy(user)
                    .createdByType("sales")
                    .merchantId(gstReq.getMid())
                    .build());
            return gstDict;
        }
        com.freewayemi.merchant.dto.gst.GstAuthResp gstAuthResp = digitalIdentityService.verifyGst(GstAuthReq.builder().gstin(gstReq.getGst()).provider(MerchantConstants.KARZA).source(Source.MERCHANTMS).build());
        PanDict gstDict;
        if (Objects.nonNull(gstAuthResp)) {
            if (Objects.nonNull(gstAuthResp.getGstStatus()) && "Cancelled".equals(gstAuthResp.getGstStatus())) {
                reportService.createEvent(CreateEventRequest.builder()
                        .eventName("GST_FAILED")
                        .reason("Entered GST has been cancelled by govt. try with another GST!")
                        .value(gstReq.getGst())
                        .createdBy(user)
                        .createdByType("sales")
                        .merchantId(gstReq.getMid())
                        .build());
                return PanDict.builder()
                        .status(Boolean.FALSE)
                        .statusHeader("GST Cancelled")
                        .statusMsg("Entered GST has been cancelled by govt.try with another GST")
                        .build();
            }
            if (Objects.nonNull(gstAuthResp.getAddressesOfPrincipalPlaceOfBusiness())) {
                String address =
                        Objects.equals(gstAuthResp.getAddressesOfPrincipalPlaceOfBusiness().getCompleteAddress(), "NA")
                                ? "" : gstAuthResp.getAddressesOfPrincipalPlaceOfBusiness().getCompleteAddress();
                if(!StringUtils.hasText(address)){
                    address =
                            Objects.equals(gstAuthResp.getAddressesOfPrincipalPlaceOfBusiness().getNatureOfBusinessAtAddress(), "NA")
                                    ? "" : gstAuthResp.getAddressesOfPrincipalPlaceOfBusiness().getNatureOfBusinessAtAddress();
                }
                String[] addressList = address.split(",");
                String pincode = "";
                if (addressList.length > 0) {
                    pincode = addressList[addressList.length - 1];
                } else {
                    pincode = "";
                }
                if (pincode.contains("pin")) {
                    String[] pincodeList = pincode.split(":");
                    if (pincodeList.length > 0) {
                        pincode = pincodeList[pincodeList.length - 1];
                    }
                }
                String line1 = "", line2 = "", city = "", state = "";
                if (addressList.length > 4) {
                    line1 = String.join(",",
                            Arrays.copyOfRange(addressList, 0, addressList.length - 4));
                } else {
                    line1 = address;
                }
                if (addressList.length > 3) {
                    line2 = addressList[addressList.length - 4].trim();
                }
                if (addressList.length > 2) {
                    city = addressList[addressList.length - 3].trim();
                }
                if (addressList.length > 1) {
                    state = addressList[addressList.length - 2].trim();
                }

                Boolean onboarding;
                if (!brandGstCheck && bg.size() == 0) {
                    onboarding = Boolean.TRUE;
                } else {
                    onboarding = Boolean.FALSE;
                }
                List<String> brandIds = bg.stream().map(BrandGst::getBrandId).collect(Collectors.toList());
                List<Brand> brands = brandBO.findByBrandId(brandIds.toArray(new String[0]));
                Map<String, String> brandLogos = new HashMap<>();
                if (!CollectionUtils.isEmpty(brands)) {
                    brandLogos = brands.stream().filter(brand -> Objects.nonNull(brand.getIcon())).collect(Collectors.toMap(brand -> brand.getId().toString(), Brand::getIcon));
                }
                gstDict = PanDict.builder()
                        .status(Boolean.TRUE)
                        .names(gstAuthResp.getMemberNames())
                        .address(address)
                        .line1(line1)
                        .line2(line2)
                        .city(city)
                        .state(state)
                        .pincode(pincode)
                        .brandIds(brandIds)
                        .anyBrandOnboarding(onboarding)
                        .brandLogs(brandLogos)
                        .businessName(gstAuthResp.getLegalNameOfBusiness())
                        .shopName(gstAuthResp.getTradeName())
                        .build();
                Map<String, String> coordinates = new HashMap<>();
                coordinates.put("lat", gstReq.getLat());
                coordinates.put("lon", gstReq.getLon());
                PincodeResp bdeLocation = helperService.getPincodeCity(coordinates);
                PanDict result = merchantLocationBO.checkBdeLocation(bdeLocation, gstDict, user, gstReq.getMid());
                if (result != null) {
                    return result;
                }
            } else {
                reportService.createEvent(CreateEventRequest.builder()
                        .eventName("GST_FAILED")
                        .reason("Unable to verify GST, please enter a valid GST no")
                        .value(gstReq.getGst())
                        .createdBy(user)
                        .createdByType("sales")
                        .merchantId(gstReq.getMid())
                        .build());
                gstDict = PanDict.builder()
                        .status(Boolean.FALSE)
                        .statusMsg("Invalid GST, please enter the correct GST number")
                        .build();
            }
        } else {
            reportService.createEvent(CreateEventRequest.builder()
                    .eventName("GST_FAILED")
                    .reason("Unable to verify GST, please enter a valid GST no")
                    .value(gstReq.getGst())
                    .createdBy(user)
                    .createdByType("sales")
                    .merchantId(gstReq.getMid())
                    .build());
            gstDict = PanDict.builder()
                    .status(Boolean.FALSE)
                    .statusMsg("Invalid GST, please enter the correct GST number")
                    .build();
        }
        if (gstDict.getStatus() != null) {
            reportService.createEvent(CreateEventRequest.builder()
                    .eventName("GST_SUCCESS")
                    .value(gstReq.getGst())
                    .createdBy(user)
                    .createdByType("sales")
                    .merchantId(gstReq.getMid())
                    .build());
        }
        LOGGER.info("gstDict: {}", gstDict);
        return gstDict;

    }

    public PanDict getAccountVerifiable(GstReq gstReq, HttpServletRequest httpServletRequest) {
        LOGGER.info("verify account details");
        String user = authCommonService.getMerchantId(httpServletRequest).getCredentials().get("storeUserId");
        reportService.createEvent(CreateEventRequest.builder()
                .eventName("ACCOUNT")
                .value(gstReq.getAccount_number())
                .createdBy(user)
                .createdByType("sales")
                .merchantId(gstReq.getMid())
                .build());
        Pattern pattern = Pattern.compile(ACCOUNT_VERIFICATION_REGEX);
        Matcher matcher = pattern.matcher(gstReq.getAccount_ifsc());
        if (!matcher.find() || !VALID_IFSC_CODE.contains(gstReq.getAccount_ifsc().substring(0, 4))) {
            return PanDict.builder().status(Boolean.FALSE).statusCode("INVALID").statusMsg("Invalid ifsc code").build();
        }
        BankAccVerificationDto bankAccVerificationDto = BankAccVerificationDto.builder()
                .accountNumber(gstReq.getAccount_number())
                .ifsc(gstReq.getAccount_ifsc())
                .source("salesApp")
                .merchantId(gstReq.getMid())
                .build();
        LOGGER.info("verify account details request: {}", bankAccVerificationDto);
        BankAccountAuthResp bankAccountAuthResp = digitalIdentityService.verifyAccount(BankAccountAuthReq.builder()
                .accountNumber(bankAccVerificationDto.getAccountNumber()).ifsc(bankAccVerificationDto.getIfsc())
                .provider(MerchantConstants.KARZA).source(Source.MERCHANTMS).build());
        if (Objects.nonNull(bankAccountAuthResp)) {

            AccountBody accountBody = AccountBody.builder()
                    .accountNumber(gstReq.getAccount_number())
                    .ifsc(gstReq.getAccount_ifsc())
                    .build();
            updateAccount(gstReq, accountBody, bankAccountAuthResp);

            String name = bankAccountAuthResp.getAccountName();
            if (!StringUtils.hasText(name)) {
                name = "Not Available";
            } else {
                name = name.split(",")[0];
            }
            PanDict accountDict;
            if ("Not Available".equals(name)) {
                accountDict = PanDict.builder().status(Boolean.FALSE).accountName(name).build();
            } else {
                accountDict = PanDict.builder().status(Boolean.TRUE).accountName(name).build();
            }

            if ("Not Available".equals(name)) {
                if ("NOT_VERIFIED".equals(bankAccountAuthResp.getStatus())) {
                    accountDict.setStatusCode("INVALID");
                } else {
                    accountDict.setStatusCode("NOT_VERIFIED");
                }
            }

            if (Objects.nonNull(accountDict.getStatus())) {
                reportService.createEvent(CreateEventRequest.builder()
                        .eventName("ACCOUNT_SUCCESS")
                        .value(gstReq.getAccount_number())
                        .createdBy(user)
                        .createdByType("sales")
                        .merchantId(gstReq.getMid())
                        .build());
            }
            return accountDict;
        } else {
            return PanDict.builder().status(Boolean.FALSE).statusCode("NOT_VERIFIED").build();
        }
    }

    public PanDict aSwitch(GstReq gstReq, HttpServletRequest httpServletRequest) {

        if ("pan".equals(gstReq.getType())) {
            return getPanVerifable(gstReq);
        }
        if ("gst".equals(gstReq.getType())) {
            return getGstVerifable(gstReq, httpServletRequest);
        }
        if ("account".equals(gstReq.getType())) {
            return getAccountVerifiable(gstReq, httpServletRequest);
        }
        return null;
    }


    public PanDict verifyMerchantDetails(GstReq gstReq, HttpServletRequest httpServletRequest) {
        LOGGER.info("merchant details:{}", gstReq);
        return aSwitch(gstReq, httpServletRequest);
    }

    public MerchantCount getMerchantCount(MerchantCountReq merchantCountReq) {
        long onboarded = merchantDAO.getMerchantCounts(merchantCountReq);
        MerchantCount merchantCount = MerchantCount.builder().merchantCount(onboarded).build();
        LOGGER.info("MerchantCount: {}", merchantCount.getMerchantCount());
        return merchantCount;
    }

    public BasicResponse getOnboardingStatus(TransactionOpsRequest transactionCountReq) {
        long count = 0;
        transactionCountReq.setFromDate(DateUtil.getFormattedDate(Date.from(merchantUserBO.getUserByIdOrDisplayId(transactionCountReq.getMerchantId()).getCreatedDate()), "yyyy-MM-dd"));
        transactionCountReq.setToDate(DateUtil.getFormattedDate(Date.from(Instant.now()), "yyyy-MM-dd"));
        TransactionDataResponse transactionDataResponse = paymentOpsService.getTransactionCount(transactionCountReq);
        if (Objects.nonNull(transactionDataResponse) && Objects.nonNull(transactionDataResponse.getTransactionCount())) {
            count = transactionDataResponse.getTransactionCount();
        }
        if (count > 0) {
            merchantDAO.updateOnboardingStatus(transactionCountReq.getMerchantId(), Boolean.TRUE);
            return BasicResponse.builder().status(Status.SUCCESS).build();
        }
        return BasicResponse.builder().status(Status.FAILED).statusMsg("Merchant has not done the test transaction,ensure the test transaction has happened").build();
    }

    public MerchantConfigs getMinversion(Integer appVersion) {
        String label = "sales_app_min_version_android";
        return merchantConfigsRepository.findByLabel(label).orElse(null);
    }

    public List<MerchantSearchResponse> getSearchMerchants(String leadOwnerId) {
        return merchantUserBO.getSearchMerchants(leadOwnerId);
    }

    public List<MerchantVisibilities> getVisibilities(String merchantId, String status) {
        return merchantVisibilitiesBO.getMerchantVisibilitiesByMerchantIdAndStatus(merchantId, status);
    }
}
