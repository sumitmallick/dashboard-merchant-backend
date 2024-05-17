package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.commons.bo.ConsumerService;
import com.freewayemi.merchant.commons.bo.NotificationService;
import com.freewayemi.merchant.commons.bo.PaymentOptionsBO;
import com.freewayemi.merchant.commons.bo.PaymentServiceBO;
import com.freewayemi.merchant.commons.bo.eligibility.EligibilityBO;
import com.freewayemi.merchant.commons.bo.eligibility.EligibilityResponse;
import com.freewayemi.merchant.commons.dto.TransactionResponse;
import com.freewayemi.merchant.commons.dto.*;
import com.freewayemi.merchant.commons.dto.refund.RefundResponse;
import com.freewayemi.merchant.commons.dto.refund.RefundTransactionRequest;
import com.freewayemi.merchant.commons.entity.PaymentProviderInfo;
import com.freewayemi.merchant.commons.exception.MerchantException;
import com.freewayemi.merchant.commons.ntbservice.bo.NtbService;
import com.freewayemi.merchant.commons.type.*;
import com.freewayemi.merchant.commons.utils.paymentConstants;
import com.freewayemi.merchant.commons.utils.Util;

import com.freewayemi.merchant.dto.EligibileBandInfo;
import com.freewayemi.merchant.dto.request.BandRequest;
import com.freewayemi.merchant.dto.request.ProviderConfigRequest;

import com.freewayemi.merchant.dto.*;

import com.freewayemi.merchant.dto.request.ValidateOtpRequest;
import com.freewayemi.merchant.dto.response.*;
import com.freewayemi.merchant.entity.Eligibilities;
import com.freewayemi.merchant.entity.MerchantOffer;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.enums.EligibilityResponseCode;
import com.freewayemi.merchant.repository.EligibilitiesRepository;
import com.freewayemi.merchant.service.MerchantService;
import com.freewayemi.merchant.service.PaymentOpsService;
import com.freewayemi.merchant.type.MerchantConstants;
import com.freewayemi.merchant.type.NtbEligibilityResponseCode;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.freewayemi.merchant.commons.utils.paymentConstants.payment_WEB_URL_V3;

