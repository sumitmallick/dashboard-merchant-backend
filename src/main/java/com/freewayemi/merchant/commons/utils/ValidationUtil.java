package com.freewayemi.merchant.commons.utils;

import com.freewayemi.merchant.commons.exception.MerchantException;
import com.freewayemi.merchant.commons.type.MerchantResponseCode;
import com.freewayemi.merchant.dto.CheckNtbEligibilityRequest;
import com.freewayemi.merchant.dto.ConsentDto;
import com.freewayemi.merchant.dto.ProviderConsent;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtil {

    private static final String AMOUNT_PATTERN = "^\\d{1,7}(\\.\\d{1,2})?$";
    private static final String MOBILE_NUMBER_PATTERN = "^(\\d{10})?$";
    private static final String MERCHANT_ORDER_ID_PATTERN = "(?=^.{1,50}$)[0-9a-zA-Z_-]*$";
    private static final String MOBILE_NUMBER_STARTS_WITH_PATTERN = "^(1|2|3|4|5)\\d+";
    private static final String SPECIAL_CHARACTER_PATTERN = "[^a-zA-Z0-9_-]";
    private static final String EMAIL_REGEX = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";
    private static final String NAME_PATTERN = "^[a-zA-Z ]+$";
    private static final String DOB_PATTERN = "^(0[1-9]|[12][0-9]|3[01])/(0[1-9]|1[0-2])/\\d{4}$"; //dd/MM/yyy
    private static final String PAN_PATTERN = "[A-Z]{5}[0-9]{4}[A-Z]";
    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);

    public static boolean validateEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean validateAmount(String amount) {
        Pattern p = Pattern.compile(AMOUNT_PATTERN);
        Matcher m = p.matcher(amount);
        if (m.matches()) {
            return !(Double.parseDouble(amount) <= 0);
        } else {
            return false;
        }
    }

    public static boolean validateMobileNumber(String mobileNumber) {
        Pattern p = Pattern.compile(MOBILE_NUMBER_PATTERN);
        Matcher m = p.matcher(mobileNumber);
        return m.matches();
    }

    public static boolean validateOrderId(String orderId) {
        Pattern p = Pattern.compile(MERCHANT_ORDER_ID_PATTERN);
        Matcher m = p.matcher(orderId);
        return m.matches();
    }

    public static boolean isGenuineMobileNumber(String mobileNumber) {
        if (mobileNumber.matches(MOBILE_NUMBER_STARTS_WITH_PATTERN)) {
            return false;
        }
        return !HOTLISTED_MOBILE_NUMBERS.contains(mobileNumber);
    }

    private static final List<String> HOTLISTED_MOBILE_NUMBERS =
            Arrays.asList("9999999999", "9123456789", "0987654321", "8888888888", "6666666666", "7777777777",
                    "8888888888", "0000000000", "9191919191", "8181818181", "7171717171", "6161616161",
                    "9123123123");

    public static boolean isContainSpecialCharacter(String value) {
        Pattern p = Pattern.compile(SPECIAL_CHARACTER_PATTERN);
        Matcher m = p.matcher(value);
        return m.find();
    }

    public static boolean validateName(String name) {
        Pattern pattern = Pattern.compile(NAME_PATTERN);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    public static boolean validateDOB(String dob) {
        Pattern pattern = Pattern.compile(DOB_PATTERN);
        Matcher matcher = pattern.matcher(dob);
        return matcher.matches();
    }

    public static boolean validatePAN(String pan) {
        Pattern pattern = Pattern.compile(PAN_PATTERN);
        Matcher matcher = pattern.matcher(pan);
        return matcher.matches();
    }

    public static void validateCheckNtbEligibility(CheckNtbEligibilityRequest checkNtbEligibilityRequest) {
        if (Util.isNotNull(checkNtbEligibilityRequest)) {
            if (!StringUtils.hasText(checkNtbEligibilityRequest.getMobile())) {
                throw new MerchantException(MerchantResponseCode.INVALID_MOBILE);
            }
            if (!ValidationUtil.validateMobileNumber(checkNtbEligibilityRequest.getMobile())) {
                throw new MerchantException(MerchantResponseCode.INVALID_MOBILE);
            }
            if (!StringUtils.hasText(checkNtbEligibilityRequest.getFirstName()) ||
                    !validateName(checkNtbEligibilityRequest.getFirstName())) {
                throw new MerchantException(MerchantResponseCode.INVALID_FIRST_NAME);
            }
            if (!StringUtils.hasText(checkNtbEligibilityRequest.getLastName()) ||
                    !validateName(checkNtbEligibilityRequest.getLastName())) {
                throw new MerchantException(MerchantResponseCode.INVALID_LAST_NAME);
            }
            if (!StringUtils.hasText(checkNtbEligibilityRequest.getEmail())) {
                throw new MerchantException(MerchantResponseCode.INVALID_EMAIL);
            }
            if (!validateEmail(checkNtbEligibilityRequest.getEmail())) {
                throw new MerchantException(MerchantResponseCode.INVALID_EMAIL);
            }
            if (!StringUtils.hasText(checkNtbEligibilityRequest.getPan()) ||
                    !validatePAN(checkNtbEligibilityRequest.getPan())) {
                throw new MerchantException(MerchantResponseCode.REQUIRE_PAN);
            }
            if (!StringUtils.hasText(checkNtbEligibilityRequest.getDob()) ||
                    !validateDOB(checkNtbEligibilityRequest.getDob())) {
                throw new MerchantException(MerchantResponseCode.REQUIRE_DOB);
            }
            if (!StringUtils.hasText(checkNtbEligibilityRequest.getPinCode())) {
                throw new MerchantException(MerchantResponseCode.INVALID_PIN);
            }
            if (!StringUtils.hasText(checkNtbEligibilityRequest.getGender())) {
                throw new MerchantException(MerchantResponseCode.REQUIRE_GENDER);
            }
            if (Util.isNull(checkNtbEligibilityRequest.getEmploymentDetails())) {
                throw new MerchantException(MerchantResponseCode.REQUIRE_EMPLOYEE_DETAILS);
            }
            if (!StringUtils.hasText(checkNtbEligibilityRequest.getFatherName())) {
                throw new MerchantException(MerchantResponseCode.REQUIRE_FATHER_NAME);
            }
            if (Util.isNull(checkNtbEligibilityRequest.getAmount())) {
                throw new MerchantException(MerchantResponseCode.REQUIRE_AMOUNT);
            }
            if (!StringUtils.hasText(checkNtbEligibilityRequest.getOrderId())) {
                throw new MerchantException(MerchantResponseCode.REQUIRE_ORDER_ID);
            }
            if (!StringUtils.hasText(checkNtbEligibilityRequest.getProductSkuCode())) {
                throw new MerchantException(MerchantResponseCode.REQUIRE_PRODUCT_SKU_CODE);
            }
            if (!StringUtils.hasText(checkNtbEligibilityRequest.getReturnUrl())) {
                throw new MerchantException(MerchantResponseCode.REQUIRE_RETURN_URL);
            }
            if (!StringUtils.hasText(checkNtbEligibilityRequest.getProviderGroup())) {
                throw new MerchantException(MerchantResponseCode.REQUIRE_PROVIDER_GROUP);
            }
            if (Util.isNull(checkNtbEligibilityRequest.getCurrentAddress())) {
                throw new MerchantException(MerchantResponseCode.REQUIRE_CURRENT_ADDRESS);
            }
            if (Util.isNull(checkNtbEligibilityRequest.getCurrentAddressSameAsPermanent())) {
                throw new MerchantException(MerchantResponseCode.REQUIRE_CURRENT_ADDRESS_SAME_AS_PERMANENT);
            }else if (Boolean.FALSE.equals(checkNtbEligibilityRequest.getCurrentAddressSameAsPermanent())) {
                if (Util.isNull(checkNtbEligibilityRequest.getPermanentAddress())) {
                    throw new MerchantException(MerchantResponseCode.REQUIRE_PERMANENT_ADDRESS);
                }
            }
            if (Util.isNull(checkNtbEligibilityRequest.getConsent())) {
                throw new MerchantException(MerchantResponseCode.PROVIDER_CONSENT);
            }
            if (Util.isNull(checkNtbEligibilityRequest.getConsent().getTimestamp())) {
                throw new MerchantException(MerchantResponseCode.REQUIRE_TIMESTAMP);
            }
            if (!StringUtils.hasText(checkNtbEligibilityRequest.getConsent().getIpAddress())) {
                throw new MerchantException(MerchantResponseCode.REQUIRE_IP_ADDRESS);
            }
            if (CollectionUtils.isEmpty(checkNtbEligibilityRequest.getConsent().getProviderConsents())) {
                throw new MerchantException(MerchantResponseCode.PROVIDER_CONSENT);
            } else {
                validateConsentDto(checkNtbEligibilityRequest);
            }
            if (Util.isNull(checkNtbEligibilityRequest.getMobileConsent())) {
                throw new MerchantException(MerchantResponseCode.REQUIRE_MOBILE_CONSENT);
            }
            if (!StringUtils.hasText(checkNtbEligibilityRequest.getMobileConsent().getReceiverMobile())) {
                throw new MerchantException(MerchantResponseCode.REQUIRE_RECEIVERS_MOBILE);
            }
            if (!ValidationUtil.validateMobileNumber(checkNtbEligibilityRequest.getMobileConsent().getReceiverMobile())) {
                throw new MerchantException(MerchantResponseCode.REQUIRE_RECEIVERS_MOBILE);
            }
            if (Util.isNull(checkNtbEligibilityRequest.getMobileConsent().getTimestamp())) {
                throw new MerchantException(MerchantResponseCode.REQUIRE_TIMESTAMP);
            }
            if (!StringUtils.hasText(checkNtbEligibilityRequest.getMobileConsent().getIpAddress())) {
                throw new MerchantException(MerchantResponseCode.REQUIRE_IP_ADDRESS);
            }
            if (!StringUtils.hasText(checkNtbEligibilityRequest.getMobileConsent().getContent())) {
                throw new MerchantException(MerchantResponseCode.REQUIRE_CONTENT);
            }
        }

    }

    private static void validateConsentDto(CheckNtbEligibilityRequest checkNtbEligibilityRequest) {
        List<ProviderConsent> providerConsents = checkNtbEligibilityRequest.getConsent().getProviderConsents();
        for (ProviderConsent providerConsent : providerConsents) {
            if (Objects.isNull(providerConsent)) {
                throw new MerchantException(MerchantResponseCode.PROVIDER_CONSENT);
            }
            if (Objects.isNull(providerConsent.getProvider())) {
                throw new MerchantException(MerchantResponseCode.PROVIDER_CONSENT_NAME);
            }
            if (CollectionUtils.isEmpty(providerConsent.getConsents())) {
                throw new MerchantException(MerchantResponseCode.PROVIDER_CONSENT_DETAILS);
            }
            List<ConsentDto> consents = providerConsent.getConsents();
            for (ConsentDto consent : consents) {
                if (!StringUtils.hasText(consent.getName())) {
                    throw new MerchantException(MerchantResponseCode.PROVIDER_CONSENT_DETAILS_NAME);
                }
                if (!StringUtils.hasText(consent.getType())) {
                    throw new MerchantException(MerchantResponseCode.PROVIDER_CONSENT_DETAILS_TYPE);
                }
                if (!StringUtils.hasText(consent.getContent())) {
                    throw new MerchantException(MerchantResponseCode.PROVIDER_CONSENT_DETAILS_TEXT);
                }
            }
        }
    }

}
