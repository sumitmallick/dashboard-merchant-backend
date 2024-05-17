package com.freewayemi.merchant.enums;

import com.freewayemi.merchant.commons.dto.ntbservices.ResidentType;

public enum ResidentTypeEnum {
    Owned(ResidentType.SelfOwned, "Owned"),
    Parental(ResidentType.Parental, "Parental"),
    Rental(ResidentType.Rented, "Rental"), PG(ResidentType.Rented, "PG"),
    BachelorAccomodationOrShared(ResidentType.Rented, "Bachelor Accomodation or Shared"),
    CompanyProvided(ResidentType.Rented, "Company Provided");

    private ResidentType residentType;
    private String display;

    ResidentTypeEnum(ResidentType residentType, String display) {
        this.residentType = residentType;
        this.display = display;
    }

    public ResidentType getResidentType() {
        return residentType;
    }

    public String getDisplay() {
        return display;
    }

    public static ResidentTypeEnum getByResidentType(ResidentType residentType) {
        for (ResidentTypeEnum residentTypeEnum : ResidentTypeEnum.values()) {
            if (residentTypeEnum.getResidentType().equals(residentType)) {
                return residentTypeEnum;
            }
        }
        return Owned;
    }
}
