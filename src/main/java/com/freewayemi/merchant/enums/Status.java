package com.freewayemi.merchant.enums;

import com.freewayemi.merchant.commons.type.TransactionCode;

public enum Status {
    SUCCESS("SUCCESS"),
    PENDING("PENDING"),
    FAILED("FAILED"),
    CREATED("CREATED"),
    APPROVED("APPROVED"),
    REGISTERED("REGISTERED"),
    PROFILED("PROFILED"),
    RESUBMISSION("RESUBMISSION"),
    REJECTED("REJECTED"),
    ONBOARDED("ONBOARDED"),
    ONBOARDING("onboarding"),
    ONBOARDING_PENDING("ONBOARDING PENDING");

    private final String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static Status getStatusByValue(String type){
        for (Status brandType:Status.values()) {
            if(brandType.status.equals(type)){
                return brandType;
            }
        }
        return null;
    }
}
