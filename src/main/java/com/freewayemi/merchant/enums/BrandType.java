package com.freewayemi.merchant.enums;

public enum BrandType {
    PARTNER("partner"),
    NON_PARTNER("nonPartner"),
    ALL_BRAND("allBrand");

    private String type;

    BrandType(String type) {
        this.type = type;
    }

    public BrandType getByType(String type){
        for (BrandType brandType:BrandType.values()) {
            if(brandType.type.equals(type)){
                return brandType;
            }
        }
        return BrandType.PARTNER;
    }

    public String getType() {
        return type;
    }
}
