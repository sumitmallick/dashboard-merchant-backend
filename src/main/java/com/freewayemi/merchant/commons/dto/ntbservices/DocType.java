package com.freewayemi.merchant.commons.dto.ntbservices;

public enum DocType {
    AGREEMENT("19","42"),
    KYC("6","23"),
    SELFIE("7","24"),
    AADHAR("2","5"),
    PAN("1","1");

    public String catId;

    public String subCatId;

    DocType(String catId,String subCatId){
        this.catId=catId;
        this.subCatId=subCatId;
    }

    public String getCatId() {
        return catId;
    }

    public String getSubCatId() {
        return subCatId;
    }
}
