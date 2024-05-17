package com.freewayemi.merchant.commons.type;

import java.util.Arrays;
import java.util.Optional;

public enum NTBLoanStatuses {

    CREATED, HUNTER_MATCH_REJECTED, CIBIL_INPROGRESS, CIBIL_REJECTED, CIBIL_FAILURE,
    CIBIL_SUCCESS, KYC_INITIATED, KYC_SUCCESS, KYC_REJECTED, SELFIE_UPLOAD, AGREEMENT_SIGNED,
    DISBURSAL_INITIATED, DISBURSAL_COMPLETED, SANCTION_SUCCESS, SANCTION_FAILED,
    PERFIOS_INITIATED, PERFIOS_FILE_PROCESSED, PERFIOS_CANCELLED, PERFIOS_REJECTED,
    ACCOUNT_INITIATED, ACCOUNT_VERIFIED, ACCOUNT_VERIFICATION_FAILED, LOAN_CANCELLED, CLICKWRAP_INITIATED,
    REFUND_INITIATED, REFUND_COMPLETED, REFUND_FAILED, SOFT_APPROVED, SUCCESS, EMANDATE_INITIATED, EMANDATE_SUCCESS, EMANDATE_FAILED, LOAN_REJECTED, LOAN_EXPIRED, payment_REJECTED;

    public static boolean isRejected(String status, String provider) {
        if (NtbProviderEnum.KKBK.name().equalsIgnoreCase(provider)) {
            return NTBLoanStatuses.CIBIL_REJECTED.name().equalsIgnoreCase(status)
                    || NTBLoanStatuses.PERFIOS_REJECTED.name().equalsIgnoreCase(status)
                    || NTBLoanStatuses.CIBIL_FAILURE.name().equalsIgnoreCase(status)
                    || NTBLoanStatuses.LOAN_REJECTED.name().equalsIgnoreCase(status)
                    || NTBLoanStatuses.KYC_REJECTED.name().equalsIgnoreCase(status)
                    || NTBLoanStatuses.payment_REJECTED.name().equalsIgnoreCase(status);
        }
        return NTBLoanStatuses.CIBIL_REJECTED.name().equalsIgnoreCase(status)
                || NTBLoanStatuses.PERFIOS_REJECTED.name().equalsIgnoreCase(status) || NTBLoanStatuses.HUNTER_MATCH_REJECTED.name().equalsIgnoreCase(status)
                || NTBLoanStatuses.KYC_REJECTED.name().equalsIgnoreCase(status) || NTBLoanStatuses.CIBIL_FAILURE.name().equalsIgnoreCase(status) ||
                NTBLoanStatuses.LOAN_EXPIRED.name().equalsIgnoreCase(status) || NTBLoanStatuses.payment_REJECTED.name().equalsIgnoreCase(status);
    }

    public static boolean isApproved(String status, String provider) {
        if (NtbProviderEnum.KKBK.name().equalsIgnoreCase(provider)) {
            return NTBLoanStatuses.EMANDATE_SUCCESS.name().equalsIgnoreCase(status)
                    || NTBLoanStatuses.DISBURSAL_INITIATED.name().equalsIgnoreCase(status) || NTBLoanStatuses.DISBURSAL_COMPLETED.name().equalsIgnoreCase(status);
        }
        return NTBLoanStatuses.EMANDATE_SUCCESS.name().equalsIgnoreCase(status) || NTBLoanStatuses.AGREEMENT_SIGNED.name().equalsIgnoreCase(status)
                || NTBLoanStatuses.SANCTION_SUCCESS.name().equalsIgnoreCase(status)
                || NTBLoanStatuses.SANCTION_FAILED.name().equalsIgnoreCase(status) || NTBLoanStatuses.CLICKWRAP_INITIATED.name().equalsIgnoreCase(status)
                || NTBLoanStatuses.DISBURSAL_INITIATED.name().equalsIgnoreCase(status);
    }

    public static boolean isPreApproved(String status, String provider) {
        if (NtbProviderEnum.KKBK.name().equalsIgnoreCase(provider)) {
            return NTBLoanStatuses.EMANDATE_FAILED.name().equalsIgnoreCase(status)
                    || NTBLoanStatuses.ACCOUNT_VERIFIED.name().equalsIgnoreCase(status) || NTBLoanStatuses.ACCOUNT_VERIFICATION_FAILED.name().equalsIgnoreCase(status)
                    || NTBLoanStatuses.AGREEMENT_SIGNED.name().equalsIgnoreCase(status);
        }
        return NTBLoanStatuses.SOFT_APPROVED.name().equalsIgnoreCase(status) || NTBLoanStatuses.KYC_INITIATED.name().equalsIgnoreCase(status)
                || NTBLoanStatuses.KYC_SUCCESS.name().equalsIgnoreCase(status) || NTBLoanStatuses.EMANDATE_FAILED.name().equalsIgnoreCase(status)
                || NTBLoanStatuses.ACCOUNT_VERIFIED.name().equalsIgnoreCase(status) || NTBLoanStatuses.ACCOUNT_VERIFICATION_FAILED.name().equalsIgnoreCase(status);
    }

    public static Optional<NTBLoanStatuses> getValue(String value) {
        return Arrays.stream(values()).filter(e -> e.name().equalsIgnoreCase(value)).findAny();
    }

    public static boolean isLoanDisbursed(String status) {
        return NTBLoanStatuses.DISBURSAL_COMPLETED.name().equalsIgnoreCase(status) || NTBLoanStatuses.DISBURSAL_INITIATED.name().equalsIgnoreCase(status);
    }

    public static boolean isCibilRejected(String loanStatus, String providerName) {
        return Arrays.asList(NTBLoanStatuses.CIBIL_FAILURE.name(), NTBLoanStatuses.CIBIL_REJECTED.name()).contains(loanStatus) || isLoanRejected(loanStatus, providerName);
    }

    public static boolean isLoanRejected(String loanStatus, String providerName) {
        return Arrays.asList(NTBLoanStatuses.LOAN_REJECTED.name(), NTBLoanStatuses.LOAN_EXPIRED.name(),
                NTBLoanStatuses.LOAN_CANCELLED.name(), NTBLoanStatuses.payment_REJECTED.name()).contains(loanStatus);
    }

    public static boolean isLoanRefunded(String loanStatus) {
        return Arrays.asList(NTBLoanStatuses.REFUND_INITIATED.name(), NTBLoanStatuses.REFUND_COMPLETED.name()).contains(loanStatus);
    }

    public static Boolean isLoanExpired(String loanStatus, String providerName) {
        return Arrays.asList(NTBLoanStatuses.LOAN_EXPIRED.name()).contains(loanStatus);
    }

    public static boolean isLoanRejectedForTxn(String loanStatus, String providerName) {
        return isLoanRejected(loanStatus, providerName) ||
                isCibilRejected(loanStatus, providerName) ||
                isKycRejected(loanStatus, providerName) ||
                isEmandateFailed(loanStatus, providerName);
    }

    public static boolean isEmandateFailed(String loanStatus, String providerName) {
        return Arrays.asList(NTBLoanStatuses.EMANDATE_FAILED.name(), NTBLoanStatuses.ACCOUNT_VERIFICATION_FAILED.name()).contains(loanStatus) || isLoanRejected(loanStatus, providerName);
    }

    private static boolean isKycRejected(String loanStatus, String providerName) {
        return Arrays.asList(NTBLoanStatuses.KYC_REJECTED.name()).contains(loanStatus) || isLoanRejected(loanStatus, providerName);
    }
}
