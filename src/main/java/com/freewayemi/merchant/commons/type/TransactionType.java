package com.freewayemi.merchant.commons.type;

public enum TransactionType {

    SALE("SALE"),
    REFUND("REFUND"),
    CASHBACK("Cashback"),
    WEEKLY_WINNER("Weekly Winner"),
    REVERSAL("Reversal"),
    REFUND_PAYOUT("Refund Payout");

    private final String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
