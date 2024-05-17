package com.freewayemi.merchant.commons.utils;

import java.util.Arrays;
import java.util.List;

public class paymentConstants {
    public static final float INSURANCE = .0099f;
    public static final float GST_MUL = .18f;

    public static final String HMAC_SHA256 = "HmacSHA256";
    public static final String SHA_256 = "SHA-256";
    public static final Float IRR = 14.0f;
    public static final Float GST = 18.0f;
    public static final String PRODENV = "prod";
    public static final String CREDIT = "CREDIT";
    public static final String DEBIT = "DEBIT";
    public static final String UPI = "UPI";
    public static final String CARDLESS = "CARDLESS";
    public static final String BNPL = "BNPL";
    public static final String NTB = "NTB";
    public static final String PAN = "PAN";
    public static final String DOB = "DOB";
    public static final String DC = "DC";
    public static final String OFFLINE = "offline";
    public static final String ONLINE = "online";
    public static final String failureStatusMessage = "Unable to complete transaction. Please try again.";
    public static final String failureProlongedProcessing = "Failed after prolonged processing state";
    public static final String debitFailureStatusMessage =
            "The debit card used is currently not supported for EMI. Please retry with credit card.";
    public static final String HDFC_TXN_LIMIT_FAILURE_MSG =
            "Transaction amount not with min and max transaction limit of HDFC";
    public static final String HDFC_ELIGIBILITY_MOBILE_AND_CARD_NO_FAILURE_MSG =
            "Card number entered did not match with your bank records";
    public static final String INCORRECT_OTP = "Invalid OTP entered";
    public static final String MAX_OTP_RETRY_ATTEMPT_REACHED = "User reached max OTP retry attempt";
    public static final String INTERNAL_SERVER_ERROR = "Internal server error occurred";

    public static final String AUTHUSER_MERCHANT = "merchant";
    public static final String STORE_USER = "storeuser";
    public static final String AUTHUSER_STORE_USER = "STORE_USER";
    public static final String CLICKWRAP = "clickwrap";
    public static final String AUTHUSER_PG_MERCHANT = "pgmerchant";
    public static final String AUTHUSER_BRAND_MERCHANT = "brandmerchant";
    public static final String AUTHUSER_CONSUMER = "consumer";
    public static final String MOBILE_CHANGE = "mobile-change";

    public static final String payment_WEB_URL = "/web/pay.html";
    public static final String payment_BUY_URL = "/web/buy.html";
    public static final String payment_STORE_URL = "/sspay/#/payment/store";
    public static final String payment_NA_URL = "/web/notactive.html";
    public static final String payment_ELIGIBILITY_URL = "/sspay/#/eligibility/eligibility";
    public static final String payment_ELIGIBILITY_SANGEETHA_URL =
            "/sspay/#/eligibility/sangeetha-mobiles-instant-emi-eligibility";
    public static final String payment_ELIGIBILITY_SANGEETHA_V2_URL =
            "/sspay/#/eligibility/v2-sangeetha-mobiles-instant-emi-eligibility";
    public static final String payment_ELIGIBILITY_REALME_URL = "/sspay/#/eligibility/realme-no-cost-emi-eligibility";
    public static final String payment_ELIGIBILITY_REALME_V2_URL =
            "/sspay/#/eligibility/v2-realme-no-cost-emi-eligibility";

    public static final String DUMMY_UPI_PIN = "123456";

    public static final String SUCCESS = "SUCCESS";
    public static final String PENDING = "PENDING";
    public static final String FAILURE = "FAILURE";
    public static final String TRUE = "TRUE";
    public static final String STATUS_ACTIVE = "active";

    public static final String VAULT_RETOKENIZE = "/api/v1/retokenize";
    public static final String VAULT_DETOKENIZE = "/api/v1/detokenize";
    public static final String VAULT_TOKENIZE = "/api/v1/tokenize";

