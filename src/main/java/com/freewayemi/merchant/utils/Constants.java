package com.freewayemi.merchant.utils;

import com.freewayemi.merchant.commons.dto.Mdr;
import com.freewayemi.merchant.commons.type.BankEnum;
import com.freewayemi.merchant.commons.type.CardTypeEnum;

import java.util.*;

public class Constants {
    public static final Map<String, String> STATUS_MAP = new HashMap<String, String>() {{
        put("dropped", "Dropped");
        put("profiled", "Processing");
        put("registered", "Registered");
        put("approved", "Onboarding Pending");
        put("onboarded", "Onboarded");
        put("resubmission", "Re-submit");
        put("rejected", "Rejected");
        put("leadcreated", "Lead Created");
    }};

    public static final Map<String, Map<String, Object>> RESUBMISSION_ACTIONS_DETAILS =
            new HashMap<String, Map<String, Object>>() {{
                put("REUPLOAD_STORE_PHOTO", new HashMap<String, Object>() {{
                    put("tag", "Re-upload");
                    put("isFileUpload", Boolean.FALSE);
                }});
                put("ACCOUNT_NO_CHANGE", new HashMap<String, Object>() {{
                    put("tag", "Re-update");
                }});
                put("REUPLOAD_KYC", new HashMap<String, Object>() {{
                    put("tag", "Re-upload");
                    put("isFileUpload", Boolean.TRUE);
                    put("docTypes", Arrays.asList("Aadhaar", "Passport", "Voter Id", "Driving Licence"));
                }});
                put("REUPLOAD_ACCOUNT", new HashMap<String, Object>() {{
                    put("tag", "Re-upload");
                    put("isFileUpload", Boolean.TRUE);
                    put("docTypes", Arrays.asList("Cancelled Cheque", "Bank Statement"));
                }});
                put("REUPLOAD_MERCHANDISE", new HashMap<String, Object>() {{
                    put("tag", "Re-Upload");
                    put("isFileUpload", Boolean.FALSE);
                    put("merchandiseTypes", Arrays.asList("Standee", "Poster", "Danglers", "Stickers"));
                }});
                put("UPLOAD_ADDRESS_PROOF", new HashMap<String, Object>() {{
                    put("tag", "Re-Upload");
                    put("isFileUpload", Boolean.TRUE);
                }});
                put("UPLOAD_BUSINESS_DOCUMENTS", new HashMap<String, Object>() {{
                    put("tag", "No-Upload");
                    put("isFileUpload", Boolean.FALSE);
                }});
                put("ADD_WEBSITE", new HashMap<String, Object>() {{
                    put("tag", "No-Upload");
                    put("isFileUpload", Boolean.FALSE);
                }});
                put("ADD_MDR_SUBVENTION", new HashMap<String, Object>() {{
                    put("tag", "No-Upload");
                    put("isFileUpload", Boolean.FALSE);
                }});
                put("UPLOAD_BUSINESS_PROOF", new HashMap<String, Object>() {{
                    put("tag", "Re-upload");
                    put("isFileUpload", Boolean.TRUE);
                    put("docTypes", Arrays.asList("Govt. Proof of Business"));
                }});
            }};

    public static final Map<Integer, Double> MDR_RATES_MAP = new HashMap<Integer, Double>() {{
        put(3, 5.0);
        put(6, 7.0);
        put(9, 9.0);
        put(12, 10.0);
        put(18, 13.5);
        put(24, 16.5);
    }};

    public static final Map<Integer, Double> CC_SUBVENTIONS_RATES_MAP = new HashMap<Integer, Double>() {{
        put(3, 2.45);
        put(6, 4.23);
        put(9, 5.97);
        put(12, 7.67);
        put(18, 10.95);
        put(24, 14.07);
    }};
    public static final Map<Integer, Double> DC_SUBVENTIONS_RATES_MAP = new HashMap<Integer, Double>() {{
        put(3, 2.61);
        put(6, 4.51);
        put(9, 6.35);
        put(12, 8.14);
        put(18, 11.62);
        put(24, 14.90);
    }};

