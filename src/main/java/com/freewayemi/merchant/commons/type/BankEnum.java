package com.freewayemi.merchant.commons.type;

import org.apache.commons.lang.StringUtils;

public enum BankEnum {

    AMEX("AMEX", "American Express", "AMEX", null, null),
    BARB("BARB", "Bank of Baroda", "BOB", null, null),
    BOB_FIN_SOL("BARB", "Bob Financial Solutions", "BOB", null, null),
    CITI("CITI", "Citi Bank", "CITI", null, null),
    HDFC("HDFC", "HDFC Bank", "HDFC", "5676712", "MYHDFC"),
    ICIC("ICIC", "ICICI Bank", "ICICI", "5676766", "DCEMI %s"),
    INDB("INDB", "IndusInd Bank", "INDUSIND", null, null),
    KKBK("KKBK", "Kotak Mahindra Bank", "KOTAK", "5676788", "DCEMI %s"),
    RATN("RATN", "RBL Bank", "RBL", null, null),
    SBIN("SBIN", "SBI", "SBI", "567676", "DCEMI"),
    SCBL("SCBL", "Standard Chartered Bank", "SCB", null, null),
    UTIB("UTIB", "Axis Bank", "AXIS", "5676782", "DCEMI %s"),
    YESB("YESB", "YES Bank", "YES", null, null),
    CL_ICICI("CL_ICICI", "ICICI Cardless Bank", "", null, null),
    HSBC("HSBC", "HSBC Bank", "HSBC", null, null),
    IDFC("IDFC", "IDFC Bank", "IDFC", null, null),
    CL_ICICI_KYC("CL_ICICI_KYC", "ICICI Kyc Cardless Bank", "", null, null),
    BNPL_HDFC("BNPL_HDFC", "HDFC Bank", "HDFC", "", ""),
    IIFL("IIFL", "IIFL Finance", "IIFL", "", ""),
    AUFB("AUFB", "AU Small Finance Bank", "", "", ""),
    HDFC_NTB("HDFC_NTB", "HDFC Bank Easy EMI", "", "", ""),
    ONECARD("ONECARD", "One Card", "", "", ""),
    ICICI_NTB("ICICI_NTB", "ICICI NTB", "", null, null),
    DMI_NTB("DMI_NTB", "DMI Finance", "", null, null),
    PEL_NTB("PEL_NTB", "Piramal Finance", "", null, null);

    private final String code;
    private final String bankName;
    private final String juspayEmiBank;
    private final String debitEmiNumber;
    private final String debitEmiText;

    BankEnum(String code, String bankName, String juspayEmiBank, String debitEmiNumber, String debitEmiText) {
        this.code = code;
        this.bankName = bankName;
        this.juspayEmiBank = juspayEmiBank;
        this.debitEmiNumber = debitEmiNumber;
        this.debitEmiText = debitEmiText;
    }

    public static String getPendingEligibilityText(String bankCode, String maskedNumber) {
        String last4 = maskedNumber.substring(maskedNumber.length() - 4);
        for (BankEnum be : BankEnum.values()) {
            if (be.getCode().equals(bankCode)) {
                if (null != be.getDebitEmiText()) {
                    return String.format(be.debitEmiText, last4);
                }
            }
        }
        return null;
    }

    public static String getPendingEligibilityTarget(String bankCode) {
        for (BankEnum be : BankEnum.values()) {
            if (be.getCode().equals(bankCode)) {
                return be.getDebitEmiNumber();
            }
        }
        return null;
    }

    public static String getBankCode(String bankName) {
        for (BankEnum be : BankEnum.values()) {
            if (StringUtils.isNotBlank(bankName) && (be.getBankName().equalsIgnoreCase(bankName)) ||
                    be.getBankName().contains(bankName)) {
                return be.getCode();
            }
        }
        if (bankName.equals("AMEX")) {
            return BankEnum.AMEX.getCode();
        }
        return null;
    }

    public static String getJuspayBankCode(String bankCode) {
        for (BankEnum be : BankEnum.values()) {
            if (be.getCode().equals(bankCode)) {
                return be.getJuspayEmiBank();
            }
        }
        return null;
    }

    public static BankEnum getCode(String code) {
        for (BankEnum be : BankEnum.values()) {
            if (StringUtils.isNotBlank(code) && be.getCode().equals(code)) {
                return be;
            }
        }
        return null;
    }

    public String getBankName() {
        return bankName;
    }

    public String getCode() {
        return code;
    }

    public String getJuspayEmiBank() {
        return juspayEmiBank;
    }

    public String getDebitEmiNumber() {
        return debitEmiNumber;
    }

    public String getDebitEmiText() {
        return debitEmiText;
    }

    public static String getBankNameFromCode(String code) {
        for (BankEnum bankEnum : BankEnum.values()) {
            if (bankEnum.getCode().equals(code)) {
                return bankEnum.getBankName();
            }
        }
        return "";
    }

    public static String getStandardBankNameFromCode(String code) {
        for (BankEnum bankEnum : BankEnum.values()) {
            if (bankEnum.getCode().equals(code)) {
                if (BankEnum.CL_ICICI.getCode().equals(code) || BankEnum.CL_ICICI_KYC.getCode().equals(code)) {
                    return BankEnum.ICIC.getBankName();
                }
                return bankEnum.getBankName();
            }
        }
        return "";
    }
}