    public static final String PAYMENTMS_INTERNAL_REDIRECT_URL = "/payment/provider/";
    public static final String DPMS_INTERNAL_REDIRECT_URL = "/upi/provider/";
    public static final String ISG_PG_PAYMENT_RETURN_URL = "/payment/callback/isgpg/";
    public static final String PAYMENT_RETURN_URL = "/payment/callback/";
    public static final String RAZORPAY_EMI_PAYMENT_RETURN_URL = "/payment/callback/razorpay/";
    public static final String CASHFREE_PG_PAYMENT_RETURN_URL =
            "/payment/callback/cashfree/%s?order_id={order_id}&order_token={order_token}";
    public static final String LYRA_PG_WEBHOOK_URL = "/payment/webhook/%s/%s";
    public static final String LYRA_PG_RETURN_URL = "/payment/callback/%s/%s";
    public static final String HDFC_NTB_RETURN_URL = "/payment/callback/hdfcntb";

    public static final List<String> SALES_REFERRAL_CODE =
            Arrays.asList("SS51283", "SS86056", "SS48402", "SS14233", "SS56909", "SS91891", "SS44148", "SS15837",
                    "SS13195", "SS18884", "SS19256", "SS49339", "SS50008", "SS48653", "SS94264", "SS69651", "SS86219",
                    "SS17845", "SS55981", "SS46059", "SS97017", "SS69118", "SS97927", "SS32000", "SS39176", "SS98246",
                    "SS27706", "SS96874", "SS42387", "SS59367", "SS22586", "SS33496", "SS65128", "SS66810", "SS80727",
                    "SS30053", "SS66913", "SS13723", "SS17457", "SS30290", "SS28479", "SS99860", "SS83500", "SS59213",
                    "SS46620", "SS48283", "SS17388", "SS45409", "SS98360", "SS23514", "SS47418", "SS25243", "SS59962",
                    "SS91703", "SS59468", "SS78656", "SS29856", "SS76770", "SS25741", "SS41061", "SS20989", "SS45134",
                    "SS16084", "SS70919", "SS77337", "SS55658", "SS47903", "SS27986", "SS81349", "SS20742", "SS32912",
                    "SS32927", "SS63961", "SS59104", "SS38932", "SS65017", "SS46463", "SS20727", "SS12821", "SS21000",
                    "SS83994", "SS49702", "SS56202", "SS74805", "SS33744", "SS61242", "SS19156", "SS24816", "SS37005",
                    "SS55016", "SS28247", "SS92611", "SS33185", "SS58136", "SS74388", "SS34754", "SS75568", "SS67616",
                    "SS92864", "SS47035", "1283", "6056", "8402", "4233", "6909", "1891", "4148", "5837", "3195",
                    "8884", "9256", "9339", "0008", "8653", "4264", "9651", "6219", "7845", "5981", "6059", "7017",
                    "9118", "7927", "2000", "9176", "8246", "7706", "ONFY", "TASKMO", "OKAYA", "TOXMO");


    public static String E1 = "Payment declined";
    public static String CE1 = "Your bank has declined this transaction. " +
            "Please ensure your card is active and has sufficient limit to perform this transaction.";
    public static String ME1 = "Transaction declined by bank. Insufficient limit or card inactive";

    public static String E2 =
            "Payment processing declined by card issuing bank. " + "Please contact issuing bank to determine reason.";
    public static String CE2 =
            "Your bank is rejecting this transaction. " + "Please ensure you have entered the correct card details. " +
                    "If the problem persists, please speak to your bank representative.";
    public static String ME2 = "Payment declined by Bank. Insufficient limit on card or transaction denied by risk. " +
            "Customer to contact bank for further resolution.";

    public static String E3 = "Payment processing failed due to error at bank or wallet gateway";
    public static String CE3 = "Your bank declined the transaction. " + "Did you cancel the transaction?";
    public static String ME3 = "Transaction cancelled by the user on bank OTP page.";

    public static String E4 = "Payment processing failed due to insufficient balance";
    public static String CE4 = "You do not have sufficient limit in your account to complete the payment. " +
            "Please complete payment using another method.";
    public static String ME4 = "Insufficient funds.";

    public static String E5 = "Payment failed because cardholder couldn't be authenticated";
    public static String CE5 =
            "Your bank has declined the transaction. " + "Please ensure you enter the correct OTP for authentication.";
    public static String ME5 = "Transaction declined by Bank because cardholder couldn't be authenticated.";

