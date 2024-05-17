package com.freewayemi.merchant.commons.type;

public enum SettlementCycleEnum {
    // payment Merchant Settlement Cycle id (SSMSCID)
    TRANSACTION("SSMSCID001", "T", "On transaction day"),
    TRANSACTION_PLUS_ONE("SSMSCID002", "TPlus1", "On transaction plus one day"),
    STANDARD("SSMSCID003", "S", "On next settlement cycle");

    private final String code;

    SettlementCycleEnum(String code, String displayMsg, String description) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static SettlementCycleEnum getByCode(String code) {
        for (SettlementCycleEnum settlementCycleEnum : SettlementCycleEnum.values()) {
            if (settlementCycleEnum.getCode().equals(code)) {
                return settlementCycleEnum;
            }
        }
        return SettlementCycleEnum.STANDARD;
    }

}
