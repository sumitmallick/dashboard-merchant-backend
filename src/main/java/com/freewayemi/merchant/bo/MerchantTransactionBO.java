package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.bo.validator.ConditionType;
import com.freewayemi.merchant.bo.validator.ConditionsExecutor;
import com.freewayemi.merchant.bo.validator.MerchantValidator;
import com.freewayemi.merchant.commons.bo.*;
import com.freewayemi.merchant.commons.bo.eligibility.EligibilityBO;
import com.freewayemi.merchant.commons.bo.eligibility.EligibilityResponse;
import com.freewayemi.merchant.commons.dto.TransactionResponse;
import com.freewayemi.merchant.commons.dto.*;
import com.freewayemi.merchant.commons.dto.deliveryorder.DeliveryOrderResp;
import com.freewayemi.merchant.commons.dto.offer.BankInterestDto;
import com.freewayemi.merchant.commons.dto.payout.RefundPayoutRequest;
import com.freewayemi.merchant.commons.dto.payout.RefundPayoutResponse;
import com.freewayemi.merchant.commons.dto.refund.RefundInquiryResponse;
import com.freewayemi.merchant.commons.dto.refund.RefundTransactionRequest;
import com.freewayemi.merchant.commons.entity.Params;
import com.freewayemi.merchant.commons.entity.PaymentProviderInfo;
import com.freewayemi.merchant.commons.exception.FreewayCustomException;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.exception.MerchantException;
import com.freewayemi.merchant.commons.ntbservice.bo.NtbService;
import com.freewayemi.merchant.commons.juspay.CardInfo;
import com.freewayemi.merchant.commons.type.*;
import com.freewayemi.merchant.commons.utils.paymentConstants;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.*;
import com.freewayemi.merchant.dto.paymentOptions.ConsumerInfo;
import com.freewayemi.merchant.dto.paymentOptions.PaymentOptionsRequest;
import com.freewayemi.merchant.dto.paymentOptions.PaymentOptionsResponse;
import com.freewayemi.merchant.dto.request.EmailRequest;
import com.freewayemi.merchant.dto.response.ConsumerProfileConstants;
import com.freewayemi.merchant.dto.response.EnquiryTransactionResponse;
import com.freewayemi.merchant.dto.sales.TransactionOpsRequest;
import com.freewayemi.merchant.dto.sales.TransactionVolumeInfo;
import com.freewayemi.merchant.entity.*;
import com.freewayemi.merchant.repository.MerchantConfigsRepository;
import com.freewayemi.merchant.service.EmailService;
import com.freewayemi.merchant.service.MerchantService;
import com.freewayemi.merchant.service.PaymentOpsService;
import com.freewayemi.merchant.utils.Constants;
import com.freewayemi.merchant.utils.MerchantCommonUtil;
import com.freewayemi.merchant.utils.MerchantStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.Valid;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.freewayemi.merchant.commons.utils.Util.getFLoat;

