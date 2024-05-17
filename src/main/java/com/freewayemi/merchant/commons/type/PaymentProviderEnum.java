package com.freewayemi.merchant.commons.type;

public enum PaymentProviderEnum {
    //enum name to be in small case to be in consistent with database.
    hdfc("hdfc"),
    axis("axis"),
    juspay("juspay"),
    icici("icici"),
    bob("bob"),
    mock("mock"),
    none("none"),
    axisupi("axisupi"),
    mockupi("mockupi"),
    isgpg("isgpg"),
    kotak("kotak"),
    juspaypg("juspaypg"),
    tpslemipg("tpslemipg"),
    axispg("axispg"),
    mockcl("mockcl"),
    icicicardless("icicicardless"),
    kotakmock("kotakmock"),
    hdfcpg("hdfcpg"),
    yesbankupi("yesbankupi"),
    icicikyccardless("icicikyccardless"),
    flexipay("flexipay"),
    idfc("idfc"),
    mockicicikyccardless("mockicicikyccardless"),
    iifl("iifl"),
    razorpayemipg("razorpayemipg"),
    yesbankupipayout("yesbankupipayout"),
    mockicicidc("mockicicidc"),
    mockaxisdc("mockaxisdc"),
    cashfreepg("cashfreepg"),
    hdfcntb("hdfcntb"),
    kotakntb("kotakntb"),
    mockflexipay("mockflexipay"),
    lyrapg("lyrapg"),
    easebuzzpg("easebuzzpg"),
    paymentmockpg("paymentmockpg"),
    razorpayupi("razorpayupi"),
    ccavenuepg("ccavenuepg"),
    ccavenueemipg("ccavenueemipg"),
    icicintb("icicintb"),
    dmintb("dmintb"),
    pelntb("pelntb"),
    cashfreeemipg("cashfreeemipg"),
    cashfreeupi("cashfreeupi"),
    ;

    PaymentProviderEnum(String displayName) {
        this.displayName = displayName;
    }

    private final String displayName;

    public String getDisplayName() {
        return displayName;
    }

    public static PaymentProviderEnum getProviderEnumByBankCode(String bankCode, CardTypeEnum cardType) {
        switch (bankCode) {
            case "UTIB":
                return PaymentProviderEnum.axis;
            case "HDFC":
            case "CL_HDFC":
                return PaymentProviderEnum.hdfc;
            case "ICIC":
                return PaymentProviderEnum.icici;
            case "KKBK":
                switch (cardType.getCardType()) {
                    case "DEBIT":
                    case "CARDLESS":
                        return PaymentProviderEnum.kotak;
                    case "NTB":
                        return PaymentProviderEnum.kotakntb;
                }
            case "IDFC":
                return PaymentProviderEnum.idfc;
            case "CL_ICICI":
                return PaymentProviderEnum.icicicardless;
            case "CL_ICICI_KYC":
                return PaymentProviderEnum.icicikyccardless;
            case "BNPL_HDFC":
                return PaymentProviderEnum.flexipay;
            case "IIFL":
                return PaymentProviderEnum.iifl;
            case "HDFC_NTB":
                return PaymentProviderEnum.hdfcntb;
            case "DMI_NTB":
                return PaymentProviderEnum.dmintb;
            case "PEL_NTB":
                return PaymentProviderEnum.pelntb;
            default:
                return PaymentProviderEnum.none;
        }
    }
}
