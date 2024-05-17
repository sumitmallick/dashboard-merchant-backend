package com.freewayemi.merchant.commons.dto.ntbservices;

public enum KycTypes {
    CKYC("KYC_CKYC"),OKYC("KYC_OKYC");

    private String kycType;

    KycTypes(String kycType) {
        this.kycType=kycType;
    }

    public String getKycType() {
        return kycType;
    }

    @Override
    public String toString() {
        return kycType;
    }
}
