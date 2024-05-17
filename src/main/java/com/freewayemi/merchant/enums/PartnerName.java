package com.freewayemi.merchant.enums;


public enum PartnerName {
    payment("payment");
    private String name;

    PartnerName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}