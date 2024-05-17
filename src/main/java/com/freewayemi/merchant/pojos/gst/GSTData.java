package com.freewayemi.merchant.pojos.gst;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GSTData {

    @JsonProperty(value = "client_id")
    private final String clientId;

    @JsonProperty(value = "pan_number")
    private final String panNumber;

    @JsonProperty(value = "gstin")
    private final String gst;

    @JsonProperty(value = "business_name")
    private final String businessName;

    @JsonProperty(value = "legal_name")
    private final String legalName;

    @JsonProperty(value = "gstin_status")
    private final String gstInStatus;

    @JsonProperty(value = "address")
    private final String address;

    @JsonProperty(value = "nature_bus_activities")
    private final List<String> activities;

    @JsonProperty(value = "taxpayer_type")
    private final String taxpayerType;
    @JsonProperty(value = "phone_number")
    private final String phoneNumber;
    @JsonProperty(value = "constitution_of_business")
    private final String constitutionOfBusiness;
    @JsonCreator
    public GSTData(@JsonProperty(value = "client_id") String clientId,
                   @JsonProperty(value = "pan_number") String panNumber,
                   @JsonProperty(value = "gstin_status") String gstInStatus,
                   @JsonProperty(value = "address") String address,
                   @JsonProperty(value = "gstin") String gst,
                   @JsonProperty(value = "business_name") String businessName,
                   @JsonProperty(value = "taxpayer_type") String taxpayerType,
                   @JsonProperty(value = "nature_bus_activities") List<String> activities,
                   @JsonProperty(value = "legal_name") String legalName,
                   @JsonProperty(value = "phone_number") String phoneNumber,
                   @JsonProperty(value = "constitution_of_business") String constitutionOfBusiness) {
        this.clientId = clientId;
        this.panNumber = panNumber;
        this.gst = gst;
        this.businessName = businessName;
        this.legalName = legalName;
        this.gstInStatus = gstInStatus;
        this.address = address;
        this.activities = activities;
        this.taxpayerType = taxpayerType;
        this.phoneNumber = phoneNumber;
        this.constitutionOfBusiness = constitutionOfBusiness;
    }
}