    public static String E6 = "Payment declined by bank";
    public static String CE6 =
            "Your bank is rejecting this transaction. " + "Please ensure you have entered the correct card details. " +
                    "If the problem persists, please speak to your bank representative.";
    public static String ME6 = "Payment declined by Bank. Insufficient limit on card or transaction denied by risk. " +
            "Customer to contact bank for further resolution.";

    public static String E7 = "Card declined by bank";
    public static String CE7 =
            "Your bank is rejecting this transaction. " + "Please ensure you have entered the correct card details. " +
                    "If the problem persists, please speak to your bank representative.";
    public static String ME7 = "Payment declined by Bank. Insufficient limit on card or transaction denied by risk. " +
            "Customer to contact bank for further resolution.";

    public static String E8 = "3D Secure authentication failed";
    public static String CE8 = "Your bank declined the transaction. Did you cancel the transaction?";
    public static String ME8 = "Transaction cancelled by the user on bank OTP page.";

    public static final String ZERO = "0";

    public static final String payment_IV_KEY = "ae296351df286a9c28f7e69b75f6a35262add05d6eaf9a0ee3034dcc8e541fef";
    public static final String HEX = "HEX";

    public static final boolean HIDE_PRICING_PROPOSAL = false;

    public static final String DIGILOCKER_REDIRECT_APP_URL = "/ntbcore/digilocker/redirect/salesapp";
    public static final String DIGILOCKER_REDIRECT_URL = "/ntbcore/digilocker/redirect";

    public static final String payment_STORE_URL_V2 = "/cv2/#/qr/";
    public static final String payment_STORE_URL_V3 = "/cv3/#/qr/";
    public static final String payment_WEB_URL_V2 = "/cv2/#/transaction/";
    public static final String payment_ELIGIBILITY_URL_V2 = "/cv2/#/eligibility";
    public static final String payment_WEB_URL_V3 = "/cv3/#/transaction/";
    public static final String payment_ELIGIBILITY_URL_V3 = "/cv3/#/eligibility";
    public static final String payment_EDU_URL_V1 = "/ev1/#/txn/%s/login";


    public static final String ALL = "ALL";

    public static final String payment_NO_COST_EMI = "No-Cost EMI";
    public static final String PARTNER_SALES = "PARTNER_SALES";
    public static final String OWNER = "OWNER";
    public static final String SALES = "SALES";
    public static final String ONBOARDING_STEP1 = "Lead Creation";
    public static final String ONBOARDING_STEP2 = "payment Business App Registration";
    public static final String ONBOARDING_STEP3 = "KYC";
    public static final String ONBOARDING_STEP4 = "Settlement Account setup";
    public static final String ONBOARDING_STEP5 = "Store Details";
    public static final String ONBOARDING_STEP6 = "Commercial Setup";
    public static final String ONBOARDING_STEP7 = "QR Activation";
    public static final String ONBOARDING_STEP8 = "Transaction Activation";
    public static final String ONBOARDING = "ONBOARDING";

    public static final String ONBOARDING_STAGE_0 = "leadcreated";
    public static final String ONBOARDING_STAGE_1 = "created";
    public static final String ONBOARDING_STAGE_2 = "address";
    public static final String ONBOARDING_STAGE_3 = "pan";
    public static final String ONBOARDING_STAGE_4 = "kyc";
    public static final String ONBOARDING_STAGE_5 = "account";
    public static final String ONBOARDING_STAGE_6 = "storeDetails";
    public static final String ONBOARDING_STAGE_7 = "commercialsV1";
    public static final String NO_COST_EMI = "noCostEmi";
    public static final String STANDARD_EMI = "standardEmi";
    public static final String CONVENIENCE_FEE = "convenienceFee";
    public static final String DEFAULT_SCHEME_PARTNER = "payment";
    public static final String VIVO_HDFC_SCHEME_PARTNER = "VIVO-HDFC";
    public static final String ONBOARDING_STAGE_8 = "qrActivation";
}
