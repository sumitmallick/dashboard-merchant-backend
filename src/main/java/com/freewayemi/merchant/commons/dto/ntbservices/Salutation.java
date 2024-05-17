package com.freewayemi.merchant.commons.dto.ntbservices;

public enum Salutation {
    Mr(Gender.M), Mrs(Gender.F), Ms(Gender.F);
    private final Gender gender;

    Salutation(Gender gender) {
        this.gender = gender;
    }

    public Gender getGender() {
        return gender;
    }

    public static Gender searchBySalutation(String s) {
        for (Salutation salutation : Salutation.values()) {
            if (s.equalsIgnoreCase(salutation.name())) {
                return salutation.getGender();
            }
        }
        return Gender.O;
    }
}
