package com.freewayemi.merchant.commons.type;

import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public enum MerchantResponseCode {

    INTERNAL_SERVER_ERROR("M500", "Something went wrong! Please try later", HttpStatus.INTERNAL_SERVER_ERROR.value()),
    BAD_REQUEST("M400", "Bad Request"),
    UNAUTHORIZED("M401", "Merchant-Authorization failed", HttpStatus.UNAUTHORIZED.value()),
    MERCHANT_NOT_FOUND("M402", "Invalid Merchant"),
    CONSUMER_MOBILE_REQUIRED("M403", "Consumer mobile number is required"),
    INVALID_CONSUMER_MOBILE("M404", "Provide valid 10 digit consumer mobile number"),
    BRAND_REQUIRED("M405", "Brand field required"),
    PRODUCT_REQUIRED("M407", "Product field required"),
    INVALID_PRODUCT("M408", "Invalid Product"),
    INVALID_AMOUNT("M409", "Invalid Amount value"),
    INVALID_PARTNER("M417", "Partner not found"),

    PRODUCTS_NOT_FOUND("410", "Products Not Found"),
    NO_OFFERS_FOUND("M460", "No offers found"),
    MINIMUM_AMOUNT_CHECK_FAILED("M461", "Amount is less than the eligible transaction limit"),
    MAXIMUM_AMOUNT_CHECK_FAILED("M462", "Amount is more than the eligible transaction limit"),
    CUSTOMER_NOT_ELIGIBLE("M463", "Oops! You are not eligible by any lender"),


    // async claim response codes
    TRANSACTION_REQUIRED("M410", "Transaction Id required"),
    SERIAL_NUMBER_REQUIRED("M411", "Serial number required"),
    INVALID_TRANSACTION("M412", "Invalid transaction Id"),
    SERIAL_NUMBER_FAILED("M413", "Serial number validation failed"),
    SERIAL_NUMBER_VALIDATION_NOT_REQUIRED("M414", "Invalid call - Serial number validation not required for this transaction"),
    TRANSACTION_NOT_SUCCESS("M415", "Transaction is not successfully completed"),
    SERIAL_NUMBER_ALREADY_CLAIMED("M416", "Serial number already validated for this transaction"),

    SUCCESS("I200","Success"),
    BRAND_NOT_FOUND("I401","Brand not Found"),
    INVALID_BRAND("M406", "Invalid Brand"),
    INVALID_MERCHANT("I403", "Merchant Id not Found"),
    SERIAL_NUMBER_CLAIMED("M201", "Serial number validated successfully", HttpStatus.OK.value()),
    SERIAL_NUMBER_CLAIM_FAILED("M202", "Serial number can not be claimed after %s calendar day"),


    //juspay ntb integration error codes
    CONSUMER_PROFILE_ALREADY_EXISTS("M410", "Consumer profile already exists"),
    INVALID_PAN("M411", "Entered PAN is invalid for Consumer profile"),
    PAN_LINKED_TO_DIFFERENT_MOBILE("M412", "The PAN entered is linked to a different mobile number. Please retry with your own PAN."),
    REQUIRE_MOBILE_CONSENT("M413", "Mobile consent is required"),
    INVALID_PIN("M415", "Enter valid pin code"),
    INVALID_EMAIL("M416", "Please provide valid email."),
    EMAIL_ALREADY_EXISTS("M417", "Email already exists. Please use another email address"),
    INVALID_ACCOUNT("M418", "Invalid Consumer Account"),
    PAN_UPDATE_NOT_ALLOWED("M419", "Consumer pan update is not allowed"),
    DOB_UPDATE_NOT_ALLOWED("M420", "Consumer date of birth update is not allowed"),
    GENDER_UPDATE_NOT_ALLOWED("M421", "Consumer gender update is not allowed"),
    INVALID_MOBILE("M422", "Please provide valid mobile number"),
    INVALID_FIRST_NAME("M423", "Please provide valid first name."),
    TRANSACTION_NOT_FOUND("M424", "Transaction not found."),
    INVALID_LAST_NAME("M425", "Please provide valid last name."),
    REQUIRE_PAN("M426", "Please provide valid PAN number"),
    REQUIRE_DOB("M427", "Please provide valid Date of Birth"),
    REQUIRE_GENDER("M428", "Please provide Gender"),
    REQUIRE_CURRENT_ADDRESS("M430", "Please provide current address"),
    REQUIRE_CURRENT_ADDRESS_SAME_AS_PERMANENT("M431", "Please provide current address same as permanent"),
    REQUIRE_FATHER_NAME("M432", "Please provide Father Name"),
    REQUIRE_AMOUNT("M433", "Please provide amount"),
    REQUIRE_ORDER_ID("M434", "Please provide order id"),
    REQUIRE_RETURN_URL("M435", "Please provide return url"),
    REQUIRE_EMPLOYEE_DETAILS("M436", "Please provide employee details"),
    PROVIDER_CONSENT("M437", "Please provide provider consent"),
    PROVIDER_CONSENT_NAME("M438", "Please provide provider consent name"),
    PROVIDER_CONSENT_DETAILS("M439", "Please provide provider consent details"),
    PROVIDER_CONSENT_DETAILS_NAME("M440", "Please provide provider consent name in details"),
    PROVIDER_CONSENT_DETAILS_TYPE("M441", "Please provide provider consent type in details"),
    PROVIDER_CONSENT_DETAILS_TEXT("M442", "Please provide provider consent text in details"),
    REQUIRE_RECEIVERS_MOBILE("M443", "Please provide valid receivers mobile"),
    REQUIRE_TIMESTAMP("M444", "Please provide timestamp"),
    REQUIRE_IP_ADDRESS("M445", "Please provide ip address"),
    REQUIRE_CONTENT("M446", "Please provide content"),
    REQUIRE_PRODUCT_SKU_CODE("M447", "Please provide product sku code"),
    REQUIRE_PERMANENT_ADDRESS("M448", "Please provide permanent address"),
    PROVIDER_NOT_SUPPORTED("M449", "Provider is not supported by the merchant"),

    INVALID_EDUCATION_TYPE("M450", "Invalid education qualification passed"),
    INVALID_EMPLOYMENT_TYPE("M451", "Invalid employment type passed"),
    INVALID_COMPANY_TYPE("M452", "Invalid company type passed"),
    INVALID_BUSINESS_TYPE("M453", "Invalid business type passed"),
    INVALID_MARITAL_STATUS("M454", "Invalid marital status passed"),
    INVALID_GENDER_TYPE("M455", "Invalid gender type passed"),
    INVALID_MONTHLY_INCOME("M456", "Entered Amount Invalid"),
    REQUIRE_PROVIDER_GROUP("M457", "Please provide the providerGroup"),

    REFUND_NOT_VALID("M458", "Refund Not valid."),
    PARTIAL_REFUND("M459", "Refund Transaction amount is less than transaction pg amount"),
    DUPLICATE_REFUND("M464", "Duplicate Refund TransactionID."),
    DISPUTE_REFUND("M465", "Refund not allowed for the transaction where dispute is raised"),
    REFUND_AFTER_30_DAYS("M466", "Refund after 30 days is not allowed by bank"),
    REFUND_AMOUNT_EXCEEDS("M467", "Insufficient balance to do refund for refund transactionId: "),
    REFUND_BALANCE_UNAVAILABLE("M468", "Refund cannot be processed as there is no positive balance available"),
    REFUND_BALANCE_UNAVAILABLE_1("M469", "Refund cannot be processed as sale balance is less then amount to be refunded"),
    REFUND_BALANCE_UNAVAILABLE_2("M470", "Refund cannot be processed as there is no successful sale transaction available today"),
    REFUND_NOT_ALLOWED_BY_BANK("M471", "Refund is not allowed by the bank"),
    REFUND_AFTER_90_DAYS("M472", "Refund after 90 days is not allowed by bank"),

    INVALID_PRODUCT_AMOUNT("M473", "Transaction Failure. Invalid product amount"),
    INVALID_SKU_CODE("M474", "Transaction Failure. Invalid product sku code"),
    INVALID_PROVIDER_GROUP("M475", "Incorrect providerGroup value passed, please share the correct value"),
    ORDERID_ALREADY_EXISTS("M476", "Successful transaction already exists for orderId %s. Pass new orderId"),


    //seamless bin validation errors
    MERCHANT_SEAMLESS_VALIDATION("M501", "Seamless Is not Enabled on the merchant"),
    BIN_VALIDATION_PAN("M502", "Mandatory parameter: PAN is not in correct format"),
    BIN_VALIDATION_CARDNUMBER("M503", "Mandatory parameter: cardNumber is not correct as per Luhn algorithm"),
    BIN_VALIDATION_EXPMONTH("M504", "Mandatory parameter: expMonth is not available"),
    BIN_VALIDATION_EXPYEAR("M505", "Mandatory parameter: expYear is not available"),
    BIN_VALIDATION_CVV("M506", "Mandatory parameter: cvv is not available"),
    BIN_VALIDATION_TENURE("M507", "Mandatory parameter: tenure is not available"),
    BIN_VALIDATION_TENURE_INVALID("M508", "Mandatory parameter: tenure is not valid"),
    BIN_VALIDATION_CARDINFO("M509", "Card Info in request is null"),
    BIN_VALIDATION_BANK_CODE("M510", "Bank Code in request and Bank Code of card is not matching"),
    BIN_VALIDATION_BANK_NAME("M511", "Bank Name in request and Bank Name with card number is not matching"),
    BIN_VALIDATION_CARD_TYPE("M512", "Card Type in request and Card type of card number is not matching"),
    ;

    private final String code;
    private final String message;
    private final Integer httpStatusCode;

    MerchantResponseCode(String code, String message) {
        this(code, message, null);
    }

    MerchantResponseCode(String code, String message, Integer httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
        CodeMapping.MERCHANT_MESSAGE_MAP.put(message, this);
        CodeMapping.MERCHANT_CODE_MAP.put(code, this);
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getFormattedMessage(Object... args) {
        return String.format(message, args);
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public static MerchantResponseCode getByMessage(String message) {
        MerchantResponseCode merchantResponseCode = CodeMapping.MERCHANT_MESSAGE_MAP.get(message);
        if (Objects.nonNull(merchantResponseCode)) {
            return merchantResponseCode;
        }
        return MerchantResponseCode.INTERNAL_SERVER_ERROR;
    }

    public static MerchantResponseCode getByCode(String code) {
        MerchantResponseCode merchantResponseCode = CodeMapping.MERCHANT_CODE_MAP.get(code);
        if (Objects.nonNull(merchantResponseCode)) {
            return merchantResponseCode;
        }
        return MerchantResponseCode.INTERNAL_SERVER_ERROR;
    }

    private static class CodeMapping {
        static Map<String, MerchantResponseCode> MERCHANT_MESSAGE_MAP = new HashMap<>();
        static Map<String, MerchantResponseCode> MERCHANT_CODE_MAP = new HashMap<>();
    }

    public static MerchantResponseCode getByMessage(String message, String args) {
        return Arrays.stream(MerchantResponseCode.values())
                .filter(respCode -> respCode.getFormattedMessage(args).equals(message))
                .findFirst()
                .orElse(MerchantResponseCode.INTERNAL_SERVER_ERROR);
    }

    public static MerchantResponseCode getByMessageV2(String message) {
        return Arrays.stream(MerchantResponseCode.values())
                .filter(respCode -> respCode.getMessage().equals(message))
                .findFirst()
                .orElse(MerchantResponseCode.INTERNAL_SERVER_ERROR);
    }
}
