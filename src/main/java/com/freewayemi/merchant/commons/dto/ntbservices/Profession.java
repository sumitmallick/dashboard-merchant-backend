package com.freewayemi.merchant.commons.dto.ntbservices;

import java.util.Arrays;

public enum Profession {
    Salaried, SelfEmployed, Others;

    public static Profession value(String status) {
        return Arrays.stream(Profession.values()).filter(e -> e.name().equalsIgnoreCase(status)).findAny().orElse(Profession.SelfEmployed);
    }
}

