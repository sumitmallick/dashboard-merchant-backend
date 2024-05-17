package com.freewayemi.merchant.enums;

public enum EligibilityStatus {
    PRE_APPROVED,
    /* PRE_APPROVED status is for the repeat users who have already
    taken a loan from the DMI via payment and have pre-defined remaining limit*/

    SOFT_APPROVED,
    /*
     * SOFT_APPROVED status is for the users who have received the limit
     * from Lender while eligibility Check/ or previously in the journey
     * */
}