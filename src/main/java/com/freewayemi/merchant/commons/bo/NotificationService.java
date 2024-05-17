package com.freewayemi.merchant.commons.bo;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.PublishRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freewayemi.merchant.commons.dto.*;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.type.BankEnum;
import com.freewayemi.merchant.commons.type.CardTypeEnum;
import com.freewayemi.merchant.commons.utils.paymentConstants;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

    private final AmazonSNS amazonSNS;
    private final String awsSnsArn;
    private final Boolean isProduction;
    private final AuditService auditService;

    @Autowired
    public NotificationService(AmazonSNS amazonSNS, @Value("${aws.sns.arn}") String awsSnsArn,
                               @Value("${payment.deployment.env}") String env, AuditService auditService) {
        this.amazonSNS = amazonSNS;
        this.awsSnsArn = awsSnsArn;
        this.isProduction = paymentConstants.PRODENV.equals(env);
        this.auditService = auditService;
    }

    @Async
    public void send(NotificationRequest notificationRequest) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String message = mapper.writeValueAsString(notificationRequest);
            send(message);
        } catch (JsonProcessingException e) {
            LOGGER.error("JsonProcessingException occurred while sending notification for transactionId: ", e);
        }
    }

    @Async
    public void send(SendNotificationRequest notificationRequest) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String message = mapper.writeValueAsString(notificationRequest);
            send(message);
        } catch (JsonProcessingException e) {
            LOGGER.error("JsonProcessingException occurred while sending notification for transactionId: ", e);
        }
    }

    @Async
    public void send(String message) {
        LOGGER.info("Sending NotificationRequest");
        amazonSNS.publish(new PublishRequest().withTopicArn(awsSnsArn).withMessage(message));
    }


    public void sendOTP(String otp, String mobile, Boolean retry, String email, Boolean isEmail) {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("otp", otp);
        if (isEmail) {
            map.put("email", email);
        }

        List<String> channels = null != retry && retry ? isEmail ? Arrays.asList("sms", "whatsapp", "email")
                : Arrays.asList("sms", "whatsapp")
                : isEmail ? Arrays.asList("sms", "email") : Collections.singletonList("sms");
        NotificationRequest notificationRequest = new NotificationRequest(channels, "OTP", map);
        send(notificationRequest);
    }

    @Async
    public void sendSmsOnboardLink(String mobile, String link) {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("link", link);

        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("sms"), "MER_ONBOARD_LINK", map);

        send(notificationRequest);
    }

    @Async
    public void reachOut(String name, String address, String mobile, String email, String pageUrl,
                         String businessName) {
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("address", address);
        map.put("mobile", mobile);
        map.put("email", email);
        map.put("pageUrl", pageUrl);
        map.put("businessName", businessName);
        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("email"), "MER_REACH_OUT", map);
        send(notificationRequest);
    }


    @Async
    public void sendAnalyticsFeed(Map<String, String> map, Map<String, String> eventProps,
                                  Map<String, String> userProps) {
        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("amplitude"), "AMPLITUDE", map);
        notificationRequest.setEventProps(eventProps);
        notificationRequest.setUserProps(userProps);
        send(notificationRequest);
    }


    public void sendAutoOTP(String otp, String mobile, Boolean retry, String source) {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("otp", otp);
        map.put("userType", source);

        List<String> channels =
                null != retry && retry ? Arrays.asList("sms", "whatsapp") : Collections.singletonList("sms");
        NotificationRequest notificationRequest = new NotificationRequest(channels, "AUTOOTP", map);
        send(notificationRequest);
    }

    @Async
    public void sendSignUpMerchant(String mobile) {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", mobile);

        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("sms"), "MER_SIGN_UP", map);
        send(notificationRequest);
    }

    @Async
    public void sendMerchantActivatedNotification(String email, String mobile, String deviceToken, String firstName,
                                                  Address address) {
        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("mobile", mobile);
        map.put("device", deviceToken);
        map.put("firstName", firstName);
        map.put("address", address.toString());

        NotificationRequest notificationRequest =
                new NotificationRequest(Arrays.asList("push", "sms", "email"), "MER_APPROVED", map);
        send(notificationRequest);
    }

    @Async
    public void sendStoreUserActivatedNotification(String mobile, String name) {
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("mobile", mobile);
        map.put("url", "https://play.google.com/store/apps/details?id=com.payment.merchant");

        NotificationRequest notificationRequest =
                new NotificationRequest(Arrays.asList("sms", "whatsapp"), "STORE_USER_ACTIVATION", map);
        send(notificationRequest);
    }

    @Async
    public void sendMerchantRejectedNotification(String email, String mobile, String deviceToken, String firstName) {
        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("mobile", mobile);
        map.put("device", deviceToken);
        map.put("firstName", firstName);

        NotificationRequest notificationRequest =
                new NotificationRequest(Arrays.asList("push", "sms", "email"), "MER_REJECTED", map);
        send(notificationRequest);
    }

    @Async
    public void updatedOffersNotification(String email, String mobile, String deviceToken, String shopName,
                                          String rate) {
        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("mobile", mobile);
        map.put("device", deviceToken);
        map.put("shopname", shopName);
        map.put("timestamp", Instant.now().toString());
        map.put("rate", rate);

        NotificationRequest notificationRequest =
                new NotificationRequest(Arrays.asList("push", "sms", "email"), "OFFER_SETUP", map);
        send(notificationRequest);
    }

    @Async
    public void sendMerchantDocumentSubmitSuccess(String firstName, String email, String mobile, String deviceToken) {
        Map<String, String> map = new HashMap<>();
        map.put("firstName", firstName);
        map.put("email", email);
        map.put("mobile", mobile);
        map.put("device", deviceToken);

        NotificationRequest notificationRequest =
                new NotificationRequest(Arrays.asList("push", "sms", "email", "whatsapp"), "DOC_SUBMIT", map);
        send(notificationRequest);
    }

    @Async
    public void sendMerchantForTransactionSuccess(String type, String email, String mobile, String deviceToken,
                                                  String amount, String txnId, String consumerMobile) {
        if (paymentConstants.OFFLINE.equals(type)) {
            Map<String, String> map = new HashMap<>();
            map.put("email", email);
            map.put("mobile", mobile);
            map.put("device", deviceToken);
            map.put("amount", amount);
            map.put("txn", txnId);
            map.put("consumer", consumerMobile);
            NotificationRequest notificationRequest =
                    new NotificationRequest(Arrays.asList("push", "sms", "email"), "MER_TXN_SUCCESS", map);
            send(notificationRequest);
        }
    }

    @Async
    public void sendMerchantForTransactionFailed(String email, String mobile, String deviceToken, String status,
                                                 String amount, String txnId, String consumerMobile) {
        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("mobile", mobile);
        map.put("device", deviceToken);
        map.put("status", status);
        map.put("amount", amount);
        map.put("txn", txnId);
        map.put("consumer", consumerMobile);

        NotificationRequest notificationRequest =
                new NotificationRequest(Arrays.asList("push", "sms", "email"), "MER_TXN_FAILED", map);
        send(notificationRequest);
    }

    @Async
    public void sendTxnInitiatedToConsumer(String mobile, String amount, String shopName, String url) {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("amount", amount);
        map.put("shopname", shopName);
        map.put("url", url);

        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("sms"), "TXN_CREATED", map);
        send(notificationRequest);
    }

    @Async
    public void sendConsumerForTransactionSuccess(String email, String mobile, String deviceToken, String amount,
                                                  String chargeAmount, String tenure, String shopName) {
        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("mobile", mobile);
        map.put("device", deviceToken);
        map.put("amount", amount);
        map.put("chargeAmount", chargeAmount);
        map.put("tenure", tenure);
        map.put("shopname", shopName);

        NotificationRequest notificationRequest =
                new NotificationRequest(Arrays.asList("push", "sms", "email"), "TXN_SUCCESS", map);
        send(notificationRequest);
    }

    @Async
    public void sendConsumerForPgTransactionSuccess(String consumerName, String mobile, String email, String logo,
                                                    String shopName, String orderId, String txnId, String tenure,
                                                    String emi, Float amount, Float discount, Float bankCharges,
                                                    Float invoice, String bank, String cardType, String bankCode,
                                                    Boolean isBrandSubventionModel, Float additionalCashback,
                                                    BrandInfo brandInfo, Float emiCashback, String bankGstDetails,
                                                    Float bankInterestRate, Instant txnSuccessDate, Float totalCashback,
                                                    Float downPayment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyy").withZone(ZoneId.of("Asia/Kolkata"));
        String successDate = formatter.format(txnSuccessDate);
        Map<String, String> map = new HashMap<>();
        Float interest = Util.getFLoat(bankCharges);
        map.put("consumerName", StringUtils.hasText(consumerName) ? consumerName : "Customer");
        map.put("mobile", mobile);
        map.put("email", email);
        map.put("logo", logo);
        map.put("shopname", shopName);
        map.put("orderId", orderId);
        map.put("txnId", txnId);
        map.put("tenure", tenure + " months");
        map.put("emi", emi);
        map.put("amount", Util.getFLoat(amount).toString());
        map.put("amountSaved", Util.getFLoat(totalCashback).toString());
        map.put("finalAmount", Util.getFLoat(invoice).toString());
        map.put("interest", interest.toString());
        map.put("payable", Util.getFLoat(invoice + bankCharges).toString());
        map.put("bank", bank);
        map.put("cardType", cardType);
        map.put("gst", Util.getFLoat(.18f * interest).toString());
        map.put("HDFC_CC_NOTE", getHdfcCCNote(bankCode, cardType, emi));
        map.put("emiType", Util.getEmiType(bankCharges, discount + emiCashback));
        map.put("bankInterestRate", String.format("%.2f", bankInterestRate));
        map.put("txnSuccessDate", successDate);
        map.put("additionalCashback", String.format("%.2f", additionalCashback));
        map.put("emiCashback", String.format("%.2f", emiCashback));
        map.put("brandName", null != brandInfo ? brandInfo.getName() : "");
        map.put("productName", null != brandInfo ? brandInfo.getProduct() : "");
        map.put("variantName", null != brandInfo ? brandInfo.getVariant() : "");
        map.put("serialNumber", null != brandInfo ? brandInfo.getSerialNumber() : "");
        map.put("bankGstDetails", bankGstDetails);
        map.put("isBrandSubventionModel",
                null == isBrandSubventionModel ? String.valueOf(false) : isBrandSubventionModel.toString());
        map.put("downPayment", Util.getFLoat(downPayment).toString());
        NotificationRequest notificationRequest = new NotificationRequest(Arrays.asList("email", "whatsapp"),
                downPayment > 0 ? "PG_TXN_SUCCESS_WITH_DOWN_PAYMENT" : "PG_TXN_SUCCESS", map);
        send(notificationRequest);
    }

    @Async
    public void sendCustomerForTransactionFailed(String mobile, String email, String logo, String shopName,
                                                 String orderId, String txnId, Float amount, String bank,
                                                 String cardType, String statusMessage) {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("email", email);
        map.put("logo", logo);
        map.put("shopname", shopName);
        map.put("orderId", orderId);
        map.put("txnId", txnId);
        map.put("amount", Util.getFLoat(amount).toString());
        map.put("bank", bank);
        map.put("cardType", cardType);
        map.put("statusMessage", statusMessage);
        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("whatsapp"), "PG_TXN_FAILED", map);
        send(notificationRequest);
    }

    private String getHdfcCCNote(String bankCode, String cardType, String emi) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd").withZone(ZoneOffset.systemDefault());
            String repaymentDate =
                    LocalDateTime.ofInstant(Instant.now(), ZoneOffset.systemDefault()).plusMonths(1).format(formatter);
            if (BankEnum.HDFC.name().equals(bankCode) && CardTypeEnum.CREDIT.name().equals(cardType)) {
                return String.format(
                        "If your next statement due date is beyond %s, your first EMI will be slightly higher than %s" +
                                ". Bank will charge interest for the additional days from %s to your due date.",
                        repaymentDate, emi, repaymentDate);
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred while getting HDFC CC Note: ", e);
        }
        return "";
    }

    @Async
    public void sendConsumerForTransactionFailed(String email, String mobile, String deviceToken, String amount,
                                                 String shopName) {
        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("mobile", mobile);
        map.put("device", deviceToken);
        map.put("amount", amount);
        map.put("shopname", shopName);

        NotificationRequest notificationRequest =
                new NotificationRequest(Arrays.asList("push", "sms", "email"), "TXN_FAILED", map);
        send(notificationRequest);
    }

    @Async
    public void sendSignUpConsumer(String mobile, String firstName) {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("firstName", firstName);

        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("sms"), "SIGN_UP", map);
        send(notificationRequest);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Async
    public void sendWebhook(String url, Object obj, Class clazz, String token,
                            Map<String, String> customWebhookHeaders) {
        new Thread(() -> {
            RestTemplate restTemplate = new RestTemplate();
            try {
                HttpEntity<Object> httpEntity =
                        new HttpEntity<Object>(obj, populateHeaders(token, customWebhookHeaders));
                LOGGER.info("Sending webhook on url: {} with object: {}", url, obj);
                Object object = restTemplate.postForObject(url, httpEntity, clazz);
                LOGGER.info("Webhook response from url: {} is: {}", url, object);
            } catch (Exception e) {
                LOGGER.error("Exception occurred while sending webhook on URL: " + url + " with params: " + obj, e);
                throw new FreewayException(
                        "Exception occurred while sending webhook on URL: " + url + " with params: " + obj);
            }
        }).start();
    }

    @Async
    public void sendWelcomeNotificationEmail(String mobile, String email) {
        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("mobile", mobile);

        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("email"), "WELCOME", map);
        send(notificationRequest);
    }

    @Async
    public void sendPaymentLinkToConsumer(String mobile, String email, String url, String shopName, String amount,
                                          String emailUrl, Boolean sendSms, Boolean sendEmail) {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("email", email);
        map.put("url", url);
        map.put("email_url", emailUrl);
        map.put("merchant", shopName);
        map.put("amount", amount);

        List<String> list = new ArrayList<>();
        if (sendSms) {
            list.add("sms");
        }
        if (sendEmail) {
            list.add("email");
        }

        NotificationRequest notificationRequest = new NotificationRequest(list, "PAYMENT_LINK", map);
        send(notificationRequest);
    }

    @Async
    public void sendStoreUserSms(String mobile, String url, String shopName) {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("url", url);
        map.put("merchant", shopName);
        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("sms"), "STORE_USER", map);
        send(notificationRequest);
    }

    @Async
    public void sendSlackMessage(String text, boolean isOffline) {
        if (!(text.contains("9028055113") || text.contains("payment Online"))) {
            sendSlackMessage(isOffline ? "txnalerts-offline" : "",
                    isProduction ? "Production: " + text : "Non-Production: " + text);
        }
    }

    @Async
    public void sendSlackMessage(String channel, String text) {
        Map<String, String> map = new HashMap<>();
        map.put("channel", channel);
        map.put("text", text);

        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("slack"), "SLACK", map);
        send(notificationRequest);
    }

    @Async
    public void sendException(String text) {
        sendSlackMessage("exceptions",
                isProduction ? "Production(Exception): " + text : "Non-Production(Exception): " + text);
    }

    @Async
    public void clickToCall(String mobile) {
        sendSlackMessage("exceptions",
                isProduction ? "Production(Exception): click to call request trigger for " + mobile
                        : "Non-Production(Exception): click to call request trigger for " + mobile);

        Map<String, String> map = new HashMap<>();
        map.put("mobile", mobile);
        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("sms"), "CLICK_TO_CALL", map);
        send(notificationRequest);
    }

    @Async
    public void sendRegistrationNotification(String firstName, String lastName, String email, String mobile) {
        Map<String, String> map = new HashMap<>();
        String name = firstName + " " + lastName;
        map.put("name", name);
        map.put("email", email);
        map.put("mobile", mobile);

        NotificationRequest notificationRequest =
                new NotificationRequest(Arrays.asList("email", "whatsapp"), "REGISTRATION", map);
        send(notificationRequest);
    }

    private HttpHeaders populateHeaders(String token, Map<String, String> customWebhookHeaders) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (!StringUtils.isEmpty(token)) {
            LOGGER.info("Sending webhook with token");
            headers.add("access-token", token);
            headers.add("Authorization", token);
        }
        if (null != customWebhookHeaders && !CollectionUtils.isEmpty(customWebhookHeaders)) {
            LOGGER.info("Sending webhook with custom headers");
            ArrayList<String> fieldNames = new ArrayList<String>(customWebhookHeaders.keySet());
            for (String fieldName : fieldNames) {
                String fieldValue = customWebhookHeaders.get(fieldName);
                headers.add(fieldName, fieldValue);
            }
        }
        return headers;
    }

    @Async
    public void qrActivation(String email, String mobile, String deviceToken, String shopName, String firstName) {
        Map<String, String> map = new HashMap<>();
        map.put("firstName", firstName);
        map.put("email", email);
        map.put("mobile", mobile);
        map.put("deviceToken", deviceToken);
        map.put("shopName", shopName);


        NotificationRequest notificationRequest =
                new NotificationRequest(Arrays.asList("sms", "push", "whatsapp"), "QR_ACTIVATION", map);
        send(notificationRequest);
    }

    @Async
    public void upiCreditNotification(String transactionId, String deviceToken) {
        Map<String, String> map = new HashMap<>();
        map.put("transactionId", transactionId);
        map.put("device", deviceToken);
        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("push"), "COLLECT", map);
        send(notificationRequest);
    }

    @Async
    public void sendConsumerHdfcDcemiMsg(String merchant, String mobile) {
        Map<String, String> map = new HashMap<>();
        map.put("merchant", merchant);
        map.put("mobile", mobile);

        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("whatsapp"), "HDFC_DCEMI", map);
        send(notificationRequest);
    }

    @Async
    public void sendConsumerLimitExd(String merchant, Float amount, String bankCode, String mobile, String last4) {
        Map<String, String> map = new HashMap<>();
        map.put("merchant", merchant);
        map.put("bankCode", bankCode);
        map.put("mobile", mobile);
        map.put("last4", last4);
        map.put("amount", amount.toString());

        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("whatsapp"), "LMT_EXCD", map);
        send(notificationRequest);
    }

    @Async
    public void sendHelperEmail(Float amount, String salesPersonEmail) {
        Map<String, String> map = new HashMap<>();
        map.put("email", salesPersonEmail);
        map.put("amount", amount.toString());

        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("email"), "HELP_MLR", map);
        send(notificationRequest);
    }

    @Async
    public void sendHelperEmailForFailure(Float amount, String salesPersonEmail, String statusMessage) {
        Map<String, String> map = new HashMap<>();
        map.put("email", salesPersonEmail);
        map.put("amount", amount.toString());
        map.put("statusMessage", statusMessage);

        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("email"), "HELP_FAILURE_MLR", map);
        send(notificationRequest);
    }

    @Async
    public void sendExceptionEmail(String subject, String body) {
        Map<String, String> map = new HashMap<>();
        map.put("subject", subject);
        map.put("body", body);

        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("email"), "EXCEPTION_EMAIL", map);
        send(notificationRequest);
    }

    @Async
    public void sendTxnAlertEmail(String transactionId, String emails, String amount, String merchantName,
                                  String status, String merchantId) {

        Map<String, String> map = new HashMap<>();
        map.put("transactionId", transactionId);
        map.put("emails", emails);
        map.put("amount", amount);
        map.put("merchantName", merchantName);
        map.put("status", status);

        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("email"), "TXN_ALERTS_EMAIL", map);
        send(notificationRequest);
    }

    public void initiateRefund(Float refundAmount, String shopName, String bankName, String paymentRefundTransactionId,
                               String mobile) {
        Map<String, String> map = new HashMap<>();
        map.put("refundAmount", String.valueOf(Util.getFLoat(refundAmount)));
        map.put("shopName", shopName);
        map.put("bankName", bankName);
        map.put("paymentRefundTransactionId", paymentRefundTransactionId);
        map.put("noOfHours", "24");
        map.put("mobile", mobile);

        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("whatsapp"), "INITIATE_REFUND", map);
        send(notificationRequest);
    }

    public void refundSentToBank(String paymentRefundTransactionId, String bankName, String mobile) {
        Map<String, String> map = new HashMap<>();
        map.put("paymentRefundTransactionId", paymentRefundTransactionId);
        map.put("bankName", bankName);
        map.put("noOfWorkingDays", "15 working day with refund status");
        map.put("mobile", mobile);

        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("whatsapp"), "REFUND_SEND_TO_BANK", map);
        send(notificationRequest);
    }

    public void refundSuccess(String paymentRefundTransactionId, Float refundAmount, String shopName, String bankName,
                              String mobile) {
        Map<String, String> map = new HashMap<>();
        map.put("paymentRefundTransactionId", paymentRefundTransactionId);
        map.put("refundAmount", String.valueOf(Util.getFLoat(refundAmount)));
        map.put("shopName", shopName);
        map.put("bankName", bankName);
        map.put("noOfWorkingDays", "4-5 days");
        map.put("mobile", mobile);

        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("whatsapp"), "REFUND_SUCCESS", map);
        send(notificationRequest);
    }

    public void refundFailed(String paymentRefundTransactionId, Float refundAmount, String shopName, String bankName,
                             String mobile) {
        Map<String, String> map = new HashMap<>();
        map.put("paymentRefundTransactionId", paymentRefundTransactionId);
        map.put("refundAmount", String.valueOf(Util.getFLoat(refundAmount)));
        map.put("shopName", shopName);
        map.put("bankName", bankName);
        map.put("noOfWorkingDays", "4-5");
        map.put("mobile", mobile);

        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("whatsapp"), "REFUND_FAILED", map);
        send(notificationRequest);
    }

    public void sendpaymentBenefits(String mobile, Float amount) {
        Map<String, String> map = new HashMap<>();
        map.put("amount", String.valueOf(Util.getFLoat(amount)));
        map.put("mobile", mobile);

        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("sms"), "payment_BENEFITS", map);
        send(notificationRequest);
    }

    public void createTxnInvoice(Map<String, String> map) {
        LOGGER.info("Create txn invoice {}", map);
        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("html2pdf"), "GENERATE_INV", map);
        send(notificationRequest);
    }

    public void sendEducationLoanSubmission(ConsumerResponse consumerResponse) {
        if (null != consumerResponse) {
            Map<String, String> map = new HashMap<>();
            map.put("name", consumerResponse.getFirstName());
            map.put("mobile", consumerResponse.getMobile());
            map.put("email", consumerResponse.getEmail());

            NotificationRequest notificationRequest =
                    new NotificationRequest(Arrays.asList("whatsapp", "email"), "EDU_LOAN_SUBMIT", map);
            send(notificationRequest);
        }
    }

    public void sendEducationLoanInitiate(ConsumerResponse consumerResponse) {
        if (null != consumerResponse) {
            Map<String, String> map = new HashMap<>();
            map.put("name", consumerResponse.getFirstName());
            map.put("mobile", consumerResponse.getMobile());
            map.put("email", consumerResponse.getEmail());

            NotificationRequest notificationRequest =
                    new NotificationRequest(Arrays.asList("whatsapp", "email"), "EDU_LOAN_INITIATE", map);
            send(notificationRequest);
        }
    }

    public void sendConsumerForCashbackSuccess(String consumerMobile, String name, Float cashback,
                                               Instant cashbackDate) {
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("mobile", consumerMobile);
        map.put("cashback", String.valueOf(Util.getFLoat(cashback)));
        map.put("cashbackDate", cashbackDate.toString());

        NotificationRequest notificationRequest =
                new NotificationRequest(Arrays.asList("email"), "CASHBACK_SUCCESS", map);
        send(notificationRequest);
    }

    public void sendVouchers(String mobile, String gvCard, String brandName) {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("card", gvCard);
        map.put("brandName", brandName);

        NotificationRequest notificationRequest =
                new NotificationRequest(Arrays.asList("whatsapp"), "SEND_GVCARD", map);
        send(notificationRequest);
    }

    public void sendGiftVoucher(String name, String email, String product, String amount, String cardId, String cardPin,
                                String activationUrl, String validity, String bank, String emi, String tenure,
                                String imageUrl) {
        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("email", email);
        map.put("product", product);
        map.put("amount", amount);
        map.put("cardId", cardId);
        map.put("cardPin", cardPin);
        map.put("activationUrl", activationUrl);
        map.put("validity", validity);
        map.put("bank", bank);
        map.put("emi", emi);
        map.put("tenure", tenure);
        map.put("imageUrl", imageUrl);
        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("email"), "GIFT_VOUCHER", map);
        send(notificationRequest);
    }

    public void sendUpdateOfferEmail(String mobile, String email, String shopName, String ip, String source) {
        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("shopName", shopName);
        map.put("timestamp", Instant.now().toString());
        map.put("platform", source);
        map.put("ip", ip);
        map.put("mobile", mobile);
        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("email"), "UPDATE_DYNAMIC_OFFER", map);
        send(notificationRequest);
    }

    @Async
    public void sendIcicipruBenefits(String mobile, String bank) {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("bank", bank);
        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("sms"), "IPRU_BENEFITS", map);
        send(notificationRequest);
    }

    @Async
    public void sendInsuranceToCustomer(String email, String key) {
        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("key", key);
        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("email"), "CONSUMER_INSURANCE_REPORT", map);
        send(notificationRequest);
    }

    @Async
    public void sendEligibilityCheckToCustomer(String mobile, String url) {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("url", url);
        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("sms"), "CHECK_ELIGIBILITY", map);
        send(notificationRequest);
    }


    @Async
    public void sendCustomOffer(String mobile, String email, String shopName, String ip, String source, String orderId,
                                String customerMobile, String product) {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("email", email);
        map.put("shopName", shopName);
        map.put("timestamp", Instant.now().toString());
        map.put("platform", source);
        map.put("ip", ip);
        map.put("orderId", orderId);
        map.put("customerMobile", customerMobile);
        map.put("product", product);
        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("email"), "DYNAMIC_OFFER_TO_CUSTOMER", map);
        send(notificationRequest);
    }

    @Async
    public void sendGvOrderStatus(String status, String transactionId, String requestObj) {
        Map<String, String> map = new HashMap<>();
        map.put("status", status);
        map.put("transaction", transactionId);
        map.put("request", requestObj);

        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("email"), "GV_ORDER_STATUS", map);
        send(notificationRequest);
    }

    @Async
    public void sendTransactionStatusToSales(String deviceToken, String status, String merchantName, String amount) {
        Map<String, String> map = new HashMap<>();
        map.put("device", deviceToken);
        map.put("status", status);
        map.put("amount", amount);
        map.put("merchantName", merchantName);

        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("push"), "TXN_STATUS_TO_SALES", map);
        send(notificationRequest);
    }

    @Async
    public void sendTransactionStatusToSales(Map<String, String> map) {
        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("push"), "TXN_STATUS_TO_SALES", map);
        send(notificationRequest);
    }

    @Async
    public void sendInitiateSeamlessTransactionAlert(String mobile, String amount, String merchantName) {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("amount", amount);
        map.put("merchantName", merchantName);

        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("email"), "INITIATE_SEAMLESS_TRANSACTION_ALERT", map);
        send(notificationRequest);
    }

    @Async
    public void sendGenericEmail(String toEMailIds, String content, String subject, String filename, String attachment,
                                 String ccEMailIds) {
        Map<String, String> map = new HashMap<>();
        map.put("toEMailIds", toEMailIds);
        map.put("content", content);
        map.put("subject", subject);
        map.put("filename", filename);
        map.put("attachment", attachment);
        map.put("ccEMailIds", ccEMailIds);

        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("email"), "GENERIC_EMAIL", map);
        send(notificationRequest);
    }

    public void sendCustomEmail(String emails, String subject, String body){

        Map<String, String> map = new HashMap<>();
        map.put("email", emails);
        map.put("subject", subject);
        map.put("body", body);
        NotificationRequest notificationRequest = new NotificationRequest(Collections.singletonList("email"), "CUSTOM_EMAIL", map);
        send(notificationRequest);
    }

    @Async
    public void sendMerReqCustomPricing(String toEMailIds, String content, String subject, String merchantId,
                                        String merchantUrl, String shopName, String contact, String address) {
        Map<String, String> map = new HashMap<>();
        map.put("toEMailIds", toEMailIds);
        map.put("content", content);
        map.put("subject", subject);
        map.put("merchantID", merchantId);
        map.put("merchantUrl", merchantUrl);
        map.put("shopName", shopName);
        map.put("contact", contact);
        map.put("address", address);

        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("email"), "CUSTOM_PRICING", map);
        send(notificationRequest);
    }

    @Async
    public void sendAutoDownpaymentRefund(String email, String mobile, Float dpAmount) {
        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("mobile", mobile);
        map.put("dpAmount", String.valueOf(dpAmount));

        NotificationRequest notificationRequest =
                new NotificationRequest(Arrays.asList("email", "sms", "whatsapp"), "NT_REFUND_DOWNPAYMENT_SUCCESS",
                        map);
        send(notificationRequest);
    }

    @Async
    public void sendDisputeOpenNotification(String toEMailIds, String ccEMailIds) {
        Map<String, String> map = new HashMap<>();
        map.put("toEMailIds", toEMailIds);
        map.put("ccEMailIds", ccEMailIds);

        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("email"), "DISPUTE_OPENED", map);
        send(notificationRequest);
    }

    @Async
    public void sendLeadCreationNotification(String mobile, String name) {
        Map<String, String> map = new HashMap<>();
//        map.put("name", name);
//        map.put("storeUserName", name);
//        map.put("mobile", mobile);
//        map.put("url", "https://play.google.com/store/apps/details?id=com.payment.merchant");
//        NotificationRequest notificationRequest = new NotificationRequest(
//                Arrays.asList("sms", "whatsapp"), "STORE_USER_ACCESS_APPROVED", map);
//        send(notificationRequest);
    }

    @Async
    public void sendStoreUserAddedNotification(String mobile, String storeUserName, String loggedInUserName,
                                               String storeUserId, String merchantId) {
        String url = isProduction ? "https://api.getpayment.com" : "http://dev.getpayment.com";
        url += "/sspay/#/user-approval?storeUserId=" + storeUserId;
        Map<String, String> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("loggedInUserName", loggedInUserName);
        map.put("storeUserName", storeUserName);
        map.put("url", url);
        auditService.prepareAndSaveSmsNotification(map, "STORE_USER_APPROVAL_REQUEST_TO_MER", "sms", null, "merchant",
                merchantId);
        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("sms"), "STORE_USER_APPROVAL_REQUEST_TO_MER", map);
        send(notificationRequest);
    }

    @Async
    public void sendForgotPasswordEmail(String email, String otp) {
        String url =
                isProduction ? "https://dashboard.getpayment.com/#/reset/" : "https://staging.getpayment.com/#/reset/";
        url += otp;
        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("body", "Please reset your password by clicking on the link " + url +
                " <br> Link will expire in next <b>10 minutes 0 seconds</b>");
        map.put("subject", "Activate your payment Account.");
        NotificationRequest notificationRequest =
                new NotificationRequest(Collections.singletonList("email"), "AP_FORGOT", map);
        send(notificationRequest);
    }

    public void sendNTBTransactionNotification(String consumerMobile, String merchantName) {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", consumerMobile);
        map.put("text", "Dear Customer,\n" +
                "Complete your purchase on " + merchantName + " by downloading the application https://payment.page.link/p2ZX and avail Instant EMI offer provided by payment.\n" +
                "-payment");
        NotificationRequest notificationRequest = new NotificationRequest(
                Collections.singletonList("sms"), "CUSTOM_SMS", map);
        send(notificationRequest);
    }

    public void sendTransactionPushNotification(String consumerMobile, String merchantName, String merchantDisplayId, String salesUserDeviceToken) {
        Map<String, String> map = new HashMap<>();
        map.put("device", salesUserDeviceToken);
        map.put("title", "BRE SUCCESS!");
        map.put("body", "Customer " + consumerMobile +" is approved for loan from ICICI on " + merchantDisplayId + "-"
                + merchantName);
        NotificationRequest notificationRequest = new NotificationRequest(Collections.singletonList("push"),
                "CUSTOM_PUSH", map);
        send(notificationRequest);
    }
}