    public static final Map<String, List<Mdr>> DAIKIN_MDRS_SUBVENTIONS = new HashMap<String, List<Mdr>>() {{
        put("mdrs",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(1.9f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(1.85f).build()
                ));
        put("subventions",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(0.0f).build()
                ));
    }};

    public static final Map<String, List<Mdr>> REALME_MDRS_SUBVENTIONS = new HashMap<String, List<Mdr>>() {{
        put("mdrs",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(1.9f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(1.85f).build()
                ));
        put("subventions",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(0.0f).build()
                ));
    }};

    public static final Map<String, List<Mdr>> BLUESTAR_MDRS_SUBVENTIONS = new HashMap<String, List<Mdr>>() {{
        put("mdrs",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(1.9f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(1.85f).build()
                ));
        put("subventions",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(0.0f).build()
                ));
    }};

    public static final Map<String, List<Mdr>> ASUS_MDRS_SUBVENTIONS = new HashMap<String, List<Mdr>>() {{
        put("mdrs",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(1.85f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(1.85f).build()
                ));
        put("subventions",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(0.0f).build()
                ));
    }};

    public static final Map<String, List<Mdr>> UNITDEALS_MDRS_SUBVENTIONS = new HashMap<String, List<Mdr>>() {{
        put("mdrs",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(1.1f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(1.1f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(1.8f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(1.85f).build()
                ));
        put("subventions",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(0.0f).build()
                ));
    }};

    public static final Map<String, List<Mdr>> UTL_MDRS_SUBVENTIONS = new HashMap<String, List<Mdr>>() {{
        put("mdrs",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(1.5f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(1.5f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(2.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(1.85f).build()
                ));
        put("subventions",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(0.0f).build()
                ));
    }};

    public static final Map<String, List<Mdr>> LAKME_MDRS_SUBVENTIONS = new HashMap<String, List<Mdr>>() {{
        put("mdrs", Arrays.asList(Mdr.builder()
                        .cardType(CardTypeEnum.DEBIT.getCardType())
                        .bankCode(BankEnum.HDFC.getCode())
                        .tenure(3)
                        .rate(3.71f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.DEBIT.getCardType())
                        .bankCode(BankEnum.HDFC.getCode())
                        .tenure(6)
                        .rate(5.61f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.DEBIT.getCardType())
                        .bankCode(BankEnum.HDFC.getCode())
                        .tenure(9)
                        .rate(7.45f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.DEBIT.getCardType())
                        .bankCode(BankEnum.HDFC.getCode())
                        .tenure(12)
                        .rate(9.25f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.DEBIT.getCardType())
                        .bankCode(BankEnum.IDFC.getCode())
                        .tenure(3)
                        .rate(3.71f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.DEBIT.getCardType())
                        .bankCode(BankEnum.IDFC.getCode())
                        .tenure(6)
                        .rate(5.61f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.DEBIT.getCardType())
                        .bankCode(BankEnum.IDFC.getCode())
                        .tenure(9)
                        .rate(7.45f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.DEBIT.getCardType())
                        .bankCode(BankEnum.IDFC.getCode())
                        .tenure(12)
                        .rate(9.25f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.DEBIT.getCardType())
                        .bankCode(BankEnum.UTIB.getCode())
                        .tenure(3)
                        .rate(3.39f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.DEBIT.getCardType())
                        .bankCode(BankEnum.UTIB.getCode())
                        .tenure(6)
                        .rate(5.06f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.DEBIT.getCardType())
                        .tenure(9)
                        .bankCode(BankEnum.UTIB.getCode())
                        .rate(7.45f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.DEBIT.getCardType())
                        .tenure(12)
                        .bankCode(BankEnum.UTIB.getCode())
                        .rate(9.25f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CARDLESS.getCardType())
                        .bankCode(BankEnum.CL_ICICI.getCode())
                        .tenure(3)
                        .rate(3.71f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CARDLESS.getCardType())
                        .bankCode(BankEnum.CL_ICICI.getCode())
                        .tenure(6)
                        .rate(5.61f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CARDLESS.getCardType())
                        .bankCode(BankEnum.CL_ICICI.getCode())
                        .tenure(9)
                        .rate(7.45f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CARDLESS.getCardType())
                        .bankCode(BankEnum.CL_ICICI.getCode())
                        .tenure(12)
                        .rate(9.25f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.DEBIT.getCardType())
                        .bankCode(BankEnum.KKBK.getCode())
                        .tenure(3)
                        .rate(4.03f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.DEBIT.getCardType())
                        .bankCode(BankEnum.KKBK.getCode())
                        .tenure(6)
                        .rate(6.15f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.DEBIT.getCardType())
                        .bankCode(BankEnum.KKBK.getCode())
                        .tenure(9)
                        .rate(8.21f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.DEBIT.getCardType())
                        .bankCode(BankEnum.KKBK.getCode())
                        .tenure(12)
                        .rate(10.2f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.AMEX.getCode())
                        .tenure(3)
                        .rate(3.87f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.AMEX.getCode())
                        .tenure(6)
                        .rate(5.31f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.AMEX.getCode())
                        .tenure(9)
                        .rate(6.72f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.AMEX.getCode())
                        .tenure(12)
                        .rate(8.11f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.BARB.getCode())
                        .tenure(3)
                        .rate(4.03f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.BARB.getCode())
                        .tenure(6)
                        .rate(5.58f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.BARB.getCode())
                        .tenure(9)
                        .rate(7.11f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.BARB.getCode())
                        .tenure(12)
                        .rate(8.6f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.CITI.getCode())
                        .tenure(3)
                        .rate(4.03f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.CITI.getCode())
                        .tenure(6)
                        .rate(5.58f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.CITI.getCode())
                        .tenure(9)
                        .rate(7.87f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.CITI.getCode())
                        .tenure(12)
                        .rate(9.57f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.HDFC.getCode())
                        .tenure(3)
                        .rate(4.35f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.HDFC.getCode())
                        .tenure(6)
                        .rate(6.13f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.HDFC.getCode())
                        .tenure(9)
                        .rate(7.87f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.HDFC.getCode())
                        .tenure(12)
                        .rate(9.57f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.HSBC.getCode())
                        .tenure(3)
                        .rate(3.95f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.HSBC.getCode())
                        .tenure(6)
                        .rate(5.45f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.HSBC.getCode())
                        .tenure(9)
                        .rate(7.3f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.HSBC.getCode())
                        .tenure(12)
                        .rate(8.85f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.ICIC.getCode())
                        .tenure(3)
                        .rate(4.03f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.ICIC.getCode())
                        .tenure(6)
                        .rate(5.86f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.ICIC.getCode())
                        .tenure(9)
                        .rate(7.49f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.ICIC.getCode())
                        .tenure(12)
                        .rate(9.09f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.INDB.getCode())
                        .tenure(3)
                        .rate(4.03f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.INDB.getCode())
                        .tenure(6)
                        .rate(5.58f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.INDB.getCode())
                        .tenure(9)
                        .rate(7.11f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.INDB.getCode())
                        .tenure(12)
                        .rate(8.11f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.KKBK.getCode())
                        .tenure(3)
                        .rate(3.87f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.KKBK.getCode())
                        .tenure(6)
                        .rate(5.31f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.KKBK.getCode())
                        .tenure(9)
                        .rate(7.49f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.KKBK.getCode())
                        .tenure(12)
                        .rate(9.09f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.RATN.getCode())
                        .tenure(3)
                        .rate(4.03f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.RATN.getCode())
                        .tenure(6)
                        .rate(5.86f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.RATN.getCode())
                        .tenure(9)
                        .rate(7.87f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.RATN.getCode())
                        .tenure(12)
                        .rate(9.57f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.SBIN.getCode())
                        .tenure(3)
                        .rate(4.19f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.SBIN.getCode())
                        .tenure(6)
                        .rate(5.86f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.SBIN.getCode())
                        .tenure(9)
                        .rate(7.49f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.SBIN.getCode())
                        .tenure(12)
                        .rate(9.09f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.SCBL.getCode())
                        .tenure(3)
                        .rate(4.03f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.SCBL.getCode())
                        .tenure(6)
                        .rate(5.58f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.SCBL.getCode())
                        .tenure(9)
                        .rate(7.49f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.SCBL.getCode())
                        .tenure(12)
                        .rate(9.09f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.UTIB.getCode())
                        .tenure(3)
                        .rate(4.03f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.UTIB.getCode())
                        .tenure(6)
                        .rate(5.58f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.UTIB.getCode())
                        .tenure(9)
                        .rate(7.49f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.UTIB.getCode())
                        .tenure(12)
                        .rate(9.09f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.YESB.getCode())
                        .tenure(3)
                        .rate(4.03f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.YESB.getCode())
                        .tenure(6)
                        .rate(5.58f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.YESB.getCode())
                        .tenure(9)
                        .rate(7.49f)
                        .build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.YESB.getCode())
                        .tenure(12)
                        .rate(9.09f)
                        .build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(1.1f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(1.9f).build(),
                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(1.85f).build()
        ));
        put("subventions",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().tenure(6).rate(0.0f).productId("6191f5e7b6583a5623d602d0").build(),
                        Mdr.builder().tenure(6).rate(0.0f).productId("619b489402f2dfa1864ed098").build(),
                        Mdr.builder().tenure(9).rate(0.0f).productId("6191f5e7b6583a5623d602d0").build(),
                        Mdr.builder().tenure(9).rate(0.0f).productId("619b489402f2dfa1864ed098").build(),
                        Mdr.builder().tenure(12).rate(0.0f).productId("6191f5e7b6583a5623d602d0").build(),
                        Mdr.builder().tenure(12).rate(0.0f).productId("619b489402f2dfa1864ed098").build(),
                        Mdr.builder().tenure(18).rate(0.0f).productId("6191f5e7b6583a5623d602d0").build(),
                        Mdr.builder().tenure(18).rate(0.0f).productId("619b489402f2dfa1864ed098").build(),
                        Mdr.builder().tenure(24).rate(0.0f).productId("6191f5e7b6583a5623d602d0").build(),
                        Mdr.builder().tenure(24).rate(0.0f).productId("619b489402f2dfa1864ed098").build(),
                        Mdr.builder().tenure(18).rate(0.0f).build(), Mdr.builder().tenure(24).rate(0.0f).build(),
                        Mdr.builder().tenure(-1).rate(9.1f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(0.0f).build()
                ));
    }};

    public static final Map<String, List<Mdr>> VOLTAS_MDRS_SUBVENTIONS = new HashMap<String, List<Mdr>>() {{
        put("mdrs",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(1.8f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(1.85f).build()
                ));
        put("subventions",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(0.0f).build()
                ));
    }};

    public static final Map<String, List<Mdr>> WHIRPOOL_MDRS_SUBVENTIONS = new HashMap<String, List<Mdr>>() {{
        put("mdrs",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(1.85f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(1.85f).build()
                ));
        put("subventions",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(0.0f).build()
                ));
    }};

    public static final Map<String, List<Mdr>> BOSCH_MDRS_SUBVENTIONS = new HashMap<String, List<Mdr>>() {{
        put("mdrs",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(1.85f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(1.85f).build()
                ));
        put("subventions",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(0.0f).build()
                ));
    }};

    public static final Map<String, List<Mdr>> BOSCH_OTHER_STORES_MDRS_SUBVENTIONS = new HashMap<String, List<Mdr>>() {{
        put("mdrs",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(1.85f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(1.85f).build()
                ));
        put("subventions",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(0.0f).build()
                ));
    }};

    public static final Map<String, List<Mdr>> MITSUBISHI_MDRS_SUBVENTIONS = new HashMap<String, List<Mdr>>() {{
        put("mdrs",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(1.7f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(1.85f).build()
                ));
        put("subventions",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(0.0f).build()
                ));
    }};

    public static final Map<String, List<Mdr>> TOP10_MDRS_SUBVENTIONS = new HashMap<String, List<Mdr>>() {{
        put("mdrs",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(0.9f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(0.9f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(1.2f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(1.85f).build()
                ));
        put("subventions",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(0.0f).build()
                ));
    }};

    public static final Map<String, List<Mdr>> MCCOY_INDIA_MDRS_SUBVENTIONS = new HashMap<String, List<Mdr>>() {{
        put("mdrs",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(1.5f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(1.5f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(2.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(1.85f).build()
                ));
        put("subventions",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(0.0f).build()
                ));
    }};

    public static final Map<String, List<Mdr>> NON_BRAND_MDRS_SUBVENTIONS = new HashMap<String, List<Mdr>>() {{
        put("mdrs",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(1.85f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(1.85f).build()
                ));
        put("subventions",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(0.0f).build()
                ));
    }};

    public static final Map<String, List<Mdr>> NON_BRAND_HEALTH_CARE_MDRS_SUBVENTIONS =
            new HashMap<String, List<Mdr>>() {{
                put("mdrs", Arrays.asList(
                        Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(3).rate(4.9f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(3).rate(4.9f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(3).rate(4.9f).build(),
                        Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(6).rate(6.9f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(6).rate(6.9f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(6).rate(6.9f).build(),
                        Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(9).rate(8.9f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(9).rate(8.9f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(9).rate(8.9f).build(),
                        Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(12).rate(9.9f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(12).rate(9.9f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(12).rate(9.9f).build(),
                        Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(1.85f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(1.85f).build()
                ));
                put("subventions", Arrays.asList(
                        Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(3).rate(2.61f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(3).rate(2.61f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(3).rate(2.45f).build(),
                        Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(6).rate(4.51f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(6).rate(4.51f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(6).rate(4.23f).build(),
                        Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(9).rate(6.35f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(9).rate(6.35f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(9).rate(5.97f).build(),
                        Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(12).rate(8.15f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(12).rate(8.15f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(12).rate(7.67f).build(),
                        Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(0.0f).build()
                ));
            }};

    public static final Map<String, List<Mdr>> KHAITAN_SOLAR_MDRS_SUBVENTIONS =
            new HashMap<String, List<Mdr>>() {{
                put("mdrs", Arrays.asList(
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("AMEX").tenure(3).rate(4.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("AMEX").tenure(6).rate(6.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("AMEX").tenure(9).rate(8.3f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("AMEX").tenure(12).rate(10f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("AMEX").tenure(18).rate(13.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("BARB").tenure(3).rate(4.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("BARB").tenure(6).rate(6.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("BARB").tenure(9).rate(8.3f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("BARB").tenure(12).rate(10f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("BARB").tenure(18).rate(13.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("CITI").tenure(3).rate(4.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("CITI").tenure(6).rate(6.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("CITI").tenure(9).rate(8.3f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("CITI").tenure(12).rate(10f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("CITI").tenure(18).rate(13.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("HSBC").tenure(3).rate(4.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("HSBC").tenure(6).rate(6.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("HSBC").tenure(9).rate(8.3f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("HSBC").tenure(12).rate(10f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("HSBC").tenure(18).rate(13.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("ICIC").tenure(3).rate(4.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("ICIC").tenure(6).rate(6.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("ICIC").tenure(9).rate(8.3f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("ICIC").tenure(12).rate(10f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("ICIC").tenure(18).rate(13.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("INDB").tenure(3).rate(4.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("INDB").tenure(6).rate(6.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("INDB").tenure(9).rate(8.3f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("INDB").tenure(12).rate(10f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("INDB").tenure(18).rate(13.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("KKBK").tenure(3).rate(4.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("KKBK").tenure(6).rate(6.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("KKBK").tenure(9).rate(8.3f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("KKBK").tenure(12).rate(10f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("KKBK").tenure(18).rate(13.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("RATN").tenure(3).rate(4.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("RATN").tenure(6).rate(6.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("RATN").tenure(9).rate(8.3f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("RATN").tenure(12).rate(10f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("RATN").tenure(18).rate(13.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("SBIN").tenure(3).rate(4.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("SBIN").tenure(6).rate(6.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("SBIN").tenure(9).rate(8.3f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("SBIN").tenure(12).rate(10f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("SBIN").tenure(18).rate(13.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("SCBL").tenure(3).rate(4.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("SCBL").tenure(6).rate(6.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("SCBL").tenure(9).rate(8.3f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("SCBL").tenure(12).rate(10f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("SCBL").tenure(18).rate(13.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("UTIB").tenure(3).rate(4.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("UTIB").tenure(6).rate(6.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("UTIB").tenure(9).rate(8.3f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("UTIB").tenure(12).rate(10f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("UTIB").tenure(18).rate(13.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("HDFC").tenure(3).rate(4.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("HDFC").tenure(6).rate(6.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("HDFC").tenure(9).rate(8.3f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("HDFC").tenure(12).rate(10f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("HDFC").tenure(18).rate(13.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("YESB").tenure(3).rate(4.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("YESB").tenure(6).rate(6.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("YESB").tenure(9).rate(8.3f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("YESB").tenure(12).rate(10f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("YESB").tenure(18).rate(13.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("AUFB").tenure(3).rate(4.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("AUFB").tenure(6).rate(6.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("AUFB").tenure(9).rate(8.3f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("AUFB").tenure(12).rate(10f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("AUFB").tenure(18).rate(13.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("ONECARD").tenure(3).rate(4.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("ONECARD").tenure(6).rate(6.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("ONECARD").tenure(9).rate(8.3f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("ONECARD").tenure(12).rate(10f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("ONECARD").tenure(18).rate(13.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("IDFC").tenure(3).rate(4.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("IDFC").tenure(6).rate(6.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("IDFC").tenure(9).rate(8.3f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("IDFC").tenure(12).rate(10f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("IDFC").tenure(18).rate(13.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("HDFC").tenure(3).rate(4.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("HDFC").tenure(6).rate(6.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("HDFC").tenure(9).rate(8.3f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("HDFC").tenure(12).rate(10f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("HDFC").tenure(18).rate(13.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("UTIB").tenure(3).rate(4.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("UTIB").tenure(6).rate(6.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("UTIB").tenure(9).rate(8.3f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("UTIB").tenure(12).rate(10f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("UTIB").tenure(18).rate(13.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("ICIC").tenure(3).rate(4.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("ICIC").tenure(6).rate(6.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("ICIC").tenure(9).rate(8.3f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("ICIC").tenure(12).rate(10f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("ICIC").tenure(18).rate(13.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("KKBK").tenure(3).rate(4.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("KKBK").tenure(6).rate(6.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("KKBK").tenure(9).rate(8.3f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("KKBK").tenure(12).rate(10f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("KKBK").tenure(18).rate(13.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("CL_ICICI").tenure(3).rate(4.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("CL_ICICI").tenure(6).rate(6.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("CL_ICICI").tenure(9).rate(8.3f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("CL_ICICI").tenure(12).rate(10f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("CL_ICICI").tenure(18).rate(13.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("CL_ICICI_KYC").tenure(3).rate(4.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("CL_ICICI_KYC").tenure(6).rate(6.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("CL_ICICI_KYC").tenure(9).rate(8.3f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("CL_ICICI_KYC").tenure(12).rate(10f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("CL_ICICI_KYC").tenure(18).rate(13.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode("KKBK").tenure(3).rate(5.35f).build(),
                                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode("KKBK").tenure(6).rate(7.91f).build(),
                                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode("KKBK").tenure(9).rate(10.38f).build(),
                                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode("KKBK").tenure(12).rate(12.76f).build(),
                                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode("KKBK").tenure(18).rate(18f).build(),
                                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode("HDFC").tenure(3).rate(5.35f).build(),
                                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode("HDFC").tenure(6).rate(7.91f).build(),
                                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode("HDFC").tenure(9).rate(10.38f).build(),
                                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode("HDFC").tenure(12).rate(12.76f).build(),
                                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode("HDFC").tenure(18).rate(18f).build(),
                                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode("ICIC").tenure(3).rate(5.35f).build(),
                                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode("ICIC").tenure(6).rate(7.91f).build(),
                                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode("ICIC").tenure(9).rate(10.38f).build(),
                                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode("ICIC").tenure(12).rate(12.76f).build(),
                                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode("ICIC").tenure(18).rate(18f).build(),
                                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(1.85f).build()
                        )
                );
                put("subventions", Arrays.asList(
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("AMEX").tenure(3).rate(2.29f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("AMEX").tenure(6).rate(3.96f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("AMEX").tenure(9).rate(5.59f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("AMEX").tenure(12).rate(7.19f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("AMEX").tenure(18).rate(10.27f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("BARB").tenure(3).rate(2.13f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("BARB").tenure(6).rate(3.96f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("BARB").tenure(9).rate(5.59f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("BARB").tenure(12).rate(7.67f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("BARB").tenure(18).rate(10.95f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("CITI").tenure(3).rate(2.13f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("CITI").tenure(6).rate(3.68f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("CITI").tenure(9).rate(5.97f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("CITI").tenure(12).rate(7.67f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("CITI").tenure(18).rate(10.95f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("HSBC").tenure(3).rate(2.05f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("HSBC").tenure(6).rate(3.55f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("HSBC").tenure(9).rate(5.4f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("HSBC").tenure(12).rate(6.94f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("HSBC").tenure(18).rate(9.93f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("ICIC").tenure(3).rate(2.6f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("ICIC").tenure(6).rate(4.5f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("ICIC").tenure(9).rate(6.35f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("ICIC").tenure(12).rate(8.1f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("ICIC").tenure(18).rate(11.6f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("INDB").tenure(3).rate(2.29f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("INDB").tenure(6).rate(3.96f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("INDB").tenure(9).rate(5.97f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("INDB").tenure(12).rate(7.67f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("INDB").tenure(18).rate(10.95f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("KKBK").tenure(3).rate(2.45f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("KKBK").tenure(6).rate(4.23f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("KKBK").tenure(9).rate(5.97f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("KKBK").tenure(12).rate(7.67f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("KKBK").tenure(18).rate(11.6f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("RATN").tenure(3).rate(2.13f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("RATN").tenure(6).rate(3.96f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("RATN").tenure(9).rate(5.97f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("RATN").tenure(12).rate(7.67f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("RATN").tenure(18).rate(10.95f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("SBIN").tenure(3).rate(2.29f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("SBIN").tenure(6).rate(3.96f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("SBIN").tenure(9).rate(5.59f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("SBIN").tenure(12).rate(7.19f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("SBIN").tenure(18).rate(7.19f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("SCBL").tenure(3).rate(1.95f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("SCBL").tenure(6).rate(3.68f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("SCBL").tenure(9).rate(5.59f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("SCBL").tenure(12).rate(7.19f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("SCBL").tenure(18).rate(7.19f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("UTIB").tenure(3).rate(2.29f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("UTIB").tenure(6).rate(3.96f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("UTIB").tenure(9).rate(5.59f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("UTIB").tenure(12).rate(7.19f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("UTIB").tenure(18).rate(10.95f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("HDFC").tenure(3).rate(2.55f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("HDFC").tenure(6).rate(4.51f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("HDFC").tenure(9).rate(6.35f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("HDFC").tenure(12).rate(8.05f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("HDFC").tenure(18).rate(11.55f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("YESB").tenure(3).rate(2.29f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("YESB").tenure(6).rate(3.96f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("YESB").tenure(9).rate(5.59f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("YESB").tenure(12).rate(7.67f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("YESB").tenure(18).rate(10.95f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("AUFB").tenure(3).rate(2.29f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("AUFB").tenure(6).rate(3.96f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("AUFB").tenure(9).rate(5.59f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("AUFB").tenure(12).rate(7.19f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("AUFB").tenure(18).rate(10.27f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("ONECARD").tenure(3).rate(2.6f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("ONECARD").tenure(6).rate(4.51f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("ONECARD").tenure(9).rate(6.35f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("ONECARD").tenure(12).rate(8.1f).build(),
                                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode("ONECARD").tenure(18).rate(11.6f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("IDFC").tenure(3).rate(2.61f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("IDFC").tenure(6).rate(4.51f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("IDFC").tenure(9).rate(6.35f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("IDFC").tenure(12).rate(8.15f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("IDFC").tenure(18).rate(11.62f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("HDFC").tenure(3).rate(2.61f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("HDFC").tenure(6).rate(4.51f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("HDFC").tenure(9).rate(6.35f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("HDFC").tenure(12).rate(8.15f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("HDFC").tenure(18).rate(11.62f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("UTIB").tenure(3).rate(2.29f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("UTIB").tenure(6).rate(3.96f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("UTIB").tenure(9).rate(6.35f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("UTIB").tenure(12).rate(8.15f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("UTIB").tenure(18).rate(11.62f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("ICIC").tenure(3).rate(2.61f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("ICIC").tenure(6).rate(4.51f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("ICIC").tenure(9).rate(6.35f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("ICIC").tenure(12).rate(8.15f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("ICIC").tenure(18).rate(8.15f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("KKBK").tenure(3).rate(3.09f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("KKBK").tenure(6).rate(5.32f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("KKBK").tenure(9).rate(7.48f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("KKBK").tenure(12).rate(9.57f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("KKBK").tenure(18).rate(9.57f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("CL_ICICI").tenure(3).rate(2.77f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("CL_ICICI").tenure(6).rate(4.78f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("CL_ICICI").tenure(9).rate(6.73f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("CL_ICICI").tenure(12).rate(8.63f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("CL_ICICI").tenure(18).rate(12.28f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("CL_ICICI_KYC").tenure(3).rate(2.61f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("CL_ICICI_KYC").tenure(6).rate(4.51f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("CL_ICICI_KYC").tenure(9).rate(6.35f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("CL_ICICI_KYC").tenure(12).rate(8.15f).build(),
                                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode("CL_ICICI_KYC").tenure(18).rate(11.62f).build(),
                                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode("KKBK").tenure(3).rate(3.09f).build(),
                                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode("KKBK").tenure(6).rate(5.32f).build(),
                                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode("KKBK").tenure(9).rate(7.48f).build(),
                                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode("KKBK").tenure(12).rate(9.57f).build(),
                                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode("KKBK").tenure(18).rate(9.57f).build(),
                                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode("HDFC").tenure(3).rate(2.61f).build(),
                                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode("HDFC").tenure(6).rate(4.51f).build(),
                                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode("HDFC").tenure(9).rate(6.35f).build(),
                                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode("HDFC").tenure(12).rate(8.15f).build(),
                                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode("HDFC").tenure(18).rate(8.15f).build(),
                                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode("ICIC").tenure(3).rate(3.09f).build(),
                                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode("ICIC").tenure(6).rate(5.32f).build(),
                                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode("ICIC").tenure(9).rate(7.48f).build(),
                                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode("ICIC").tenure(12).rate(9.57f).build(),
                                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode("ICIC").tenure(18).rate(9.57f).build(),
                                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(0.0f).build()
                        )
                );
            }};

    public static final Map<String, List<Mdr>> LIFE_STYLE_MDRS_SUBVENTIONS = new HashMap<String, List<Mdr>>() {{
        put("mdrs",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(3).rate(4.90f).build(),
                        Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(3).rate(4.90f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(3).rate(4.90f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(1.85f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(1.85f).build()
                ));
        put("subventions", Arrays.asList(Mdr.builder()
                        .cardType(CardTypeEnum.DEBIT.getCardType())
                        .bankCode(BankEnum.KKBK.getCode())
                        .tenure(3)
                        .rate(3.56f)
                        .build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(3).rate(2.61f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(0.0f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(3).rate(2.45f).build(), Mdr.builder()
                        .cardType(CardTypeEnum.CREDIT.getCardType())
                        .bankCode(BankEnum.ONECARD.getCode())
                        .tenure(3)
                        .rate(2.61f)
                        .build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(0.0f).build(),
                Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(3).rate(2.61f).build(),
                Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(0.0f).build(),
                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(0.0f).build()
        ));
    }};

    public static final Map<String, List<Mdr>> HERO_EV_MDRS_SUBVENTIONS = new HashMap<String, List<Mdr>>() {{
        put("mdrs",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(1.85f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(1.85f).build()
                ));
        put("subventions",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(0.0f).build()
                ));
    }};

    public static final Map<String, List<Mdr>> FRANKE_FABER_MDRS_SUBVENTIONS = new HashMap<String, List<Mdr>>() {{
        put("mdrs",
                Arrays.asList(
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(null).tenure(3).rate(1.85f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(null).tenure(6).rate(1.85f).build(),
                        Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(null).tenure(3).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(null).tenure(6).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).bankCode(null).tenure(3).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).bankCode(null).tenure(6).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode(null).tenure(3).rate(1.85f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode(null).tenure(6).rate(1.85f).build()
                )
        );
        put("subventions",
                Arrays.asList(
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(null).tenure(3).rate(0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(null).tenure(6).rate(0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(null).tenure(3).rate(0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(null).tenure(6).rate(0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).bankCode(null).tenure(3).rate(0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).bankCode(null).tenure(6).rate(0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode(null).tenure(3).rate(0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode(null).tenure(6).rate(0f).build()
                )
        );
    }};

    public static final Map<String, List<Mdr>> MOTO_VOLT_MDRS_SUBVENTIONS = new HashMap<String, List<Mdr>>() {{
        put("mdrs",
                Arrays.asList(
                        Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(null).tenure(3).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(null).tenure(6).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(null).tenure(9).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(null).tenure(12).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(null).tenure(18).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).bankCode(null).tenure(3).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).bankCode(null).tenure(6).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).bankCode(null).tenure(9).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).bankCode(null).tenure(12).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).bankCode(null).tenure(18).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(null).tenure(3).rate(1.85f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(null).tenure(6).rate(1.85f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(null).tenure(9).rate(1.85f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(null).tenure(12).rate(1.85f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(null).tenure(18).rate(1.85f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode(null).tenure(3).rate(1.85f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode(null).tenure(6).rate(1.85f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode(null).tenure(9).rate(1.85f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode(null).tenure(12).rate(1.85f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode(null).tenure(18).rate(1.85f).build()
                )
        );
        put("subventions", Arrays.asList(
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(null).tenure(3).rate(0f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(null).tenure(6).rate(0f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(null).tenure(9).rate(0f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(null).tenure(12).rate(0f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(null).tenure(18).rate(0f).build(),
                Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).bankCode(null).tenure(3).rate(0f).build(),
                Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).bankCode(null).tenure(6).rate(0f).build(),
                Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).bankCode(null).tenure(9).rate(0f).build(),
                Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).bankCode(null).tenure(12).rate(0f).build(),
                Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).bankCode(null).tenure(18).rate(0f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(null).tenure(3).rate(0f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(null).tenure(6).rate(0f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(null).tenure(9).rate(0f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(null).tenure(12).rate(0f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(null).tenure(18).rate(0f).build(),
                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode(null).tenure(3).rate(0f).build(),
                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode(null).tenure(6).rate(0f).build(),
                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode(null).tenure(9).rate(0f).build(),
                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode(null).tenure(12).rate(0f).build(),
                Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).bankCode(null).tenure(18).rate(0f).build()
        ));
    }};
    public static final Map<String, List<Mdr>> TI_INDIA_MDRS_SUBVENTIONS = new HashMap<String, List<Mdr>>() {{
        put("mdrs", Arrays.asList(
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.AMEX.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.AMEX.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.AMEX.getCode()).tenure(9).rate(8.25f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.BARB.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.BARB.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.BARB.getCode()).tenure(9).rate(8.25f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.CITI.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.CITI.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.CITI.getCode()).tenure(9).rate(8.25f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.HSBC.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.HSBC.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.HSBC.getCode()).tenure(9).rate(8.25f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.ICIC.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.ICIC.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.ICIC.getCode()).tenure(9).rate(8.25f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.INDB.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.INDB.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.INDB.getCode()).tenure(9).rate(8.25f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.KKBK.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.KKBK.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.KKBK.getCode()).tenure(9).rate(8.25f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.RATN.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.RATN.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.RATN.getCode()).tenure(9).rate(8.25f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.SBIN.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.SBIN.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.SBIN.getCode()).tenure(9).rate(8.25f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.SCBL.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.SCBL.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.SCBL.getCode()).tenure(9).rate(8.25f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.UTIB.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.UTIB.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.UTIB.getCode()).tenure(9).rate(8.25f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.HDFC.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.HDFC.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.HDFC.getCode()).tenure(9).rate(8.25f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.YESB.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.YESB.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.YESB.getCode()).tenure(9).rate(8.25f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.AUFB.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.AUFB.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.AUFB.getCode()).tenure(9).rate(8.25f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.ONECARD.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.ONECARD.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.ONECARD.getCode()).tenure(9).rate(8.25f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.HDFC.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.HDFC.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.HDFC.getCode()).tenure(9).rate(8.25f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.UTIB.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.UTIB.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.UTIB.getCode()).tenure(9).rate(8.25f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.ICIC.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.ICIC.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.ICIC.getCode()).tenure(9).rate(8.25f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.KKBK.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.KKBK.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.KKBK.getCode()).tenure(9).rate(8.25f).build(),
                Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).bankCode(BankEnum.CL_ICICI.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).bankCode(BankEnum.CL_ICICI.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).bankCode(BankEnum.CL_ICICI.getCode()).tenure(9).rate(8.25f).build()
        ));
        put("subventions", Arrays.asList(
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.AMEX.getCode()).tenure(3).rate(2.29f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.AMEX.getCode()).tenure(6).rate(3.96f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.AMEX.getCode()).tenure(9).rate(5.59f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.BARB.getCode()).tenure(3).rate(2.13f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.BARB.getCode()).tenure(6).rate(3.96f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.BARB.getCode()).tenure(9).rate(5.59f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.CITI.getCode()).tenure(3).rate(2.13f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.CITI.getCode()).tenure(6).rate(3.68f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.CITI.getCode()).tenure(9).rate(5.97f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.HSBC.getCode()).tenure(3).rate(2.09f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.HSBC.getCode()).tenure(6).rate(3.68f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.HSBC.getCode()).tenure(9).rate(5.71f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.ICIC.getCode()).tenure(3).rate(2.61f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.ICIC.getCode()).tenure(6).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.ICIC.getCode()).tenure(9).rate(6.35f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.INDB.getCode()).tenure(3).rate(2.29f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.INDB.getCode()).tenure(6).rate(3.96f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.INDB.getCode()).tenure(9).rate(5.97f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.KKBK.getCode()).tenure(3).rate(2.61f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.KKBK.getCode()).tenure(6).rate(4.51f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.KKBK.getCode()).tenure(9).rate(6.35f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.RATN.getCode()).tenure(3).rate(2.13f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.RATN.getCode()).tenure(6).rate(3.96f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.RATN.getCode()).tenure(9).rate(5.97f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.SBIN.getCode()).tenure(3).rate(2.65f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.SBIN.getCode()).tenure(6).rate(4.42f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.SBIN.getCode()).tenure(9).rate(6.35f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.SCBL.getCode()).tenure(3).rate(1.95f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.SCBL.getCode()).tenure(6).rate(3.68f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.SCBL.getCode()).tenure(9).rate(5.59f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.UTIB.getCode()).tenure(3).rate(2.29f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.UTIB.getCode()).tenure(6).rate(3.96f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.UTIB.getCode()).tenure(9).rate(5.59f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.HDFC.getCode()).tenure(3).rate(2.61f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.HDFC.getCode()).tenure(6).rate(4.51f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.HDFC.getCode()).tenure(9).rate(6.35f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.YESB.getCode()).tenure(3).rate(2.29f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.YESB.getCode()).tenure(6).rate(3.96f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.YESB.getCode()).tenure(9).rate(5.59f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.AUFB.getCode()).tenure(3).rate(2.29f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.AUFB.getCode()).tenure(6).rate(3.96f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.AUFB.getCode()).tenure(9).rate(5.59f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.ONECARD.getCode()).tenure(3).rate(2.61f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.ONECARD.getCode()).tenure(6).rate(4.51f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.ONECARD.getCode()).tenure(9).rate(6.35f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.HDFC.getCode()).tenure(3).rate(2.61f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.HDFC.getCode()).tenure(6).rate(4.51f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.HDFC.getCode()).tenure(9).rate(6.35f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.UTIB.getCode()).tenure(3).rate(2.29f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.UTIB.getCode()).tenure(6).rate(3.96f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.UTIB.getCode()).tenure(9).rate(6.35f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.ICIC.getCode()).tenure(3).rate(2.61f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.ICIC.getCode()).tenure(6).rate(4.51f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.ICIC.getCode()).tenure(9).rate(6.35f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.KKBK.getCode()).tenure(3).rate(3.09f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.KKBK.getCode()).tenure(6).rate(5.25f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.KKBK.getCode()).tenure(9).rate(7f).build(),
                Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).bankCode(BankEnum.CL_ICICI.getCode()).tenure(3).rate(2.77f).build(),
                Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).bankCode(BankEnum.CL_ICICI.getCode()).tenure(6).rate(4.78f).build(),
                Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).bankCode(BankEnum.CL_ICICI.getCode()).tenure(9).rate(6.73f).build()
        ));
    }};
    public static final Map<String, List<Mdr>> HERCULES_MDRS_SUBVENTIONS = new HashMap<String, List<Mdr>>() {{
        put("mdrs", Arrays.asList(
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.AMEX.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.AMEX.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.BARB.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.BARB.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.CITI.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.CITI.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.HSBC.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.HSBC.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.ICIC.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.ICIC.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.INDB.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.INDB.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.KKBK.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.KKBK.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.RATN.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.RATN.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.SBIN.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.SBIN.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.SCBL.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.SCBL.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.UTIB.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.UTIB.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.HDFC.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.HDFC.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.YESB.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.YESB.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.AUFB.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.AUFB.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.ONECARD.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.ONECARD.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.HDFC.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.HDFC.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.UTIB.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.UTIB.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.ICIC.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.ICIC.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.KKBK.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.KKBK.getCode()).tenure(6).rate(6.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).bankCode(BankEnum.CL_ICICI.getCode()).tenure(3).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).bankCode(BankEnum.CL_ICICI.getCode()).tenure(6).rate(6.5f).build()
        ));
        put("subventions", Arrays.asList(
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.AMEX.getCode()).tenure(3).rate(2.29f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.AMEX.getCode()).tenure(6).rate(3.96f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.BARB.getCode()).tenure(3).rate(2.13f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.BARB.getCode()).tenure(6).rate(3.96f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.CITI.getCode()).tenure(3).rate(2.13f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.CITI.getCode()).tenure(6).rate(3.68f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.HSBC.getCode()).tenure(3).rate(2.09f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.HSBC.getCode()).tenure(6).rate(3.68f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.ICIC.getCode()).tenure(3).rate(2.61f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.ICIC.getCode()).tenure(6).rate(4.5f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.INDB.getCode()).tenure(3).rate(2.29f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.INDB.getCode()).tenure(6).rate(3.96f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.KKBK.getCode()).tenure(3).rate(2.61f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.KKBK.getCode()).tenure(6).rate(4.51f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.RATN.getCode()).tenure(3).rate(2.13f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.RATN.getCode()).tenure(6).rate(3.96f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.SBIN.getCode()).tenure(3).rate(2.65f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.SBIN.getCode()).tenure(6).rate(4.42f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.SCBL.getCode()).tenure(3).rate(1.95f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.SCBL.getCode()).tenure(6).rate(3.68f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.UTIB.getCode()).tenure(3).rate(2.29f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.UTIB.getCode()).tenure(6).rate(3.96f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.HDFC.getCode()).tenure(3).rate(2.61f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.HDFC.getCode()).tenure(6).rate(4.51f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.YESB.getCode()).tenure(3).rate(2.29f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.YESB.getCode()).tenure(6).rate(3.96f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.AUFB.getCode()).tenure(3).rate(2.29f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.AUFB.getCode()).tenure(6).rate(3.96f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.ONECARD.getCode()).tenure(3).rate(2.61f).build(),
                Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).bankCode(BankEnum.ONECARD.getCode()).tenure(6).rate(4.51f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.HDFC.getCode()).tenure(3).rate(2.61f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.HDFC.getCode()).tenure(6).rate(4.51f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.UTIB.getCode()).tenure(3).rate(2.29f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.UTIB.getCode()).tenure(6).rate(3.96f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.ICIC.getCode()).tenure(3).rate(2.61f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.ICIC.getCode()).tenure(6).rate(4.51f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.KKBK.getCode()).tenure(3).rate(3.09f).build(),
                Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).bankCode(BankEnum.KKBK.getCode()).tenure(6).rate(5.25f).build(),
                Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).bankCode(BankEnum.CL_ICICI.getCode()).tenure(3).rate(2.77f).build(),
                Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).bankCode(BankEnum.CL_ICICI.getCode()).tenure(6).rate(4.78f).build()
        ));
    }};

    public static final Map<String, List<Mdr>> DEFAULT_MDRS_SUBVENTIONS = new HashMap<String, List<Mdr>>() {{
        put("mdrs",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(1.25f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(1.9f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(1.85f).build()
                ));
        put("subventions",
                Arrays.asList(Mdr.builder().cardType(CardTypeEnum.DEBIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CARDLESS.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.CREDIT.getCardType()).tenure(-1).rate(0.0f).build(),
                        Mdr.builder().cardType(CardTypeEnum.NTB.getCardType()).tenure(-1).rate(0.0f).build()
                ));
    }};

    public static final Map<String, String> EXCEPTION_BRANDS_MAP = new HashMap<String, String>() {
        {
            put("612bfa1fb5784f34b60af4cd", "daikin");
            put("612c87ed8078404a9ee701c9", "daikin");
            put("6138a84b6bad2c458cf99a09", "realme");
            put("613066ce93820955fdac0f00", "realme");
            put("61557ca69e5b467e54f33831", "bluestar");
            put("6156cb2efe94ef6a6132e90d", "bluestar");
            put("616559c64ecd3e3dab9d4289", "asus");
            put("61612913fad3581653f5b839", "asus");
            put("616e88da2b5c9994ea788350", "unitedeals");
            put("6133895f3d8688e3875d70a7", "UTL");
            put("618cd3c7e3dfaca783ec263d", "lakme");
            put("61eaf5c09de6b1c8fd09af29", "voltas");
            put("61d4169d1328581b3c9dfec6", "voltas");
            put("61cac41c036bba63f4037c44", "whirlpool");
            put("618baa1219bc49cffae39d53", "whirlpool");
            put("62260aa9f681ed5682a4df5b", "bosch siemens brand stores");
            put("6221cc0c3c6ad47d28150325", "bosch siemens brand stores");
            put("6221cc693c6ad47d28150381", "bosch siemens other stores");
            put("62260ab3f681ed5682a4df5c", "bosch siemens other stores");
            put("61703f1294cfcc77a975364d", "mitsubishi electric");
            put("621334a813a05f0f14c3cf50", "mitsubishi electric");
            put("626a3cc2e99788e50d79a377", "top 10");
            put("62792f6c105443562b204460", "top 10");
            put("627ce053e884e782f9b3c43d", "Mccoy India");
            put("625d4fde385c634fb493f25e", "Mccoy India");
            put("63c154f148c22686155f8f97", "Lifestyle");
            put("63e5eb2bb5c983e613290fa8", "Hero EV");
            put("628dd837b1176ee652e8b85e", "Non Brand ( Mobile / Electronics /Home appliances)");
            put("628f5a765e3a0868fe878fee", "Non Brand ( Mobile / Electronics /Home appliances)");
            put("6290a5019762d9a7e66116f0", "Non Brand - Health and Wellness, Beauty and Fragrance, Furniture");
            put("629862418207a8f21124ab78", "Non Brand - Health and Wellness, Beauty and Fragrance, Furniture");
            put("645c792745eaf501a15f90b8", "Khaitan Solar");
            put("6467200a335d3dd2da6ecaeb", "Franke Faber");
            put("646c907a257d748090396be9", "Franke Faber");
            put("648802197a65d120367c8c00", "Motovolt");
            put("64b91ba055b877c12500b817", "TI India");
            put("64b917a86e6096b19d136060", "Hercules Fitness");
        }
    };

    public static final List<String> VALID_IFSC_CODE =
            Arrays.asList("KSCB", "KUCB", "KVBL", "KVGB", "LAVB", "MAHB", "MAHG", "MCBL", "MDBK", "MDCB", "MHCB",
                    "MKPB", "MSBL", "MSCI", "MSHQ", "MSLM", "MSNU", "MUBL", "MVCB", "NBAD", "NBRD", "NCUB", "NESF",
                    "NGSB", "NICB", "NJBK", "NKGS", "NMCB", "NNSB", "NOSC", "NSPB", "NTBL", "NUCB", "NVNM", "ORCB",
                    "PJSB", "PKGB", "PMEC", "PSIB", "PUCB", "PUNB", "PUSD", "PYTM", "QNBA", "RABO", "RATN", "RBIN",
                    "RBIS", "RDCB", "RMGB", "RNSB", "RRBP", "RSBL", "RSCB", "RSSB", "SABR", "SAHE", "SANT", "SBIN",
                    "SBLS", "SCBL", "SDCB", "SDCE", "SECB", "SHBK", "SIBL", "SIDC", "SJSB", "SKSB", "SMBC", "SMCB",
                    "SNBK", "SOGE", "SPCB", "SRCB", "STCB", "SUNB", "SURY", "SUSB", "SUTB", "SVBL", "SVCB", "SVSH",
                    "TBMC", "TBSB", "TDCB", "TGMB", "THRS", "TJSB", "TMBL", "TMSB", "TNCB", "TNSC", "TPSC", "TSAB",
                    "TSSB", "TTCB", "UBIN", "UCBA", "UJVN", "UOVB", "UPCB", "URBN", "UTIB", "UTKS", "UUCB", "VARA",
                    "VASJ", "VCOB", "VSBL", "VVSB", "WBSC", "YESB", "ZCBL", "ZSBL", "ABHY", "ADCC", "AHDC", "AIRP",
                    "AJAR", "AJHC", "AKJB", "AMCB", "AMDN", "ANZB", "APBL", "APGB", "APGV", "APMC", "ARBL", "ASBL",
                    "AUBL", "AUCB", "BACB", "BARA", "BARB", "BARC", "BBKM", "BCBM", "BCEY", "BDBL", "BKID", "BMCB",
                    "BNPA", "BNSB", "BOFA", "BOTM", "CBIN", "CCBL", "CHAS", "CITI", "CIUB", "CLBL", "CNRB", "COAS",
                    "COSB", "CRES", "CRLY", "CRUB", "CSBK", "CTCB", "DBSS", "DCBL", "DEOB", "DEUT", "DICG", "DLSC",
                    "DLXB", "DMKJ", "DNSB", "DOHB", "DURG", "EBIL", "EIBI", "ESFB", "ESMF", "FDRL", "FINO", "FIRN",
                    "FSFB", "GBCB", "GDCB", "GSCB", "HARC", "HCBL", "HDFC", "HPSC", "HSBC", "HVBK", "IBBK", "IBKL",
                    "IBKO", "ICBK", "ICIC", "IDFB", "IDIB", "IDUK", "INDB", "IOBA", "IPOS", "ITBL", "JAKA", "JANA",
                    "JASB", "JIOP", "JJSB", "JPCB", "JSBL", "JSBP", "JSFB", "JTSC", "KACE", "KAIJ", "KANG", "KARB",
                    "KBKB", "KCCB", "KDCB", "KJSB", "KKBK", "KLGB", "KNSB", "KOEX", "KOLH", "KSBK");

    public static Map<String, List<String>> userTypes() {
        Map<String, List<String>> userTypes = new HashMap<>();
        userTypes.put("ADMIN", Arrays.asList("SUPER_ADMIN", "ADMIN", "OPS", "FINANCE", "SALES", "SALES_ONLINE"));
        userTypes.put("MERCHANT", Arrays.asList("MERCHANT", "MERCHANT_OPS", "MERCHANT_FINANCE", "MERCHANT_SALES"));
        userTypes.put("BANKER",
                Arrays.asList("BANKER", "USFB_FCU_AGENCY_EXECUTIVE", "USFB_FCU_AGENCY_SUPERVISOR", "USFB_AUDIT",
                        "USFB_CPV_AGENCY_EXECUTIVE", "USFB_CPV_AGENCY_SUPERVISOR", "USFB_CREDIT_OFFICER_L1",
                        "USFB_CREDIT_OFFICER_L2", "USFB_CREDIT_OFFICER_L3", "USFB_OPS_AGENCY_EXECUTIVE",
                        "USFB_OPS_MANAGER", "USFB_OPS_IN_MANAGER", "USFB_SALES_AGENCY_EXECUTIVE", "USFB_SALES_MANAGER",
                        "USFB_SALES_IN_MANAGER"));
        userTypes.put("STORE_USER", Arrays.asList("STORE_USER"));
        return userTypes;
    }


    public static Map<String, List<String>> getPermissions() {
        return new HashMap<String, List<String>>() {
            {
                put("VIEW_DASHBOARD", Arrays.asList("SUPER_ADMIN", "ADMIN", "SALES", "SALES_ONLINE", "OPS"));
                put("VIEW_MERCHANTS", Arrays.asList("SUPER_ADMIN", "ADMIN", "OPS", "SALES"));
                put("VIEW_CONSUMERS", Arrays.asList("SUPER_ADMIN", "ADMIN", "OPS"));
                put("VIEW_TRANSACTIONS", Arrays.asList("SUPER_ADMIN", "ADMIN", "OPS", "FINANCE", "SALES", "SALES_ONLINE", "MERCHANT",
                        "MERCHANT_OPS", "MERCHANT_FINANCE", "MERCHANT_SALES"));
                put("VIEW_REFUNDS", Arrays.asList("SUPER_ADMIN", "ADMIN", "OPS", "FINANCE",
                        "MERCHANT_OPS", "MERCHANT_FINANCE", "MERCHANT_SALES"));
                put("VIEW_PAYMENT_LINKS", Arrays.asList("SUPER_ADMIN", "ADMIN", "SALES_ONLINE", "FINANCE", "MERCHANT", "MERCHANT_OPS",
                        "MERCHANT_FINANCE",
                        "MERCHANT_SALES"));
                put("VIEW_PORTAL_USERS", Arrays.asList("SUPER_ADMIN", "ADMIN"));
                put("VIEW_USER_DASHBOARD", Arrays.asList("SUPER_ADMIN", "ADMIN"));
                put("VIEW_SESSIONS", Arrays.asList("SUPER_ADMIN", "ADMIN"));
                put("VIEW_DOWNPAYMENTS", Arrays.asList("SUPER_ADMIN", "ADMIN"));
                put("VIEW_LOAN_APPLICATIONS", Arrays.asList("SUPER_ADMIN", "ADMIN"));
                put("VIEW_EDUCATION_LOANS", Arrays.asList("SUPER_ADMIN", "ADMIN", "SALES_ONLINE"));
                put("VIEW_UPI_TRANSACTIONS", Arrays.asList("SUPER_ADMIN", "ADMIN"));
                put("VIEW_ELIGIBILITY", Arrays.asList("SUPER_ADMIN", "ADMIN"));
                put("VIEW_SETTLEMENT", Arrays.asList("SUPER_ADMIN", "ADMIN", "OPS", "FINANCE"));
                put("VIEW_RECONCILIATION", Arrays.asList("SUPER_ADMIN", "ADMIN"));
                put("CREATE_PAYMENT_LINKS", Arrays.asList("MERCHANT", "MERCHANT_SALES"));
                put("ELIGIBILITY", Arrays.asList("SUPER_ADMIN", "ADMIN"));
                put("AVOIDSMS", Arrays.asList("MERCHANT", "MERCHANT_SALES"));
                put("REFUND", Arrays.asList("SUPER_ADMIN", "OPS"));
                put("MERCHANT_REFUND", Collections.singletonList("MERCHANT_OPS"));
                put("EDIT_MERCHANT", Arrays.asList("SUPER_ADMIN", "ADMIN", "OPS"));
                put("EDIT_PORTAL_USERS", Arrays.asList("SUPER_ADMIN", "ADMIN"));
                put("UPLOAD_SETTLEMENT", Arrays.asList("SUPER_ADMIN", "OPS"));
                put("VIEW_LEAD_ACTIVITIES", Arrays.asList("SUPER_ADMIN", "ADMIN", "SALES"));
                put("VIEW_MERCHANT_ACTIVITIES", Arrays.asList("SUPER_ADMIN", "ADMIN", "SALES"));
                put("VIEW_TRANSACTION_ACTIVITIES", Arrays.asList("SUPER_ADMIN", "ADMIN", "SALES", "SALES_ONLINE"));
                put("VIEW_CALL_LOGS", Arrays.asList("SUPER_ADMIN", "ADMIN", "OPS"));
                put("VIEW_LEADS", Arrays.asList("SUPER_ADMIN", "ADMIN", "SALES"));
                put("VIEW_UNASSIGNED_LEADS", Arrays.asList("SUPER_ADMIN", "ADMIN"));
                put("VIEW_LEADS_PIPELINE", Arrays.asList("SUPER_ADMIN", "ADMIN", "SALES"));
                put("VIEW_LEADS_SUMMARY", Arrays.asList("SUPER_ADMIN", "ADMIN", "SALES"));
                put("VIEW_DYNAMIC_PRICING", new ArrayList<>());
                put("VIEW_STORE_LINKS", Arrays.asList("SUPER_ADMIN", "ADMIN"));
                put("VIEW_BRANDS", Arrays.asList("SUPER_ADMIN", "ADMIN", "SALES", "OPS"));
                put("VIEW_BYJUS_DASHBOARD", Arrays.asList("SUPER_ADMIN", "ADMIN"));
                put("VIEW_INVOICES", Arrays.asList("SUPER_ADMIN", "ADMIN", "OPS", "FINANCE"));
                put("UPLOAD_INVOICES", Arrays.asList("SUPER_ADMIN", "ADMIN", "OPS", "FINANCE"));
                put("VIEW_PRODUCTS", Arrays.asList("SUPER_ADMIN", "ADMIN"));
                put("VIEW_PRODUCT_SKUS", Arrays.asList("SUPER_ADMIN", "ADMIN"));
                put("ADD_BRAND_PRODUCTS", new ArrayList<>());
                put("APPROVE_BRAND_PRODUCTS", new ArrayList<>());
                put("REJECT_BRAND_PRODUCTS", new ArrayList<>());
                put("DELETE_BRAND_PRODUCTS", new ArrayList<>());
                put("VIEW_EARNINGS", Arrays.asList("SUPER_ADMIN", "ADMIN"));
                put("TWL_VIEW_LOANS", Arrays.asList("SUPER_ADMIN", "ADMIN", userTypes().get("BANKER").get(1)));
                put("TWL_VIEW_REMARKS", Arrays.asList("SUPER_ADMIN", "ADMIN", userTypes().get("BANKER").get(1)));
                put("NTB_VIEW_REMARKS", Arrays.asList("SUPER_ADMIN", "ADMIN"));
                put("NTB_VIEW_LOANS", Arrays.asList("SUPER_ADMIN", "ADMIN"));
                put("NTB_DOWNLOAD_LOANS", Arrays.asList("SUPER_ADMIN", "ADMIN"));
                put("NTB_UPLOAD_STATEMENT", Arrays.asList("SUPER_ADMIN", "ADMIN"));
                put("TWL_CREATE_REMARKS", Arrays.asList("SUPER_ADMIN", "ADMIN", userTypes().get("BANKER").get(1)));
                put("NTB_CREATE_REMARKS", Arrays.asList("SUPER_ADMIN", "ADMIN"));
                put("REPORTS", Arrays.asList("SUPER_ADMIN", "ADMIN", "OPS", "FINANCE"));
                put("VIEW_CASHBACKS", Arrays.asList("SUPER_ADMIN", "ADMIN", "OPS", "FINANCE"));
                put("VIEW_PAYOUTS", Arrays.asList("SUPER_ADMIN", "ADMIN", "OPS", "FINANCE"));
                put("VIEW_DASHBOARD_ANALYTICS", Arrays.asList("SUPER_ADMIN", "ADMIN", "SALES", "SALES_ONLINE", "OPS"));
                put("VIEW_SALES_LEADERBOARD", Arrays.asList("SUPER_ADMIN", "ADMIN"));
                put("MERCHANDISE", Arrays.asList("SUPER_ADMIN", "ADMIN"));
                put("FOS_OWNERS", Arrays.asList("SUPER_ADMIN", "ADMIN"));
                put("VIEW_ASSESTS", Arrays.asList("SUPER_ADMIN", "ADMIN"));
                put("VIEW_PAYMENT_CONFIGS", Arrays.asList("SUPER_ADMIN", "ADMIN"));
                put("CREATE_DISPUTE", Arrays.asList("", ""));
                put("MERCHANT_REFUND_PAYOUT", Arrays.asList("", ""));
                put("MAP_QR", Arrays.asList("", ""));
                put("REMOVE_QR", Arrays.asList("", ""));
                put("DOWNLOAD_TRANSACTIONS", Arrays.asList("", ""));
                put("DOWNLOAD_LEADS", Arrays.asList("", ""));
                put("DOWNLOAD_UNASSIGNEDLEADS", Arrays.asList("", ""));
                put("DOWNLOAD_MERCHANTS", Arrays.asList("", ""));
                put("DOWNLOAD_PAYMENTS", Arrays.asList("", ""));
                put("DOWNLOAD_ELIGIBILITY", Arrays.asList("", ""));
                put("DOWNLOAD_LEADACTIVITY", Arrays.asList("", ""));
                put("DOWNLOAD_MERCHANTACTIVITY", Arrays.asList("", ""));
                put("DOWNLOAD_TRANSACTIONSACTIVITY", Arrays.asList("", ""));
                put("DOWNLOAD_CALLLOGS", Arrays.asList("", ""));
                put("DOWNLOAD_SETTLEMENTS", Arrays.asList("", ""));
                put("DOWNLOAD_CASHBACK", Arrays.asList("", ""));
                put("DOWNLOAD_PAYOUTS", Arrays.asList("", ""));
                put("DOWNLOAD_REFUNDS", Arrays.asList("", ""));
                put("DOWNLOAD_USERDASHBOARD", Arrays.asList("", ""));
                put("DOWNLOAD_DOWNPAYMENTS", Arrays.asList("", ""));
                put("DOWNLOAD_EDUCATIONLOANS", Arrays.asList("", ""));
                put("DOWNLOAD_LOANAPPLICATIONS", Arrays.asList("", ""));
                put("MERCHANT_ACTIVITY_UPLOAD", new ArrayList<>());
                put("CANCEL_MERCHANT_ACTIVITY", new ArrayList<>());
                put("VIEW_DISPUTES", new ArrayList<>());
                put("MERCHANT_HANDLE_DISPUTE", new ArrayList<>());
                put("HANDLE_DISPUTE", new ArrayList<>());
                put("DOWNLOAD_DISPUTES", new ArrayList<>());
                put("UPLOAD_DISPUTE_DOC", new ArrayList<>());
                put("VIEW_DISPUTE_DOC", new ArrayList<>());
                put("UPLOAD_BULK_PROVIDER", new ArrayList<>());
                put("APPROVED_BULK_PROVIDER", new ArrayList<>());
                put("STORE_USER", new ArrayList<>());
                put("NTB_UPDATE_LOAN_STATUS", new ArrayList<>());
                put("NTB_MARK_EMANDATE_SUCCESS", new ArrayList<>());
                put("NTB_RESET_AGREEMENT_ATTEMPTS", new ArrayList<>());
                put("NTB_UPLOAD_FILE", new ArrayList<>());
                put("VIEW_VISIBILITIES", Arrays.asList("SUPER_ADMIN", "ADMIN", "OPS", "FINANCE", "SALES"));
                put("UPLOAD_VISIBILITIES", Arrays.asList("SUPER_ADMIN", "ADMIN", "OPS", "FINANCE", "SALES"));
                put("DEACTIVATE_VISIBILITIES", Arrays.asList("SUPER_ADMIN", "ADMIN", "OPS", "FINANCE", "SALES"));
                put("VIEW_FAILED_TRANSACTIONS", new ArrayList<>());
                put("UPLOAD_FAILED_TRANSACTION_MERCHANTS", new ArrayList<>());
                put("VIEW_UNSETTLED_TXNS", new ArrayList<>());
                put("DOWNLOAD_UNSETTLED_TXNS", new ArrayList<>());
                put("VIEW_INCENTIVES", Arrays.asList("SUPER_ADMIN", "ADMIN"));
                put("EDIT_INCENTIVES", new ArrayList<>());
                put("CREATE_PAYOUT", new ArrayList<>());
                put("VIEW_DASHBOARD_BRAND_SEARCH", new ArrayList<>());
                put("VIEW_CONSUMER_APP_VERSION", new ArrayList<>());
                put("EDIT_CONSUMER_APP_VERSION", new ArrayList<>());
                put("UNLOCK_PORTAL_USERS", new ArrayList<>());
                put("OPS_UPDATE_CO_ORDINATES", new ArrayList<>());
                put("OPS_UPDATE_STORE_CLASSIFICATION", new ArrayList<>());
                put("OPS_UPDATE_TRANSACTION_CASHBACK", new ArrayList<>());
                put("OPS_UPDATE_BRAND_GST", new ArrayList<>());
                put("OPS_UPDATE_TRANSACTION_PAYOUT", new ArrayList<>());
                put("OPS_UPDATE_MERCHANT_STORE_CODE", new ArrayList<>());
                put("OPS_UPDATE_TAG_BRAND", new ArrayList<>());
                put("OPS_UPDATE_UNTAG_BRAND", new ArrayList<>());
                put("OPS_CONFIGURATION", new ArrayList<>());
                put("OPS_GET_DUPLICATE_SERIAL_NUMBERS", new ArrayList<>());
                put("OPS_UPDATE_DOWN_PAYMENT", new ArrayList<>());
                put("OPS_UPDATE_BRAND_SUBVENTION", new ArrayList<>());
                put("OPS_UPDATE_MDR_SUBVENTION", new ArrayList<>());
                put("OPS_UPDATE_MERCHANT_CHECKOUTVERSION", new ArrayList<>());
                put("OPS_DEACTIVATE_GST_FOR_BRANDS", new ArrayList<>());
                put("OPS_UPDATE_MERCHANT_CREDIT_BANKS_EXCLUSION", new ArrayList<>());
                put("OPS_UPDATE_MERCHANT_PROVIDERS", new ArrayList<>());
                put("OPS_UPDATE_USER_MERCHANTS", new ArrayList<>());
                put("OPS_UPDATE_MERCHANT_SUBVENTIONS", new ArrayList<>());
                put("OPS_UPDATE_ELIGIBILITY_PERMISSION", new ArrayList<>());
                put("VIEW_OPS_PANEL", new ArrayList<>());
                put("PROCESS_PAYOUT", new ArrayList<>());
                put("REFUND_ARN_MAP", new ArrayList<>());
                put("VIEW_EMI_CONVERSION_REPORTS", new ArrayList<>());
                put("SEND_EMI_CONVERSION_REPORT", new ArrayList<>());
                put("UPLOAD_ADDITIONAL_CASHBACK", new ArrayList<>());
                put("BULK_REFUND_PROCESS_UPLOAD", new ArrayList<>());
                put("INITIATE_REFUND_POST_TAT", new ArrayList<>());
                put("VIEW_PROVIDER_CONFIG", new ArrayList<>());
                put("UPDATE_PROVIDER_CONFIG", new ArrayList<>());
                put("VIEW_SYSTEM_SETTINGS", new ArrayList<>());
                put("VIEW_ELIGIBILITY_PROVIDER_SETTINGS", new ArrayList<>());
                put("VIEW_ELIGIBILITY_MS_CONFIG_SETTINGS", new ArrayList<>());
                put("VIEW_ELIGIBILITY_CONFIG", new ArrayList<>());
                put("UPDATE_ELIGIBILITY_CONFIG", new ArrayList<>());
                put("ENABLE_ELIGIBILITY_FLAG_FOR_MERCHANT", new ArrayList<>());
            }
        };
    }

    public static String SIGNATORY_ID_PROOF = "signatoryIDProof";
    public static String SIGNATORY_ADDRESS_PROOF = "signatoryAddressProof";
    public static String ADHAR_NUMBER = "Aadhaar Number";
    public static String REUPLOAD_KYC = "REUPLOAD_KYC";
    public static String KYC = "Kyc";
    public static String ACCOUNT = "Account";
    public static String STORE_PHOTO = "storePhoto";
    public static String ACCOUNT_PROOF = "accountProof";
    public static String REUPLOAD_ACCOUNT = "REUPLOAD_ACCOUNT";
    public static String ADDRESS_PROOF = "addressProof";
    public static String UPLOAD_ADDRESS_PROOF = "UPLOAD_ADDRESS_PROOF";
    public static String MERCHANDISE = "merchandise";
    public static String REUPLOAD_MERCHANDISE = "REUPLOAD_MERCHANDISE";
    public static String SHOP = "shop";
    public static String SHOW_ROOM_PHOTO = "showroomPhoto";
    public static String REUPLOAD_STORE_PHOTO = "REUPLOAD_STORE_PHOTO";
    public static String ACCOUNT_NO = "accountNo";
    public static String ACCOUNT_NO_CHANGE = "ACCOUNT_NO_CHANGE";
    public static List<String> CARD_TYPES = Arrays.asList("CREDIT", "DEBIT", "CARDLESS");
    public static List<Integer> TENURES = Arrays.asList(3, 6, 9, 12, 18, 24);

    public static Map<String, Object> DESTINATION = new HashMap<String, Object>() {{
        put("sms", "mobile");
        put("email", "email");
        put("slack", "channel");
        put("push", "deviceToken");
        put("whatsapp", "mobile");
    }};
    public static List<String> ALLOWED_IPS_FOR_ADMINS = Arrays.asList("127.0.0.1");
    public static String VERSION = "1.1";
    public static List<String> STAGES = Arrays.asList("ownerKyc", "businessKyc", "accountSetup", "commercialSetup", "merchandise", "createStoreUser");
    public static List<String> CO_BRADNING_STAGES = Arrays.asList("leadcreated", "ownerKyc", "businessKyc", "accountSetup", "commercialSetup", "merchandise", "createStoreUser");
    public static Integer DEFAULT_LIMIT = 10;
    public static String exclusionCreditBanks = "exclusion_credit_banks";

    public static String TRANSACTION_NOT_FOUND = "Transaction not found.";

    public static String MERCHANT_ADMIN = "MERCHANT_ADMIN";

    public static String MERCHANT = "MERCHANT";

    public static List<String> loanStages = new ArrayList<>( Arrays.asList("Loan Created","Bank Statement","KYC","eNach", "Agreement Signing", "Disbursal"));

    public static String payment_PARTNER = "payment";
    public static List<String> STATUS_LIST = Arrays.asList("profiled", "resubmission", "rejected", "approved");
    public static String DEFAULT_LOAN_STAGE = "Loan Created";
    public static List<String> TRANSACTION_FAILURE_STATUS = new ArrayList<>(Arrays.asList("failed","expired"));
}