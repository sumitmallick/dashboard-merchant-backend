package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.commons.bo.AppContentBO;
import com.freewayemi.merchant.commons.bo.PaymentServiceBO;
import com.freewayemi.merchant.commons.dto.AppContentCard;
import com.freewayemi.merchant.commons.dto.StoreUserTransaction;
import com.freewayemi.merchant.commons.dto.StoreUserTransactionStatusReq;
import com.freewayemi.merchant.commons.dto.TransactionResponse;
import com.freewayemi.merchant.commons.entity.AppContents;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.utils.DateUtil;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.response.*;
import com.freewayemi.merchant.entity.AdminAuthUser;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.utils.MerchantStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.freewayemi.merchant.commons.utils.paymentConstants.SALES_REFERRAL_CODE;

@Component
public class HomeBO {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeBO.class);
    private final MerchantTransactionBO merchantTransactionBO;
    private final MerchantOfferConfigBO merchantOfferConfigBO;
    private final AppContentBO appContentBO;
    private final MerchantEarningsBO merchantEarningsBO;
    private final BrandBO brandBO;
    private final PaymentServiceBO paymentServiceBO;
    private final AdminAuthUserBO adminAuthUserBO;
    private final StoreUserRewardsBO storeUserRewardsBO;

    @Autowired
    public HomeBO(MerchantTransactionBO merchantTransactionBO, MerchantOfferConfigBO merchantOfferConfigBO,
                  AppContentBO appContentBO, MerchantEarningsBO merchantEarningsBO,
                  BrandBO brandBO, PaymentServiceBO paymentServiceBO, AdminAuthUserBO adminAuthUserBO,
                  StoreUserRewardsBO storeUserRewardsBO) {
        this.merchantTransactionBO = merchantTransactionBO;
        this.merchantOfferConfigBO = merchantOfferConfigBO;
        this.appContentBO = appContentBO;
        this.merchantEarningsBO = merchantEarningsBO;
        this.brandBO = brandBO;
        this.paymentServiceBO = paymentServiceBO;
        this.adminAuthUserBO = adminAuthUserBO;
        this.storeUserRewardsBO = storeUserRewardsBO;
    }

    public HomeResponse get(MerchantUser user, String storeUserId) {
        LOGGER.info("Home API call for displayId: {} storeUserId:{}", user.getDisplayId(), storeUserId);
        AppContentCard infoSection = this.getInfoSection(user);
        List<AppContentCard> knoSection = this.getKnoSection(user);
        List<AppContentCard> statSection = appContentBO.getStatSection();
        Boolean hasBrand = brandBO.hasBrand(user.getParams());
        List<AppContentCard> banners = hasBrand ? this.getBanners(user) : new ArrayList<>();

        if (StringUtils.hasText(storeUserId)) {
            StoreUserTransaction storeUserTransaction = paymentServiceBO.getTransaction(storeUserId,
                    StoreUserTransactionStatusReq.builder().build());
            List<TransactionResponse> txns = null != storeUserTransaction ?
                    storeUserTransaction.getTransactionResponses() : null;
            List<TransactionResponse> maxFiveTxns = (null != txns && txns.size() > 5) ? txns.subList(0, 5) : txns;
            StoreUserHomeData storeUserHomeData = getStoreUserHomeData(storeUserId);
            return HomeResponse
                    .builder()
                    .showTxnSummary(false)
                    .showInfoSection(false)
                    .showDataDashboard(false)
                    .showKnoSection(knoSection != null).knoSections(knoSection)
                    .showEmiInfo(false)
                    .showStatSection(statSection != null).statSections(statSection)
                    .showWelcomeScreens(false)
                    .showSettlementTab(false)
                    .showPocketDetails(false)
                    .showLastRedeemedAmount(false)
                    .lastRedeemedAmount(0l)
                    .showDailyEarningPromo(false)
                    .showOtherUserEarningPromo(false)
                    .showConsumerNumberPromo(false)
                    .showOtherUserNumberPromo(true)
                    .showTxnList(true)
                    .txnList(maxFiveTxns)
                    .showUpdateAccount(!storeUserHomeData.getIsAccountDetailsAvailable())
                    .showStoreUserStats(false)
                    .showBanners(hasBrand).banners(banners)
                    .storeUserHomeData(storeUserHomeData)
                    .build();
        }
        if (MerchantStatus.registered.name().equals(user.getStatus())) {
            return HomeResponse
                    .builder()
                    .showTxnSummary(false)
                    .showInfoSection(infoSection != null).infoSection(infoSection)
                    .showDataDashboard(false)
                    .showKnoSection(knoSection != null).knoSections(knoSection)
                    .showTxnList(false)
                    .showEmiInfo(false)
                    .showStatSection(statSection != null).statSections(statSection)
                    .showWelcomeScreens(false)
                    .showSettlementTab(false)
                    .showPocketDetails(false)
                    .showLastRedeemedAmount(false)
                    .lastRedeemedAmount(0l)
                    .showBanners(hasBrand).banners(banners)
                    .build();
        }
        if (MerchantStatus.profiled.name().equals(user.getStatus()) ||
                MerchantStatus.resubmission.name().equals(user.getStatus())) {
            Map<String, String> offers = new HashMap<>();
            boolean showEmiInfo =
                    null != user.getReferredBy() && SALES_REFERRAL_CODE.contains(user.getReferredBy().toUpperCase());
            if (showEmiInfo) {
                offers = merchantOfferConfigBO.getMerchantOffersTenuresText(user);
            }
            return HomeResponse
                    .builder()
                    .showTxnSummary(false)
                    .showInfoSection(infoSection != null).infoSection(infoSection)
                    .showDataDashboard(false)
                    .showKnoSection(knoSection != null).knoSections(knoSection)
                    .showTxnList(false)
                    .showEmiInfo(showEmiInfo && !offers.isEmpty()).emiInfo(EmiInfo.builder().offers(offers).build())
                    .showStatSection(knoSection != null).statSections(statSection)
                    .showWelcomeScreens(false)
                    .showSettlementTab(false)
                    .showPocketDetails(false)
                    .showLastRedeemedAmount(false)
                    .lastRedeemedAmount(0l)
                    .showBanners(hasBrand).banners(banners)
                    .build();
        }
        if (MerchantStatus.approved.name().equals(user.getStatus())) {
            List<TransactionResponse> totalTxns =
                    merchantTransactionBO.getTransactionByMerchantId(user.getId().toString(), null);
            List<TransactionResponse> thisMonthSuccessTxns =
                    merchantTransactionBO.getSuccessTransactionByMerchantId(user.getId().toString(), null);
            Map<String, String> offers = merchantOfferConfigBO.getMerchantOffersTenuresText(user);
            List<TransactionResponse> txns1 = (null != totalTxns && totalTxns.size() > 5) ? totalTxns.subList(0, 5) : totalTxns;
            TxnSummary txnSummary = getTxnSummary(thisMonthSuccessTxns);
            DataDashBoard dataDashBoard = getDataDashboard(totalTxns);
            PocketDetails pocketDetails = merchantEarningsBO.getPocketDetails(user.getId().toString());
            return HomeResponse
                    .builder()
                    .showTxnSummary(txnSummary.getTxnCount() > 0).txnSummary(txnSummary)
                    .showInfoSection(infoSection != null).infoSection(infoSection)
                    .showDataDashboard(dataDashBoard.getCcPer() > 0 || dataDashBoard.getDcPer() > 0 ||
                            !dataDashBoard.getTenures().isEmpty()).dataDashBoard(getDataDashboard(totalTxns))
                    .showKnoSection(knoSection != null).knoSections(knoSection)
                    .showTxnList(txns1.size() > 0).txnList(txns1)
                    .showEmiInfo(!offers.isEmpty()).emiInfo(EmiInfo.builder().offers(offers).build())
                    .showStatSection(knoSection != null && txnSummary.getTxnCount() == 0).statSections(statSection)
                    .showWelcomeScreens(false)
                    .showSettlementTab(txnSummary.getTxnCount() > 0)
                    .showPocketDetails(null != pocketDetails).pocketDetails(pocketDetails)
                    .showLastRedeemedAmount(true)
                    .lastRedeemedAmount(merchantEarningsBO.getLastRedeemedAmount(user.getId().toString()))
                    .showBanners(hasBrand).banners(banners)
                    .build();
        }
        if (MerchantStatus.rejected.name().equals(user.getStatus())) {
            return HomeResponse
                    .builder()
                    .showTxnSummary(false)
                    .showInfoSection(false)
                    .showDataDashboard(false)
                    .showKnoSection(false)
                    .showTxnList(false)
                    .showEmiInfo(false)
                    .showWelcomeScreens(false)
                    .showStatSection(false)
                    .showSettlementTab(false)
                    .showPocketDetails(false)
                    .showLastRedeemedAmount(false)
                    .lastRedeemedAmount(0l)
                    .showBanners(hasBrand).banners(banners)
                    .build();
        }
        throw new FreewayException("merchant status invalid");
    }

    private String getLanding(MerchantUser user) {
        if (user.getShopName() == null) {
            return "SignUpStep2Screen";
        } else if (user.getGst() == null) {
            return "SignUpStep3Screen";
        } else if (user.getAccount() == null || user.getAccount().getNumber() == null) {
            return "SignUpStep4Screen";
        } else if (!user.isDocsSubmit()) {
            return "SignUpStep5Screen";
        }
        return "SignUpStep5Screen";
    }

    public AppContentCard getInfoSection(MerchantUser user) {
        if (user.getStatus().equals(MerchantStatus.registered.name())) {
            return AppContentCard
                    .builder()
                    .icon("https://storage.googleapis.com/payment-f76df.appspot.com/info/info0.png")
                    .landing(this.getLanding(user))
                    .text("Complete your profile to start selling with payment instantly.")
                    .subText("Click here to continue >")
                    .title("Sell more, sell smart!")
                    .type("app")
                    .build();
        } else if (user.getStatus().equals(MerchantStatus.profiled.name())) {
            return AppContentCard
                    .builder()
                    .icon("https://storage.googleapis.com/payment-f76df.appspot.com/info/info0.png")
                    .landing(null)
                    .text("Your profile has been successfully submitted.")
                    .subText("Your approval will take a maximum of 1 day. We will keep you updated.")
                    .title("Awesome!")
                    .type(null)
                    .build();
        } else if (user.getStatus().equals(MerchantStatus.resubmission.name())) {
            return AppContentCard
                    .builder()
                    .icon("https://storage.googleapis.com/payment-f76df.appspot.com/info/info0.png")
                    .landing("SignUpStep5Screen")
                    .text(StringUtils.isEmpty(user.getResubmissionReason()) ?
                            "Please correctly fill your details and resubmit your application." :
                            user.getResubmissionReason())
                    .subText("Click here to continue >")
                    .title("Your approval is on hold.")
                    .type("app")
                    .build();
        } else {
            Optional<AppContents> appContentsOp = appContentBO.findByContentTypeAndActive("info", true);
            if (appContentsOp.isPresent()) {
                AppContents appContents = appContentsOp.get();
                return AppContentCard
                        .builder()
                        .icon(appContents.getIcon())
                        .landing(appContents.getLanding())
                        .text(appContents.getText())
                        .subText(appContents.getSubText())
                        .title(appContents.getTitle())
                        .type(appContents.getType())
                        .build();
            }
        }
        return null;
    }

    public List<AppContentCard> getKnoSection(MerchantUser user) {
        Optional<List<AppContents>> appContentsOp1 =
                appContentBO.findAllByContentTypeAndActiveAndCategory("knowledge_custom", true, user.getCategory());
        boolean showEmiInfo =
                null != user.getReferredBy() && SALES_REFERRAL_CODE.contains(user.getReferredBy().toUpperCase());
        Optional<List<AppContents>> appContentsOp;
        if (user.getStatus().equals(MerchantStatus.profiled.name()) && showEmiInfo) {
            appContentsOp = appContentBO
                    .findAllByContentTypeAndActiveAndUserStatusOrUserStatus("knowledge", true, user.getStatus(),
                            "referred");
        } else {
            appContentsOp = appContentBO
                    .findAllByContentTypeAndActiveAndUserStatus("knowledge", true, user.getStatus());
        }
        if (appContentsOp.isPresent()) {
            List<AppContents> appContentsList = new ArrayList<>();
            appContentsOp.get().stream().forEach(contents -> {
                if (contents.getMinAndroidVersion() == null) {
                    appContentsList.add(contents);
                } else if (StringUtils.hasText(user.getAppVersion()) &&
                        new Integer(user.getAppVersion()) >= contents.getMinAndroidVersion()) {
                    appContentsList.add(contents);
                }
            });
            appContentsOp = Optional.of(appContentsList);
        }
        appContentsOp1.orElseGet(ArrayList::new).addAll(appContentsOp.orElseGet(ArrayList::new));
        return appContentsOp1.map(contents -> contents.stream().map(appContents -> AppContentCard.builder()
                .icon(appContents.getIcon())
                .landing(appContents.getLanding())
                .text(appContents.getText())
                .subText(appContents.getSubText())
                .title(appContents.getTitle())
                .type(appContents.getType())
                .build())
                .collect(Collectors.toList())).orElse(null);
    }

    private TxnSummary getTxnSummary(List<TransactionResponse> txns) {
        List<Float> txnAmts =
                txns.stream().filter(transactionResponse -> transactionResponse.getStatus().equals("success"))
                        .map(TransactionResponse::getAmount).collect(Collectors.toList());
        Float totalAmt = 0.0f;
        for (Float txnAmt : txnAmts) {
            totalAmt += txnAmt;
        }
        return TxnSummary.builder().amount((double) totalAmt).txnCount(txnAmts.size()).build();
    }

    private DataDashBoard getDataDashboard(List<TransactionResponse> txns) {
        if (Objects.isNull(txns)){
            return DataDashBoard.builder().ccPer(0.0).dcPer(0.0).tenures(new HashMap<>()).build();
        }
        else{
            List<TransactionResponse> successTxns =
                    txns.stream().filter(transactionResponse -> transactionResponse.getStatus().equals("success"))
                            .collect(Collectors.toList());
            if (successTxns.size() == 0) {
                return DataDashBoard.builder().ccPer(0.0).dcPer(0.0).tenures(new HashMap<>()).build();
            }
            double ccPer = (double) successTxns.stream()
                    .filter(transactionResponse -> transactionResponse.getCardType() != null &&
                            transactionResponse.getCardType().equals("CREDIT")).count() /
                    txns.stream().filter(transactionResponse -> transactionResponse.getStatus().equals("success")).count();
            double dcPer = 1 - ccPer;
            Map<Integer, Integer> map = new HashMap<>();
            for (Integer tenure : Arrays.asList(3, 6, 9, 12, 18, 24)) {
                map.put(tenure, getByTenure(txns, tenure));
            }
            return DataDashBoard.builder().ccPer(ccPer).dcPer(dcPer).tenures(map).build();
        }
    }

    private Integer getByTenure(List<TransactionResponse> txns, int tenure) {
        return (int) txns.stream().filter(transactionResponse -> transactionResponse.getStatus().equals("success"))
                .filter(transactionResponse -> transactionResponse.getTenure() == tenure).count();
    }

    private List<AppContentCard> getBanners(MerchantUser user) {
        Optional<List<AppContents>> appContentsOp =
                appContentBO.findAllByContentTypeAndActiveAndUserStatus("banner", true, user.getStatus());
        List<String> brandIds = null;
        if (user.getParams() != null) {
            brandIds = Util.getCombinedBrandIds(user.getParams().getBrandId(), user.getParams().getBrandIds());
        }
        if (appContentsOp.isPresent() && !CollectionUtils.isEmpty(brandIds)) {
            List<AppContents> appContentsList = new ArrayList<AppContents>();
            Set<String> iconSet = new HashSet<>();
            for (String brandId : brandIds) {
                List<AppContents> matchList = appContentsOp.get().stream()
                        .filter(content -> content.getEntityId() != null && null != user.getParams() &&
                                content.getEntityId().equalsIgnoreCase(brandId))
                        .filter(content -> !iconSet.contains(content.getIcon()))
                        .collect(Collectors.toList());
                appContentsList.addAll(matchList);
                matchList.forEach(content -> iconSet.add(content.getIcon()));
            }
            appContentsList.sort(Comparator.comparing(AppContents::getOrder));
            return appContentsList.stream().map(appContents -> AppContentCard.builder()
                    .icon(appContents.getIcon())
                    .landing(appContents.getLanding())
                    .text(appContents.getText())
                    .subText(appContents.getSubText())
                    .title(appContents.getTitle())
                    .type(appContents.getType())
                    .build())
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }


    private StoreUserHomeData getStoreUserHomeData(String storeUserId) {
        Float total = 0f;
        Double totalRewardEarned = 0d;
        Boolean isAccountDetailsAvail = false;
        try {
            StoreUserTransaction storeUserTransaction = paymentServiceBO.getTransaction(storeUserId,
                    StoreUserTransactionStatusReq.builder()
                            .transFrom(DateUtil.getFirstDayOfCurrentMonth())
                            .transTo(DateUtil.getToday())
                            .status("success")
                            .build());
            if (storeUserTransaction != null &&
                    !CollectionUtils.isEmpty(storeUserTransaction.getTransactionResponses())) {
                for (TransactionResponse transactionResponse : storeUserTransaction.getTransactionResponses()) {
                    total = total + transactionResponse.getAmount();
                }
            }
            AdminAuthUser auu = adminAuthUserBO.findById(storeUserId);
            if (null != auu && null != auu.getAccountDetails()) {
                isAccountDetailsAvail = StringUtils.hasText(auu.getAccountDetails().getAccountNumber()) ||
                        StringUtils.hasText(auu.getAccountDetails().getVpa());
            }
            totalRewardEarned = storeUserRewardsBO.getTotalProcessedRewards(storeUserId);
        } catch (Exception e) {
            LOGGER.error("Exception occurred : {} ", e.getMessage());
        }
        return StoreUserHomeData.builder()
                .targetTransaction(100000.0f)
                .rewardAmount(1000.0f)
                .currentMonthTransactions(total)
                .isAccountDetailsAvailable(isAccountDetailsAvail)
                .totalRewardsEarned(totalRewardEarned)
                .build();
    }
}
