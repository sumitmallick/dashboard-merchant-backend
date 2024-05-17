package com.freewayemi.merchant.dto.gst;

import com.freewayemi.merchant.type.Source;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GstDetailsDTO {
    private String paymentRefId;
    private Source source;
    private String gstin;
    private String provider;
    private Integer code;
    private String status;
    private String gst;
    private String statusMessage;

    private String gstStatus;
    private List<String> memberNames;
    private GstContact gstContact;
    private String cancelledFlag;
    private String complianceRating;
    private String gstRegistrationDate;
    private String tradeName;
    private List<String> natureOfBusiness;
    private List<GstAddress> addressesOfAdditionalPlacesOfBusiness;
    private GstAddress addressesOfPrincipalPlaceOfBusiness;
    private String stateJurisdictionCode;
    private String lastUpdated;
    private String centralJurisdictionCode;
    private String stateJurisdiction;
    private String taxPayerType;
    private String dateOfCancellationRegistration;
    private String constitutionOfBusiness;
    private String legalNameOfBusiness;
    private String centralJurisdiction;
    private GstHsnDetails gstHsnDetails;
    private GstSacDetails gstSacDetails;
    private String annualTurnOver;
    private String mandatoryEInvoice;
    private String natureOfCoreBusinessActivity;
    private String aadhaarAuthenticated;
    private String grossTotalIncomeFinancialYear;
    private String ekycVerified;
    private String percentageOfTaxInCash;
    private Boolean complianceDetails;
    private String grossTotalIncomeAsPerIT;
    private String aggregatedTurnOverFY;
    private String percentTaxInCashFY;
}
