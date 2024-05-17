package com.freewayemi.merchant.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EmploymentDetailsDto {
    private final String educationQualification;

    private final String monthlyIncome;
    private final String employmentType;
    private final String companyName;
    private final String companyType;
    private final String businessType;
    private final String businessName;

    @JsonCreator
    public EmploymentDetailsDto(@JsonProperty("educationQualification") String educationQualification,
                                @JsonProperty("monthlyIncome") String monthlyIncome,
                                @JsonProperty("employmentType") String employmentType,
                                @JsonProperty("companyName") String companyName,
                                @JsonProperty("companyType") String companyType,
                                @JsonProperty("businessType") String businessType,
                                @JsonProperty("businessName") String businessName) {
        this.educationQualification = educationQualification;
        this.monthlyIncome = monthlyIncome;
        this.employmentType = employmentType;
        this.companyName = companyName;
        this.companyType = companyType;
        this.businessType = businessType;
        this.businessName = businessName;
    }
}
