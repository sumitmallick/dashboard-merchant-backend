package com.freewayemi.merchant.service.notifications;

import com.freewayemi.merchant.dto.response.BankDetailsResponse;
import com.freewayemi.merchant.service.PaymentOpsService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class NotificationHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpressionResolver.class);

    private static final String ZONE_ASIA = "Asia/Kolkata";
    private static final String ANY_CREDIT_CARD = "Any credit card";

    private enum EligibilityType {
        preApproved, NTB, CC;
    }

    private final PaymentOpsService paymentOpsService;

    @Autowired
    public NotificationHelper(PaymentOpsService paymentOpsService) {
        this.paymentOpsService = paymentOpsService;
    }

    public String preferredEligibility(List<String> eligibilities) {
        String preferredEligibility = "";
        if (CollectionUtils.isNotEmpty(eligibilities)) {
            boolean ntbEligible = false;
            boolean ccEligible = false;
            boolean preApproved = false;
            for (String eligibility: eligibilities) {
                if (eligibility.endsWith("_NTB")) {
                    ntbEligible = true;
                } else if (EligibilityType.CC.name().equalsIgnoreCase(eligibility)) {
                    ccEligible = true;
                } else {
                    preApproved = true;
                    // Pre-approved has the highest preference hence breaking the loop here
                    break;
                }
            }
            if (preApproved) {
                preferredEligibility = EligibilityType.preApproved.name();
            } else if (ntbEligible) {
                preferredEligibility = EligibilityType.NTB.name();
            } else if (ccEligible) {
                preferredEligibility = EligibilityType.CC.name();
            }
        }
        return preferredEligibility;
    }

    public String bankNamesString(List<String> eligibilities) {
        if (CollectionUtils.isEmpty(eligibilities)) {
            return "";
        }
        if (eligibilities.size() == 1 && EligibilityType.CC.name().equalsIgnoreCase(eligibilities.get(0))) {
            return ANY_CREDIT_CARD;
        }
        List<String> bankNames = new ArrayList<>();
        BankDetailsResponse bankDetails = paymentOpsService.getBankDetails();
        for (String eligibility: eligibilities) {
            if (EligibilityType.CC.name().equalsIgnoreCase(eligibility)) {
                bankNames.add(ANY_CREDIT_CARD);
            } else {
                if (bankDetails != null && bankDetails.getBankEnumDetailsMap() != null &&
                        bankDetails.getBankEnumDetailsMap().containsKey(eligibility)) {
                    bankNames.add(bankDetails.getBankEnumDetailsMap().get(eligibility).getBankName());
                } else {
                    LOGGER.debug("No bank name found for: {}", eligibility);
                    bankNames.add(eligibility);
                }
            }
        }
        if (bankNames.size() == 1) {
            return bankNames.get(0);
        }
        String last = bankNames.remove(bankNames.size() - 1);
        return String.join(", ", bankNames) + " and " + last;
    }

    public String formatDate(Object date, String pattern, String zone) {
        Instant parsedInstant = null;
        if (date instanceof String) {
            parsedInstant = Instant.parse(date.toString());
        } else if (date instanceof Double) {
            parsedInstant = Instant.ofEpochSecond(((Double) date).longValue());
        } else {
            return date != null ? date.toString() : "";
        }

        ZoneId fromZone = ZoneId.systemDefault();
        ZoneId toZone = ZoneId.of(zone);
        ZonedDateTime zonedDateTime = parsedInstant.atZone(fromZone);
        ZonedDateTime toZonedDateTime = zonedDateTime.withZoneSameInstant(toZone);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        return dtf.format(toZonedDateTime);
    }

    public String formatDate(Object date, String pattern) {
        return formatDate(date, pattern, ZONE_ASIA);
    }
}
