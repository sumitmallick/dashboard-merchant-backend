package com.freewayemi.merchant.commons.dto.ntbservices;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KYCResponseDto {

    @JsonProperty(value = "KycId")
    private String KycId;

    @JsonProperty(value = "cKYCID")
    private String cKYCID;

    @JsonProperty(value = "kycType")
    private KycTypes kycType;

    @JsonProperty(value = "kycAge")
    private String kycAge;

    @JsonProperty(value = "kycDob")
    private String kycDob;

    @JsonProperty(value = "kycPhotoImageType")
    private String kycPhotoImageType;

    @JsonProperty(value = "kycPhotoBytes")
    private String kycPhotoBytes;

    @JsonProperty(value = "kycFullName")
    private String kycFullName;

    @JsonProperty(value = "kycFatherFullName")
    private String kycFatherFullName;

    @JsonProperty(value = "requestStatus")
    private String requestStatus;

    @JsonProperty(value = "kycCPAN")
    private String kycCPAN;

    @JsonProperty(value = "kycMotherFullName")
    private String kycMotherFullName;

    @JsonProperty(value = "kycGender")
    private String kycGender;

    @JsonProperty(value = "kycPerAdd1")
    private String kycPerAdd1;

    @JsonProperty(value = "kycPerAdd2")
    private String kycPerAdd2;

    @JsonProperty(value = "kycPerAdd3")
    private String kycPerAdd3;

    @JsonProperty(value = "kycPerAddCity")
    private String kycPerAddCity;

    @JsonProperty(value = "kycPerAddDistrict")
    private String kycPerAddDistrict;

    @JsonProperty(value = "kycPerAddState")
    private String kycPerAddState;

    @JsonProperty(value = "kycPerAddCountry")
    private String kycPerAddCountry;

    @JsonProperty(value = "kycPerAddPin")
    private String kycPerAddPin;

    @JsonProperty(value = "kycPerAddSameasCorAdd")
    private String kycPerAddSameasCorAdd;

    @JsonProperty(value = "kycCorAdd1")
    private String kycCorAdd1;

    @JsonProperty(value = "kycCorAdd2")
    private String kycCorAdd2;

    @JsonProperty(value = "kycCorAdd3")
    private String kycCorAdd3;

    @JsonProperty(value = "kycCorAddCity")
    private String kycCorAddCity;

    @JsonProperty(value = "kycCorAddDistrict")
    private String kycCorAddDistrict;

    @JsonProperty(value = "kycCorAddState")
    private String kycCorAddState;

    @JsonProperty(value = "kycCorAddCountry")
    private String kycCorAddCountry;

    @JsonProperty(value = "kycCorAddPin")
    private String kycCorAddPin;

    @JsonProperty(value = "kycMobileNumber")
    private String kycMobileNumber;

    @JsonProperty(value = "kycEmailAdd")
    private String kycEmailAdd;

    @JsonProperty(value = "kycTransactionStatus")
    private String kycTransactionStatus;

    @JsonProperty(value = "kycReferenceId")
    private String kycReferenceId;

    @JsonProperty(value = "kycRawXml")
    private String kycRawXml;

    @JsonProperty(value = "kycAadharCareOf")
    private String kycAadharCareOf;

    @JsonProperty(value = "aadhaarZipFileBase64")
    private String aadhaarZipFileBase64;

    @JsonProperty(value = "kycZipPasscode")
    private String kycZipPasscode;

    @JsonProperty(value = "kycCorAddPostOffice")
    private String kycCorAddPostOffice;

    @JsonProperty(value = "kycCorAddVtc")
    private String kycCorAddVtc;

    @JsonProperty(value = "kycPerAddPostOffice")
    private String kycPerAddPostOffice;

    @JsonProperty(value = "kycPerAddVtc")
    private String kycPerAddVtc;

    @JsonProperty(value = "kycTimeStamp")
    private String kycTimeStamp;

    @JsonProperty(value = "kycCorAddLandmark")
    private String kycCorAddLandmark;

    @JsonProperty(value = "kycPerAddLandmark")
    private String kycPerAddLandmark;

    @JsonProperty(value = "kycPerAddStreet")
    private String kycPerAddStreet;

    @JsonProperty(value = "kycCorAddStreet")
    private String kycCorAddStreet;

    @JsonProperty(value = "kycStatus")
    private String kycStatus;

    @JsonProperty(value = "kycRawDocument")
    private String kycRawDocument;

    @JsonProperty(value = "kycRawDocumentFileType")
    private String kycRawDocumentFileType;

    @JsonProperty(value = "kycLocality")
    private String kycLocality;

    @JsonProperty(value = "kycUid")
    private String kycUid;

    @JsonProperty(value = "lastFourDigitOfAadhaarCard")
    private String lastFourDigitOfAadhaarCard;

    @JsonProperty(value = "kycStatusMessage")
    private String kycStatusMessage;

    @JsonProperty(value = "code")
    private String code;

    @JsonProperty(value = "message")
    private String message;
}
