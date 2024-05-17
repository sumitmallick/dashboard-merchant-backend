package com.freewayemi.merchant.commons.type;

public enum EmiTenureEnum {
    THREE(3, 90),
    FOUR(4, 120),
    SIX(6, 180),
    EIGHT(8, 240),
    NINE(9, 270),
    TWELVE(12, 360),
    FIFTEEN(15, 450),
    EIGHTEEN(18, 540),
    TWENTY(20, 600),
    TWENTYFOUR(24, 720),
    THIRTY_M(30, 900),
    THIRTYSIX(36, 1080),
    FIFTEEN_DAY(0, 15),
    THIRTY(1, 30),
    SIXTY(2, 60),
    NINETY(3, 90);

    private final Integer month;
    private final Integer days;

    EmiTenureEnum(Integer month, Integer days) {
        this.month = month;
        this.days = days;
    }

    public static EmiTenureEnum getEmiTenureEnum(Integer tenure) {
        for (EmiTenureEnum emiTenureEnum : EmiTenureEnum.values()) {
            if (emiTenureEnum.getMonth().equals(tenure)) {
                return emiTenureEnum;
            }
        }
        return null;
    }

    public Integer getMonth() {
        return month;
    }

    public Integer getDays() {
        return days;
    }
}
