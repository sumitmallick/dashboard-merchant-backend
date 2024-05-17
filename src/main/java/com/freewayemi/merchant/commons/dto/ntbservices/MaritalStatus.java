package com.freewayemi.merchant.commons.dto.ntbservices;

import java.util.Arrays;

public enum MaritalStatus {
    Single, Married;

    public static MaritalStatus value(String status) {
        return Arrays.stream(MaritalStatus.values()).filter(e -> e.name().equalsIgnoreCase(status)).findAny().orElse(MaritalStatus.Single);
    }
}
