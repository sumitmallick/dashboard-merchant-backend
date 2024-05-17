package com.freewayemi.merchant.commons.dto.ntbservices;

import java.util.Arrays;

public enum EducationEnum {
    Doctorate("Doctorate"),
    Graduate("Graduate"),
    Professional("Professional"),
    PostGraduate("Post Graduate"),
    UnderGraduate("Under Graduate"),
    Vocational("Vocational");

    private String displayName;

    EducationEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static EducationEnum findByDisplayName(String displayName) {
        return Arrays.stream(EducationEnum.values()).filter(e -> e.name().equalsIgnoreCase(displayName)).findAny().orElse(EducationEnum.Vocational);
    }
}
