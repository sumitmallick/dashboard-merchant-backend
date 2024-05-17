package com.freewayemi.merchant.commons.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.freewayemi.merchant.commons.bo.brms.Input;
import com.freewayemi.merchant.commons.bo.eligibility.EligibilityResponse;
import com.freewayemi.merchant.commons.dto.*;
import com.freewayemi.merchant.commons.dto.offer.BankInterestDto;
import com.freewayemi.merchant.commons.dto.offer.InterestPerTenureDto;
import com.freewayemi.merchant.commons.entity.Params;
import com.freewayemi.merchant.commons.exception.FreewayCustomException;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.ntbservice.helper.NtbServiceConstants;
import com.freewayemi.merchant.commons.type.*;
import com.freewayemi.merchant.dto.request.PartnerInfo;
import com.freewayemi.merchant.dto.response.ProviderMasterConfigInfo;
import com.freewayemi.merchant.dto.response.ProviderSchemeDetail;
import com.freewayemi.merchant.type.PartnerCodeEnum;
import org.apache.commons.lang.BooleanUtils;
import org.apache.http.annotation.Obsolete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Util {
    private static final Logger LOGGER = LoggerFactory.getLogger(Util.class);

    private static final String STX_PREFIX = "S";
    private static final String UPI_STX_PREFIX = "SU";
    private static final String MR_REFUND_PREFIX = "MR";
    private static final String SR_REFUND_PREFIX = "SR";
    private static final String DP_REFUND_PREFIX = "MDPR";
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static final String PIPE = "|";
    private static final Float NO_COST_EMI_DIFF = 5f;

    public static String generateOtp(Boolean isProduction) {
        if (isProduction) {
            int num = RANDOM.nextInt(900000) + 100000;
            return String.valueOf(num % 1000000);
        }
        return "123456";
    }

    public static String generateUniqueNumber() {
        int num = RANDOM.nextInt(90000) + 10000;
        return String.valueOf(num % 100000);
    }

    public static String getAlphaNumericString(int n) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index = (int) (AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }

    public static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }

    public static PriceResponse getPriceResponse(String id, float subvention, int tenure, float price) {
        float irr = paymentConstants.IRR / 12.0f / 100.0f;
        float k = (float) Math.pow(1 + irr, tenure);
        float principal = (price * (k - 1)) / (tenure * irr * k);
        float discount = price - principal;
        float emi = price / tenure;
        float discountMerchant = price * (subvention / 100.0f);
        float settlement = price - discountMerchant;
        float gst = discountMerchant * (paymentConstants.GST / 100.0f);
        float netSettlement = settlement - gst;
        return PriceResponse.builder()
                .offerId(id)
                .tenure(tenure)
                .amount(getFLoat(price))
                .discount(getFLoat(discount))
                .bankCharges(getFLoat(discount))
                .emi(getFLoat(emi))
                .irr(getFLoat(paymentConstants.IRR))
                .settlement(getFLoat(settlement))
                .gst(getFLoat(gst))
                .gstPer(getFLoat(paymentConstants.GST))
                .netSettlement(getFLoat(netSettlement))
                .effectiveIrr("No Cost EMI")
                .discountMerchant(discountMerchant)
                .build();
    }

    public static PriceResponse getGSTCardPriceResponse(String id, String cardType, String bankCode, int tenure,
                                                        float price, Float cFee, Float cashback,
                                                        Instant expectedCashbackDate, float processingFeeRate,
                                                        float maxProcessingFee, Float irrpa,
                                                        ProviderParams providerParams) {
        cFee = null == cFee ? 0.0f : cFee;
        if (null != irrpa) {
            double irr = irrpa / 12.0f / 100.0f;
            double k = Math.pow(1 + irr, tenure);
            double Y = k / (k - 1);
            double X = (1.18 * irr * tenure * Y);
            double mul = X - .18f;
            double naya = price / mul;
            float discount = (float) ((price - naya) / 1.18);
            double emi = naya * irr * Y;
            float gst = discount * .18f;
            float pgAmount = Util.getFLoat(getPgAmount(price, cFee, discount, gst, tenure));
            float processingFee = Util.getFLoat(Float.min(processingFeeRate * pgAmount, maxProcessingFee));
            float gstOnProcessingFee = Util.getGstOnProcessingFeeByState(processingFee, providerParams);
            return PriceResponse.builder()
                    .offerId(id)
                    .tenure(tenure)
                    .amount(getFLoat(price))
                    .discount(getFLoat(discount))
                    .bankCharges(getFLoat(discount))
                    .emi(getFLoat(emi))
                    .irr(getFLoat(irrpa))
                    .effectiveIrr("No Cost EMI")
                    .cashback(Math.min(cashback, getFLoat(discount)))
                    .convFee(cFee)
                    .gstOnConvFee(getFLoat(cFee * .18f))
                    .pgAmount(pgAmount)
                    .processingFee(processingFee)
                    .gstOnProcessingFee(gstOnProcessingFee)
                    .build();
        } else {
            return null;
        }
    }


    @Obsolete
    public static PriceResponse getCardPriceResponse_old(String id, String cardType, String bankCode, int tenure,
                                                         float amount, Float convFee, Float cashback,
                                                         Instant expectedCashbackDate, Boolean subventGst,
                                                         float processingFeeRate, float maxProcessingFee, Float irrpa,
                                                         boolean subventProcessingFee, float merchantMaxDiscount,
                                                         ProviderParams providerParams) {
        convFee = null == convFee ? 0.0f : convFee;
        float price = amount + convFee;
        if (null != irrpa) {
            double irr = irrpa / 12.0f / 100.0f;
            double k = Math.pow(1 + irr, tenure);
            double principal = CardTypeEnum.BNPL.getCardType().equalsIgnoreCase(cardType) ? tenure == 0 ? price
                    : price - (price * irr * tenure) : (price * (k - 1)) / (tenure * irr * k);
            double principalWithoutCFee = CardTypeEnum.BNPL.getCardType().equalsIgnoreCase(cardType) ? tenure == 0 ? amount
                    : amount - (amount * irr * tenure) : (amount * (k - 1)) / (tenure * irr * k);
            double emi =
                    CardTypeEnum.BNPL.getCardType().equalsIgnoreCase(cardType) ? tenure == 0 ? price + (convFee * .18f)
                            : (price + (convFee * .18f)) / tenure : (price + (convFee * .18f)) / tenure;
            float discount = (float) (price - principal);
            float discountWithoutCFee = (float) (amount - principalWithoutCFee);
            float gst = null != subventGst && subventGst ? discount * .18f : 0.0f;
            float pgAmount = Util.getFLoat(getPgAmount(price, convFee, discount, gst, tenure));
            float processingFee = Util.getFLoat(Float.min(processingFeeRate * pgAmount, maxProcessingFee));
            float discountProcessingFee =
                    subventProcessingFee ? getDiscountOnProcessingFee(getFLoat(discount), merchantMaxDiscount,
                            Math.min(cashback, getFLoat(discount)), processingFee, cashback) : 0.f;
            float gstOnProcessingFee = Util.getGstOnProcessingFeeByState(processingFee, providerParams);
            return PriceResponse.builder()
                    .offerId(id)
                    .tenure(tenure)
                    .amount(getFLoat(price))
                    .discount(getFLoat(discountWithoutCFee))
                    .bankCharges(getFLoat(discount))
                    .emi(getFLoat(emi))
                    .irr(getFLoat(irrpa))
                    .effectiveIrr("No Cost EMI")
                    .cashback(Math.min(cashback, getFLoat(discountWithoutCFee)))
                    .expectedCashbackDate(expectedCashbackDate)
                    .convFee(convFee)
                    .gstOnConvFee(getFLoat(convFee * .18f))
                    .pgAmount(pgAmount)
                    .processingFee(processingFee)
                    .gstOnProcessingFee(gstOnProcessingFee)
                    .discountOnProcessingFee(discountProcessingFee)
                    .interestWithoutCFee(discountWithoutCFee)
                    .build();
        } else {
            return null;
        }
    }

    public static PriceResponse getCardPriceResponse(String id, String cardType, String bankCode, int tenure,
                                                     float amount, Float convFee, Float cashback,
                                                     Instant expectedCashbackDate, Boolean subventGst,
                                                     float processingFeeRate, float maxProcessingFee, Float irrpa,
                                                     boolean subventProcessingFee, float merchantMaxDiscount,
                                                     ProviderParams providerParams) {
        convFee = null == convFee ? 0.0f : convFee;
        float price = amount + convFee + (convFee * .18f);
        if (null != irrpa) {
            double irr = irrpa / 12.0f / 100.0f;
            double k = (float) Math.pow(1 + irr, tenure);
            double principal = CardTypeEnum.BNPL.getCardType().equalsIgnoreCase(cardType) ? tenure == 0 ? price
                    : price - (price * irr * tenure) : (price * (k - 1)) / (tenure * irr * k);
            double principalWithoutCFee = CardTypeEnum.BNPL.getCardType().equalsIgnoreCase(cardType) ? tenure == 0 ? amount
                    : amount - (amount * irr * tenure) : (amount * (k - 1)) / (tenure * irr * k);
            double emi =
                    CardTypeEnum.BNPL.getCardType().equalsIgnoreCase(cardType) ? tenure == 0 ? price : price / tenure
                            : price / tenure;
            float discount = (float) (price - principal);
            float discountWithoutCFee = (float) (amount - principalWithoutCFee);
            float gst = null != subventGst && subventGst ? discount * .18f : 0.0f;
            float pgAmount = Util.getFLoat(getPgAmount(amount, convFee, discount, gst, tenure));
            float processingFee = Util.getFLoat(Float.min(processingFeeRate * pgAmount, maxProcessingFee));
            float discountProcessingFee =
                    subventProcessingFee ? getDiscountOnProcessingFee(getFLoat(discount), merchantMaxDiscount,
                            Math.min(cashback, getFLoat(discount)), processingFee, cashback) : 0.f;
            float gstOnProcessingFee = Util.getGstOnProcessingFeeByState(processingFee, providerParams);
            return PriceResponse.builder()
                    .offerId(id)
                    .tenure(tenure)
                    .amount(getFLoat(price))
                    .discount(getFLoat(discountWithoutCFee))
                    .bankCharges(getFLoat(discount))
                    .emi(getFLoat(emi))
                    .irr(getFLoat(irrpa))
                    .effectiveIrr("No Cost EMI")
                    .cashback(Math.min(cashback, getFLoat(discountWithoutCFee)))
                    .expectedCashbackDate(expectedCashbackDate)
                    .convFee(convFee)
                    .gstOnConvFee(getFLoat(convFee * .18f))
                    .pgAmount(pgAmount)
                    .processingFee(processingFee)
                    .gstOnProcessingFee(gstOnProcessingFee)
                    .discountOnProcessingFee(discountProcessingFee)
                    .interestWithoutCFee(discountWithoutCFee)
                    .build();
        } else {
            return null;
        }
    }

    private static Float getEffectivePrincipal(float rate, int tenure, float interest) {
        double irr = rate / 12.0f / 100.0f;
        double k;
        if (tenure == 0) {
            k = Math.pow(1 + irr, 0.5);
            return (float) (interest / ((irr * 0.5f * (k / (k - 1))) - 1));
        } else {
            k = Math.pow(1 + irr, tenure);
            return (float) (interest / ((irr * tenure * (k / (k - 1))) - 1));
        }
    }

    public static PriceResponse noCostEmiWithdownPayment(Float cFee, Float cashback, Instant expectedCashbackDate,
                                                         Integer tenure, Float irrpa, String cardType, String ccieName,
                                                         float discount, float price, Boolean subventGst,
                                                         float processingFeeRate, float maxProcessingFee,
                                                         int advanceEmiTenure, boolean subventProcessingFee,
                                                         float maxDiscount, ProviderParams providerParams) {
        // Considering Discount + Cashback as bank Interest
        // In Merchant Subvention case Cashback=0 and Discount=Bank_Charges
        // In Brand Subvention case Cashback=Bank_Charges and Discount=0
        // Down Payment module is not tested for combines Scenario
        float effectivePrincipal = getEffectivePrincipal(irrpa, tenure, discount + cashback);
        return noCostEmiAmountManual(cFee, cashback, expectedCashbackDate, tenure, irrpa, cardType, ccieName, discount,
                effectivePrincipal, price - effectivePrincipal, subventGst, processingFeeRate, maxProcessingFee,
                advanceEmiTenure, subventProcessingFee, maxDiscount, providerParams);
    }

    public static PriceResponse noCostEmiAmountManual(Float convFee, Float cashback, Instant expectedCashbackDate,
                                                      Integer tenure, Float irrpa, String cardType, String ccieName,
                                                      float discount, float price, Boolean subventGst,
                                                      float processingFeeRate, float maxProcessingFee,
                                                      int advanceEmiTenure, boolean subventProcessingFee,
                                                      float maxDiscount, ProviderParams providerParams,
                                                      String calculationType) {
        return noCostEmiAmountManual(convFee, cashback, expectedCashbackDate, tenure, irrpa, cardType, ccieName,
                discount, price, null, subventGst, processingFeeRate, maxProcessingFee, advanceEmiTenure,
                subventProcessingFee, maxDiscount, providerParams, calculationType);
    }

    public static PriceResponse noCostEmiAmountManual(Float convFee, Float cashback, Instant expectedCashbackDate,
                                                      Integer tenure, Float irrpa, String cardType, String ccieName,
                                                      float discount, float price, Boolean subventGst,
                                                      float processingFeeRate, float maxProcessingFee,
                                                      int advanceEmiTenure, boolean subventProcessingFee,
                                                      float maxDiscount, ProviderParams providerParams) {
        return noCostEmiAmountManual(convFee, cashback, expectedCashbackDate, tenure, irrpa, cardType, ccieName,
                discount, price, null, subventGst, processingFeeRate, maxProcessingFee, advanceEmiTenure,
                subventProcessingFee, maxDiscount, providerParams);
    }

    public static PriceResponse noCostEmiAmountManual(Float convFee, Float cashback, Instant expectedCashbackDate,
                                                      Integer tenure, Float irrpa, String cardType, String ccieName,
                                                      float discount, float price, Float downPayment,
                                                      Boolean subventGst, float processingFeeRate,
                                                      float maxProcessingFee, int advanceEmiTenure,
                                                      boolean subventProcessingFee, float maxDiscount,
                                                      ProviderParams providerParams, String calculationType) {
        convFee = null == convFee ? 0.0f : convFee;
        float txnAmount = price;
        price = price + convFee + (convFee * .18f);
        double irr = irrpa / 12.0f / 100.0f;
        double k = (float) Math.pow(1 + irr, tenure);
        double finalp = CardTypeEnum.BNPL.getCardType().equalsIgnoreCase(cardType) ? price + (price * irr * tenure)
                : (k / (k - 1)) * price * irr * tenure;
        double finalLpWithoutCFee = CardTypeEnum.BNPL.getCardType().equalsIgnoreCase(cardType) ? txnAmount + (txnAmount * irr * tenure)
                : (k / (k - 1)) * txnAmount * irr * tenure;
        double emi = CardTypeEnum.BNPL.getCardType().equalsIgnoreCase(cardType) ? tenure == 0 ? finalp : finalp / tenure
                : finalp - price - discount < 0 ? (price + discount) / tenure : (finalp) / tenure;
        String effectiveIrr = finalp - price - discount < 10 ? "No Cost EMI"
                : getEffectiveIrr((float) emi, tenure, price + discount, irrpa.toString()) + "% pa";
        double interest = finalp - price;
        double interestWithoutCFee = finalLpWithoutCFee - txnAmount;
        float gst = null != subventGst && subventGst ? getFLoat(interest) * .18f : 0.0f;
        float pgAmount = Util.getFLoat(getPgAmount(txnAmount + (float) Math.min(discount, interest), convFee,
                (float) Math.min(discount, interest), gst, tenure));
        float processingFee = Util.getFLoat(Float.min(processingFeeRate * pgAmount, maxProcessingFee));
        float gstOnProcessingFee = Util.getGstOnProcessingFeeByState(processingFee, providerParams);
        float discountProcessingFee =
                subventProcessingFee ? getDiscountOnProcessingFee(getFLoat(Math.min(discount, interest)), maxDiscount,
                        getFLoat(Math.min(cashback, getFLoat(interest))), processingFee, cashback) : 0.f;
        int effectiveTenure = advanceEmiTenure > 0 ? tenure + advanceEmiTenure : tenure;
        String cashbackTypeStr = null;
        if (isSubvented(discount, cashback) && StringUtils.hasText(calculationType) &&
                calculationType.equals("ICICI_CARDLESS_NOCOST_EMI")) {
            irr = irrpa / 12.0f / 100.0f;
            k = Math.pow(1 + irr, tenure);
            double netAmount = ((txnAmount) / irr / tenure) * (k - 1) / k;
            interest = txnAmount - netAmount;
            cashback = Math.min(cashback, getFLoat(interest));
            interestWithoutCFee = getFLoat(interest);
            LOGGER.info("cashback: {}", cashback);
            emi = (txnAmount - cashback + interest) / (tenure * 1.0f);
            cashbackTypeStr = CashbackType.instant.name();
        }

        return PriceResponse.builder()
                .offerId(ccieName)
                .tenure(effectiveTenure)
                .amount(getFLoat(finalp))
                .discount(getFLoat(Math.min(discount, interestWithoutCFee)))
                .bankCharges(getFLoat(interest))
                .emi(getFLoat(emi))
                .irr(getFLoat(irrpa))
                .effectiveIrr(effectiveIrr)
                .downPayment(getFLoat(downPayment))
                .cashback(Math.min(cashback, getFLoat(interestWithoutCFee)))
                .expectedCashbackDate(expectedCashbackDate)
                .convFee(convFee)
                .gstOnConvFee(getFLoat(convFee * .18f))
                .pgAmount(pgAmount)
                .processingFee(processingFee)
                .gstOnProcessingFee(gstOnProcessingFee)
                .advanceEmiTenure(advanceEmiTenure)
                .discountOnProcessingFee(discountProcessingFee)
                .cashbackType(cashbackTypeStr)
                .interestWithoutCFee(getFLoat(interestWithoutCFee))
                .build();
    }

    public static PriceResponse noCostEmiAmountManual(Float convFee, Float cashback, Instant expectedCashbackDate,
                                                      Integer tenure, Float irrpa, String cardType, String ccieName,
                                                      float discount, float price, Float downPayment,
                                                      Boolean subventGst, float processingFeeRate,
                                                      float maxProcessingFee, int advanceEmiTenure,
                                                      boolean subventProcessingFee, float maxDiscount,
                                                      ProviderParams providerParams) {
        convFee = null == convFee ? 0.0f : convFee;
        float txnAmount = price;
        price = price + convFee + (convFee * .18f);
        double irr = irrpa / 12.0f / 100.0f;
        double k = (float) Math.pow(1 + irr, tenure);
        double finalp = CardTypeEnum.BNPL.getCardType().equalsIgnoreCase(cardType) ? price + (price * irr * tenure)
                : (k / (k - 1)) * price * irr * tenure;
        double finalpWithoutCFee = CardTypeEnum.BNPL.getCardType().equalsIgnoreCase(cardType) ? txnAmount + (txnAmount * irr * tenure)
                : (k / (k - 1)) * txnAmount * irr * tenure;
        double emi = CardTypeEnum.BNPL.getCardType().equalsIgnoreCase(cardType) ? tenure == 0 ? finalp : finalp / tenure
                : finalp - price - discount < 0 ? (price + discount) / tenure : (finalp) / tenure;
        String effectiveIrr = finalp - price - discount < 10 ? "No Cost EMI"
                : getEffectiveIrr((float) emi, tenure, price + discount, irrpa.toString()) + "% pa";
        double interest = finalp - price;
        double interestWithoutCFee = finalpWithoutCFee - txnAmount;
        float gst = null != subventGst && subventGst ? getFLoat(interest) * .18f : 0.0f;
        float pgAmount = Util.getFLoat(getPgAmount(txnAmount + (float) Math.min(discount, interest), convFee,
                (float) Math.min(discount, interest), gst, tenure));
        float processingFee = Util.getFLoat(Float.min(processingFeeRate * pgAmount, maxProcessingFee));
        float gstOnProcessingFee = Util.getGstOnProcessingFeeByState(processingFee, providerParams);
        float discountProcessingFee =
                subventProcessingFee ? getDiscountOnProcessingFee(getFLoat(Math.min(discount, interest)), maxDiscount,
                        getFLoat(Math.min(cashback, getFLoat(interest))), processingFee, cashback) : 0.f;
        int effectiveTenure = advanceEmiTenure > 0 ? tenure + advanceEmiTenure : tenure;
        return PriceResponse.builder()
                .offerId(ccieName)
                .tenure(effectiveTenure)
                .amount(getFLoat(finalp))
                .discount(getFLoat(Math.min(discount, interestWithoutCFee)))
                .bankCharges(getFLoat(interest))
                .emi(getFLoat(emi))
                .irr(getFLoat(irrpa))
                .effectiveIrr(effectiveIrr)
                .downPayment(getFLoat(downPayment))
                .cashback(Math.min(cashback, getFLoat(interestWithoutCFee)))
                .expectedCashbackDate(expectedCashbackDate)
                .convFee(convFee)
                .gstOnConvFee(getFLoat(convFee * .18f))
                .pgAmount(pgAmount)
                .processingFee(processingFee)
                .gstOnProcessingFee(gstOnProcessingFee)
                .advanceEmiTenure(advanceEmiTenure)
                .discountOnProcessingFee(discountProcessingFee)
                .interestWithoutCFee(getFLoat(interestWithoutCFee))
                .build();
    }

    private static String getEffectiveIrr(float emi, int tenure, float principal, String defaultV) {
        double out1 = emi / principal;
        for (int x = 1; x < 15; x++) {
            double raisen;
            if (tenure == 0) {
                raisen = Math.pow(1 + (x * 1.0 / 1200.0), 0.5f);
            } else {
                raisen = Math.pow(1 + (x * 1.0 / 1200.0), tenure);
            }
            double raisenminus = raisen - 1.0;
            double out = (raisen / raisenminus) * (x * 1.0) / 1200.0;
            if (out > out1) {
                return Integer.toString(x);
            }
        }
        return defaultV;
    }

    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            return input;
        }
    }

    public static String sha256WithSalt(String input, String salt) {
        String output = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] b = md.digest(input.getBytes(StandardCharsets.UTF_8));
            output = bytesToHex(b);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("NoSuchAlgorithmException occurred while generating SHA256: ", e);
        }
        return output;
    }

    public static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static Float getFLoat(Float input) {
        return null == input ? null : Float.valueOf(df.format(input));
    }

    public static Float getFLoat(Double input) {
        return null == input ? null : Float.valueOf(df.format(input));
    }

    public static boolean isEmptyString(String stringToCheck) {
        return stringToCheck == null || stringToCheck.trim().length() <= 0;
    }

    public static boolean isNotNull(Object object) {
        return (object != null) ? Boolean.TRUE : Boolean.FALSE;
    }

    public static boolean isNull(Object object) {
        return (object == null) ? Boolean.TRUE : Boolean.FALSE;
    }

    public static String generatepaymentTxnId() {
        return STX_PREFIX + generateRandomNumber();
    }

    public static String generateUpipaymentTxnId() {
        return UPI_STX_PREFIX + generateRandomNumber();
    }

    public static String generateRandomNumber() {
        String currentTimestamp = DateUtil.getDateTimeWithPattern("ddMMyyHHmmss", LocalDateTime.now());
        return currentTimestamp + generate7CharacterRandomNumber().toString();
    }

    public static Integer generate7CharacterRandomNumber() {
        return 1000000 + RANDOM.nextInt(9000000);
    }

    public static String generate19DigitRandomNumber() {
        String currentTimestamp = DateUtil.getDateTimeWithPattern("ddMMyyHHmmss", LocalDateTime.now());
        return STX_PREFIX + currentTimestamp + generate6CharacterRandomNumber().toString();
    }

    private static Integer generate6CharacterRandomNumber() {
        return 100000 + RANDOM.nextInt(900000);
    }

    public static Integer generate10CharacterRandomNumber() {
        return 1000000000 + RANDOM.nextInt(900000) + RANDOM.nextInt(9000);
    }

    public static String generate20DigitRandomNumber() {
        String currentTimestamp = DateUtil.getDateTimeWithPattern("ddMMyyHHmmss", LocalDateTime.now());
        return currentTimestamp + generate8CharacterRandomNumber().toString();
    }

    public static Integer generate8CharacterRandomNumber() {
        return 10000000 + RANDOM.nextInt(900000) + RANDOM.nextInt(9000);
    }

    public static String generate18CharacterpaymentTxnId() {
        String currentTimestamp = DateUtil.getDateTimeWithPattern("ddMMyyHHmmss", LocalDateTime.now());
        return STX_PREFIX + currentTimestamp + generate5CharacterRandomNumber().toString();
    }

    public static String generate18CharacterpaymentTxnIdWithPrefix(String prefix) {
        String currentTimestamp = DateUtil.getDateTimeWithPattern("ddMMyyHHmmss", LocalDateTime.now());
        return prefix + currentTimestamp + generate5CharacterRandomNumber();
    }

    private static Integer generate5CharacterRandomNumber() {
        return 10000 + RANDOM.nextInt(90000);
    }

    public static String convertToQueryString(Class<?> clazz, Object o) {
        StringBuffer stringBuffer = new StringBuffer("?");
        try {
            for (Field f : clazz.getDeclaredFields()) {
                f.setAccessible(true);
                stringBuffer.append(f.getName() + "=" + String.valueOf(f.get(o)) + "&");
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred while converting to query string: ", e);
        }
        String queryString = stringBuffer.toString();
        return queryString.substring(0, queryString.length() - 1);
    }

    public static String getNoOfCharFromStringFromEnd(String str, int number) {
        String response = "";
        if (!isEmptyString(str) && str.length() > number) {
            response = str.substring(str.length() - number);
        } else {
            response = str;
        }
        return response;
    }

    public static String convertToString(Object object, boolean isWithRootValue) {
        String response = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (isWithRootValue) {
                objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
            }
            response = objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            LOGGER.error("JsonProcessingException occurred while converting object to string: ", e);
        }
        return response;
    }

    public static <T> T convertToJson(String stringToBeConverted, Class<T> classType) {
        return objectMapper.convertValue(stringToBeConverted, classType);
    }

    public static <T> T convertToJsonObject(String stringToBeConverted, Class<T> classType) throws IOException {
        return objectMapper.readValue(stringToBeConverted, classType);
    }

    public static String getExpMonthAndExpYear(String vaultExpMonth, String expMonthPattern, String vaultExpYear,
                                               String expYearPattern) {
        return getExpMonth(vaultExpMonth, expMonthPattern) + getExpYear(vaultExpYear, expYearPattern);
    }

    public static String getExpYearAndMonth(String vaultExpYear, String expYearPattern, String vaultExpMonth,
                                            String expMonthPattern) {
        return getExpYear(vaultExpYear, expYearPattern) + getExpMonth(vaultExpMonth, expMonthPattern);
    }

    public static String getBin(String cardNumber) {
        cardNumber = StringUtils.isEmpty(cardNumber) ? cardNumber : cardNumber.replaceAll(" ", "");
        if (!StringUtils.isEmpty(cardNumber) && cardNumber.length() >= 6) {
            return cardNumber.substring(0, 6);
        }
        return cardNumber;
    }

    public static String getExpMonth(String vaultExpMonth, String pattern) {
        String expMon;
        int vaultExpMonLen = vaultExpMonth.length();
        if ("MM".equalsIgnoreCase(pattern) && vaultExpMonLen > 1) {
            expMon = vaultExpMonth;
        } else if ("M".equalsIgnoreCase(pattern) && vaultExpMonLen > 1) {
            expMon = vaultExpMonth.substring(vaultExpMonLen - 1);
        } else if ("MM".equalsIgnoreCase(pattern) && vaultExpMonLen == 1) {
            expMon = "0" + vaultExpMonth;
        } else {
            expMon = vaultExpMonth;
        }

        return expMon;
    }

    public static String getExpYear(String vaultExpYear, String pattern) {
        String expYear;
        int vaultExpYearLen = vaultExpYear.length();

        if ("YYYY".equalsIgnoreCase(pattern) && vaultExpYearLen > 2) {
            expYear = vaultExpYear;
        } else if ("YY".equalsIgnoreCase(pattern) && vaultExpYearLen > 2) {
            expYear = vaultExpYear.substring(vaultExpYearLen - 2);
        } else if ("YYYY".equalsIgnoreCase(pattern) && vaultExpYearLen == 2) {
            expYear = "20" + vaultExpYear;
        } else {
            expYear = vaultExpYear;
        }
        return expYear;
    }

    public static String getMaskCardNumber(String cardNumber) {
        String maskedCardNumber = null;
        if (Util.isNotNull(cardNumber)) {
            cardNumber = cardNumber.replaceAll(" ", "");
            int cardNumLen = cardNumber.length();

            switch (cardNumLen) {
                case 0:
                    break;
                case 16:
                    maskedCardNumber = cardNumber.substring(0, 4) + " " + cardNumber.substring(4, 6) + "XX" + " XXXX " +
                            cardNumber.substring(cardNumLen - 4);
                    break;
                case 19:
                    maskedCardNumber =
                            cardNumber.substring(0, 4) + " " + cardNumber.substring(4, 6) + "XX" + " XXXX XXXX " +
                                    cardNumber.substring(cardNumLen - 4);
                    break;
                default:
                    maskedCardNumber = getFirst4AndLast4(cardNumber, cardNumLen);
                    break;
            }
        }
        return maskedCardNumber;
    }

    public static String getTruncatedLast4DigitsOfTheCardNumber(String cardNumber) {
        String maskedCardNumber = null;
        cardNumber = cardNumber.replaceAll(" ", "");
        int cardNumLen = cardNumber.length();

        switch (cardNumLen) {
            case 0:
                break;
            case 16:
                maskedCardNumber = "XXXX XXXX XXXX " + cardNumber.substring(cardNumLen - 4);
                break;
            case 19:
                maskedCardNumber = "XXXX XXXX XXXX XXXX " + cardNumber.substring(cardNumLen - 4);
                break;
            default:
                maskedCardNumber = "XXXX XXXX" + " XXXX " + cardNumber.substring(cardNumLen - 4);
                break;
        }
        return maskedCardNumber;
    }

    public static String getCharactersFromStart(String cardNumber, int length) {
        cardNumber = cardNumber.replaceAll(" ", "");
        return StringUtils.hasText(cardNumber) ? cardNumber.substring(0, length) : "";
    }

    public static String getRblTruncatedCardNumber(String cardNumber) {
        cardNumber = cardNumber.replaceAll(" ", "");
        int cardNumLen = cardNumber.length();
        return "XX" + cardNumber.substring(cardNumLen - 4);
    }

    public static String getCharactersFromEnd(String data, int length) {
        data = data.replaceAll(" ", "");
        int dataLen = data.length();
        return data.substring(dataLen - length);
    }

    private static String getFirst4AndLast4(String cardNumber, int cardNumLen) {
        return cardNumber.substring(0, 4) + " XXXX" + " XXXX " + cardNumber.substring(cardNumLen - 4);
    }

    public static String truncateString(String input) {
        String truncatedString = "";
        if (!StringUtils.isEmpty(input)) {
            for (int i = 1; i <= input.length(); i++) {
                truncatedString = truncatedString.concat("X");
            }
        }
        return truncatedString;
    }

    public static String handleServiceFailureResp(String error, String paramName) {
        try {
            if (!StringUtils.isEmpty(error)) {
                JsonNode jsonResp = convertToJsonObject(error, JsonNode.class);
                return jsonResp.get(paramName).asText();
            }
            throw new FreewayException("Something went wrong!");
        } catch (JsonParseException e) {
            LOGGER.error("JsonParseException occurred while paring error: " + error, e);
            throw new FreewayException(paymentConstants.INTERNAL_SERVER_ERROR);
        } catch (JsonMappingException e) {
            LOGGER.error("JsonMappingException occurred while paring error: " + error, e);
            throw new FreewayException(paymentConstants.INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            LOGGER.error("IOException occurred while paring error: " + error, e);
            throw new FreewayException(paymentConstants.INTERNAL_SERVER_ERROR);
        }
    }

    public static String createProviderHtml(String formActionUrl, Map<String, String> reqParams) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<!DOCTYPE html><html><head>")
                .append("<title>Payment Redirection</title>")
                .append("</head>")
                .append("<body onload=\"document.submitForm.submit()\" >")
                .append("<form name=\"submitForm\" action=")
                .append("\"")
                .append(formActionUrl)
                .append("\"")
                .append(" method=")
                .append("\"")
                .append("post")
                .append("\"")
                .append(" >");

        ArrayList<String> fieldNames = new ArrayList<String>(reqParams.keySet());
        for (String fieldName : fieldNames) {
            String fieldValue = reqParams.get(fieldName);
            stringBuilder.append("<input type=\"hidden\" name=")
                    .append("\"")
                    .append(fieldName)
                    .append("\"")
                    .append(" id=")
                    .append("\"")
                    .append(fieldName)
                    .append("\"")
                    .append(" value=")
                    .append("'")
                    .append(fieldValue)
                    .append("'")
                    .append(" />");
        }
        stringBuilder.append("</form>").append("</body>").append("</html>");
        return stringBuilder.toString();
    }

    public static String createMockKycHtml(String formActionUrl) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<!DOCTYPE html><html><head>")
                .append("<title>payment Mock Kyc</title>")
                .append("</head>")
                .append("<body> <center>")
                .append("<form name=\"submitForm\" action=")
                .append("\"")
                .append(formActionUrl)
                .append("\"")
                .append(" method=")
                .append("\"")
                .append("GET")
                .append("\"")
                .append(" >");
        stringBuilder.append(
                "Aadhaar No : <input id=\"txt_aadhar\" type=\"text\" value=\"\" maxlength=\"12\"> </input><br><br>");
        stringBuilder.append(
                "Enter Otp: <input id=\"txt_otp\" type=\"text\" value=\"\" maxlength=\"6\"> </input><br><br>");
        stringBuilder.append("<input type=\"submit\" name=\"status\" value=\"success\" > </input>");
        stringBuilder.append("&nbsp;&nbsp;&nbsp;");
        stringBuilder.append("<input type=\"submit\" name=\"status\" value=\"failed\" > </input>");
        stringBuilder.append("</form>").append("</center> </body>").append("</html>");
        return stringBuilder.toString();
    }

    public static List<OfferResponse> getOffersFromMdrs(List<Mdr> mdrs, List<OfferResponse> subventions,
                                                        Boolean isConvFee) {
        boolean isFlat = true;
        for (Mdr mdr : mdrs) {
            if (null != mdr.getCardType()) {
                isFlat = false;
            }
        }
        List<OfferResponse> list = new ArrayList<>();
        if (isFlat) {
            for (Integer tenure : new Integer[]{3, 6, 9, 12, 18, 24}) {
                Mdr mdr = getOfferFromMdr(null, tenure, mdrs);
                String type = getTypeFromSubventions("DEBIT", tenure, subventions, isConvFee);
                if (null != mdr) {
                    list.add(new OfferResponse(mdr.getUuid(), tenure, mdr.getRate(), true, null, mdr.getProductId(),
                            null, mdr.getBankCode(), null, null, null, null, null, null, null, null, null, null, null,
                            null, null, null, null, null, null, null, null).setType(type));
                }
            }
        } else {
            for (String cardType : new String[]{"DEBIT", "CREDIT"}) {
                for (Integer tenure : new Integer[]{3, 6, 9, 12, 18, 24}) {
                    Mdr mdr = getOfferFromMdr(cardType, tenure, mdrs);
                    String type = getTypeFromSubventions(cardType, tenure, subventions, isConvFee);
                    if (null != mdr) {
                        list.add(new OfferResponse(mdr.getUuid(), tenure, mdr.getRate(), true, cardType,
                                mdr.getProductId(), null, mdr.getBankCode(), null, null, null, null, null, null, null,
                                null, null, null, null, null, null, null, null, null, null, null, null).setType(type));
                    }
                }
            }
        }
        return list;
    }

    private static String getTypeFromSubventions(String cardType, Integer tenure, List<OfferResponse> subventions,
                                                 Boolean isConvFee) {
        if (null != isConvFee && isConvFee) {
            return "Convenience Fee";
        }
        if (null == subventions || subventions.isEmpty()) {
            return null;
        }
        float threshold = getPer(cardType, tenure);
        OfferResponse offer = getOfferFromSubvention(cardType, tenure, subventions);
        if (null == offer) {
            return null;
        }
        if (offer.getSubvention() == 0) {
            return "Cost EMI";
        } else if (offer.getSubvention() >= threshold) {
            return "No Cost EMI";
        }
        return "Low Cost EMI";
    }

    private static OfferResponse getOfferFromSubvention(String cardType, Integer tenure,
                                                        List<OfferResponse> subventions) {
        for (OfferResponse mdr : subventions) {
            if (tenure.equals(mdr.getTenure()) && null != cardType && cardType.equals(mdr.getCardType())) {
                return mdr;
            }
        }
        for (OfferResponse mdr : subventions) {
            if ((tenure.equals(mdr.getTenure()) && null == mdr.getCardType()) ||
                    (null != mdr.getTenure() && mdr.getTenure().equals(-1) && cardType.equals(mdr.getCardType()))) {
                return mdr;
            }
        }
        for (OfferResponse mdr : subventions) {
            if (null != mdr.getTenure() && mdr.getTenure().equals(-1) && null == mdr.getCardType()) {
                return mdr;
            }
        }
        return null;
    }

    private static float getPer(String cardType, Integer tenure) {
        switch (tenure) {
            case 3:
                return "CREDIT".equals(cardType) ? 2.45f : 2.61f;
            case 6:
                return "CREDIT".equals(cardType) ? 4.23f : 4.51f;
            case 9:
                return "CREDIT".equals(cardType) ? 5.97f : 6.35f;
            case 12:
                return "CREDIT".equals(cardType) ? 7.67f : 8.15f;
            case 18:
                return "CREDIT".equals(cardType) ? 10.95f : 11.62f;
            case 24:
                return "CREDIT".equals(cardType) ? 14.07f : 14.90f;
        }
        return 0.0f;
    }

    private static Mdr getOfferFromMdr(String cardType, Integer tenure, List<Mdr> mdrs) {
        for (Mdr mdr : mdrs) {
            if (tenure.equals(mdr.getTenure()) && null != cardType && cardType.equals(mdr.getCardType())) {
                return mdr;
            }
        }
        for (Mdr mdr : mdrs) {
            if ((tenure.equals(mdr.getTenure()) && null == mdr.getCardType()) ||
                    (null != mdr.getTenure() && mdr.getTenure().equals(-1) && null != cardType &&
                            cardType.equals(mdr.getCardType()))) {
                return mdr;
            }
        }
        for (Mdr mdr : mdrs) {
            if (null != mdr.getTenure() && mdr.getTenure().equals(-1) && null == mdr.getCardType()) {
                return mdr;
            }
        }
        return null;
    }

    public static Float getMdr(List<Mdr> mdrs, String bankCode, String cardType, Integer tenure, String productId,
                               Float txnAmount) {
        if (StringUtils.isEmpty(productId)) {
            for (Mdr mdr : mdrs) {
                if (bankCode.equals(mdr.getBankCode()) && cardType.equals(mdr.getCardType()) &&
                        tenure.equals(mdr.getTenure()) && isValidTxnAmount(txnAmount, mdr.getMinAmount())) {
                    return mdr.getRate() / 100.0f;
                }
                if ("CARDLESS".equalsIgnoreCase(cardType) && "HDFC".equalsIgnoreCase(bankCode) &&
                        "DEBIT".equalsIgnoreCase(mdr.getCardType()) && "HDFC".equalsIgnoreCase(mdr.getBankCode()) &&
                        tenure.equals(mdr.getTenure()) && isValidTxnAmount(txnAmount, mdr.getMinAmount())) {
                    return mdr.getRate() / 100.0f;
                }
                if ("CARDLESS".equalsIgnoreCase(cardType) && "KKBK".equalsIgnoreCase(bankCode) &&
                        "DEBIT".equalsIgnoreCase(mdr.getCardType()) && "KKBK".equalsIgnoreCase(mdr.getBankCode()) &&
                        tenure.equals(mdr.getTenure()) && isValidTxnAmount(txnAmount, mdr.getMinAmount())) {
                    return mdr.getRate() / 100.0f;
                }
//                if ("NTB".equalsIgnoreCase(cardType) && "KKBK".equalsIgnoreCase(bankCode) &&
//                        "DEBIT".equalsIgnoreCase(mdr.getCardType()) && "KKBK".equalsIgnoreCase(mdr.getBankCode()) &&
//                        tenure.equals(mdr.getTenure()) && isValidTxnAmount(txnAmount, mdr.getMinAmount())) {
//                    return mdr.getRate() / 100.0f;
//                }
            }
        }

        String pdt = null == productId ? "" : productId;
        List<Mdr> nMdrs = mdrs.stream()
                .map(mdr -> mdr.setScore(getScore(mdr, bankCode, cardType, tenure, productId, txnAmount)))
                .collect(Collectors.toList())
                .stream()
                .sorted(Comparator.comparingInt(Mdr::getScore).reversed())
                .map(mdr -> {
                    Mdr nMdr = Mdr.builder().build();
                    nMdr.setCardType(null == mdr.getCardType() ? cardType : mdr.getCardType());
                    nMdr.setBankCode(null == mdr.getBankCode() ? bankCode : mdr.getBankCode());
                    nMdr.setTenure(null == mdr.getTenure() || mdr.getTenure().equals(-1) ? tenure : mdr.getTenure());
                    nMdr.setProductId(null == mdr.getProductId() ? pdt : mdr.getProductId());
                    nMdr.setRate(mdr.getRate());
                    nMdr.setMinAmount(null == mdr.getMinAmount() ? txnAmount : mdr.getMinAmount());
                    return nMdr;
                })
                .collect(Collectors.toList());
        for (Mdr mdr : nMdrs) {
            if (mdr.getBankCode().equals(bankCode) && mdr.getCardType().equals(cardType) &&
                    mdr.getTenure().equals(tenure) && mdr.getProductId().equals(pdt) &&
                    isValidTxnAmount(txnAmount, mdr.getMinAmount())) {
                return mdr.getRate() / 100.0f;
            }
            if ("CARDLESS".equalsIgnoreCase(cardType) && "HDFC".equalsIgnoreCase(bankCode) &&
                    "DEBIT".equalsIgnoreCase(mdr.getCardType()) && "HDFC".equalsIgnoreCase(mdr.getBankCode()) &&
                    tenure.equals(mdr.getTenure()) && isValidTxnAmount(txnAmount, mdr.getMinAmount())) {
                return mdr.getRate() / 100.0f;
            }
            if ("CARDLESS".equalsIgnoreCase(cardType) && "KKBK".equalsIgnoreCase(bankCode) &&
                    "DEBIT".equalsIgnoreCase(mdr.getCardType()) && "KKBK".equalsIgnoreCase(mdr.getBankCode()) &&
                    tenure.equals(mdr.getTenure()) && isValidTxnAmount(txnAmount, mdr.getMinAmount())) {
                return mdr.getRate() / 100.0f;
            }
//            if ("NTB".equalsIgnoreCase(cardType) && "KKBK".equalsIgnoreCase(bankCode) &&
//                    "DEBIT".equalsIgnoreCase(mdr.getCardType()) && "KKBK".equalsIgnoreCase(mdr.getBankCode()) &&
//                    tenure.equals(mdr.getTenure()) && isValidTxnAmount(txnAmount, mdr.getMinAmount())) {
//                return mdr.getRate() / 100.0f;
//            }
        }
        return null;
    }

    private static boolean isValidTxnAmount(Float txnAmount, Float minimumOfferAmount) {
        return null != txnAmount && null != minimumOfferAmount &&
                Util.getFLoat(txnAmount) >= Util.getFLoat(minimumOfferAmount);
    }

    private static Integer getScore(Mdr mdr, String bankCode, String cardType, Integer tenure, String productId,
                                    Float txnAmount) {
        int score = 0;
        if (tenure.equals(mdr.getTenure())) {
            score += 1;
        }
        if (cardType.equals(mdr.getCardType())) {
            score += 1;
        }
        if (bankCode.equals(mdr.getBankCode())) {
            score += 1;
        }
        if (null != productId && productId.equals(mdr.getProductId())) {
            score += 1;
        }
        if (null != txnAmount && null != mdr.getMinAmount() && txnAmount >= mdr.getMinAmount()) {
            score += 1;
        }
        return score;
    }

    public static String formatMobile(String mobile) {
        LOGGER.info("Formatting mobile: {}", mobile);
        if (StringUtils.isEmpty(mobile) || mobile.length() < 10) {
            throw new FreewayException("Invalid mobile number");
        } else {
            mobile = mobile.replaceAll("[^0-9]", "");
            mobile = mobile.substring(mobile.length() - 10);
        }
        return mobile;
    }

    public static String maskedEmail(String email) {
        try {
            String prefix = email.split("@")[0];
            String suffix = email.split("@")[1];
            return prefix.substring(0, 2) + stars(prefix.length() - 3) + prefix.substring(prefix.length() - 1) + "@" +
                    suffix;
        } catch (Exception e) {

        }
        return email;
    }

    public static String maskedMobile(String mobile) {
        try {
            return mobile.substring(0, 2) + stars(mobile.length() - 3) + mobile.substring(mobile.length() - 1);
        } catch (Exception e) {

        }
        return mobile;
    }

    private static String stars(int num) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < num; i++) {
            sb.append("*");
        }
        return sb.toString();
    }

    public static String getQueryString(Map<String, String> params) throws FreewayException {
        try {
            Map<String, String> sortedParams = new TreeMap<String, String>(params);
            StringBuilder queryStr = new StringBuilder("");
            for (String curkey : sortedParams.keySet()) {
                queryStr.append(curkey).append("=").append(sortedParams.get(curkey)).append("&");
            }
            queryStr.deleteCharAt(queryStr.length() - 1);
            return URLEncoder.encode(queryStr.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("UnsupportedEncodingException occurred while generating query string: ", e);
            throw new FreewayException(e.getMessage());
        }
    }

    public static String generateUniqueOrderId() {
        int num = RANDOM.nextInt(80000) + 2000;
        return String.valueOf(num % 100000);
    }

    public static Boolean isSubventGst(CardTypeEnum cardTypeEnum, TransactionResponse tr) {
        return CardTypeEnum.CREDIT.getCardType().equals(cardTypeEnum.getCardType()) && "true".equals(
                null != tr.getMerchantParams() ? StringUtils.hasText(tr.getMerchantParams().getSubventGst())
                        ? tr.getMerchantParams().getSubventGst() : Boolean.FALSE.toString() : Boolean.FALSE.toString());
    }

    public static String truncateMobile(String mobile) {
        return mobile.substring(0, 2) + "XXXXX" + mobile.substring(mobile.length() - 3);
    }

    public static String getEmiType(Float bankCharges, Float subvention) {
        if (Util.isNotNull(bankCharges)) {
            if (Util.isNotNull(subvention) && subvention != 0.0f) {
                float diff = Util.getFLoat(bankCharges) - Util.getFLoat(subvention);
                LOGGER.info("EMI Type: {}, {}, {}", Util.getFLoat(bankCharges), Util.getFLoat(subvention), diff);
                if (diff < 5.0f) {
                    return "No Cost";
                }
                return "Low Cost";
            }
        }
        return "Standard";
    }

    public static EligibilityResponse getEligibilityResponseByBank(List<EligibilityResponse> eligibilityResponses,
                                                                   BankEnum bankEnum) {
        return !CollectionUtils.isEmpty(eligibilityResponses) ? eligibilityResponses.stream()
                .filter(er -> bankEnum.getCode().equalsIgnoreCase(er.getBankCode()))
                .findFirst()
                .orElse(null) : null;
    }

    public static Float getPgAmount(float amount, float convFee, float discount, float gst, int tenure) {
        return amount + convFee + convFee * .18f - discount - gst -
                (tenure > 0 ? 0f : (amount * CardInterestEnum.BNPL_HDFC_15.getInterestEnum().getInterest()) / 1200);
    }

    public static Float getDiscountOnProcessingFee(float discount, float maxDiscount, float cashback,
                                                   float processingFee, float maxCashback) {
        float processingFeeIncGst = processingFee + (processingFee * .18f);
        float availableDiscount = Util.getFLoat((maxDiscount + maxCashback) - (discount + cashback));
        return availableDiscount > 0 ? Util.getFLoat(Math.min(processingFeeIncGst, availableDiscount)) : 0;
    }

    public static boolean isSubventProcessingFee(TransactionResponse tr) {
        return null != tr.getMerchantParams() && null != tr.getMerchantParams().getSubventProcessingFee() &&
                tr.getMerchantParams().getSubventProcessingFee();
    }

    public static boolean isFixDownpaymentFlow(Boolean downpaymentEnabled, String cardType, Float discount,
                                               Float cashback, String mccCode, String bankCode,
                                               Boolean isFixDownpaymentEnabled) {
        return null != downpaymentEnabled && downpaymentEnabled && BooleanUtils.isTrue(isFixDownpaymentEnabled) &&
                (discount > 0f || cashback > 0f) && !CardTypeEnum.CREDIT.name().equals(cardType);
//        return null != downpaymentEnabled && downpaymentEnabled && BooleanUtils.isTrue(isFixDownpaymentEnabled) &&
//                ((CardTypeEnum.NTB.getCardType().equalsIgnoreCase(cardType) && (discount > 0f || cashback > 0f)) ||
//                        (BankEnum.HDFC.getCode().equalsIgnoreCase(bankCode) &&
//                                (CardTypeEnum.DEBIT.name().equals(cardType) ||
//                                        CardTypeEnum.CARDLESS.name().equals(cardType)) && "5571".equals(mccCode)));
    }

    public static String generate22DigitRandomNumber() {
        String currentTimestamp = DateUtil.getDateTimeWithPattern("ddMMyyHHmmss", LocalDateTime.now());
        return currentTimestamp + generate10CharacterRandomNumber().toString();
    }

    public static String getEffectiveCardType(String bankCode, String cardType) {
        if (CardTypeEnum.CARDLESS.getCardType().equalsIgnoreCase(cardType) &&
                (BankEnum.HDFC.getCode().equalsIgnoreCase(bankCode) ||
                        BankEnum.KKBK.getCode().equalsIgnoreCase(bankCode))) {
            return CardTypeEnum.DEBIT.getCardType();
        }
//        else if (CardTypeEnum.NTB.getCardType().equalsIgnoreCase(cardType) && BankEnum.KKBK.getCode()
//        .equalsIgnoreCase(bankCode)) {
//            return CardTypeEnum.DEBIT.getCardType();
//        }
        return cardType;
    }

    public static String generateMerchantRefund18CharacterTxnId() {
        String currentTimestamp = DateUtil.getDateTimeWithPattern("ddMMyyHHmmss", LocalDateTime.now());
        return MR_REFUND_PREFIX + currentTimestamp + generate5CharacterRandomNumber();
    }

    public static String generatepaymentRefund18CharacterTxnId() {
        String currentTimestamp = DateUtil.getDateTimeWithPattern("ddMMyyHHmmss", LocalDateTime.now());
        return SR_REFUND_PREFIX + currentTimestamp + generate5CharacterRandomNumber();
    }

    public static String generateDpRefundTxnId() {
        return DP_REFUND_PREFIX + DateUtil.getDateTimeWithPattern("ddMMyyHHmmss", LocalDateTime.now()) +
                generate5CharacterRandomNumber() + generate5CharacterRandomNumber();
    }

    public static boolean useTokenization(String cardType, String bankCode, Params merchantParams) {
        return isUseTokenization(cardType, bankCode, merchantParams);
    }

    public static String convertFloatToTwoDecimal(Float value) {
        return String.format(Locale.US, "%.2f", value);
    }

    private static boolean isUseTokenization(String cardType, String bankCode, Params merchantParams) {
        if ("DEBIT".equals(cardType)) {
            switch (bankCode) {
                case "UTIB":
                    return isTokenizationEnabledForDcAxisBank(merchantParams);
                case "ICIC":
                    return isTokenizationEnabledForDcIciciBank(merchantParams);
                default:
                    return false;
            }
        }
        return false;
    }

    public static boolean isViaVault(String cardType, String bankCode, CallVaultDto callVaultDto) {
        if (CardTypeEnum.CREDIT.getCardType().equals(cardType)) {
            switch (bankCode) {
                case "AUFB":
                    return Util.isNull(callVaultDto.getCallVaultForAufbCreditCard()) ||
                            callVaultDto.getCallVaultForAufbCreditCard();
                case "AMEX":
                    return Util.isNull(callVaultDto.getCallVaultForAmexCreditCard()) ||
                            callVaultDto.getCallVaultForAmexCreditCard();
                case "BARB":
                    return Util.isNull(callVaultDto.getCallVaultForBobCreditCard()) ||
                            callVaultDto.getCallVaultForBobCreditCard();
                case "CITI":
                    return Util.isNull(callVaultDto.getCallVaultForCitiCreditCard()) ||
                            callVaultDto.getCallVaultForCitiCreditCard();
                case "HDFC":
                    return Util.isNull(callVaultDto.getCallVaultForHdfcCreditCard()) ||
                            callVaultDto.getCallVaultForHdfcCreditCard();
                case "HSBC":
                    return Util.isNull(callVaultDto.getCallVaultForHsbcCreditCard()) ||
                            callVaultDto.getCallVaultForHsbcCreditCard();
                case "ICIC":
                    return Util.isNull(callVaultDto.getCallVaultForIciciCreditCard()) ||
                            callVaultDto.getCallVaultForIciciCreditCard();
                case "INDB":
                    return Util.isNull(callVaultDto.getCallVaultForIndusIndCreditCard()) ||
                            callVaultDto.getCallVaultForIndusIndCreditCard();
                case "KKBK":
                    return Util.isNull(callVaultDto.getCallVaultForKotakCreditCard()) ||
                            callVaultDto.getCallVaultForKotakCreditCard();
                case "RATN":
                    return Util.isNull(callVaultDto.getCallVaultForRblCreditCard()) ||
                            callVaultDto.getCallVaultForRblCreditCard();
                case "SBIN":
                    return Util.isNull(callVaultDto.getCallVaultForSbiCreditCard()) ||
                            callVaultDto.getCallVaultForSbiCreditCard();
                case "SCBL":
                    return Util.isNull(callVaultDto.getCallVaultForScbCreditCard()) ||
                            callVaultDto.getCallVaultForScbCreditCard();
                case "UTIB":
                    return Util.isNull(callVaultDto.getCallVaultForAxisCreditCard()) ||
                            callVaultDto.getCallVaultForAxisCreditCard();
                case "YESB":
                    return Util.isNull(callVaultDto.getCallVaultForYesCreditCard()) ||
                            callVaultDto.getCallVaultForYesCreditCard();
                case "ONECARD":
                    return Util.isNull(callVaultDto.getCallVaultForOneCardCreditCard()) ||
                            callVaultDto.getCallVaultForOneCardCreditCard();
            }
        } else if (CardTypeEnum.DEBIT.getCardType().equals(cardType)) {
            switch (bankCode) {
                case "ICIC":
                    return Util.isNull(callVaultDto.getCallVaultForIciciDebitCard()) ||
                            callVaultDto.getCallVaultForIciciDebitCard();
                case "UTIB":
                    return Util.isNull(callVaultDto.getCallVaultForAxisDebitCard()) ||
                            callVaultDto.getCallVaultForAxisDebitCard();
            }
        }
        return false;
    }

    private static boolean isTokenizationEnabledForDcIciciBank(Params merchantParams) {
        // Making flag return reverse due to wrong frontend handling
        if (Util.isNotNull(merchantParams) && Util.isNotNull(merchantParams.getCallVaultForIciciDebitCard())) {
            return BooleanUtils.isFalse(merchantParams.getCallVaultForIciciDebitCard());
        }
        return true;
    }

    private static boolean isTokenizationEnabledForDcAxisBank(Params merchantParams) {
        return Util.isNotNull(merchantParams) && Util.isNotNull(merchantParams.getUseTokenizationForAxisDebitCard()) &&
                merchantParams.getUseTokenizationForAxisDebitCard();
    }

    public static String populateCardData(String cardNumber, String expMonth, String expYear, String cvv) {
        return cardNumber + PIPE + expMonth + PIPE + expYear + PIPE + cvv;
    }

    public static boolean isTransactionFailed(String status) {
        return StringUtils.hasText(status) && TransactionStatus.failed.name().equals(status);
    }

    public static String createMockMandateHtml(String formActionUrl) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<!DOCTYPE html><html><head>")
                .append("<title>payment Mock Mandate</title>")
                .append("</head>")
                .append("<body>")
                .append("<div style=\"display: flex; flex-flow: column; justify-content: center;align-items: center;" +
                        "\">")
                .append("<h1> Kotak Mock Mandate </h1>")
                .append("<form name=\"submitForm\" action=")
                .append("\"")
                .append(formActionUrl)
                .append("\"")
                .append(" method=")
                .append("\"")
                .append("GET")
                .append("\"")
                .append(" >");
        stringBuilder.append("<input type=\"submit\" name=\"status\" value=\"success\" > </input>");
        stringBuilder.append("&nbsp;&nbsp;&nbsp;");
        stringBuilder.append("<input type=\"submit\" name=\"status\" value=\"failed\" > </input>");
        stringBuilder.append("</form>").append("</div>").append("</body>").append("</html>");
        return stringBuilder.toString();
    }

    public static float getGstOnProcessingFeeByState(Float processingFee, ProviderParams providerParams) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_UP);
        String state = null != providerParams ? providerParams.getState() : null;
        String bankCode =
                null != providerParams && null != providerParams.getBankEnum() ? providerParams.getBankEnum().getCode()
                        : null;
        float gstOnProcessingFee = 0f;
        if (null != processingFee && processingFee != 0f) {
            if (NtbServiceConstants.MAHARASHTRA.equalsIgnoreCase(state) &&
                    BankEnum.IIFL.getCode().equalsIgnoreCase(bankCode)) {
                float cgstOnProcessingFee = (processingFee * 0.09f * 100f) / 100f;
                float sgstOnProcessingFee = (processingFee * 0.09f * 100f) / 100f;
                cgstOnProcessingFee = Float.parseFloat(df.format(cgstOnProcessingFee));
                sgstOnProcessingFee = Float.parseFloat(df.format(sgstOnProcessingFee));
                gstOnProcessingFee = cgstOnProcessingFee + sgstOnProcessingFee;
            } else {
                gstOnProcessingFee = (processingFee * 0.18f * 100f) / 100f;
                gstOnProcessingFee = Float.parseFloat(df.format(gstOnProcessingFee));
            }
        }
        return Util.getFLoat(gstOnProcessingFee);
    }

    public static PaymentProviderEnum getProviderEnumByCardTypeAndBankCode(String cardType, String bankCode) {
        if (StringUtils.hasText(cardType)) {
            switch (cardType) {
                case "DEBIT":
                    switch (bankCode) {
                        case "HDFC":
                            return PaymentProviderEnum.hdfc;
                        case "KKBK":
                            return PaymentProviderEnum.kotak;
                        case "IDFC":
                            return PaymentProviderEnum.idfc;
                        case "ICIC":
                            return PaymentProviderEnum.icici;
                        case "UTIB":
                            return PaymentProviderEnum.axis;
                        default:
                            return PaymentProviderEnum.none;
                    }
                case "CARDLESS":
                    switch (bankCode) {
                        case "CL_ICICI":
                            return PaymentProviderEnum.icicicardless;
                        case "CL_ICICI_KYC":
                            return PaymentProviderEnum.icicikyccardless;
                        case "HDFC":
                            return PaymentProviderEnum.hdfc;
                        case "KKBK":
                            return PaymentProviderEnum.kotak;
                        default:
                            return PaymentProviderEnum.none;
                    }
                case "BNPL":
                    switch (bankCode) {
                        case "BNPL_HDFC":
                            return PaymentProviderEnum.flexipay;
                        default:
                            return PaymentProviderEnum.none;
                    }
                case "NTB":
                    switch (bankCode) {
                        case "IIFL":
                            return PaymentProviderEnum.iifl;
                        case "KKBK":
                            return PaymentProviderEnum.kotakntb;
                        case "HDFC_NTB":
                            return PaymentProviderEnum.hdfcntb;
                        default:
                            return PaymentProviderEnum.none;
                    }
                default:
                    return PaymentProviderEnum.none;
            }
        }
        return getProviderEnum(bankCode);
    }

    public static PaymentProviderEnum getProviderEnum(String bankCode) {
        switch (bankCode) {
            case "UTIB":
                return PaymentProviderEnum.axis;
            case "HDFC":
            case "CL_HDFC":
                return PaymentProviderEnum.hdfc;
            case "ICIC":
                return PaymentProviderEnum.icici;
            case "KKBK":
                return PaymentProviderEnum.kotak;
            case "IDFC":
                return PaymentProviderEnum.idfc;
            case "CL_ICICI":
                return PaymentProviderEnum.icicicardless;
            case "CL_ICICI_KYC":
                return PaymentProviderEnum.icicikyccardless;
            case "BNPL_HDFC":
                return PaymentProviderEnum.flexipay;
            case "IIFL":
                return PaymentProviderEnum.iifl;
            default:
                return PaymentProviderEnum.none;
        }
    }

    public static boolean isTrue(Boolean bool) {
        return bool != null && bool;
    }

    public static String[] getSplitDataByPipe(String pipeSeparatedData) {
        return pipeSeparatedData.split("\\|");
    }

    public static boolean isUnderValidTimePeriod(Instant currentDate, Instant validFrom, Instant validTo) {
        if (null == validFrom && null == validTo) {
            return true;
        } else if (null != validFrom && null != validTo) {
            return currentDate.isAfter(validFrom) && currentDate.isBefore(validTo);
        } else if (null != validFrom) {
            return currentDate.isAfter(validFrom);
        } else {
            return currentDate.isBefore(validTo);
        }
    }

    public static List<InterestPerTenureDto> getInterestPerTenureDtoList(
            List<InterestPerTenureDto> interestPerTenureDtos, CardTypeEnum cardType, BankEnum bankCode) {
        Instant currentDate = Instant.now();
        return interestPerTenureDtos.stream()
                .filter(interestPerTenureDto -> !BooleanUtils.isFalse(interestPerTenureDto.getIsActive()))
                .filter(interestPerTenureDto -> Util.isUnderValidTimePeriod(currentDate,
                        interestPerTenureDto.getValidFrom(), interestPerTenureDto.getValidTo()))
                .filter(interestPerTenureDto ->
                        cardType.getCardType().equalsIgnoreCase(interestPerTenureDto.getCardType().getCardType()) &&
                                bankCode.getCode().equalsIgnoreCase(interestPerTenureDto.getBankEnum().getCode()))
                .collect(Collectors.toList());
    }

    public static List<InterestPerTenureDto> getInterestPerTenureDtoByBankAndCardType(BankInterestDto bankInterestDto,
                                                                                      CardTypeEnum cardType,
                                                                                      BankEnum bankCode) {
        Instant currentDate = Instant.now();
        if (BooleanUtils.isFalse(bankInterestDto.getIsActive()) ||
                CollectionUtils.isEmpty(bankInterestDto.getInterestPerTenureDtos()) ||
                !Util.isUnderValidTimePeriod(currentDate, bankInterestDto.getValidFrom(),
                        bankInterestDto.getValidTo())) {
            return null;
        }
        return getInterestPerTenureDtoList(bankInterestDto.getInterestPerTenureDtos(), cardType, bankCode);
    }

    public static InterestPerTenureDto getInterestPerTenureDtoByBankAndCardTypeAndTenure(
            BankInterestDto bankInterestDto, CardTypeEnum cardType, BankEnum bankCode, Integer tenure) {
        Instant currentDate = Instant.now();
        if (BooleanUtils.isFalse(bankInterestDto.getIsActive()) ||
                CollectionUtils.isEmpty(bankInterestDto.getInterestPerTenureDtos()) ||
                !Util.isUnderValidTimePeriod(currentDate, bankInterestDto.getValidFrom(),
                        bankInterestDto.getValidTo())) {
            return null;
        }
        List<InterestPerTenureDto> interestPerTenureDtos = bankInterestDto.getInterestPerTenureDtos()
                .stream()
                .filter(interestPerTenureDto -> !BooleanUtils.isFalse(interestPerTenureDto.getIsActive()))
                .filter(interestPerTenureDto -> Util.isUnderValidTimePeriod(currentDate,
                        interestPerTenureDto.getValidFrom(), interestPerTenureDto.getValidTo()))
                .filter(interestPerTenureDto -> cardType.equals(interestPerTenureDto.getCardType()) &&
                        bankCode.equals(interestPerTenureDto.getBankEnum()) &&
                        tenure.equals(interestPerTenureDto.getTenure()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(interestPerTenureDtos)) {
            return null;
        }
        if (interestPerTenureDtos.size() > 1) {
            //LOGGER.error(TransactionCode.FAILED_183.getDashboardStatusMsg() + " merchant display id: " +
            // bankInterestCardDto.getMerchantDisplayId() + " brand display id: " + bankInterestCardDto
            // .getBrandDisplayId())
            throw new FreewayCustomException(TransactionCode.FAILED_184);
        }
        return interestPerTenureDtos.get(0);
    }

    public static boolean isCashbackConfiguredAsInstantDiscount(TransactionResponse tr) {
        return null != tr.getIsBrandSubventionModel() && tr.getIsBrandSubventionModel() &&
                null != tr.getAdditionInfo() && null != tr.getAdditionInfo().getBrandInfo() &&
                StringUtils.hasText(tr.getAdditionInfo().getBrandInfo().getSubventionType()) &&
                SubventionType.DISCOUNT.getDisplayName()
                        .equalsIgnoreCase(tr.getAdditionInfo().getBrandInfo().getSubventionType());
    }

    public static Input getInput(TransactionResponse tr, String effectiveCardType, BankEnum bankEnum, Integer emiTenure,
                                 Integer effectiveTenure) {
        return Input.builder()
                .cardType(effectiveCardType)
                .bankCode(bankEnum.getCode())
                .tenure(emiTenure)
                .effectiveTenure(effectiveTenure)
                .offers(tr.getPgOffers())
                .brandSubventions(tr.getBrandSubventions())
                .convFeeRates(tr.getConvFeeRates())
                .productId(tr.getProductId())
                .brandProductId(null == tr.getAdditionInfo() || null == tr.getAdditionInfo().getBrandInfo() ? null
                        : tr.getAdditionInfo().getBrandInfo().getBrandProductId())
                .txnAmount(tr.getAmount())
                .merchantId(tr.getMerchantId())
                .isSubvented(tr.getIsSubvented())
                .build();
    }

    public static Input getInput(TransactionResponse tr, String effectiveCardType, BankEnum bankEnum, Integer emiTenure,
                                 Integer effectiveTenure, String merchantState) {
        Input input = getInput(tr, effectiveCardType, bankEnum, emiTenure, effectiveTenure);
        input.setMerchantState(merchantState);
        return input;
    }

    public static Input getInputV3(MerchantResponse merchantResponse, String effectiveCardType, BankEnum bankEnum, Integer emiTenure,
                                   Integer effectiveTenure, Float amount, String brandProductId) {
        return Input.builder()
                .cardType(effectiveCardType)
                .bankCode(bankEnum.getCode())
                .tenure(emiTenure)
                .effectiveTenure(effectiveTenure)
                .offers(merchantResponse.getOffers())
                .brandSubventions(merchantResponse.getBrandSubventions())
                .convFeeRates(merchantResponse.getConvFeeRates())
                .productId(null)    // productId and brandProductId are not related
                .brandProductId(null == merchantResponse.getBrandInfo() ? null
                        : merchantResponse.getBrandInfo().getBrandProductId())
                .txnAmount(amount)
                .merchantId(merchantResponse.getMerchantId())
                .isSubvented(null)
                .build();
    }

    public static Input getInputV3(MerchantResponse merchantResponse, String effectiveCardType, BankEnum bankEnum, Integer emiTenure,
                                   Integer effectiveTenure, String merchantState, Float amount, String brandProductId) {
        Input input = getInputV3(merchantResponse, effectiveCardType, bankEnum, emiTenure, effectiveTenure, amount, brandProductId);
        input.setMerchantState(merchantState);
        return input;
    }

//    public static boolean isGSTOnEmiSubvented(TransactionResponse tr, CardTypeEnum cardTypeEnum) {
//        return CardTypeEnum.CREDIT.getCardType().equals(cardTypeEnum.getCardType()) &&
//                "true".equals(null != tr.getMerchantParams() ? StringUtils.hasText(tr.getMerchantParams()
//                .getSubventGst())
//                        ? tr.getMerchantParams().getSubventGst()
//                        : Boolean.FALSE.toString()
//                        : Boolean.FALSE.toString());
//    }

    public static boolean isNotCostEMI(Float interest, Float subvention) {
        if (Util.isNotNull(subvention) && Util.isNotNull(interest)) {
            return interest - subvention <= NO_COST_EMI_DIFF;
        }
        return false;
    }

    public static boolean isLowCostEMI(Float interest, Float subvention) {
        if (Util.isNotNull(subvention) && Util.isNotNull(interest) && subvention != 0f) {
            return interest - subvention > NO_COST_EMI_DIFF;
        }
        return false;
    }

    public static boolean isStandardCostEMI(Float interest, Float subvention) {
        return Util.isNotNull(subvention) && subvention == 0f;
    }

    public static EMIOfferType getTypeOfEMIOffer(PriceResponse priceResponse) {
        Float subvention = Util.isNotNull(priceResponse.getDiscount()) && priceResponse.getDiscount() > 0f
                ? priceResponse.getDiscount() : priceResponse.getCashback();
        Float interest = priceResponse.getBankCharges();
        if (Util.isNotCostEMI(interest, subvention)) {
            return EMIOfferType.NOCOST;
        } else if (Util.isLowCostEMI(interest, subvention)) {
            return EMIOfferType.LOWCOST;
        } else return EMIOfferType.STANDARD;
    }

    public static Float getBankProcessingFeeIncGst(Float pgAmount, InterestPerTenureDto interestPerTenureDto) {
        Double processingFeeIncGst = null;
        Double loanAmount = pgAmount.doubleValue();
        if (Util.isNotNull(interestPerTenureDto)) {
            if (Util.isNotNull(interestPerTenureDto.getBankPfFlatAmount()) &&
                    interestPerTenureDto.getBankPfFlatAmount() > 0d) {
                processingFeeIncGst = interestPerTenureDto.getBankPfFlatAmount();
            }
            if (Util.isNotNull(interestPerTenureDto.getBankPfInPercentage()) &&
                    interestPerTenureDto.getBankPfInPercentage() > 0d) {
                processingFeeIncGst = (interestPerTenureDto.getBankPfInPercentage() / 100) * loanAmount;
                if (Util.isNotNull(interestPerTenureDto.getBankPfMaxAmount()) &&
                        interestPerTenureDto.getBankPfMaxAmount() > 0d) {
                    processingFeeIncGst = Double.min(processingFeeIncGst, interestPerTenureDto.getBankPfMaxAmount());
                }
            }
            if (null != processingFeeIncGst) {
                processingFeeIncGst = processingFeeIncGst + processingFeeIncGst * .18d;
            }
        }
        return null != processingFeeIncGst ? processingFeeIncGst.floatValue() : null;
    }

    public static Float getBankProcessingFee(Float pgAmount, InterestPerTenureDto interestPerTenureDto) {
        Double processingFee = null;
        Double loanAmount = pgAmount.doubleValue();
        if (Util.isNotNull(interestPerTenureDto)) {
            if (Util.isNotNull(interestPerTenureDto.getBankPfFlatAmount()) &&
                    interestPerTenureDto.getBankPfFlatAmount() > 0d) {
                processingFee = interestPerTenureDto.getBankPfFlatAmount();
            }
            if (Util.isNotNull(interestPerTenureDto.getBankPfInPercentage()) &&
                    interestPerTenureDto.getBankPfInPercentage() > 0d) {
                processingFee = (interestPerTenureDto.getBankPfInPercentage() / 100) * loanAmount;
                if (Util.isNotNull(interestPerTenureDto.getBankPfMaxAmount()) &&
                        interestPerTenureDto.getBankPfMaxAmount() > 0d) {
                    processingFee = Double.min(processingFee, interestPerTenureDto.getBankPfMaxAmount());
                }
            }
        }
        return null != processingFee ? processingFee.floatValue() : null;
    }

    public static Float getReverseInterestAmount(int tenure, Float amount, Float irrpa) {
        double irr = irrpa / 12.0f / 100.0f; //= 0.01083333333
        double k = (float) Math.pow(1 + irr, tenure); //1.01083333333 ^ 3 = 1.03285335474
        double price = (amount / irr / tenure) * (k - 1) / k;// = 29361.5482459
        return (float) (amount - price);
    }

    public static List<String> getCombinedBrandIds(String brandId, List<String> brandIds) {
        List<String> combinedBrandIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(brandIds)) {
            combinedBrandIds.addAll(brandIds);
        }
        if (brandId != null && !combinedBrandIds.contains(brandId)) {
            combinedBrandIds.add(brandId);
        }
        return combinedBrandIds;
    }

    public static String getRandomString(int n) {
        String AlphaNumericString = "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index = RANDOM.nextInt(AlphaNumericString.length());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }

    public static String getMd5(String input) {
        try {

            MessageDigest md = MessageDigest.getInstance("MD5");

            byte[] messageDigest = md.digest(input.getBytes());

            BigInteger no = new BigInteger(1, messageDigest);

            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, List<String>> userTypes() {
        Map<String, List<String>> userTypes = new HashMap<>();
        userTypes.put("ADMIN", Arrays.asList("SUPER_ADMIN", "ADMIN", "OPS", "FINANCE", "SALES", "SALES_ONLINE"));
        userTypes.put("MERCHANT", Arrays.asList("MERCHANT", "MERCHANT_OPS", "MERCHANT_FINANCE", "MERCHANT_SALES"));
        userTypes.put("BANKER",
                Arrays.asList("BANKER", "USFB_FCU_AGENCY_EXECUTIVE", "USFB_FCU_AGENCY_SUPERVISOR", "USFB_AUDIT",
                        "USFB_CPV_AGENCY_EXECUTIVE", "USFB_CPV_AGENCY_SUPERVISOR", "USFB_CREDIT_OFFICER_L1",
                        "USFB_CREDIT_OFFICER_L2", "USFB_CREDIT_OFFICER_L3", "USFB_OPS_AGENCY_EXECUTIVE",
                        "USFB_OPS_MANAGER", "USFB_OPS_IN_MANAGER", "USFB_SALES_AGENCY_EXECUTIVE", "USFB_SALES_MANAGER",
                        "USFB_SALES_IN_MANAGER"));
        userTypes.put("STORE_USER", Arrays.asList("STORE_USER"));
        return userTypes;
    }

    public static String getUserTypeByRole(String role) {
        Map<String, List<String>> userTypes = userTypes();
        for (String key : userTypes.keySet()) {
            if (userTypes.get(key).contains(role)) {
                return key;
            }
        }
        return null;
    }

    public static boolean isSubvented(Float discount, Float cashback) {
        return ((Objects.nonNull(discount) && discount > 0f) || (Objects.nonNull(cashback) && cashback > 0f));
    }

    public static String getPartnerCodeByTransactionResponse(TransactionResponse tr) {
        return Objects.nonNull(tr.getPartner()) ? tr.getPartner() : PartnerCodeEnum.payment.getPartnerCode();
    }

    public static List<Integer> findAllTenuresInt(String cardType, String bankCode, Map<String, ProviderMasterConfigInfo> providerMasterConfigInfoMap) {
        List<Integer> tenures = new ArrayList<>();
        if (providerMasterConfigInfoMap.containsKey(cardType + "_" + bankCode)) {
            tenures = providerMasterConfigInfoMap.get(cardType + "_" + bankCode).getTenureConfig().keySet().stream().map(Integer::valueOf).collect(Collectors.toList());
        }
        return tenures;
    }

    public static Float getIRR(String cardType, String bankCode, Integer tenure, Map<String, ProviderMasterConfigInfo> providerMasterConfigInfoMap) {
        Float irr = 0.0f;
        if (providerMasterConfigInfoMap.containsKey(cardType + "_" + bankCode)) {
            Map<String, ProviderSchemeDetail> providerSchemeDetailMap = providerMasterConfigInfoMap.get(cardType + "_" + bankCode).getTenureConfig();
            if (providerSchemeDetailMap.containsKey(String.valueOf(tenure))) {
                return providerSchemeDetailMap.get(String.valueOf(tenure)).getBankIrr();
            }
        }
        return irr;
    }

    public static Boolean findEmiOption(String cardType, String bankCode, Integer tenure, Map<String, ProviderMasterConfigInfo> providerMasterConfigInfoMap) {
        String effectiveCardType = Util.getEffectiveCardType(bankCode, cardType);
//        String effectiveCardType = "CARDLESS".equalsIgnoreCase(cardType) &&
//                ("HDFC".equalsIgnoreCase(bankCode) || "KKBK".equalsIgnoreCase(bankCode)) ? "DEBIT" : cardType;
        if (providerMasterConfigInfoMap.containsKey(effectiveCardType + "_" + bankCode)) {
            if (providerMasterConfigInfoMap.get(effectiveCardType + "_" + bankCode).getTenureConfig().containsKey(String.valueOf(tenure))) {
                return Boolean.TRUE;
            }
        }
        return null;
    }

    public static String parseThymeleafTemplate(String templateName, Context context) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        return templateEngine.process(templateName, context);
    }

    public static void generatePdfFromHtml(String html, String path) throws IOException {
        OutputStream outputStream = Files.newOutputStream(Paths.get(path));
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();
    }

    public static Float calculateFixedDownPayment(Float price, Integer tenure, Integer effectiveTenure) {
        if (price == null || tenure == null || effectiveTenure == null || tenure == 0) {
            return 0f;
        }
        int advanceEmiTenure = tenure - effectiveTenure;
        return (price / tenure) * advanceEmiTenure;
    }

    public static String generateRandomDisplayId() {
        SecureRandom random = new SecureRandom();

        int firstDigit = random.nextInt(9) + 1; // Generate a random first digit between 1 and 9
        StringBuilder sb = new StringBuilder();
        sb.append(firstDigit);

        for (int i = 0; i < 6; i++) {
            int digit = random.nextInt(10); // Generate a random digit between 0 and 9
            sb.append(digit);
        }

        return sb.toString();
    }

    public static String getNextOnboardingStage(PartnerInfo partner, String currentStage) {
        List<String> stages = partner.getStages();
        if (!CollectionUtils.isEmpty(stages) && stages.contains(currentStage)) {
            int index = stages.indexOf(currentStage);
            if (index < stages.size() - 1) {
                return stages.get(index + 1);
            } else {
                return "";
            }
        } else {
            LOGGER.info("No Stage found for the onboarding: {}", currentStage);
            return "";
//            throw new FreewayException("No Stage found for the onboarding");
        }
    }

    public static String getLastOnboardingStage(PartnerInfo partner) {
        if (Objects.nonNull(partner) && !CollectionUtils.isEmpty(partner.getStages())) {
            return partner.getStages().get(partner.getStages().size() - 1);
        } else {
            return "";
        }
    }
}
