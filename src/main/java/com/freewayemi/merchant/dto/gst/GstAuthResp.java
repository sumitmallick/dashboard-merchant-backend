package com.freewayemi.merchant.dto.gst;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GstAuthResp {
    private final String paymentRefId;
    private final Integer code;
    private final String status;
    private final String statusMessage;
    private final String gst;

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
    private String aggreTurnOverFY;
    private String percentTaxInCashFY;
    private String provider;
    private String aggreTurnOver;
    private List<BusinessDetail> businessDetails;
    @JsonCreator
    public GstAuthResp(
            @JsonProperty("paymentRefId") String paymentRefId,
            @JsonProperty("provider") String provider,
            @JsonProperty("code") Integer code,
            @JsonProperty("status") String status,
            @JsonProperty("gst") String gst,
            @JsonProperty("statusMessage") String statusMessage,
            @JsonProperty("gstStatus") String gstStatus,
            @JsonProperty("memberNames") List<String> memberNames,
            @JsonProperty("gstContact") GstContact gstContact,
            @JsonProperty("cancelledFlag") String cancelledFlag,
            @JsonProperty("complianceRating") String complianceRating,
            @JsonProperty("gstRegistrationDate") String gstRegistrationDate,
            @JsonProperty("tradeName") String tradeName,
            @JsonProperty("natureOfBusiness") List<String> natureOfBusiness,
            @JsonProperty("addressesOfAdditionalPlacesOfBusiness") List<GstAddress> addressesOfAdditionalPlacesOfBusiness,
            @JsonProperty("addressesOfPrincipalPlaceOfBusiness") GstAddress addressesOfPrincipalPlaceOfBusiness,
            @JsonProperty("stateJurisdictionCode") String stateJurisdictionCode,
            @JsonProperty("lastUpdated") String lastUpdated,
            @JsonProperty("centralJurisdictionCode") String centralJurisdictionCode,
            @JsonProperty("stateJurisdiction") String stateJurisdiction,
            @JsonProperty("taxPayerType") String taxPayerType,
            @JsonProperty("dateOfCancellationRegistration") String dateOfCancellationRegistration,
            @JsonProperty("constitutionOfBusiness") String constitutionOfBusiness,
            @JsonProperty("legalNameOfBusiness") String legalNameOfBusiness,
            @JsonProperty("centralJurisdiction") String centralJurisdiction,
            @JsonProperty("gstHsnDetails") GstHsnDetails gstHsnDetails,
            @JsonProperty("gstSacDetails") GstSacDetails gstSacDetails,
            @JsonProperty("annualTurnOver") String annualTurnOver,
            @JsonProperty("mandatoryEInvoice") String mandatoryEInvoice,
            @JsonProperty("natureOfCoreBusinessActivity") String natureOfCoreBusinessActivity,
            @JsonProperty("aadhaarAuthenticated") String aadhaarAuthenticated,
            @JsonProperty("grossTotalIncomeFinancialYear") String grossTotalIncomeFinancialYear,
            @JsonProperty("ekycVerified") String ekycVerified,
            @JsonProperty("percentageOfTaxInCash") String percentageOfTaxInCash,
            @JsonProperty("complianceDetails") Boolean complianceDetails,
            @JsonProperty("grossTotalIncomeAsPerIT") String grossTotalIncomeAsPerIT,
            @JsonProperty("aggreTurnOverFY") String aggreTurnOverFY,
            @JsonProperty("percentTaxInCashFY") String percentTaxInCashFY,
            @JsonProperty("aggreTurnOver") String aggreTurnOver,
            @JsonProperty("businessDetails") List<BusinessDetail> businessDetails
    ) {
        this.paymentRefId = paymentRefId;
        this.code = code;
        this.status = status;
        this.gst = gst;
        this.statusMessage = statusMessage;
        this.gstStatus = gstStatus;
        this.memberNames = memberNames;
        this.gstContact = gstContact;
        this.cancelledFlag = cancelledFlag;
        this.complianceRating = complianceRating;
        this.gstRegistrationDate = gstRegistrationDate;
        this.tradeName = tradeName;
        this.natureOfBusiness = natureOfBusiness;
        this.addressesOfAdditionalPlacesOfBusiness = addressesOfAdditionalPlacesOfBusiness;
        this.addressesOfPrincipalPlaceOfBusiness = addressesOfPrincipalPlaceOfBusiness;
        this.stateJurisdictionCode = stateJurisdictionCode;
        this.lastUpdated = lastUpdated;
        this.centralJurisdictionCode = centralJurisdictionCode;
        this.stateJurisdiction = stateJurisdiction;
        this.taxPayerType = taxPayerType;
        this.dateOfCancellationRegistration = dateOfCancellationRegistration;
        this.constitutionOfBusiness = constitutionOfBusiness;
        this.legalNameOfBusiness = legalNameOfBusiness;
        this.centralJurisdiction = centralJurisdiction;
        this.gstHsnDetails = gstHsnDetails;
        this.gstSacDetails = gstSacDetails;
        this.annualTurnOver = annualTurnOver;
        this.mandatoryEInvoice = mandatoryEInvoice;
        this.natureOfCoreBusinessActivity = natureOfCoreBusinessActivity;
        this.aadhaarAuthenticated = aadhaarAuthenticated;
        this.grossTotalIncomeFinancialYear = grossTotalIncomeFinancialYear;
        this.ekycVerified = ekycVerified;
        this.percentageOfTaxInCash = percentageOfTaxInCash;
        this.complianceDetails = complianceDetails;
        this.grossTotalIncomeAsPerIT = grossTotalIncomeAsPerIT;
        this.aggreTurnOverFY = aggreTurnOverFY;
        this.percentTaxInCashFY = percentTaxInCashFY;
        this.provider = provider;
        this.aggreTurnOver = aggreTurnOver;
        this.businessDetails = businessDetails;
    }
}