@Component
public class MerchantTransactionBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantTransactionBO.class);

    private final PaymentServiceBO paymentServiceBO;
    private final MerchantUserBO merchantUserBO;
    private final String transactionUrl;
    private final OfferBO offerBO;
    private final ConsumerService consumerService;
    private final PaymentLinkServiceBO paymentLinkServiceBO;
    private final NotificationService notificationService;
    private final String baseUrl;
    private final PaymentOptionsBO paymentOptionsBO;
    private final MerchantOfferConfigBO merchantOfferConfigBO;
    private final StoreLinkServiceBO storeLinkServiceBO;
    private final BrandProductBO brandProductBO;
    private final AdminAuthUserBO adminAuthUserBO;
    private final ConditionsExecutor conditionsExecutor;
    private final PaymentOpsService paymentOpsService;
    private final MerchantDiscountRateBO merchantDiscountRateBO;
    private final MerchantValidator merchantValidator;
    private final EligibilityBO eligibilityBO;
    private final ConsumerProfileServiceBO consumerProfileServiceBO;
    private final BankInterestBO bankInterestBO;
    private final MerchantCommonUtil merchantCommonUtil;
    private final MerchantInstantDiscountConfigurationBO merchantInstantDiscountConfigurationBO;
    private final BrandBO brandBO;
    private final Boolean instantCashbackEnvEnabled;
    private final MerchantService merchantService;
    private final Boolean isProduction;

    private final MerchantConfigsRepository merchantConfigsRepository;
    private final EmailService emailService;
    private final MerchantMiddlewareBO merchantMiddlewareBO;
    private final NtbService ntbService;


    @Autowired
    public MerchantTransactionBO(PaymentServiceBO paymentServiceBO, MerchantUserBO merchantUserBO,
                                 @Value("${payment.transaction.url}") String transactionUrl, OfferBO offerBO,
                                 ConsumerService consumerService, PaymentLinkServiceBO paymentLinkServiceBO,
                                 NotificationService notificationService, @Value("${payment.base.url}") String baseUrl,
                                 PaymentOptionsBO paymentOptionsBO, MerchantOfferConfigBO merchantOfferConfigBO,
                                 StoreLinkServiceBO storeLinkServiceBO, BrandProductBO brandProductBO,
                                 AdminAuthUserBO adminAuthUserBO, ConditionsExecutor conditionsExecutor,
                                 PaymentOpsService paymentOpsService, MerchantDiscountRateBO merchantDiscountRateBO, MerchantValidator merchantValidator,
                                 EligibilityBO eligibilityBO, ConsumerProfileServiceBO consumerProfileServiceBO,
                                 BankInterestBO bankInterestBO, MerchantCommonUtil merchantCommonUtil,
                                 MerchantInstantDiscountConfigurationBO merchantInstantDiscountConfigurationBO,
                                 BrandBO brandBO,
                                 @Value("${instant.cashback.enabled}") Boolean instantCashbackEnvEnabled,
                                 MerchantService merchantService, @Value("${payment.deployment.env}") String env,
                                 MerchantConfigsRepository merchantConfigsRepository, EmailService emailService,
                                 MerchantMiddlewareBO merchantMiddlewareBO, NtbService ntbService) {
        this.paymentServiceBO = paymentServiceBO;
        this.merchantUserBO = merchantUserBO;
        this.transactionUrl = transactionUrl;
        this.offerBO = offerBO;
        this.consumerService = consumerService;
        this.paymentLinkServiceBO = paymentLinkServiceBO;
        this.notificationService = notificationService;
        this.baseUrl = baseUrl;
        this.paymentOptionsBO = paymentOptionsBO;
        this.merchantOfferConfigBO = merchantOfferConfigBO;
        this.storeLinkServiceBO = storeLinkServiceBO;
        this.brandProductBO = brandProductBO;
        this.adminAuthUserBO = adminAuthUserBO;
        this.conditionsExecutor = conditionsExecutor;
        this.paymentOpsService = paymentOpsService;
        this.merchantDiscountRateBO = merchantDiscountRateBO;
        this.merchantValidator = merchantValidator;
        this.eligibilityBO = eligibilityBO;
        this.consumerProfileServiceBO = consumerProfileServiceBO;
        this.bankInterestBO = bankInterestBO;
        this.merchantCommonUtil = merchantCommonUtil;
        this.merchantInstantDiscountConfigurationBO = merchantInstantDiscountConfigurationBO;
        this.brandBO = brandBO;
        this.instantCashbackEnvEnabled = instantCashbackEnvEnabled;
        this.merchantService = merchantService;
        this.isProduction = paymentConstants.PRODENV.equals(env);
        this.merchantConfigsRepository = merchantConfigsRepository;
        this.emailService = emailService;
        this.merchantMiddlewareBO = merchantMiddlewareBO;
        this.ntbService = ntbService;
    }

    private Integer getMaxTenureBasisProductId(TransactionRequest request, MerchantUser mu) {
        try {
            String productId = request.getBrandRequest().getBrandProductId();
            Integer mapping = mu.getParams().getProductMaxTenureMapping().getOrDefault(productId, null);
            if (null != mapping) {
                LOGGER.info("returning maxTenure as {} for product {}", mapping, productId);
                return mapping;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public TransactionResponse initiate(String merchant, @Valid TransactionRequest request, String sourceIp,
                                        String storeUserId, String partner) {
        MerchantUser mu = merchantUserBO.getUserById(merchant);
        if (!MerchantStatus.approved.name().equals(mu.getStatus())) {
            throw new FreewayException("merchant not approved.");
        }
        if (null != mu.getParams() && null != mu.getParams().getValidAmounts() &&
                mu.getParams().getValidAmounts().size() > 0) {
            boolean throwError = true;
            for (Float amount : mu.getParams().getValidAmounts()) {
                if (amount.floatValue() == request.getAmount().floatValue()) {
                    throwError = false;
                    break;
                }
            }
            if (throwError) {
                throw new FreewayException(
                        "Transaction for other products is restricted / Invalid, select product from dropdown only!");
            }
        }

        List<OfferResponse> offers;
        List<Mdr> mdrs;
        boolean isDynamicOffer = false;
        if (null != request.getDynamicOffersConfig() && null != request.getDynamicOffersConfig().getDynamicOffers()) {
            LOGGER.info("Transaction initiate request for merchant {} with request {}", merchant, request);
            isDynamicOffer = true;
            MerchantOfferConfig config = merchantOfferConfigBO.get(mu.getId().toString());
            MerchantUserBO.GetMdrsAndOffers getMdrsAndOffers =
                    new MerchantUserBO.GetMdrsAndOffers(mu.getId().toString(), request.getDynamicOffersConfig(),
                            config).invoke();
            mdrs = getMdrsAndOffers.getMdrs();
            DecimalFormat df = new DecimalFormat("0.000");
            offers = getMdrsAndOffers.getOffers()
                    .stream()
                    .map(offer -> new OfferResponse(null == offer.getId() ? null : offer.getId().toString(),
                            offer.getTenure(), Float.valueOf(df.format(offer.getSubvention())), null,
                            offer.getCardType(), offer.getProductId(), null, offer.getBankCode(), offer.getValidFrom(),
                            offer.getValidTo(), offer.getMinAmount(), null, null, null, null, null, null, null, null,
                            null, offer.getEffectiveTenure(), offer.getOfflineAdvanceEmiTenure(),
                            offer.getMinMarginDownPaymentAmount(), offer.getMaxMarginDownPaymentAmount(),
                            offer.getApplicableStates(), offer.getExclusionStates(), offer.getMaxAmount()))
                    .collect(Collectors.toList());
        } else {
            mdrs = mu.getMdrs();
            offers = offerBO.getPgMerchantOffers(mu.getId().toString());
        }

        if (null != offers && offers.size() > 0) {
            ConsumerResponse cr = consumerService.getOrCreateConsumer(request.getMobile());
            BrandInfo brandInfo = brandProductBO.getBrandInfo(request.getBrandRequest());
            BankInterestDto bankInterestDtoOnMerchant =
                    bankInterestBO.getBankInterestByMerchantId(mu.getId().toString(), mu.getDisplayId());
            BankInterestDto bankInterestDtoOnBrand = bankInterestBO.getBankInterestByBrandInfo(brandInfo);
            validateRequestAmount(request, brandInfo);
            AdminAuthUser auu = adminAuthUserBO.findById(storeUserId);
            PaymentTransactionRequest.MerchantInfo mi = PaymentTransactionRequest.MerchantInfo.builder()
                    .merchantName(mu.getShopName())
                    .merchantDisplayId(mu.getDisplayId())
                    .storeUserId(storeUserId)
                    .storeUserName(null != auu ? auu.getName() : "")
                    .storeUserMobile(null != auu ? auu.getMobile() : "")
                    .merchantMobile(mu.getMobile())
                    .merchantEmail(mu.getEmail())
                    .merchantType(mu.getType())
                    .merchantDeviceToken(mu.getDeviceToken())
                    .offerResponses(offers)
                    .mdrs(mdrs)
                    .merchantParams(mu.getParams())
                    .merchantOrderId(Util.generateUniqueNumber())
                    .downPaymentEnabled(mu.getDownPaymentEnabled())
                    .supportedDpProviders(mu.getSupportedDpProviders())
                    .category(mu.getCategory())
                    .subCategory(mu.getSubCategory())
                    .mccCode(mu.getMccCode())
                    .isDynamicOffer(isDynamicOffer)
                    .dynamicOffers(isDynamicOffer ? request.getDynamicOffersConfig().getDynamicOffers() : null)
                    .supportedDpProviders(mu.getSupportedDpProviders())
                    .isSeamless(mu.getIsSeamless())
                    .isConvFee(mu.getIsConvFee())
                    .canCxBuyInsurance(mu.getCanCxBuyInsurance())
                    .convFeeRates(mu.getConvFeeRates())
                    .isInvoiceEnabled(mu.getIsInvoiceEnabled())
                    .isGiftVoucherEnabled(mu.getIsGiftVoucherEnabled())
                    .isInvoicingModel(mu.getIsInvoicingModel())
                    .isBrandSubventionModel(mu.getIsBrandSubventionModel())
                    .brandSubventions(offerBO.getBrandSubventionsForProduct(null != brandInfo ? brandInfo.getBrandId()
                                    : null != mu.getParams() ? mu.getParams().getBrandId() : null,
                            null != brandInfo ? brandInfo.getBrandProductId() : null, merchant, mu.getPartner()))
                    .brandInfo(brandInfo)
                    .maxTenure(getMaxTenureBasisProductId(request, mu))
                    .gst(mu.getGst())
                    .address(mu.getAddress())
                    .brandMdrs(merchantDiscountRateBO.getMerchantDiscountRateByBrandId(
                            null != brandInfo ? brandInfo.getBrandId() : null))
                    .settlementConfigDto(SettlementConfigDto.builder()
                            .settlementCycle(Util.isNotNull(mu.getSettlementConfig()) &&
                                    Util.isNotNull(mu.getSettlementConfig().getSettlementCycle())
                                    ? mu.getSettlementConfig().getSettlementCycle() : SettlementCycleEnum.STANDARD)
                            .lyraPgSettlementConfig(Util.isNotNull(mu.getSettlementConfig()) ?
                                    new LyraPgSettlementConfigDto(mu.getSettlementConfig().getLyraPgSettlementConfig()) : null)
                            .build())
                    .bankInterestDtoOnMerchant(bankInterestDtoOnMerchant)
                    .bankInterestDtoOnBrand(bankInterestDtoOnBrand)
                    .partner(mu.getPartner())
                    .masterMerchants(mu.getMasterMerchants())
                    .businessName(mu.getBusinessName())
                    .build();
            PaymentTransactionRequest.ConsumerInfo ci =
                    PaymentTransactionRequest.ConsumerInfo.builder().consumerId(cr.getConsumerId()).build();
            PaymentTransactionRequest.ProductInfo pi = PaymentTransactionRequest.ProductInfo.builder()
                    .name(request.getProductName())
                    .invoiceNumber(request.getInvoiceNumber())
                    .quantity(request.getQuantity())
                    .catalogProductId(request.getCatalogProductId())
                    .build();
            TransactionResponse response = paymentServiceBO.createTransaction(merchant,
                    new PaymentTransactionRequest(request.getAmount(), request.getMobile(), null, mi, ci,
                            TransactionStatus.initiated.name(), pi, TransactionSource.merchantApp.name(), false, null,
                            null, request.getTenure(), request.getCardInfo(), null, null, null, null, null, null,
                            null, partner));
            if (isDynamicOffer) {
                notificationService.sendCustomOffer(mu.getMobile(), mu.getEmail(), mu.getShopName(), sourceIp,
                        "Merchant Business App", response.getMerchantOrderId(), request.getMobile(),
                        request.getProductName());
            }
            return response;
        }
        throw new FreewayException("Bad Request - No Offers available.");
    }

    private void validateRequestAmount(TransactionRequest request, BrandInfo brandInfo) {
        if (null != brandInfo && null != brandInfo.getMinAmount() && null != request.getAmount() &&
                request.getAmount() < brandInfo.getMinAmount()) {
            throw new FreewayException(String.format("Amount should be greater than product minimum amount of Rs %s.",
                    brandInfo.getMinAmount()));
        }
    }

    public PgTransactionResponse createPgTransaction(MerchantUser mu, PgTransactionRequest request, Boolean isSeamless,
                                                     String transactionSource) {
        merchantValidator.validateV2(mu, request, isSeamless, transactionSource);

        if(Util.isNotNull(request) && Util.isNotNull(request.getCardInfo())) {
            CardInfo cardInfo = request.getCardInfo();
            {
                if(StringUtils.hasText(cardInfo.getCode()) && StringUtils.hasText(cardInfo.getType())) {
                    if (CardTypeEnum.DEBIT.getCardType().equals(cardInfo.getType()) && BankEnum.CL_ICICI.getCode().equals(request.getCardInfo().getCode())){
                        request.getCardInfo().setType(CardTypeEnum.CARDLESS.getCardType());
                    }
                }
            }
        }
        if (!MerchantStatus.approved.name().equals(mu.getStatus())) {
            throw new FreewayException("merchant not approved.");
        }
        if (!CollectionUtils.isEmpty(request.getCustomParams())) {
            String paymentTxnId = request.getCustomParams().getOrDefault("paymentRefNo", "");
            if (!StringUtils.isEmpty(paymentTxnId)) {
                return new PgTransactionResponse(request.getOrderId(), paymentTxnId, transactionUrl + paymentTxnId, null);
            }
        }

        if (Objects.nonNull(mu.getParams()) && Boolean.TRUE.equals(mu.getParams().getEnableDefaultBrandProduct())) {
            if (Objects.nonNull(mu.getParams().getBrandIds()) && mu.getParams().getBrandIds().size() == 1) {
                Brand brand = brandBO.getBrandById(mu.getParams().getBrandIds().get(0));
                if (Objects.nonNull(brand) && StringUtils.hasText(brand.getDefaultProductId())) {
                    BrandProduct brandProduct = brandProductBO.getBrandProduct(brand.getDefaultProductId());
                    if (Objects.nonNull(brandProduct) && brand.getId().toString().equals(brandProduct.getBrandId()) &&
                            request.getAmount() <= brandProduct.getAmount() && request.getAmount() >= brandProduct.getMinAmount()
                            && Boolean.TRUE.equals(brandProduct.getIsValid())
                    ) {
                        request.setProductId(brandProduct.getId().toString());
                    } else {
                        brandProduct = brandProductBO.getById(brand.getDefaultProductId());
                        String subject = "Transaction Failed with Enable Brand Product";
                        if (!isProduction) {
                            subject = "DEV: Transaction Failed with Enable Brand Product";
                        }
                        String content = "There is no brand produt available with given default product Id";
                        if (Objects.nonNull(brandProduct)) {
                            content = "Basic Brand product details :" + " <br></br>" +
                                    "Product Id: " + brandProduct.getId().toString() + " <br></br>" +
                                    "Product Name: " + brandProduct.getProduct() + " <br></br>" +
                                    "category: " + brandProduct.getCategory() + " <br></br>" +
                                    "variant: " + brandProduct.getVariant() + " <br></br>" +
                                    "model: " + brandProduct.getModelNo() + " <br></br>" +
                                    "Display Id: " + mu.getDisplayId() + " <br></br>" +
                                    "request: " + request.toString() + " <br></br>" +
                                    "Validations Failed: Amount Range or Validity or BrandId";
                        }

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
                        throw new FreewayException("Transaction Failure. Please try after some time");
                    }
                }
            }
        }

        PaymentTransactionRequest.MerchantInfo mi = populateMerchantInfo(mu, request, isSeamless);
        PaymentTransactionRequest.ConsumerInfo ci = populateConsumerInfo(request);
        TransactionResponse tr = paymentServiceBO.createTransaction(mu.getId().toString(),
                new PaymentTransactionRequest(request.getAmount(), request.getMobile(), null, mi, ci,
                        TransactionStatus.initiated.name(), populateProductInfo(request, mi), transactionSource, false,
                        null, request.getCardData(), request.getTenure(), request.getCardInfo(), null,
                        request.getLatitude(), request.getLongitude(), request.getIp(), null, request.getIsSubvention(),
                        request.getSubventionAmount(), request.getPartner()));
        return new PgTransactionResponse(request.getOrderId(), tr.getTxnId(),
                merchantCommonUtil.getPaymentRedirectionUrl(mu, tr.getQrCode(), tr.getTxnId(), tr.getEncryptedTxnId()), null);
    }

    private PaymentTransactionRequest.ProductInfo populateProductInfo(PgTransactionRequest request,
                                                                      PaymentTransactionRequest.MerchantInfo mi) {
        if (mi != null && mi.getBrandInfo() != null) {
            return PaymentTransactionRequest.ProductInfo.builder()
                    .name(mi.getBrandInfo().getVariant())
                    .build();
        }
        String productId;
        String productName;
        PaymentTransactionRequest.ProductInfo pi = null;

        List<PaymentTransactionRequest.ProductInfo> products = request.getProducts();
        if (!CollectionUtils.isEmpty(products)) {
            PaymentTransactionRequest.ProductInfo singleProduct = products.get(0);
            productId = Util.isEmptyString(request.getProductId()) ? singleProduct.getProductId()
                    : request.getProductId();
            productName = Util.isEmptyString(request.getProductName()) ? singleProduct.getName()
                    : request.getProductName();
            pi = PaymentTransactionRequest.ProductInfo.builder()
                    .productId(productId)
                    .name(productName)
                    .invoiceNumber(singleProduct.getInvoiceNumber())
                    .quantity(singleProduct.getQuantity())
                    .catalogProductId(singleProduct.getCatalogProductId())
                    .code(singleProduct.getCode())
                    .amount(singleProduct.getAmount())
                    .skuCode(singleProduct.getSkuCode())
                    .serialNo(singleProduct.getSerialNo())
                    .manufacturer(singleProduct.getManufacturer())
                    .category(singleProduct.getCategory())
                    .subCategory(singleProduct.getSubCategory())
                    .model(singleProduct.getModel())
                    .imeiNo(singleProduct.getImeiNo())
                    .additionalInfo(singleProduct.getAdditionalInfo())
                    .build();
        } else {
            pi = PaymentTransactionRequest.ProductInfo.builder()
                    .productId(request.getProductId())
                    .name(request.getProductName())
                    .build();
        }
        return pi;
    }

    public List<TransactionResponse> getTransactionByMerchantId(String merchantId, String mobile) {
        if (StringUtils.hasText(mobile)) {
            return paymentServiceBO.searchByMerchantIdAndConsumerMobile(merchantId, mobile);
        } else {
            return paymentServiceBO.searchByMerchantId(merchantId, "");
        }
    }

    public List<TransactionResponse> getSuccessTransactionByMerchantId(String merchantId, String mobile) {
        return paymentServiceBO.searchByMerchantId(merchantId, "success");
    }

    public List<TransactionResponse> getStoreUserTransactions(String storeUserId) {
        StoreUserTransaction storeUserTransaction =
                paymentServiceBO.getTransaction(storeUserId, StoreUserTransactionStatusReq.builder().build());
        return null != storeUserTransaction ? storeUserTransaction.getTransactionResponses() : new ArrayList<>();
    }

    public List<PriceResponse> simulate(String merchant, Float price) {
        List<PriceResponse> resp = new ArrayList<>();
        MerchantUser mu = merchantUserBO.getUserById(merchant);
        if (null != mu.getMdrs()) {
            for (String cardType : Arrays.asList("CREDIT", "DEBIT")) {
                for (Integer tenure : Arrays.asList(3, 6, 9, 12, 18, 24)) {
                    Float rate = Util.getMdr(mu.getMdrs(), "HDFC", cardType, tenure, null, price);
                    Float mdrCharges = rate * price;
                    Float gst = mdrCharges * (paymentConstants.GST / 100.0f);
                    resp.add(PriceResponse.builder()
                            .offerId(cardType + "_" + tenure)
                            .tenure(tenure)
                            .amount(getFLoat(price))
                            .discount(0.0f)
                            .bankCharges(0.0f)
                            .emi(0.0f)
                            .irr(getFLoat(paymentConstants.IRR))
                            .settlement(getFLoat(price - mdrCharges))
                            .gst(getFLoat(gst))
                            .gstPer(getFLoat(paymentConstants.GST))
                            .netSettlement(getFLoat(price - mdrCharges - gst))
                            .effectiveIrr("No Cost EMI")
                            .discountMerchant(0.0f)
                            .build());
                }
            }
        }
        return resp;
    }

    public Map<String, List<PriceResponse>> simulate2(String merchant, Float price) {
        MerchantUser mu = merchantUserBO.getUserById(merchant);
        List<OfferResponse> offerResponses = offerBO.getPgMerchantOffers(mu.getId().toString());
        TransactionResponse tr =
                new TransactionResponse(null, null, null, null, null, null, null, null, null, price, null, null, null,
                        null, null, null, null, mu.getShopName(), null, null, null, null, null, null, null, null, null, null,
                        null, null, null, null == mu.getOffers() ? null : mu.getOffers()
                        .stream()
                        .map(o -> Util.getPriceResponse("o.getId()", o.getSubvention(), o.getTenure(), price))
                        .collect(Collectors.toList()), null, null, null, null, offerResponses, false, mu.getIsConvFee(), Objects.nonNull(mu.getIsConvFee()) && mu.getIsConvFee() ? mu.getConvFeeRates() : new ArrayList<>(),
                        mu.getParams(), null, mu.getId().toString(), null, null, null, null, false, null, null, null,
                        null, null, null, false, null, null, null, false, AdditionInfo.builder()
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
                        .build(), null, false, null, null, null, null, null, null, null, null, null, null, null, null,
                        null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                        null, null, null, mu.getPartner(), null, null, null, null, null, null, null, null, null);
        return paymentOptionsBO.getPgPaymentOptionsV2(tr);
    }

    public TransactionResponse getTransactionById(String transactionId) {
        return paymentServiceBO.getTransactionById(transactionId);
    }

    public TransactionResponse getTransactionByMerchantIdAndOrderId(String merchantId, String orderId) {
        MerchantUser mu = merchantUserBO.getUserByMerchantIdOrDisplayId(merchantId);
        // checking whether online inquiry is enabled or not
        MerchantCommonUtil.isOnlineInquiryEnabled(mu, orderId);
        return paymentServiceBO.getTransactionByMerchantIdAndOrderId(merchantId, orderId);
    }

    public TransactionResponse processRefundTransaction(String paymentTxnId, RefundTransactionRequest refundTxnReq) {
        return paymentServiceBO.processRefund(paymentTxnId, refundTxnReq);
    }

    public TransactionResponse processRefundNtbTransaction(String paymentTxnId, RefundTransactionRequest refundTxnReq) {
        return paymentServiceBO.processNtbRefund(paymentTxnId, refundTxnReq);
    }

    public PaymentLinkResponse createPaymentLink(MerchantUser mu, PgTransactionRequest request) {
        PaymentLinkResponse paymentLinkResponse;
        try {
            if (Objects.nonNull(request.getCardInfo())) {
                conditionsExecutor.executeConditions(ConditionType.SEALMESS_PAYMENTLINK_CONDITIONS, request);
            }
            Boolean sendSmsShortLink =
                    null != mu.getPaymentLinkInfo() && null != mu.getPaymentLinkInfo().getSendSmsShortLink() &&
                            mu.getPaymentLinkInfo().getSendSmsShortLink();
            Boolean sendEmailShortLink =
                    null != mu.getPaymentLinkInfo() && null != mu.getPaymentLinkInfo().getSendEmailShortLink() &&
                            mu.getPaymentLinkInfo().getSendEmailShortLink();
            Integer expiryTimeInMinutes = Util.isNotNull(mu.getPaymentLinkInfo()) &&
                    Util.isNotNull(mu.getPaymentLinkInfo().getExpiryInMinutes()) ? mu.getPaymentLinkInfo()
                    .getExpiryInMinutes() : null;
            paymentLinkResponse = paymentLinkServiceBO.createPaymentLink(
                    new PaymentLinkRequest(request.getAmount(), request.getMobile(), request.getOrderId(),
                            request.getEmail(), request.getProductName(), request.getProductId(), request.getProductSkuCode(),
                            request.getConsumerName(), request.getAddress(), request.getCustomParams(),
                            mu.getId().toString(), mu.getShopName(), request.getIframe(), request.getFirstName(),
                            request.getMiddleName(), request.getLastName(), request.getReturnUrl(),
                            request.getWebhookUrl(), request.getSendPaymentLink(), sendSmsShortLink, sendEmailShortLink,
                            request.getProducts(), request.getMaxTenure(), request.getPan(), request.getDob(),
                            request.getAnnualIncome(), request.getCardData(), request.getTenure(),
                            request.getCardInfo(), request.getIsSubvention(), request.getSubventionAmount(),
                            request.getGender(), request.getPartner(), expiryTimeInMinutes, request.getProviderGroup()));
        } catch (FreewayCustomException e) {
            LOGGER.error("Custom Freeway Exception occurred as :{} while creating payment link for params: {}", e,
                    request);
            TransactionCode tc = TransactionCode.getByCode(e.getCode());
            paymentLinkResponse = PaymentLinkResponse.builder()
                    .code(tc.getCode())
                    .message(tc.getStatusMsg())
                    .orderId(request.getOrderId())
                    .build();
        }
        return paymentLinkResponse;
    }

    public PaymentLinkResponse getPaymentLink(String merchantId, String orderId) {
        return paymentLinkServiceBO.getPaymentLink(merchantId, orderId);
    }

    public StoreLinkResponse createStoreLink(MerchantUser merchantUser, StoreLinkRequest storeLinkRequest) {
        StoreLinkResponse storeLinkResponse =
                storeLinkServiceBO.createStoreLink(String.valueOf(merchantUser.getId()), storeLinkRequest);
        notificationService.sendStoreUserSms(storeLinkRequest.getMobile(), storeLinkResponse.getStoreLink(),
                merchantUser.getShopName());
        return storeLinkResponse;
    }

    public SettlementResponse getSettlementsByMerchantId(String merchant, String partner) {
        MerchantUser user = merchantUserBO.getUserById(merchant);
        MerchantUser partnerUser = null;
        String userMobile = user.getMobile();
        List<String> partners = user.getPartners();
        if (Util.isNotNull(partner) && Util.isNotNull(partners)) {
            if (Util.isNotNull(partners) && partners.contains(partner)) {
                partnerUser = merchantUserBO.getUserByMobile(userMobile + "_" + partner);
                user = partnerUser;
                merchant = user.getId().toString();
            } else {
                throw new FreewayException(userMobile + " doesn't have any partner merchant with partner: " + partner);
            }
        }
        return paymentServiceBO.getSettlement(merchant);
    }

    public RefundPayoutResponse createRefundAsPayout(MerchantUser mu, RefundPayoutRequest request, String source) {
        try {
            conditionsExecutor.executeConditions(ConditionType.CREATE_REFUND_PAYOUT_CONDITIONS, mu);
        } catch (FreewayCustomException e) {
            TransactionCode tc = TransactionCode.getByCode(e.getCode());
            return RefundPayoutResponse.builder()
                    .statusCode(tc.getCode())
                    .status(tc.getStatus())
                    .statusMessage(tc.getStatusMsg())
                    .orderId(request.getOrderId())
                    .refundPayoutOrderId(request.getRefundPayoutOrderId())
                    .build();
        }
        request.setSource(source);
        return paymentServiceBO.createRefundPayout(String.valueOf(mu.getId()), request);
    }

    public List<TransactionResponse> searchTransactions(String merchantId,
                                                        TransactionSearchFilter transactionSearchFilter) {
        if (StringUtils.hasText(merchantId) && Util.isNotNull(transactionSearchFilter)) {
            return paymentServiceBO.searchTransactions("merchant", merchantId, transactionSearchFilter);
        }
        return new ArrayList<>();
    }

    public DeliveryOrderResp getDeliveryOrder(MerchantUser mu, String orderIdOrpaymentTxnId) {
        try {
            conditionsExecutor.executeConditions(ConditionType.GET_DELIVERY_ORDER_DETAILS, mu);
            return paymentServiceBO.getDeliveryOrder(String.valueOf(mu.getId()), orderIdOrpaymentTxnId);
        } catch (FreewayCustomException e) {
            TransactionCode tc = TransactionCode.getByCode(e.getCode());
            return DeliveryOrderResp.builder()
                    .statusCode(tc.getCode())
                    .status(tc.getStatus())
                    .statusMessage(tc.getStatusMsg())
                    .orderId(orderIdOrpaymentTxnId)
                    .build();
        }
    }

    public PaymentProviderTransactionResponseV2 payPayment(PgConsumerPaymentRequest pgConsumerPaymentRequest, String transactionId, String consumerId) {
        try {
            return consumerService.payPayment(pgConsumerPaymentRequest, transactionId, consumerId);
        } catch (FreewayCustomException e) {
            TransactionCode tc = TransactionCode.getByCode(e.getCode());
            return PaymentProviderTransactionResponseV2.baseResponseBuilder()
                    .code(tc.getCode())
                    .status(tc.getStatus())
                    .statusMessage(tc.getStatusMsg())
                    .build();
        }
    }

    public Map<String, List<PriceResponse>> getPricingOptions(String merchantId,
                                                              PricingOptionsRequest pricingOptionsRequest) {
        //Create transaction response and generate pricing
        ConsumerResponse consumerResponse =
                consumerService.getOrCreateConsumer(pricingOptionsRequest.getConsumerMobile());
        MerchantUser merchantUser = merchantUserBO.getUserById(merchantId);
//        List<OfferResponse> offerResponses = offerBO.getPgMerchantOffers(merchantUser.getId().toString());
        List<PaymentProviderEnum> merchantSuppPaymentProviders =
                paymentServiceBO.getProviders(merchantUser.getId().toString());
//        List<OfferResponse> brandSubRequest = offerBO.getBrandSubventionsForProduct(pricingOptionsRequest.getBrandId(),
//                pricingOptionsRequest.getProductId(), merchantId, merchantUser.getPartner());
        List<EligibilityResponse> eligibilityResponses =
                paymentServiceBO.getEligibilityCheck(merchantSuppPaymentProviders, pricingOptionsRequest.getAmount(),
                        pricingOptionsRequest.getConsumerMobile(), merchantUser.getPartner());
//        DownPaymentConfigDto downPaymentConfigDto =
//                paymentServiceBO.getDownPaymentConfig(merchantUser.getId().toString());
//        List<String> eligibilities = eligibilityBO.findAll(merchantSuppPaymentProviders, eligibilityResponses);
//        List<EligibilityResponse> ntbEligibilities =
//                eligibilityBO.findNtbAllNtbEligibilities(merchantSuppPaymentProviders, eligibilityResponses);
//        BankInterestDto bankInterestDtoOnMerchant =
//                bankInterestBO.getBankInterestByMerchantId(merchantUser.getId().toString(),
//                        merchantUser.getDisplayId());
//        BankInterestDto bankInterestDtoOnBrand = bankInterestBO.getBankInterestByBrandInfo(
//                BrandInfo.builder().brandId(pricingOptionsRequest.getBrandId()).build());
//        TransactionRequest transactionRequest = new TransactionRequest(null, null, null, null, null, null, null, null, BrandRequest.builder().brandProductId(pricingOptionsRequest.getProductId()).build(), null, null);
//        TransactionResponse tr = new TransactionResponse(null, null, null, null, null, null, null, null, null,
//                pricingOptionsRequest.getAmount(), null, null, null, null, null, null, null, merchantUser.getShopName(),
//                null, null, null, null, null, null, null, null, null, null, null, null,
//                null == merchantUser.getOffers() ? null : merchantUser.getOffers()
//                        .stream()
//                        .map(o -> Util.getPriceResponse("o.getId()", o.getSubvention(), o.getTenure(),
//                                pricingOptionsRequest.getAmount()))
//                        .collect(Collectors.toList()), null, null, pricingOptionsRequest.getProductId(), null,
//                offerResponses, false, merchantUser.getIsConvFee(), Objects.nonNull(merchantUser.getIsConvFee()) && merchantUser.getIsConvFee() ? merchantUser.getConvFeeRates() : new ArrayList<>(), merchantUser.getParams(), null, merchantUser.getId().toString(),
//                null, null, eligibilities, null, merchantUser.getDownPaymentEnabled(), null, null, null, null, null,
//                null, false, null, null, getMaxTenureBasisProductId(transactionRequest, merchantUser), false, AdditionInfo.builder()
//                .cardLastFourDigit("")
//                .availableLimit("")
//                .giftVoucherId(null)
//                .txnInvoiceId(null)
//                .banksMaxEligibilityTenure(null)
//                .cardId(null)
//                .cashbackStatus(null)
//                .expectedCashbackDate(null)
//                .cxBuyingInsuranceReportUrl(null)
//                .dcMaxTenure(null)
//                .ccMaxTenure(null)
//                .brandInfo(null)
//                .payLinkCreateBy(null)
//                .brandInfo(BrandInfo.builder().brandProductId(pricingOptionsRequest.getProductId()).brandId(pricingOptionsRequest.getBrandId()).build())
//                .build(), null, false, merchantUser.getIsBrandSubventionModel(), brandSubRequest, null, null, null,
//                null, null, null, ntbEligibilities, null, null, null, null, null, null, null, null, null, null,
//                downPaymentConfigDto, null, null, bankInterestDtoOnMerchant, bankInterestDtoOnBrand, null, null, null,
//                null, null, null, null, merchantUser.getPartner());
//        Boolean isInstantCashbackEnabled = false;
//        tr.setIsInstantCashbackEnabled(false);
//        Brand brand = StringUtils.hasText(pricingOptionsRequest.getBrandId()) ? brandBO.findById(
//                pricingOptionsRequest.getBrandId()) : null;
//        MerchantInstantDiscountConfigResp merchantInstantDiscountConfigResp = null;
//        if (null != brand) {
//            merchantInstantDiscountConfigResp =
//                    merchantInstantDiscountConfigurationBO.getMerchantInstantDiscountConfigurationResp(merchantId,
//                            pricingOptionsRequest.getBrandId(), brand.getBrandFeeRateInstantDiscount());
//            if (merchantInstantDiscountConfigResp != null) {
////                tr.setIsInstantCashbackEnabled(true);
////                tr.setMerchantInstantDiscountConfigResp(merchantInstantDiscountConfigResp);
//                isInstantCashbackEnabled = true;
//            }
//        } else {
//            LOGGER.error("getPricingOptions: MerchantInstantDiscountConfigurationResponse : " +
//                    "merchantId: {}, brandId: {}, Brand is null.", merchantId, pricingOptionsRequest.getBrandId());
//        }
//        if (isInstantCashbackEnabled && instantCashbackEnvEnabled) {
//            return paymentServiceBO.getPgPaymentOptionsOnEligibilityV3(tr).getCardOffers();
//        }

        // Commented above code that triggered instant cashback flow based on configuration. Need to re-enable after
        // proper handling is done for such transactions with payment link as source.
        MerchantResponse merchantResponse = getMerchantInfo(merchantUser.getId().toString(), pricingOptionsRequest.getProductId(), pricingOptionsRequest.getBrandId());
        ConsumerInfo consumerInfo = getConsumerInfo(consumerResponse, eligibilityResponses);
        PaymentOptionsRequest paymentOptionsRequest = PaymentOptionsRequest.builder()
                .merchantResponse(merchantResponse)
                .consumerInfo(consumerInfo)
                .productAmount(pricingOptionsRequest.getAmount())
                .partner(merchantUser.getPartner())
                .productId(null)
                .build();

        PaymentOptionsResponse paymentOptionsResponse = paymentServiceBO.getPaymentOptions(paymentOptionsRequest);
        if (Objects.nonNull(paymentOptionsResponse) && Objects.nonNull(paymentOptionsResponse.getCardOffers())) {
            return paymentOptionsResponse.getCardOffers();
        }
        throw new FreewayCustomException(TransactionCode.FAILED_183);
//                return paymentOptionsBO.getPgPaymentOptionsOnEligibilityV3(tr);
    }


    public MerchantResponse getMerchantInfo(String merchantId, String productId, String brandId) {
        return merchantService.getMerchantDetails(merchantId, productId, null, null, brandId, null, null);
    }

    private ConsumerInfo getConsumerInfo(ConsumerResponse consumerResponse, List<EligibilityResponse> eligibilityResponses) {
        ConsumerInfo consumerInfo = new ConsumerInfo();
        consumerInfo.setConsumerId(consumerResponse.getConsumerId());
        consumerInfo.setConsumerName(consumerResponse.getFirstName());
        consumerInfo.setMobile(consumerResponse.getMobile());
        consumerInfo.setDob(consumerResponse.getDob());
        consumerInfo.setEmail(consumerResponse.getEmail());
        consumerInfo.setGender(consumerResponse.getGender());
        consumerInfo.setEligibilities(eligibilityResponses);
        return consumerInfo;
    }


    public PgTransactionResponse createSeamlessTransactionV2(MerchantUser mu, PgTransactionRequest request,
                                                             Boolean isSeamless, String transactionSource,
                                                             Boolean isCV3PaymentLink) {
        merchantValidator.validateV2(mu, request, isSeamless, transactionSource);
        if (!MerchantStatus.approved.name().equals(mu.getStatus())) {
            throw new FreewayException("merchant not approved.");
        }
        if (!CollectionUtils.isEmpty(request.getCustomParams())) {
            String paymentTxnId = request.getCustomParams().getOrDefault("paymentRefNo", "");
            if (!StringUtils.isEmpty(paymentTxnId)) {
                return new PgTransactionResponse(request.getOrderId(), paymentTxnId, transactionUrl + paymentTxnId, null);
            }
        }
        PaymentTransactionRequest.MerchantInfo mi = populateMerchantInfo(mu, request, isSeamless);
        PaymentTransactionRequest.ConsumerInfo ci = populateConsumerInfo(request);
        LOGGER.info("Sending request to payment service to create transaction for order id: {} and mobile: {}",
                request.getOrderId(), request.getMobile());
        TransactionResponse transactionResponse = paymentServiceBO.createTransaction(mu.getId().toString(),
                new PaymentTransactionRequest(request.getAmount(), request.getMobile(), null, mi, ci,
                        TransactionStatus.initiated.name(), populateProductInfo(request, mi), transactionSource, false,
                        null, request.getCardData(), request.getTenure(), request.getCardInfo(), null,
                        request.getLatitude(), request.getLongitude(), request.getIp(), null, request.getIsSubvention(),
                        request.getSubventionAmount(), request.getPartner()));

        //user should be re-directed to order-summary page instead of otp page if seamless txn has no card data
        if (Objects.nonNull(transactionResponse) && Objects.nonNull(transactionResponse.getAdditionInfo()) &&
                RedirectStageEnum.ORDER_SUMMARY.getDisplayName()
                        .equalsIgnoreCase(transactionResponse.getAdditionInfo().getRedirectStage())) {
            LOGGER.info("Processing transaction for no card-details for order id: {}", request.getOrderId());
            String paymentRedirectUrl = merchantCommonUtil.getPaymentRedirectionUrl(mu, transactionResponse.getQrCode(),
                    transactionResponse.getTxnId(), transactionResponse.getEncryptedTxnId());
            return new PgTransactionResponse(request.getOrderId(), transactionResponse.getTxnId(), paymentRedirectUrl, null);
        }

        if (Util.isNull(request.getCardData())) {
            PriceResponse applicableOfferResponse = paymentServiceBO.getPgPaymentOptionByPG(
                    transactionResponse.getTxnId(), transactionResponse.getCardType(),
                    transactionResponse.getBankCode(), transactionResponse.getTenure(),
                    transactionResponse.getAdvanceEmiTenure(),
                    Objects.nonNull(transactionResponse.getDownPaymentInfo()) &&
                            Objects.nonNull(transactionResponse.getDownPaymentInfo().getAmount()) ?
                            transactionResponse.getDownPaymentInfo().getAmount() : null);
            return new PgTransactionResponse(request.getOrderId(), transactionResponse.getTxnId(), null, applicableOfferResponse);
        }

        if(isCV3PaymentLink || CardTypeEnum.DEBIT.name().equalsIgnoreCase(request.getCardInfo().getType()) ||
                CardTypeEnum.CARDLESS.name().equalsIgnoreCase(request.getCardInfo().getType())) {
            LOGGER.info("Processing transaction for card type DEBIT or Cardless or CREDIT for order id: {}",
                    request.getOrderId());
            String paymentRedirectUrl = merchantCommonUtil.getPaymentRedirectionUrl(mu, transactionResponse.getQrCode(),
                    transactionResponse.getTxnId(), transactionResponse.getEncryptedTxnId());
            return new PgTransactionResponse(request.getOrderId(), transactionResponse.getTxnId(), paymentRedirectUrl, null);
        } else {
            LOGGER.info("Create transaction response received for order id: {} and mobile: {}", request.getOrderId(),
                    request.getMobile());
            String transactionId = transactionResponse.getTxnId();
            LOGGER.info("Sending request to get pgConsumerPaymentRequest for order id: {} and mobile: {} and" +
                    " transaction id: {}", request.getOrderId(), request.getMobile(), transactionId);
            PgConsumerPaymentRequest pgConsumerPaymentRequest = paymentServiceBO.getPgConsumerRequest(transactionId);
            LOGGER.info("Response received to get pgConsumerPaymentRequest for order id: {} and mobile: {} and" +
                    " transaction id: {}", request.getOrderId(), request.getMobile(), transactionId);
            LOGGER.info("Sending request to get or create card order id: {} and mobile: {} and" + " transaction id: {}",
                    request.getOrderId(), request.getMobile(), transactionId);
            consumerProfileServiceBO.getOrCreateCard(ci.getConsumerId(), pgConsumerPaymentRequest);
            LOGGER.info(
                    "Received response to get or create card order id: {} and mobile: {} and" + " transaction id: {}",
                    request.getOrderId(), request.getMobile(), transactionId);
            LOGGER.info("Sending request to payment ms to process seamless payment request for order id: {} and" +
                    " transaction id: {}", request.getOrderId(), transactionId);
            PaymentProviderTransactionResponse pptr =
                    consumerService.processSeamlessTransaction(transactionId, pgConsumerPaymentRequest);
            LOGGER.info("Sending request to payment ms to process seamless payment request for order id: {} and" +
                    " transaction id: {} as: {}", request.getOrderId(), transactionId, pptr);
            return new PgTransactionResponse(request.getOrderId(), transactionResponse.getTxnId(),
                    pptr.getRedirectUrl(), null);
        }
    }

    public PaymentTransactionRequest.MerchantInfo populateMerchantInfo(MerchantUser mu, PgTransactionRequest request,
                                                                       Boolean isSeamless) {
        List<OfferResponse> offerResponses = offerBO.getPgMerchantOffers(mu.getId().toString());
        BrandInfo brandInfo = getBrandInfo(request, mu);
        List<OfferResponse> brandSubventions = offerBO.getBrandSubventionsForProduct(
                null != brandInfo ? brandInfo.getBrandId()
                        : null != mu.getParams() ? mu.getParams().getBrandId() : null,
                null != brandInfo ? brandInfo.getBrandProductId() : null, mu.getId().toString(), mu.getPartner());
        String returnUrl = StringUtils.isEmpty(request.getReturnUrl()) ? mu.getReturnUrl() : request.getReturnUrl();
        BankInterestDto bankInterestDtoOnMerchant =
                bankInterestBO.getBankInterestByMerchantId(mu.getId().toString(), mu.getDisplayId());
        BankInterestDto bankInterestDtoOnBrand = bankInterestBO.getBankInterestByBrandInfo(brandInfo);
        List<PaymentProviderInfo> allowedProviders = MerchantService.getAllowedProviders(mu, request.getProviderGroup());

        // setting encTxnLinkEnabled based on providerGroup flag
        Boolean encTxnLinkEnabled = MerchantService.getProviderGroupEncTxnFlag(mu, request.getProviderGroup());
        if(Objects.nonNull(encTxnLinkEnabled) && Objects.nonNull(mu.getParams())) {
            mu.getParams().setEncTxnLinkEnabled(encTxnLinkEnabled);
        }

        return PaymentTransactionRequest.MerchantInfo.builder()
                .merchantName(mu.getShopName())
                .merchantMobile(mu.getMobile())
                .merchantEmail(mu.getEmail())
                .merchantType(mu.getType())
                .merchantOrderId(request.getOrderId())
                .merchantReturnUrl(returnUrl)
                .merchantWebhookUrl(
                        StringUtils.isEmpty(request.getWebhookUrl()) ? mu.getWebhookUrl() : request.getWebhookUrl())
                .mdrs(mu.getMdrs())
                .merchantParams(mu.getParams())
                .offerResponses(offerResponses)
                .downPaymentEnabled(mu.getDownPaymentEnabled())
                .category(mu.getCategory())
                .subCategory(mu.getSubCategory())
                .mccCode(mu.getMccCode())
                .supportedDpProviders(mu.getSupportedDpProviders())
                .customParams(request.getCustomParams())
                .isConvFee(mu.getIsConvFee())
                .canCxBuyInsurance(mu.getCanCxBuyInsurance())
                .convFeeRates(mu.getConvFeeRates())
                .isSeamless(isSeamless)
                .isInvoicingModel(mu.getIsInvoicingModel())
                .isBrandSubventionModel(mu.getIsBrandSubventionModel())
                .brandSubventions(brandSubventions)
                .brandInfo(brandInfo)
                .isGiftVoucherEnabled(mu.getIsGiftVoucherEnabled())
                .isInvoiceEnabled(mu.getIsInvoiceEnabled())
                .maxTenure(request.getMaxTenure())
                .gst(mu.getGst())
                .address(mu.getAddress())
                .brandMdrs(merchantDiscountRateBO.getMerchantDiscountRateByBrandId(
                        null != brandInfo ? brandInfo.getBrandId() : null))
                .settlementConfigDto(SettlementConfigDto.builder()
                        .settlementCycle(Util.isNotNull(mu.getSettlementConfig()) &&
                                Util.isNotNull(mu.getSettlementConfig().getSettlementCycle()) ? mu.getSettlementConfig()
                                .getSettlementCycle() : SettlementCycleEnum.STANDARD)
                        .lyraPgSettlementConfig(Util.isNotNull(mu.getSettlementConfig()) ?
                                new LyraPgSettlementConfigDto(mu.getSettlementConfig().getLyraPgSettlementConfig()) : null)
                        .build())
                .bankInterestDtoOnMerchant(bankInterestDtoOnMerchant)
                .bankInterestDtoOnBrand(bankInterestDtoOnBrand)
                .merchantDisplayId(mu.getDisplayId())
                .businessName(mu.getBusinessName())
                .allowedProviders(allowedProviders)
                .partner(mu.getPartner())
                .build();
    }

    private BrandInfo getBrandInfo(PgTransactionRequest request, MerchantUser mu) {
        BrandInfo brandInfo;
        try {
            brandInfo = StringUtils.hasText(request.getProductId()) || StringUtils.hasText(request.getProductSkuCode()) ?
                    brandProductBO.getBrandInfo(new BrandRequest(null, request.getProductId(), null,
                            null, request.getProductSkuCode())) : null;
        } catch (Exception e) {
            LOGGER.error("Exception while generating brandInfo", e);
            throw new FreewayException("Transaction Failure. Please try after some time");
        }
        if (StringUtils.hasText(request.getProductSkuCode()) && brandInfo == null) {
            throw new FreewayException("Transaction Failure. Invalid product sku code");
        }
        if (brandInfo != null) {
            if (request.getAmount() == null ||
                    (brandInfo.getAmount() != null && request.getAmount() > brandInfo.getAmount()) ||
                    (brandInfo.getMinAmount() != null && request.getAmount() < brandInfo.getMinAmount())) {
                throw new FreewayException("Transaction Failure. Invalid product amount");
            }
            try {
                validateMerchantBrand(mu, brandInfo.getBrandId());
            } catch (MerchantException e) {
                throw new FreewayException(e.getMessage());
            }
        }
        return brandInfo;
    }

    public PaymentTransactionRequest.ConsumerInfo populateConsumerInfo(PgTransactionRequest request) {
        ConsumerResponse cr = consumerService.getOrCreateConsumer(request.getMobile(), request.getEmail());
        LOGGER.info("getOrCreateConsumer, mobile:{}, email:{}", request.getMobile(), request.getEmail());
        return PaymentTransactionRequest.ConsumerInfo.builder()
                .email(request.getEmail())
                .consumerId(cr.getConsumerId())
                .consumerName(request.getConsumerName())
                .address(request.getAddress())
                .iframe(request.getIframe())
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .returnUrl(request.getReturnUrl())
                .pan(request.getPan())
                .gender(request.getGender())
                .dob(request.getDob())
                .annualIncome(request.getAnnualIncome())
                .build();
    }

    public RefundInquiryResponse getRefundTransaction(String paymentTxnId, MerchantUser mu) {
        return paymentServiceBO.getRefundInquiryResponse(paymentTxnId, String.valueOf(mu.getId()));
    }

    public TransactionVolumeInfo getTransactionVolume(TransactionOpsRequest transactionOpsRequest) {
        return paymentOpsService.getTransactionVolume(transactionOpsRequest);
    }

    public Map<String, Map<String, List<MerchantPriceResponse>>> getOffers(String displayMerchantId,
                                                                           MerchantOfferRequest request,
                                                                           boolean consumerRequired, boolean internal) {

        // this method should throw MerchantException only as we have custom exception handler implemented for it.
        // hence, throwing default merchant exception for any other exception caught other than MerchantException.
        try {

            Map<String, Map<String, List<MerchantPriceResponse>>> offerResponse = getOffersResponse(displayMerchantId, request, consumerRequired, internal);
            if (CollectionUtils.isEmpty(offerResponse)) {
                throw new MerchantException(MerchantResponseCode.NO_OFFERS_FOUND);
            }
            return offerResponse;
        } catch (MerchantException me) {
            throw me;
        } catch (Exception e) {
            LOGGER.error("Exception caught while getting merchant offers", e);
            throw new MerchantException(MerchantResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    public MerchantEligibleOfferResponse getMerchantOffersInternal(String displayMerchantId,
                                                                   MerchantOfferRequest request,
                                                                   boolean consumerRequired, boolean internal) {
        Map<String, Map<String, List<MerchantPriceResponse>>> cardOffers = getOffers(displayMerchantId, request, consumerRequired, internal);
        return MerchantEligibleOfferResponse.builder().cardOffers(cardOffers).build();
    }

    private Map<String, Map<String, List<MerchantPriceResponse>>> getOffersResponse(String displayMerchantId,
                                                                                    MerchantOfferRequest request,
                                                                                    boolean consumerRequired,
                                                                                    boolean internal) {

        validateOffersRequest(displayMerchantId, request, consumerRequired, internal);
        MerchantUser merchantUser = merchantUserBO.getUserByMerchantIdOrDisplayId(displayMerchantId);
        if (merchantUser == null) {
            throw new MerchantException(MerchantResponseCode.MERCHANT_NOT_FOUND);
        }
        String brandId = getBrandIdFromCodeOrId(request.getBrand(), request.getBrandId());
        if (StringUtils.isEmpty(brandId) &&
                (StringUtils.hasText(request.getBrand()) || StringUtils.hasText(request.getBrandId()))) {
            throw new MerchantException(MerchantResponseCode.INVALID_BRAND);
        }
        if (StringUtils.hasText(brandId)) {
            validateMerchantBrand(merchantUser, brandId);
        }

        if (Optional.of(merchantUser).map(MerchantUser::getParams).map(Params::getProductSkuRequired)
                .orElse(Boolean.FALSE)) {
            if (!internal && StringUtils.isEmpty(request.getProductSkuCode())) {
                throw new MerchantException(MerchantResponseCode.PRODUCT_REQUIRED);
            }
            if (internal && (StringUtils.isEmpty(request.getBrandProductId()) ||
                    StringUtils.isEmpty(request.getProductSkuCode()))) {
                throw new MerchantException(MerchantResponseCode.PRODUCT_REQUIRED);
            }
            if (StringUtils.isEmpty(brandId)) {
                throw new MerchantException(MerchantResponseCode.BRAND_REQUIRED);
            }
        }

        String brandProductId = null;
        if (StringUtils.hasText(request.getProductSkuCode())) {
            LOGGER.info("Getting product info with product sku code: {}", request.getProductSkuCode());
            BrandProduct product = brandProductBO.getBySkuCodeOrModelNo(request.getProductSkuCode());
            validateBrandProduct(product, brandId, request.getAmount());
            brandProductId = product.getId().toString();
        } else if (internal && StringUtils.hasText(request.getBrandProductId())) {
            LOGGER.info("Getting product info with brand product id: {} for internal", request.getBrandProductId());
            brandProductId = request.getBrandProductId();
            BrandProduct product = brandProductBO.getById(brandProductId);
            validateBrandProduct(product, brandId, request.getAmount());
        }

        ConsumerInfo consumerInfo;
        List<PaymentProviderEnum> merchantSuppPaymentProviders =
                paymentServiceBO.getProviders(merchantUser.getId().toString());
        if (consumerRequired) {
            ConsumerResponse consumerResponse = consumerService.getOrCreateConsumer(request.getConsumerMobile());
            List<EligibilityResponse> eligibilityResponses =
                    paymentServiceBO.getEligibilityCheck(merchantSuppPaymentProviders, Float.valueOf(request.getAmount()),
                            request.getConsumerMobile(), merchantUser.getPartner());
            List<EligibilityResponse> eligibilityResponsesData = eligibilityResponses.stream().
                    filter(eligibilityResponse -> Boolean.TRUE.equals(eligibilityResponse.getEligible()))
                    .collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(eligibilityResponsesData)) {
                consumerInfo = getConsumerInfo(consumerResponse, eligibilityResponsesData);
            } else {
                List<EligibilityResponse> eligibilityResponsesCode = eligibilityResponses.stream().filter(eligibilityResponse -> Objects.nonNull(eligibilityResponse.getCode())).sorted(Comparator.comparing(EligibilityResponse::getCode)).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(eligibilityResponsesCode)) {
                    throw new MerchantException(MerchantResponseCode.CUSTOMER_NOT_ELIGIBLE);
                } else {
                    throw new MerchantException(MerchantResponseCode.getByCode(
                            EligibilityResponseCode.findMerchantCodeByCode(eligibilityResponsesCode.get(0).getCode()))
                    );
                }
            }
        } else {
            // create eligibilities with supported providers
            consumerInfo = new ConsumerInfo();
            consumerInfo.setEligibilities(
                    eligibilityBO.createAllEligibilities(merchantSuppPaymentProviders,
                            Arrays.asList(CardTypeEnum.DEBIT, CardTypeEnum.CARDLESS, CardTypeEnum.NTB)));
        }

        MerchantResponse merchantResponse = getMerchantInfo(merchantUser.getId().toString(), brandProductId, brandId);

        // margin money flow condition -> merchant down payment enabled and brand down payment available options present
        boolean marginMoneyFlow = Boolean.TRUE.equals(merchantResponse.getDownPaymentEnabled()) &&
                !CollectionUtils.isEmpty(Optional.of(merchantResponse)
                        .map(MerchantResponse::getBrandInfo)
                        .map(BrandInfo::getMarginMoneyConfig)
                        .map(MarginMoneyConfigDto::getDownPaymentAvailableOptions)
                        .orElse(null));
        PaymentOptionsRequest paymentOptionsRequest = PaymentOptionsRequest.builder()
                .merchantResponse(merchantResponse)
                .consumerInfo(consumerInfo)
                .productAmount(Float.valueOf(request.getAmount()))
                .partner(merchantUser.getPartner())
                .productId(null)
                .build();
        if(Objects.nonNull(paymentOptionsRequest.getConsumerInfo())) {
            LOGGER.info("paymentOptionsRequest: {}", paymentOptionsRequest.getConsumerInfo().getEligibilities());
        }
        PaymentOptionsResponse paymentOptionsResponse = paymentServiceBO.getPaymentOptions(paymentOptionsRequest);
        if (paymentOptionsResponse == null || paymentOptionsResponse.getCardOffers() == null) {
            LOGGER.info("Did not get card offers from paymentService");
            throw new MerchantException(MerchantResponseCode.NO_OFFERS_FOUND);
        }
        return transformCardOffers(request, paymentOptionsResponse.getCardOffers(), marginMoneyFlow);
    }

    private void validateOffersRequest(String displayMerchantId, MerchantOfferRequest request, boolean consumerRequired,
                                       boolean internal) {
        if (consumerRequired) {
            validateConsumerMobile(request.getConsumerMobile());
        }
        if (!isValidAmount(request.getAmount())) {
            throw new MerchantException(MerchantResponseCode.INVALID_AMOUNT);
        }
        if (StringUtils.isEmpty(displayMerchantId)) {
            throw new MerchantException(MerchantResponseCode.MERCHANT_NOT_FOUND);
        }
        if (StringUtils.hasText(request.getProductSkuCode()) && StringUtils.isEmpty(request.getBrand())) {
            throw new MerchantException(MerchantResponseCode.BRAND_REQUIRED);
        }
        if (StringUtils.isEmpty(request.getProductSkuCode()) && StringUtils.hasText(request.getBrand())) {
            throw new MerchantException(MerchantResponseCode.PRODUCT_REQUIRED);
        }
    }

    private void validateBrandProduct(BrandProduct product, String brandId, String amount) {
        if (product == null) {
            throw new MerchantException(MerchantResponseCode.INVALID_PRODUCT);
        }
        if (brandId == null || !brandId.equals(product.getBrandId())) {
            // Product does not belong to given brand
            throw new MerchantException(MerchantResponseCode.INVALID_PRODUCT);
        }
        validateProductAmount(product, Float.valueOf(amount));
    }

    private boolean isValidAmount(String amount) {
        if (StringUtils.isEmpty(amount)) {
            return false;
        }
        try {
            float requestAmount = Float.parseFloat(amount);
            if (requestAmount >= 2000.0f && requestAmount <= 500000.0f) {
                return true;
            }
        } catch (Exception e) {
            LOGGER.debug("Invalid product amount: " + amount, e);
        }
        LOGGER.info("Product amount is not valid: {}", amount);
        return false;
    }

    private void validateProductAmount(BrandProduct product, Float amount) {
        if (amount == null) {
            throw new MerchantException(MerchantResponseCode.INVALID_AMOUNT);
        }
        if (product != null) {
            if ((product.getMinAmount() != null && amount < product.getMinAmount()) || amount > product.getAmount()) {
                throw new MerchantException(MerchantResponseCode.INVALID_AMOUNT);
            }
        }
    }

    private void validateMerchantBrand(MerchantUser merchantUser, String brandId) {
        List<String> brandIds = merchantUser.getParams() != null ?
                Util.getCombinedBrandIds(merchantUser.getParams().getBrandId(),
                        merchantUser.getParams().getBrandIds()) :
                new ArrayList<>();
        if (!brandIds.contains(brandId)) {
            throw new MerchantException(MerchantResponseCode.INVALID_BRAND);
        }
    }

    private String getBrandIdFromCodeOrId(String brandCode, String brandId) {
        if (StringUtils.hasText(brandId)) {
            return brandId;
        }
        if (StringUtils.hasText(brandCode)) {
            Brand brand = brandBO.getByBrandCodeOrName(brandCode);
            return brand != null ? brand.getId().toString() : null;
        }
        return null;
    }

    private void validateConsumerMobile(String consumerMobile) {
        if (StringUtils.isEmpty(consumerMobile)) {
            throw new MerchantException(MerchantResponseCode.CONSUMER_MOBILE_REQUIRED);
        }
        if (!consumerMobile.matches("^\\d{10}$")) {
            throw new MerchantException(MerchantResponseCode.INVALID_CONSUMER_MOBILE);
        }
    }

    private Map<String, Map<String, List<MerchantPriceResponse>>> transformCardOffers(
            MerchantOfferRequest request, Map<String, List<PriceResponse>> cardOffers, boolean marginMoneyFlow) {
        LOGGER.info("cardOffers: {} : {} : {}", cardOffers, marginMoneyFlow, request);
        Map<String, Map<String, List<MerchantPriceResponse>>> response = new HashMap<>();

        for (Map.Entry<String, List<PriceResponse>> cardOffer : cardOffers.entrySet()) {
            String key = cardOffer.getKey();
            CardTypeEnum cardType = getCardTypeFromKey(key);
            if (cardType != null && checkFilter(request.getCardTypes(), cardType.getCardType())) {
                BankEnum bank = getBankFromKey(key, cardType);
                if (bank != null && checkFilter(request.getBankCodes(), bank.getCode())) {
                    Map<String, List<MerchantPriceResponse>> cardResponses = response
                            .computeIfAbsent(cardType.getCardType(), k -> new HashMap<>());

                    List<MerchantPriceResponse> bankResponses = cardResponses
                            .computeIfAbsent(bank.getCode(), k -> new ArrayList<>());

                    List<PriceResponse> priceResponses = marginMoneyFlow ?
                            filterMarginMoneyPriceResponses(cardOffer.getValue()) :
                            filterNonDownPaymentPriceResponses(cardOffer.getValue());
                    LOGGER.info("priceResponse: {}", priceResponses);
                    List<String> tenures=new ArrayList<>(Arrays.asList("3", "6", "9", "12", "24", "36"));

                    List<String> fitnerTenures=new ArrayList<>();

                    for(String t:tenures){
                        fitnerTenures.add(t);
                        if(t.equals(request.getMaxTenure())){
                            break;
                        }
                    }



                    if (cardOffer.getValue() != null) {
                        bankResponses.addAll(
                                priceResponses.stream()
                                        .filter(pR -> checkFilter(request.getTenures(), pR.getTenure()))
                                        .filter(pR ->checkFilter(fitnerTenures, pR.getTenure()))
                                        .filter(this::isValidPriceResponseAmount)
                                        .map(this::createMerchantPriceResponse)
                                        .collect(Collectors.toList())
                        );
                    }
                    if (cardResponses.containsKey(bank.getCode()) && cardResponses.get(bank.getCode()).isEmpty()) {
                        cardResponses.remove(bank.getCode());
                    }
                }
            }

            if (Objects.nonNull(cardType) &&
                    response.containsKey(cardType.getCardType()) && response.get(cardType.getCardType()).isEmpty()) {
                response.remove(cardType.getCardType());
            }

        }
        LOGGER.info("response: {}", response);
        return response;
    }

    private boolean isValidPriceResponseAmount(PriceResponse priceResponse) {
        return priceResponse.getPgAmount() != null &&
                (priceResponse.getMinTxnVal() == null || priceResponse.getPgAmount() >= priceResponse.getMinTxnVal()) &&
                (priceResponse.getMaxTxnVal() == null || priceResponse.getPgAmount() <= priceResponse.getMaxTxnVal());
    }

    private List<PriceResponse> filterNonDownPaymentPriceResponses(List<PriceResponse> priceResponses) {
        // UI logic for filtering out standard offer if down payment offer present for same tenure/offerId

        if (CollectionUtils.isEmpty(priceResponses)) {
            return new ArrayList<>();
        }

        List<String> dpOfferIds = priceResponses.stream()
                .filter(priceResponse -> priceResponse.getDownPayment() != null && priceResponse.getDownPayment() > 0)
                .map(PriceResponse::getOfferId)
                .collect(Collectors.toList());

        return priceResponses.stream()
                .filter(priceResponse -> !dpOfferIds.contains(priceResponse.getOfferId()) ||
                        (priceResponse.getDownPayment() != null && priceResponse.getDownPayment() > 0))
                .collect(Collectors.toList());
    }

    private List<PriceResponse> filterMarginMoneyPriceResponses(List<PriceResponse> priceResponses) {
        // UI logic for filtering out margin money flow down payment price responses

        if (CollectionUtils.isEmpty(priceResponses)) {
            return new ArrayList<>();
        }

        return priceResponses.stream()
                .filter(priceResponse -> !("ZERO".equals(priceResponse.getDownpaymentType()) &&
                        priceResponse.getDownPayment() != null && priceResponse.getDownPayment() > 0))
                .collect(Collectors.toList());
    }

    private MerchantPriceResponse createMerchantPriceResponse(PriceResponse priceResponse) {
        return MerchantPriceResponse.builder()
                .tenure(getString(priceResponse.getTenure()))
                .advanceEmiTenure((priceResponse.getAdvanceEmiTenure() == null ||
                        priceResponse.getAdvanceEmiTenure() == 0) ? null :
                        getString(priceResponse.getAdvanceEmiTenure()))
                .emi(getString(priceResponse.getEmi()))
                .irr(getString(priceResponse.getIrr()))
                .cashback(getAmountString(priceResponse.getCashback()))
                .cashbackType(priceResponse.getCashbackType())
                .additionalCashback(getAmountString(priceResponse.getAdditionalCashback()))
                .discount(getAmountString(priceResponse.getDiscount()))
                .additionalDiscount(null)
                .processingFee(getAmountString(priceResponse.getBankProcessingFee()))
                .gstOnProcessingFee(calculateGstOnProcessingFee(priceResponse.getBankProcessingFee(),
                        priceResponse.getBankProcessingIncGst()))
                .downPaymentAmount(getAmountString(priceResponse.getDownPayment()))
                .offerType(Util.getTypeOfEMIOffer(priceResponse).name())
                .totalRepaymentAmount(getString(priceResponse.getEmi() * priceResponse.getTenure()))
                .build();
    }

    private String calculateGstOnProcessingFee(Float bankProcessingFee, Float bankProcessingIncGst) {
        if (bankProcessingIncGst != null && bankProcessingIncGst > 0f &&
                bankProcessingFee != null && bankProcessingFee > 0f && bankProcessingIncGst > bankProcessingFee) {
            return getAmountString(bankProcessingIncGst - bankProcessingFee);
        }
        return getAmountString(null);
    }

    private String getAmountString(Object value) {
        String string = getString(value);
        return string != null ? string : "0";
    }

    private String getString(Object value) {
        return value != null ? value.toString() : null;
    }

    private boolean checkFilter(List<String> filterValues, Object actualValue) {
        return actualValue != null &&
                (CollectionUtils.isEmpty(filterValues) || filterValues.contains(actualValue.toString()));
    }

    private BankEnum getBankFromKey(String key, CardTypeEnum cardType) {
        if (key != null && cardType != null) {
            switch (cardType) {
                case DEBIT:
                case CARDLESS:
                    return BankEnum.getCode(key.replaceFirst("^D_", ""));
                case NTB:
                    return BankEnum.getCode(key.replaceFirst("^NTB_", ""));
                default:
                    return BankEnum.getCode(key);
            }
        }
        return null;
    }

    private CardTypeEnum getCardTypeFromKey(String key) {
        if (key != null) {
            if (key.startsWith("D_")) {
                return CardTypeEnum.DEBIT;
            } else if (key.startsWith("NTB_")) {
                return CardTypeEnum.NTB;
            }
            return CardTypeEnum.CREDIT;
        }
        return null;
    }

    public PostPaymentResponse validateOtp(ValidateOtpRequest validateOtpRequest, String paymentTxnId) {
        return paymentServiceBO.validateOtp(validateOtpRequest, paymentTxnId);
    }

    public AsyncClaimResponse asyncClaim(AsyncClaimRequest request, MerchantUser mu) {
        try {
            return merchantMiddlewareBO.asyncVerifyAndClaim(request, mu);
        } catch (MerchantException me) {
            throw me;
        } catch (Exception e) {
            LOGGER.error("Exception caught while async unclaim product", e);
            throw new MerchantException(MerchantResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    public PgTransactionResponse createTransaction(PgTransactionRequest request, MerchantUser mu){
        if(Util.isNotNull(request) && Util.isNotNull(request.getCardInfo()) && Objects.nonNull(request.getTenure())){
            try {
                if (!mu.getIsSeamless()) {
                    throw new MerchantException(MerchantResponseCode.MERCHANT_SEAMLESS_VALIDATION);
                }

                CardInfo cardInfo = request.getCardInfo();
                Boolean isSeamless = Boolean.FALSE;
                if (StringUtils.hasText(cardInfo.getType())) {
                    if (CardTypeEnum.CREDIT.getCardType().equals(cardInfo.getType())) {
                        isSeamless = Boolean.TRUE;
                    }
                    if (CardTypeEnum.DEBIT.getCardType().equals(cardInfo.getType())) {
                        if (StringUtils.hasText(request.getMobile())) {
                            isSeamless = Boolean.TRUE;
                        }
                    }
                    if (CardTypeEnum.CARDLESS.getCardType().equals(cardInfo.getType())) {
                        isSeamless = Boolean.TRUE;
                    }
                }
                if (Boolean.TRUE.equals(isSeamless)) {
                    return createSeamlessTransactionV2(mu, request, true, TransactionSource.seamless.name(), true);
                }
            }catch (Exception e){
                throw new MerchantException(MerchantResponseCode.getByMessageV2(e.getMessage()));
            }
        }

        return createPgTransaction(mu, request, false, TransactionSource.merchantPg.name());
    }

    public ConsumerProfileConstants getProfileConstant(){
        ConsumerProfileConstants consumerProfileConstants = consumerService.getProfileConstant();
        List<String> loanStages = Constants.loanStages;
        consumerProfileConstants.setLoanStages(loanStages);
        return consumerProfileConstants;

    }

    public EnquiryResponse getTransactionInfo(String transactionId, String partner, MerchantUser mu) {
        String userMobile = mu.getMobile();
        MerchantUser partnerUser = null;
        String merchant;
        List<String> partners = mu.getPartners();
        if (Util.isNotNull(partner) && Util.isNotNull(partners)) {
            if (Util.isNotNull(partners) && partners.contains(partner)) {
                partnerUser = merchantUserBO.getUserByMobile(userMobile + "_" + partner);
                mu = partnerUser;
                merchant = mu.getId().toString();
            } else {
                throw new FreewayException(userMobile + " doesn't have any partner merchant with partner: " + partner);
            }
        } else {
            merchant = mu.getId().toString();
        }
        LOGGER.info("Received request to get transaction by id for transaction id: {} from merchant: {} ",
                transactionId, merchant);

        EnquiryTransactionResponse enquiryTransactionResponse = null;

        CompletableFuture<TransactionResponse> transactionResponseCompletableFuture = CompletableFuture.supplyAsync(() -> paymentServiceBO.getNtbTransactionById(transactionId));
        CompletableFuture<List<NtbLoanResponse>> loanResponseCompletableFuture = CompletableFuture.supplyAsync(() -> ntbService.getLoanStage(transactionId, merchant));

        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(transactionResponseCompletableFuture, loanResponseCompletableFuture);

        try {
            TransactionResponse tr = transactionResponseCompletableFuture.get();
            List<NtbLoanResponse> loanResponses = loanResponseCompletableFuture.get();
            LOGGER.info("Response from API 1: {}", tr);
            LOGGER.info("Response from API 2: {}", loanResponses);
            EnquiryTransactionResponse response = new EnquiryTransactionResponse(tr);
            if(CollectionUtils.isEmpty(loanResponses) && !Constants.TRANSACTION_FAILURE_STATUS.contains(tr.getStatus())){
                response.setLoanStage(Constants.DEFAULT_LOAN_STAGE);
            }
            if (!CollectionUtils.isEmpty(loanResponses)) {
                getLoanStage(response, loanResponses);
            }
            enquiryTransactionResponse = response;
        } catch (Exception e) {
            LOGGER.error("Exception occurred while getting enquiry details: {}", e.getMessage());
            throw new MerchantException(MerchantResponseCode.getByMessage(e.getMessage()));
        }
        return new EnquiryResponse(TransactionCode.SUCCESS.getCode(), TransactionCode.SUCCESS.getStatus(), "", enquiryTransactionResponse);
    }

    private void getLoanStage(EnquiryTransactionResponse enquiryTransactionResponse, List<NtbLoanResponse> loanResponses) {
        enquiryTransactionResponse.setLoanStage(loanResponses.get(0).getCurrentStage());
    }
}