@Component
public class MerchantEligibilitiesBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantEligibilitiesBO.class);

    private final EligibilitiesRepository eligibilitiesRepository;
    private final PaymentServiceBO paymentServiceBO;
    private final NotificationService notificationService;
    private final MerchantTransactionBO merchantTransactionBO;
    private final Boolean isProduction;
    private final PaymentOptionsBO paymentOptionsBO;
    private final OfferBO offerBO;
    private final EligibilityBO eligibilityBO;
    private final BankInterestBO bankInterestBO;
    private final SchemeConfigBO schemeConfigBO;
    private final ConsumerService consumerService;
    private final NtbService ntbService;
    private final String baseUrl;
    private final Integer pollingInterval;

    @Autowired
    public MerchantEligibilitiesBO(EligibilitiesRepository eligibilitiesRepository, PaymentServiceBO paymentServiceBO,
                                   NotificationService notificationService, MerchantTransactionBO merchantTransactionBO,
                                   @Value("${payment.deployment.env}") String env, PaymentOptionsBO paymentOptionsBO,
                                   OfferBO offerBO, EligibilityBO eligibilityBO, BankInterestBO bankInterestBO,
                                   SchemeConfigBO schemeConfigBO, ConsumerService consumerService, NtbService ntbService,
                                   @Value("${payment.base.url}") String baseUrl,
                                   @Value("${payment.polling.interval}") Integer pollingInterval) {
        this.eligibilitiesRepository = eligibilitiesRepository;
        this.paymentServiceBO = paymentServiceBO;
        this.notificationService = notificationService;
        this.merchantTransactionBO = merchantTransactionBO;
        this.isProduction = paymentConstants.PRODENV.equals(env);
        this.paymentOptionsBO = paymentOptionsBO;
        this.offerBO = offerBO;
        this.eligibilityBO = eligibilityBO;
        this.bankInterestBO = bankInterestBO;
        this.schemeConfigBO = schemeConfigBO;
        this.consumerService = consumerService;
        this.ntbService = ntbService;
        this.baseUrl = baseUrl;
        this.pollingInterval = pollingInterval;
    }

    public void save(Eligibilities eligibilities) {
        eligibilitiesRepository.save(eligibilities);
    }

    public Eligibilities findById(String id) {
        Optional<Eligibilities> optional = eligibilitiesRepository.findById(id);
        return optional.orElse(null);
    }

    public CheckEligibilityResponse getCheckEligibilityWithOtp(MerchantUser mu, CheckEligibilityRequest request) {
        try {
            String otp = Util.generateOtp(isProduction);
            String hashedOtp = BCrypt.hashpw(otp, BCrypt.gensalt());
            notificationService.sendOTP(otp, request.getMobile(), false, null, false);
            long otpExpiry = System.currentTimeMillis() + 15 * 60 * 1000;

            Eligibilities eligibilities = new Eligibilities();
            eligibilities.setMerchantId(String.valueOf(mu.getId()));
            eligibilities.setName(request.getConsumerName());
            eligibilities.setEmail(request.getEmail());
            eligibilities.setAmount(request.getAmount());
            eligibilities.setMobile(request.getMobile());
            eligibilities.setSource(EligibilityApiType.MERCHANT_ELIGIBILITY_API_WITH_OTP.name());
            eligibilities.setAddress(request.getAddress());
            eligibilities.setCustomParams(request.getCustomParams());
            eligibilities.setOtp(hashedOtp);
            eligibilities.setOtpExpiry(otpExpiry);
            eligibilities.setProviderGroup(request.getProviderGroup());
            save(eligibilities);
            return CheckEligibilityResponse.builder()
                    .code(EligibilityResponseCode.SUCCESS_WITH_OTP.getCode())
                    .message(EligibilityResponseCode.SUCCESS_WITH_OTP.getMessage())
                    .paymentRefNo(String.valueOf(eligibilities.getId()))
                    .build();
        } catch (Exception e) {
            LOGGER.error("Exception occurred while validating otp: ", e);
        }
        return CheckEligibilityResponse.builder()
                .code(EligibilityResponseCode.FAILED_10.getCode())
                .message(EligibilityResponseCode.FAILED_10.getMessage())
                .build();
    }

    private List<PaymentProviderEnum> commonProviders(List<PaymentProviderInfo> allowedProviders,
                                                      List<PaymentProviderInfo> supportedProviders) {
        return supportedProviders.stream()
                .filter(supportedProvider -> !supportedProvider.getDisabled())
                .filter(configuredProvider -> allowedProviders.stream()
                        .anyMatch(allowedProvider ->
                                configuredProvider.getProvider().equals(allowedProvider.getProvider()) &&
                                        configuredProvider.getBank().equals(allowedProvider.getBank()) &&
                                        configuredProvider.getType().equals(allowedProvider.getType())))
                .map(PaymentProviderInfo::getProvider)
                .collect(Collectors.toList());
    }

    private List<PaymentProviderEnum> getProviders(List<PaymentProviderInfo> supportedProviders, MerchantUser mu, String providerGroup) {
        List<PaymentProviderInfo> allowedProviders = new ArrayList<>();
        if (StringUtils.hasText(providerGroup)) {
            allowedProviders = MerchantService.getAllowedProviders(mu, providerGroup);
            if (!CollectionUtils.isEmpty(allowedProviders)) {
                return commonProviders(allowedProviders, supportedProviders);
            }
        }
        return supportedProviders.stream().map(PaymentProviderInfo::getProvider).collect(Collectors.toList());
    }

    public CheckEligibilityResponse checkEligibility(MerchantUser mu, CheckEligibilityRequest request, String source) {
        List<PaymentProviderInfo> providers = paymentServiceBO.getProvidersInfo(String.valueOf(mu.getId()));
        List<PaymentProviderEnum> supportedProviders = getProviders(providers, mu, request.getProviderGroup());
        LOGGER.info("supportedProviders: {}", supportedProviders);
        List<EligibilityResponse> eligibilityList =
                paymentServiceBO.getEligibilityCheck(supportedProviders, request.getAmount(), request.getMobile(), mu.getPartner());
        LOGGER.info("eligibilityList: {}", eligibilityList);
        List<EligibilityDetail> eligibilityDetails = getEligibilityDetails(eligibilityList, mu.getPartner(), mu);

        Eligibilities dbEligibilities =
                populateEligibilities(String.valueOf(mu.getId()), eligibilityList, supportedProviders,
                        getConsumerName(request), request.getEmail(), request.getAmount(), request.getMobile(), source,
                        request.getAddress(), request.getCustomParams(), request.getPan(), request.getDob(),
                        request.getAnnualIncome());

        save(dbEligibilities);
        LOGGER.info("eligibilityList: {}", eligibilityList);
        String instantEmiEligibility = getPreApprovedEligibilities(eligibilityList);
        String ntbEmiEligibility = getNtbEligibilities(eligibilityList);
        String ccEmiAvailability = "N";
        if (EligibilityBO.isCreditCardEmiAvailable(supportedProviders)) {
            ccEmiAvailability = "Y";
        }
        if (Objects.nonNull(mu.getParams()) && Boolean.TRUE.equals(mu.getParams().getSplitEligibilities())) {
            return getCheckEligibilityResponseWithSplitElgibilities(mu, instantEmiEligibility, ccEmiAvailability, ntbEmiEligibility, eligibilityDetails);
        }
        instantEmiEligibility = getInstantEligibilities(eligibilityList, mu);
        ccEmiAvailability = "N";
        if (EligibilityBO.isCreditCardEmiAvailable(supportedProviders)) {
            ccEmiAvailability = "Y";
        }
        return getCheckEligibilityResponseWithOutSplitElgibilities(mu, instantEmiEligibility, ccEmiAvailability, ntbEmiEligibility, eligibilityDetails);
    }

    public CheckEligibilityResponse getCheckEligibilityResponseWithSplitElgibilities(MerchantUser mu, String instantEmiEligibility,
                                                                                     String ccEmiAvailability, String ntbEmiEligibility, List<EligibilityDetail> eligibilityDetails) {
        return null != mu.getIsSeamless() && mu.getIsSeamless() ? CheckEligibilityResponse.builder()
                .preApprovedEligibility(instantEmiEligibility)
                .ccEmiAvailability(ccEmiAvailability)
                .ntbEligibility(ntbEmiEligibility)
                .eligibilityDetails(eligibilityDetails)
                .build() :
                (Objects.nonNull(mu.getParams().getDisplayEligibilityBand())
                        && Boolean.TRUE.equals(mu.getParams().getDisplayEligibilityBand().getApiIntegration())) ?
                        CheckEligibilityResponse.builder()
                                .preApprovedEligibility(instantEmiEligibility)
                                .ccEmiAvailability(ccEmiAvailability)
                                .ntbEligibility(ntbEmiEligibility)
                                .eligibilityDetails(eligibilityDetails)
                                .build() :
                        CheckEligibilityResponse.builder()
                                .preApprovedEligibility(instantEmiEligibility)
                                .ccEmiAvailability(ccEmiAvailability)
                                .ntbEligibility(ntbEmiEligibility)
                                .build();
    }

    public CheckEligibilityResponse getCheckEligibilityResponseWithOutSplitElgibilities(MerchantUser mu, String instantEmiEligibility,
                                                                                        String ccEmiAvailability, String ntbEmiEligibility, List<EligibilityDetail> eligibilityDetails) {
        return null != mu.getIsSeamless() && mu.getIsSeamless() ? CheckEligibilityResponse.builder()
                .instantEmiEligibility(instantEmiEligibility)
                .ccEmiAvailability(ccEmiAvailability)
                .eligibilityDetails(eligibilityDetails)
                .build() :
                Objects.nonNull(mu.getParams())
                        && Objects.nonNull(mu.getParams().getDisplayEligibilityBand())
                        && Boolean.TRUE.equals(mu.getParams().getDisplayEligibilityBand().getApiIntegration()) ?
                        CheckEligibilityResponse.builder()
                                .instantEmiEligibility(instantEmiEligibility)
                                .ccEmiAvailability(ccEmiAvailability)
                                .eligibilityDetails(eligibilityDetails)
                                .build() :
                        CheckEligibilityResponse.builder()
                                .instantEmiEligibility(instantEmiEligibility)
                                .ccEmiAvailability(ccEmiAvailability)
                                .build();
    }

    private String getPreApprovedEligibilities(List<EligibilityResponse> eligibilityList) {
        String instantEmiEligibility = "N";
        for (EligibilityResponse eligibilityResponse : eligibilityList) {
            if (eligibilityResponse.getEligible() && !CardTypeEnum.NTB.name().equals(eligibilityResponse.getCardType())) {
                instantEmiEligibility = "Y";
                break;
            }
        }
        return instantEmiEligibility;
    }

    private String getNtbEligibilities(List<EligibilityResponse> eligibilityList) {
        String ntbEmiEligibility = "N";
        for (EligibilityResponse eligibilityResponse : eligibilityList) {
            if (eligibilityResponse.getEligible() && CardTypeEnum.NTB.name().equals(eligibilityResponse.getCardType())) {
                ntbEmiEligibility = "Y";
                break;
            }
        }
        return ntbEmiEligibility;
    }

    private String getInstantEligibilities(List<EligibilityResponse> eligibilityList, MerchantUser mu) {
        String instantEmiEligibility = getPreApprovedEligibilities(eligibilityList);

        LOGGER.info("instantEmiEligibility: {}", instantEmiEligibility);
        if ("N".equals(instantEmiEligibility)) {
            if (Objects.nonNull(mu.getParams()) && (Objects.isNull(mu.getParams().getDisableNTBEligibility()) || Boolean.FALSE.equals(mu.getParams().getDisableNTBEligibility()))) {
                if (eligibilityList.stream().anyMatch(eligibilityResponse -> (CardTypeEnum.NTB.name().equals(eligibilityResponse.getCardType())) && eligibilityResponse.getEligible())) {
                    instantEmiEligibility = "Y";
                }
            } else {
                LOGGER.info("Based on disable Ntb eligibility flag for the merchant. instantEmiEligibility value was No");
            }
        }
        return instantEmiEligibility;
    }

    public Map<String, BandInfo> getBandEligibleBankResponse(List<EligibilityResponse> eligibilities) {
        LOGGER.info("eligibilities: {}", eligibilities);
        Map<String, String> bandEligibleBank = new HashMap<>();
        for (EligibilityResponse eligibility : eligibilities) {
            if (Objects.nonNull(eligibility) && Boolean.TRUE.equals(eligibility.getEligible()) && StringUtils.hasText(eligibility.getBand()) && StringUtils.hasText(eligibility.getProvider())) {
                bandEligibleBank.put(eligibility.getBand() + "_" + eligibility.getProvider(), eligibility.getProvider());
            }
        }
        Map<String, BandInfo> bandEligibleBankResponse = new HashMap<>();
        if (!bandEligibleBank.isEmpty()) {
            ProviderConfigRequest providerConfigRequest = ProviderConfigRequest.builder().searchType("PROVIDER_BAND").build();
            List<BandRequest> bandRequests = new ArrayList<>();
            for (Map.Entry<String, String> entry : bandEligibleBank.entrySet()) {
                bandRequests.add(BandRequest.builder().band(entry.getKey().split("_")[0]).provider(entry.getValue()).build());
            }
            providerConfigRequest.setProviderBands(bandRequests);
            ProviderConfigResponse providerConfigResponse = paymentServiceBO.getProviderConfig(providerConfigRequest);
            LOGGER.info("providerConfigResponse: {}", providerConfigResponse);
            if (Objects.nonNull(providerConfigResponse) && !CollectionUtils.isEmpty(providerConfigResponse.getProviderConfigs())) {
                for (ProviderConfig providerConfig : providerConfigResponse.getProviderConfigs()) {
                    bandEligibleBankResponse.put(providerConfig.getPaymentProvider() + "_" + providerConfig.getBandInfo().getBand(),
                            providerConfig.getBandInfo());
                }
            }
        }
        LOGGER.info("bandEligibleBankResponse: {}", bandEligibleBankResponse);
        return bandEligibleBankResponse;
    }

    private List<EligibilityDetail> getEligibilityDetails(List<EligibilityResponse> eligibilities, String partner, MerchantUser merchantUser) {
        List<EligibilityDetail> eligibilityDetails = new ArrayList<>();
        Map<String, ProviderMasterConfigInfo> providerMasterConfigInfoMap = schemeConfigBO.constructProviderMasterConfigMap(partner);
        Map<String, BandInfo> bandEligibleBankResponse = getBandEligibleBankResponse(eligibilities);
        for (EligibilityResponse eligibility : eligibilities) {
            List<Integer> tenures = Util.findAllTenuresInt("DEBIT", eligibility.getBankCode(), providerMasterConfigInfoMap);
            List<PaymentOption> paymentOptions = new ArrayList<>();
            for (Integer tenure : tenures) {
                Float interest = Util.getIRR("DEBIT", eligibility.getBankCode(), tenure, providerMasterConfigInfoMap);
                paymentOptions.add(PaymentOption.builder().interestRate(interest).tenure(tenure).build());
            }
            if (Objects.nonNull(merchantUser) && Objects.nonNull(merchantUser.getParams())
                    && Objects.nonNull(merchantUser.getParams().getDisplayEligibilityBand())
                    && Boolean.TRUE.equals(merchantUser.getParams().getDisplayEligibilityBand().getApiIntegration())
                    && bandEligibleBankResponse.containsKey(eligibility.getProvider() + "_" + eligibility.getBand())) {
                BandInfo bandInfo = bandEligibleBankResponse.get(eligibility.getProvider() + "_" + eligibility.getBand());
                eligibilityDetails.add(EligibilityDetail.builder()
                        .bankCode(eligibility.getBankCode())
                        .bandInfo(EligibileBandInfo.builder()
                                .minEligibleAmount(bandInfo.getMinAmount())
                                .maxEligibleAmount(bandInfo.getMaxAmount()).build())
                        .cardType("DEBIT")
                        .paymentOptions(paymentOptions)
                        .build());
            } else {
                eligibilityDetails.add(EligibilityDetail.builder()
                        .bankCode(eligibility.getBankCode())
                        .cardType("DEBIT")
                        .paymentOptions(paymentOptions)
                        .build());
            }

            if (BankEnum.CL_ICICI.getCode().equalsIgnoreCase(eligibility.getBankCode())) {
                List<Integer> cardlessTenures =
                        Util.findAllTenuresInt("CARDLESS", eligibility.getBankCode(), providerMasterConfigInfoMap);
                for (Integer tenure : cardlessTenures) {
                    Float interest = Util.getIRR("CARDLESS", eligibility.getBankCode(), tenure, providerMasterConfigInfoMap);
                    paymentOptions.add(PaymentOption.builder().interestRate(interest).tenure(tenure).build());
                }
                if (Objects.nonNull(merchantUser) && Objects.nonNull(merchantUser.getParams())
                        && Objects.nonNull(merchantUser.getParams().getDisplayEligibilityBand())
                        && Boolean.TRUE.equals(merchantUser.getParams().getDisplayEligibilityBand().getApiIntegration()) &&
                        bandEligibleBankResponse.containsKey(eligibility.getProvider() + "_" + eligibility.getBand())) {
                    BandInfo bandInfo = bandEligibleBankResponse.get(eligibility.getProvider() + "_" + eligibility.getBand());
                    eligibilityDetails.add(EligibilityDetail.builder()
                            .bankCode(eligibility.getBankCode())
                            .bandInfo(EligibileBandInfo.builder()
                                    .minEligibleAmount(bandInfo.getMinAmount())
                                    .maxEligibleAmount(bandInfo.getMaxAmount()).build())
                            .cardType("DEBIT")
                            .paymentOptions(paymentOptions)
                            .build());
                } else {
                    eligibilityDetails.add(EligibilityDetail.builder()
                            .bankCode(eligibility.getBankCode())
                            .cardType("CARDLESS")
                            .paymentOptions(paymentOptions)
                            .build());
                }
            }

        }
        return eligibilityDetails;
    }

    private Eligibilities populateEligibilities(String merchantId, List<EligibilityResponse> eligibilityResponses,
                                                List<PaymentProviderEnum> supportedProviders, String consumerName,
                                                String email, Float amount, String mobile, String source,
                                                Address address, Map<String, String> customParams, String pan,
                                                String dob, String annualIncome) {
        Eligibilities eligibilities = new Eligibilities();
        eligibilities.setMerchantId(merchantId);
        eligibilities.setEligibilities(eligibilityResponses);
        eligibilities.setSupportedProviders(supportedProviders);
        eligibilities.setName(consumerName);
        eligibilities.setEmail(email);
        eligibilities.setAmount(amount);
        eligibilities.setMobile(mobile);
        eligibilities.setSource(source);
        eligibilities.setAddress(address);
        eligibilities.setCustomParams(customParams);
        eligibilities.setPan(pan);
        eligibilities.setDob(dob);
        eligibilities.setAnnualIncome(annualIncome);
        return eligibilities;
    }

    public CheckEligibilityResponse validateOtpAndSendEligibilities(MerchantUser mu, ValidateOtpRequest request) {
        try {
            Eligibilities dbEligibilities = findById(request.getpaymentRefNo());
            if (Objects.isNull(dbEligibilities)) {
                return CheckEligibilityResponse.builder()
                        .code(EligibilityResponseCode.FAILED_13.getCode())
                        .message(EligibilityResponseCode.FAILED_13.getMessage())
                        .build();
            }
            Float amount = dbEligibilities.getAmount();
            if (!BCrypt.checkpw(request.getOtp(), dbEligibilities.getOtp())) {
                return CheckEligibilityResponse.builder()
                        .code(EligibilityResponseCode.FAILED_11.getCode())
                        .message(EligibilityResponseCode.FAILED_11.getMessage())
                        .build();
            }
            if (System.currentTimeMillis() > dbEligibilities.getOtpExpiry()) {
                return CheckEligibilityResponse.builder()
                        .code(EligibilityResponseCode.FAILED_12.getCode())
                        .message(EligibilityResponseCode.FAILED_12.getMessage())
                        .build();
            }
            List<PaymentProviderInfo> providers = paymentServiceBO.getProvidersInfo(String.valueOf(mu.getId()));
            List<PaymentProviderEnum> supportedProviders = getProviders(providers, mu, dbEligibilities.getProviderGroup());

            List<EligibilityResponse> eligibilityList =
                    paymentServiceBO.getEligibilityCheck(supportedProviders, amount, dbEligibilities.getMobile(), mu.getPartner());
            dbEligibilities.setSupportedProviders(supportedProviders);
            dbEligibilities.setEligibilities(eligibilityList);
            save(dbEligibilities);
            List<EligibilityDetail> eligibilityDetails = new ArrayList<>();
            boolean provideEligibilityByBank = Util.isNotNull(mu) && Util.isNotNull(mu.getParams()) &&
                    Util.isNotNull(mu.getParams().getProvideEligibilityByBank()) &&
                    mu.getParams().getProvideEligibilityByBank();
            if (provideEligibilityByBank) {
                List<MerchantOffer> merchantOffers = mu.getOffers();
                List<OfferResponse> pgOffers = offerBO.getPgMerchantOffers(mu.getId().toString());
                List<String> eligibilies = eligibilityBO.findAll(supportedProviders, eligibilityList);
                TransactionResponse tr =
                        new TransactionResponse(null, null, null, null, null, null, null, null, null, amount, null,
                                null, null, null, null, null, null, mu.getShopName(), null, null, null, null, null, null,
                                null, null, null, null, null, null, null, null == merchantOffers ? null
                                : merchantOffers.stream()
                                .map(o -> Util.getPriceResponse("o.getId()", o.getSubvention(), o.getTenure(),
                                        amount))
                                .collect(Collectors.toList()), null, null, null, null, pgOffers, false, mu.getIsConvFee(),
                                Objects.nonNull(mu.getIsConvFee()) && mu.getIsConvFee() ? mu.getConvFeeRates() : new ArrayList<>(), mu.getParams(), null, String.valueOf(mu.getId()), null, null, eligibilies, null,
                                false, null, null, null, null, null, null, false, null, null, null, false,
                                AdditionInfo.builder()
                                        .cardLastFourDigit("")
                                        .availableLimit("")
                                        .giftVoucherId(null)
                                        .txnInvoiceId(null)
                                        .banksMaxEligibilityTenure(null)
                                        .cardId(null)
                                        .cashbackStatus(null)
                                        .expectedCashbackDate(null)
                                        .cxBuyingInsuranceReportUrl(null)
                                        .dcMaxTenure(null)
                                        .ccMaxTenure(null)
                                        .brandInfo(null)
                                        .payLinkCreateBy(null)
                                        .build(), null, false, null, null, null, null, null, null, null, null, null,
                                null, null, null, null, null, null, null, null, null, null, null, null, null,
                                bankInterestBO.getBankInterestByMerchantId(mu.getId().toString(), mu.getDisplayId()),
                                null, null, null, null, null,
                                null, null, null, mu.getPartner(), null, null, null, null, null, null, null, null, null);
                Map<String, List<PriceResponse>> cardOffers = paymentOptionsBO.getPgPaymentOptionsV2(tr);
                List<String> eligibilitiesList = tr.getEligibilities();
                if (!CollectionUtils.isEmpty(eligibilitiesList)) {
                    for (String bankCode : eligibilitiesList) {
                        String eligibilityBankCode = "";
                        String cardType = CardTypeEnum.DEBIT.getCardType();
                        if (BankEnum.CL_ICICI.getCode().equalsIgnoreCase(bankCode) ||
                                BankEnum.CL_ICICI_KYC.getCode().equalsIgnoreCase(bankCode)) {
                            cardType = CardTypeEnum.CARDLESS.getCardType();
                        }
                        eligibilityBankCode = "D_" + bankCode;
                        List<PriceResponse> priceResponses = cardOffers.get(eligibilityBankCode);
                        if (!CollectionUtils.isEmpty(priceResponses)) {
                            List<PaymentOption> paymentOptions = new ArrayList<>();
                            for (PriceResponse priceResponse : priceResponses) {
                                paymentOptions.add(PaymentOption.builder()
                                        .interestRate(priceResponse.getIrr())
                                        .tenure(priceResponse.getTenure())
                                        .emiAmount(priceResponse.getEmi())
                                        .totalPayableAmount(priceResponse.getEmi() * priceResponse.getTenure())
                                        .build());
                            }
                            if (Objects.nonNull(mu.getParams()) && Boolean.TRUE.equals(mu.getParams().getSplitEligibilities())) {
                                eligibilityDetails.add(EligibilityDetail.builder()
                                        .bankCode(bankCode)
                                        .cardType(cardType)
                                        .preApprovedEligibility(getPreApprovedEligibilities(eligibilityList))
                                        .ntbEligibility(getNtbEligibilities(eligibilityList))
                                        .paymentOptions(paymentOptions)
                                        .build());
                            } else {
                                eligibilityDetails.add(EligibilityDetail.builder()
                                        .bankCode(bankCode)
                                        .cardType(cardType)
                                        .instantEmiEligibility(getInstantEligibilities(eligibilityList, mu))
                                        .paymentOptions(paymentOptions)
                                        .build());
                            }
                        }
                    }
                }
            }
//            else {
//                eligibilityDetails = getEligibilityDetails(eligibilityList);
//            }

            String instantEmiEligibility = getPreApprovedEligibilities(eligibilityList);
            String ntbEmiEligibility = getNtbEligibilities(eligibilityList);
            String ccEmiAvailability = "N";
            if (EligibilityBO.isCreditCardEmiAvailable(supportedProviders)) {
                ccEmiAvailability = "Y";
            }
            if (Objects.nonNull(mu.getParams()) && Boolean.TRUE.equals(mu.getParams().getSplitEligibilities())) {
                if ((null != mu.getIsSeamless() && mu.getIsSeamless()) || provideEligibilityByBank) {
                    return CheckEligibilityResponse.builder()
                            .code(EligibilityResponseCode.SUCCESS_WITH_OTP.getCode())
                            .message(EligibilityResponseCode.SUCCESS_WITH_OTP.getMessage())
                            .preApprovedEligibility(instantEmiEligibility)
                            .ccEmiAvailability(ccEmiAvailability)
                            .ntbEligibility(ntbEmiEligibility)
                            .eligibilityDetails(eligibilityDetails)
                            .build();
                } else {
                    return CheckEligibilityResponse.builder()
                            .code(EligibilityResponseCode.SUCCESS.getCode())
                            .message(EligibilityResponseCode.SUCCESS.getMessage())
                            .preApprovedEligibility(instantEmiEligibility)
                            .ccEmiAvailability(ccEmiAvailability)
                            .ntbEligibility(ntbEmiEligibility)
                            .build();
                }
            }

            instantEmiEligibility = getInstantEligibilities(eligibilityList, mu);
            ccEmiAvailability = "N";
            if (EligibilityBO.isCreditCardEmiAvailable(supportedProviders)) {
                ccEmiAvailability = "Y";
            }
            if ((null != mu.getIsSeamless() && mu.getIsSeamless()) || provideEligibilityByBank) {
                return CheckEligibilityResponse.builder()
                        .code(EligibilityResponseCode.SUCCESS_WITH_OTP.getCode())
                        .message(EligibilityResponseCode.SUCCESS_WITH_OTP.getMessage())
                        .instantEmiEligibility(instantEmiEligibility)
                        .ccEmiAvailability(ccEmiAvailability)
                        .eligibilityDetails(eligibilityDetails)
                        .build();
            } else {
                return CheckEligibilityResponse.builder()
                        .code(EligibilityResponseCode.SUCCESS.getCode())
                        .message(EligibilityResponseCode.SUCCESS.getMessage())
                        .instantEmiEligibility(instantEmiEligibility)
                        .ccEmiAvailability(ccEmiAvailability)
                        .build();
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred while validating otp: ", e);
        }
        return CheckEligibilityResponse.builder()
                .code(EligibilityResponseCode.FAILED_10.getCode())
                .message(EligibilityResponseCode.FAILED_10.getMessage())
                .build();
    }

    public CheckEligibilityResponse checkEligibilityWithCardDetails(MerchantUser mu, CheckEligibilityRequest request,
                                                                    String source) {
        List<PaymentProviderInfo> providers = paymentServiceBO.getProvidersInfo(String.valueOf(mu.getId()));
        List<PaymentProviderEnum> supportedProviders = getProviders(providers, mu, request.getProviderGroup());
        PgTransactionResponse pgTxnResp = null;
        if (paymentConstants.DEBIT.equalsIgnoreCase(request.getCardInfo().getType())) {
            if (BankEnum.HDFC.getBankName().equalsIgnoreCase(request.getCardInfo().getBank()) ||
                    BankEnum.KKBK.getBankName().equalsIgnoreCase(request.getCardInfo().getBank())) {
                PgTransactionRequest pgTxnReq =
                        new PgTransactionRequest(request.getAmount(), request.getMobile(), "OrderId",
                                request.getEmail(), "", "", "", request.getConsumerName(), request.getAddress(),
                                request.getCustomParams(), false, request.getFirstName(), request.getMiddleName(),
                                request.getLastName(), "", "", false, request.getCardData(), request.getTenure(),
                                request.getCardInfo(), request.getProducts(), null, request.getPan(), request.getDob(),
                                null, null, request.getAnnualIncome(), null, null, request.getGender(),
                                request.getPartner(), request.getProviderGroup());
                pgTxnResp = merchantTransactionBO.createPgTransaction(mu, pgTxnReq, true,
                        TransactionSource.merchantEligibilityWithCard.name());
            }
        }
        String transactionId = StringUtils.isEmpty(pgTxnResp) ? Util.generatepaymentTxnId() : pgTxnResp.getpaymentTxnId();
        List<EligibilityResponse> eligibilityList =
                paymentServiceBO.getEligibilityWithCardDetails(transactionId, request);
        List<EligibilityDetail> eligibilityDetails = getEligibilityDetails(eligibilityList, mu.getPartner(), null);
        String consumerName = !Util.isEmptyString(request.getConsumerName()) ? request.getConsumerName()
                : request.getFirstName() + " " + request.getMiddleName() + " " + request.getLastName();

        Eligibilities dbEligibilities =
                populateEligibilities(String.valueOf(mu.getId()), eligibilityList, supportedProviders, consumerName,
                        request.getEmail(), request.getAmount(), request.getMobile(), source, request.getAddress(),
                        request.getCustomParams(), request.getPan(), request.getDob(), request.getAnnualIncome());
        save(dbEligibilities);


        String instantEmiEligibility = getPreApprovedEligibilities(eligibilityList);
        String ntbEmiEligibility = getNtbEligibilities(eligibilityList);
        String ccEmiAvailability = "N";
        if (EligibilityBO.isCreditCardEmiAvailable(supportedProviders)) {
            ccEmiAvailability = "Y";
        }
        if (Objects.nonNull(mu.getParams()) && Boolean.TRUE.equals(mu.getParams().getSplitEligibilities())) {
            return getCheckEligibilityResponseWithSplitElgibilities(mu, instantEmiEligibility, ccEmiAvailability, ntbEmiEligibility, eligibilityDetails);
        }

        instantEmiEligibility = getInstantEligibilities(eligibilityList, mu);
        ccEmiAvailability = "N";
        if (EligibilityBO.isCreditCardEmiAvailable(supportedProviders)) {
            ccEmiAvailability = "Y";
        }

        return getCheckEligibilityResponseWithOutSplitElgibilities(mu, instantEmiEligibility, ccEmiAvailability, ntbEmiEligibility, eligibilityDetails);

    }

    public void persistEligibilityDetails(CheckEligibilityRequest request) {
        Eligibilities eligibility = populateEligibilities(request.getMerchantId(), request.getEligibilityResponses(),
                request.getMerchantSupportedProviders(), getConsumerName(request), request.getEmail(),
                request.getAmount(), request.getMobile(), request.getSource(), request.getAddress(),
                request.getCustomParams(), request.getPan(), request.getDob(), request.getAnnualIncome());
        eligibilitiesRepository.save(eligibility);
    }

    private String getConsumerName(CheckEligibilityRequest request) {
        return !Util.isEmptyString(request.getConsumerName()) ? request.getConsumerName()
                : request.getFirstName() + " " + request.getMiddleName() + " " + request.getLastName();
    }

    public List<String> getDistinctValues() {
        List<String> result = new ArrayList<>();
        for (PaymentProviderEnum provider : PaymentProviderEnum.values()) {
            result.add(provider.getDisplayName());
        }
        return result;
    }

    public CheckEligibilityResponseV2 checkEligibilityV2(MerchantUser mu, CheckEligibilityRequest request,
                                                         String source) {
        List<PaymentProviderInfo> providers = paymentServiceBO.getProvidersInfo(String.valueOf(mu.getId()));
        List<PaymentProviderEnum> supportedProviders = getProviders(providers, mu, request.getProviderGroup());
        List<EligibilityResponse> eligibilityList =
                getEligibilityResponse(supportedProviders, request.getAmount(), request.getMobile(), mu.getPartner());

        save(populateEligibilities(String.valueOf(mu.getId()), eligibilityList, supportedProviders,
                getConsumerName(request), request.getEmail(), request.getAmount(), request.getMobile(), source,
                request.getAddress(), request.getCustomParams(), request.getPan(), request.getDob(),
                request.getAnnualIncome()));
        String ccEmiAvailability = MerchantConstants.N;
        if (EligibilityBO.isCreditCardEmiAvailable(supportedProviders)) {
            ccEmiAvailability = MerchantConstants.Y;
        }
        String instantEmiEligibility = getPreApprovedEligibilities(eligibilityList);
        String ntbEmiEligibility = getNtbEligibilities(eligibilityList);
        CheckEligibilityResponseV2 checkEligibilityResponseV2;
        if (Objects.nonNull(mu.getParams()) && Boolean.TRUE.equals(mu.getParams().getSplitEligibilities())) {
            checkEligibilityResponseV2 = CheckEligibilityResponseV2.builder()
                    .preApprovedEligibility(instantEmiEligibility)
                    .ntbEligibility(ntbEmiEligibility)
                    .ccEmiAvailability(ccEmiAvailability)
                    .build();
        } else {
            checkEligibilityResponseV2 = CheckEligibilityResponseV2.builder()
                    .instantEmiEligibility(getInstantEligibilities(eligibilityList, mu))
                    .ccEmiAvailability(ccEmiAvailability)
                    .build();
        }
        return Objects.nonNull(mu.getParams()) &&
                BooleanUtils.isTrue(mu.getParams().getIsEligibilitiesWithBankDetails())
                ? checkEligibilityResponseV2.toBuilder()
                .eligibilityDetails(getEligibilityDetailsV2(eligibilityList))
                .build() :
                (Objects.nonNull(mu.getParams().getDisplayEligibilityBand())
                        && Boolean.TRUE.equals(mu.getParams().getDisplayEligibilityBand().getApiIntegration())) ?
                        checkEligibilityResponseV2.toBuilder()
                                .eligibilityDetails(getEligibilityDetailsV2(eligibilityList))
                                .build() :
                        checkEligibilityResponseV2;
    }

    private List<EligibilityDetailV2> getEligibilityDetailsV2(List<EligibilityResponse> eligibilities) {
        return eligibilities.parallelStream().flatMap(eligibility -> {
            Stream.Builder<EligibilityDetailV2> eligibilityDetailStream = Stream.builder();
            if (BooleanUtils.isTrue(eligibility.getEligible())) {
                eligibilityDetailStream.add(EligibilityDetailV2.builder()
                        .bankCode(eligibility.getBankCode())
                        .bankName(BankEnum.getBankNameFromCode(eligibility.getBankCode()))
                        .cardType(CardTypeEnum.DEBIT.getCardType())
                        .build());
                if (BankEnum.CL_ICICI.getCode().equalsIgnoreCase(eligibility.getBankCode())) {
                    eligibilityDetailStream.add(EligibilityDetailV2.builder()
                            .bankCode(eligibility.getBankCode())
                            .bankName(BankEnum.getBankNameFromCode(eligibility.getBankCode()))
                            .cardType(CardTypeEnum.CARDLESS.getCardType())
                            .build());
                }
            }
            return eligibilityDetailStream.build();
        }).collect(Collectors.toList());
    }

    private List<PaymentProviderEnum> getSupportedProviders(String merchantId) {
        return paymentServiceBO.getProviders(merchantId);
    }

    private List<EligibilityResponse> getEligibilityResponse(List<PaymentProviderEnum> providers, Float amount,
                                                             String mobile, String partner) {
        return paymentServiceBO.getEligibilityCheck(providers, amount, mobile, partner);
    }


    public CheckNtbEligibilityResponse checkNtbEligibility(CheckNtbEligibilityRequest request, MerchantUser mu) {

        List<PaymentProviderEnum> supportedProviders;
        try {
            List<PaymentProviderInfo> providers = paymentServiceBO.getProvidersInfo(String.valueOf(mu.getId()));
            supportedProviders = getProviders(providers, mu, request.getProviderGroup());
        } catch (Exception ex) {
            throw new MerchantException(MerchantResponseCode.getByMessage(ex.getMessage()));
        }

        if (CollectionUtils.isEmpty(supportedProviders)) {
            throw new MerchantException(MerchantResponseCode.PROVIDER_NOT_SUPPORTED);
        } else {
            List<ProviderConsent> providerConsents = request.getConsent().getProviderConsents();
            for (ProviderConsent providerConsent : providerConsents) {
                if (!supportedProviders.contains(PaymentProviderEnum.getProviderEnumByBankCode(providerConsent.getProvider(), CardTypeEnum.NTB))) {
                    throw new MerchantException(MerchantResponseCode.PROVIDER_NOT_SUPPORTED);
                }
            }
        }

        List<String> profileStages = new ArrayList<>();
        if (Objects.nonNull(request.getCurrentAddress())) {
            profileStages.add("CurrentAddress");
        }
        if (Objects.nonNull(request.getEmploymentDetails())) {
            profileStages.add("EmploymentDetails");
        }
        if (Objects.nonNull(request.getPermanentAddress())) {
            profileStages.add("PermanentAddress");
        }
        ConsumerCreationRequest consumerCreationRequest = ConsumerCreationRequest.builder()
                .firstName(request.getFirstName())
                .fatherName(request.getFatherName())
                .dob(request.getDob())
                .currentAddress(request.getCurrentAddress())
                .email(request.getEmail())
                .pan(request.getPan())
                .lastName(request.getLastName())
                .middleName(request.getMiddleName())
                .mobile(request.getMobile())
                .gender(request.getGender())
                .permanentAddress(request.getPermanentAddress())
                .pinCode(request.getPinCode())
                .currentAddressSameAsPermanent(request.getCurrentAddressSameAsPermanent())
                .employmentDetails(request.getEmploymentDetails())
                .profileStages(profileStages)
                .maritalStatus(request.getMaritalStatus())
                .isEmailVerified(Objects.nonNull(request.getEmailConsent()))
                .build();

        ConsumerProfileResponse consumerProfileResponse = consumerService.getConsumerProfile(consumerCreationRequest, mu.getId().toString());
        PgTransactionRequest pgTransactionRequest = getPgTransactionRequest(request, consumerProfileResponse);
        PgTransactionResponse pgTransactionResponse;
        try {
            pgTransactionResponse = merchantTransactionBO.createPgTransaction(mu, pgTransactionRequest, false, "ntbCheckEligibilityApi");
        } catch (Exception ex) {
            throw new MerchantException(MerchantResponseCode.getByMessage(ex.getMessage(), request.getOrderId()), request.getOrderId());
        }
        if (Objects.nonNull(pgTransactionResponse)) {
            //save consents
            ConsentRequestV2 consentRequestV2 = new ConsentRequestV2("lenderEligibilityCheck", "NTBEligibilityAPI", request.getConsent().getIpAddress(), request.getConsent().getTimestamp(), pgTransactionResponse.getpaymentTxnId(), consumerProfileResponse.getConsumerId(), request.getConsent().getProviderConsents());
            ntbService.saveProvideConsent(consentRequestV2);
            if (Util.isNotNull(request.getMobileConsent())) {
                ConsentInfo mobileConsentInfo = ConsentInfo.builder().type("MOBILE").receiverMobile(request.getMobileConsent().getReceiverMobile()).content(request.getMobileConsent().getContent()).build();
                ConsentRequest mobileConsent = ConsentRequest.builder().ipAddress(request.getMobileConsent().getIpAddress())
                        .consentInfo(mobileConsentInfo).consumerId(consumerProfileResponse.getConsumerId()).
                        transactionId(pgTransactionResponse.getpaymentTxnId())
                        .timeStamp(request.getMobileConsent().getTimestamp())
                        .stage("EXTERNAL_LOAN_ELGIBILITY_CHECK").build();
                consumerService.saveMobileEmailConsent(mobileConsent, consumerProfileResponse.getConsumerId());
            } else {
                throw new MerchantException(MerchantResponseCode.REQUIRE_MOBILE_CONSENT);
            }
            if (Util.isNotNull(request.getEmailConsent())) {
                ConsentInfo emailConsentInfo = ConsentInfo.builder().type("EMAIL").receiverMobile(request.getEmailConsent().getReceiverEmail()).content(request.getEmailConsent().getContent()).build();
                ConsentRequest emailConsent = ConsentRequest.builder().ipAddress(request.getEmailConsent()
                                .getIpAddress()).consentInfo(emailConsentInfo).consumerId(consumerProfileResponse
                                .getConsumerId()).transactionId(pgTransactionResponse.getpaymentTxnId())
                        .timeStamp(request.getEmailConsent().getTimestamp())
                        .stage("EXTERNAL_LOAN_ELGIBILITY_CHECK").build();
                consumerService.saveMobileEmailConsent(emailConsent, consumerProfileResponse.getConsumerId());
            }

            List<String> bankCodes = request.getConsent().getProviderConsents().stream().map(ProviderConsent::getProvider).collect(Collectors.toList());
            EligibilityCheckRequest eligibilityCheckRequest = EligibilityCheckRequest.builder().bankCode(bankCodes)
                    .consumerId(consumerProfileResponse.getConsumerId())
                    .source("MERCHANTMS")
                    .transactionId(pgTransactionResponse.getpaymentTxnId()).build();
            ntbService.checkEligibility(eligibilityCheckRequest, consumerProfileResponse.getConsumerId());
            return new CheckNtbEligibilityResponse(0, "success", "Requested Successfully", new com.freewayemi.merchant.dto.TransactionResponse(pgTransactionResponse.getpaymentTxnId(), pollingInterval));
        }
        throw new MerchantException(MerchantResponseCode.INTERNAL_SERVER_ERROR);
    }

    private static PgTransactionRequest getPgTransactionRequest(CheckNtbEligibilityRequest request, ConsumerProfileResponse consumerProfileResponse) {
        if (Objects.isNull(consumerProfileResponse)) {
            throw new MerchantException(MerchantResponseCode.INTERNAL_SERVER_ERROR);
        }
        AddressDto addressDto = request.getCurrentAddress();
        Address address = new Address(addressDto.getPincode(), addressDto.getCity(), addressDto.getLine1(), addressDto.getLine2(), addressDto.getState(), addressDto.getCountry(), null, null, null, null);
        return new PgTransactionRequest(request.getAmount(), request.getMobile(), request.getOrderId(),
                request.getEmail(), request.getProductName(), null, request.getProductSkuCode(), request.getFirstName() + " " + request.getLastName(), address, request.getCustomParams(),
                null, request.getFirstName(), request.getMiddleName(), request.getLastName(), request.getReturnUrl(), request.getWebhookUrl(), false, null, null, null, request.getProducts(),
                null, request.getPan(), null, null, null, null, null, null, request.getGender(), null, request.getProviderGroup());
    }

    public ProviderEligibilityRes ntbEligibilityPolling(String transactionId, MerchantUser merchantUser) {
        TransactionResponse transactionResponse = paymentServiceBO.getNtbTransactionById(transactionId);
        ProviderEligibilityResponse providerEligibilityResponse = ntbService.poling(transactionId, transactionResponse.getConsumerId(), merchantUser.getId().toString());

        List<ProviderEligibilityData> providerEligibilityResponseBodies = getProviderEligibilityData(providerEligibilityResponse);

        ProviderEligibility providerEligibility = ProviderEligibility.builder().build();
        if (Objects.nonNull(providerEligibilityResponse) && "success".equals(providerEligibilityResponse.getStatus())) {
            providerEligibility.setLenderEligibilityList(providerEligibilityResponseBodies);
            providerEligibility.setCreditType(providerEligibilityResponse.getCreditType());
            providerEligibility.getLenderEligibilityList().stream().filter(Objects::nonNull)
                    .filter(lender -> Objects.nonNull(lender.getIsEligible()) && Boolean.TRUE.equals(lender.getIsEligible()))
                    .forEach(lenderEligibility -> lenderEligibility.setPaymentLink(baseUrl + payment_WEB_URL_V3 + transactionId));
        }
        NtbEligibilityResponseCode ntbEligibilityResponseCode =
                NtbEligibilityResponseCode.getByNtbCode(providerEligibilityResponse.getCode());
        return ProviderEligibilityRes.builder().data(providerEligibility)
                .code(ntbEligibilityResponseCode.getCode())
                .message(ntbEligibilityResponseCode.getMessage())
                .status(ntbEligibilityResponseCode.getStatus())
                .build();
    }

    private static List<ProviderEligibilityData> getProviderEligibilityData(ProviderEligibilityResponse providerEligibilityResponse) {
        List<ProviderEligibilityData> providerEligibilityResponseBodies = new ArrayList<>();
        if (Util.isNotNull(providerEligibilityResponse) && !CollectionUtils.isEmpty(providerEligibilityResponse.getEligibilities())) {
            for (ProviderEligibilityApiResponse providerEligibilityApiResponse : providerEligibilityResponse.getEligibilities()) {
                ProviderEligibilityData providerEligibilityData = new ProviderEligibilityData(providerEligibilityApiResponse.getProviderName(),
                        providerEligibilityApiResponse.getIsEligible(), providerEligibilityApiResponse.getIsAccountAggregationRequired());
                providerEligibilityResponseBodies.add(providerEligibilityData);
            }
        }
        return providerEligibilityResponseBodies;
    }

    public com.freewayemi.merchant.dto.RefundResponse requestRefund(String transactionId, RefundTransactionRequest refundTransactionRequest) {
        if(StringUtils.hasText(transactionId)) {
            TransactionResponse tr = merchantTransactionBO.processRefundNtbTransaction(transactionId, refundTransactionRequest);
            RefundResponse refundResponse = tr.getRefund();
            com.freewayemi.merchant.dto.RefundResponse response = new com.freewayemi.merchant.dto.RefundResponse();
            if (Objects.nonNull(refundResponse)) {
                if (!CollectionUtils.isEmpty(refundResponse.getRefunds())) {
                    RefundData refundData = new RefundData(refundResponse.getOrderId(), refundResponse.getpaymentTxnId(),
                            refundResponse.getRefunds(), refundResponse.getTimestamp());
                    response.setData(refundData);
                    response.setCode(NtbEligibilityResponseCode.REFUND_SUCCESS.getCode());
                    response.setMessage(NtbEligibilityResponseCode.REFUND_SUCCESS.getMessage());
                    response.setStatus(NtbEligibilityResponseCode.REFUND_SUCCESS.getStatus());
                    return response;
                }
            }
        }
        throw  new MerchantException(MerchantResponseCode.INTERNAL_SERVER_ERROR);
    }

}
