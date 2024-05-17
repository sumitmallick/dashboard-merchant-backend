package com.freewayemi.merchant.commons.dto.ntbservices;

public enum Gender {
    M("male"), F("female"), O("others");

    private String display;

    Gender(String display) {
        this.display = display;
    }
}
