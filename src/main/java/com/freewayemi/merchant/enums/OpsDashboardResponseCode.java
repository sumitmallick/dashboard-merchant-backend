package com.freewayemi.merchant.enums;

public enum OpsDashboardResponseCode {

    SUCCESS(0,"Success"),
    BRAND_NOT_FOUND(1,"Brand not Found"),
    INVALID_BRAND(2, "Invalid Brand"),
    INVALID_MERCHANT(3,"Merchant Id not Found"),
    INVALID_PRODUCT(4, "Invalid Product"),

    ;
    private final Integer code;
    private final String message;
    public Integer getCode(){return code;}
    public String getMessage(){return message;}

    OpsDashboardResponseCode(Integer code,String message){
        this.code=code;
        this.message=message;
    }
}
