package com.freewayemi.merchant.commons.dto.karza;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@JsonDeserialize(builder = DigilockerDownloadResponse.DigilockerDownloadResponseBuilder.class)
@Builder(builderClassName = "DigilockerDownloadResponseBuilder", toBuilder = true)
public class DigilockerDownloadResponse {

    @JsonProperty("result")
    private final Result[] result;

    @JsonProperty("requestId")
    private final String requestId;

    @JsonProperty("statusCode")
    private final String statusCode;

    @JsonPOJOBuilder(withPrefix = "")
    public static class DigilockerDownloadResponseBuilder {
    }

    @Data
    @JsonDeserialize(builder = Result.ResultBuilder.class)
    @Builder(builderClassName = "ResultBuilder", toBuilder = true)
    public static class Result {

        @JsonProperty("documentUri")
        private final String documentUri;

        @JsonProperty("rawFiles")
        private final RowFiles rawFiles;

        @JsonProperty("parsedFile")
        private final ParsedFiles parsedFile;

        @JsonProperty("mimes")
        private final String[] mimes;

        @JsonProperty("issuerId")
        private final String issuerId;

        @JsonProperty("description")
        private final String description;

        @JsonProperty("doctype")
        private final String doctype;

        @JsonProperty("uri")
        private final String uri;

        @JsonProperty("date")
        private final String date;

        @JsonProperty("issuer")
        private final String issuer;

        @JsonProperty("isParseable")
        private final Boolean isParseable;

        @JsonProperty("xmlSignatureVerified")
        private final String xmlSignatureVerified;

        @JsonPOJOBuilder(withPrefix = "")
        public static class ResultBuilder {
        }
    }

    @Data
    @JsonDeserialize(builder = RowFiles.RowFilesBuilder.class)
    @Builder(builderClassName = "RowFilesBuilder", toBuilder = true)
    public static class RowFiles {

        @JsonProperty("xml")
        private final Xml xml;

        @JsonProperty("json")
        private final String json;

        @JsonProperty("pdfB64")
        private final PdfB64 pdfB64;

        @JsonPOJOBuilder(withPrefix = "")
        public static class RowFilesBuilder {
        }
    }

    @Data
    @JsonDeserialize(builder = Xml.XmlBuilder.class)
    @Builder(builderClassName = "XmlBuilder", toBuilder = true)
    public static class Xml {
        @JsonProperty("content")
        private final String content;

        @JsonProperty("status")
        private final String status;

        @JsonProperty("signatureVerified")
        private final Boolean signatureVerified;

        @JsonPOJOBuilder(withPrefix = "")
        public static class XmlBuilder {
        }
    }

    @Data
    @JsonDeserialize(builder = PdfB64.PdfB64Builder.class)
    @Builder(builderClassName = "PdfB64Builder", toBuilder = true)
    public static class PdfB64 {
        @JsonProperty("content")
        private final String content;

        @JsonProperty("status")
        private final String status;

        @JsonPOJOBuilder(withPrefix = "")
        public static class PdfB64Builder {
        }
    }

    @Data
    @JsonDeserialize(builder = ParsedFiles.ParsedFilesBuilder.class)
    @Builder(builderClassName = "ParsedFilesBuilder", toBuilder = true)
    public static class ParsedFiles {
        @JsonProperty("status")
        private final String status;

        @JsonProperty("data")
        private final ParsedFileData data;

        @JsonProperty("xmlSignatureVerified")
        private final Boolean xmlSignatureVerified;

        @JsonPOJOBuilder(withPrefix = "")
        public static class ParsedFilesBuilder {
        }
    }

    @Data
    @JsonDeserialize(builder = ParsedFileData.ParsedFileDataBuilder.class)
    @Builder(builderClassName = "ParsedFileDataBuilder", toBuilder = true)
    public static class ParsedFileData {

        @JsonProperty("status")
        private final String status;

        @JsonProperty("validFromDate")
        private final String validFromDate;

        @JsonProperty("name")
        private final String name;

        @JsonProperty("language")
        private final String language;

        @JsonProperty("additionalData")
        private final AdditionalData additionalData;

        @JsonProperty("issuedTo")
        private final IssuedTo issuedTo;

        @JsonProperty("number")
        private final String number;

        @JsonProperty("type")
        private final String type;

        @JsonProperty("issuedAt")
        private final String issuedAt;

        @JsonProperty("issuedDate")
        private final String issuedDate;

        @JsonPOJOBuilder(withPrefix = "")
        public static class ParsedFileDataBuilder {
        }
    }

    @Data
    @JsonDeserialize(builder = IssuedBy.IssuedByBuilder.class)
    @Builder(builderClassName = "IssuedByBuilder", toBuilder = true)
    public static class IssuedBy {
        @JsonProperty("code")
        private final String code;

        @JsonProperty("uid")
        private final String uid;

        @JsonProperty("tin")
        private final String tin;

        @JsonProperty("address")
        private final Address address;

        @JsonProperty("type")
        private final String type;

        @JsonProperty("name")
        private final String name;

        @JsonPOJOBuilder(withPrefix = "")
        public static class IssuedByBuilder {
        }
    }

    @Data
    @JsonDeserialize(builder = IssuedTo.IssuedToBuilder.class)
    @Builder(builderClassName = "IssuedToBuilder", toBuilder = true)
    public static class IssuedTo {
        @JsonProperty("maritalStatus")
        private final String maritalStatus;

        @JsonProperty("uid")
        private final String uid;

        @JsonProperty("dob")
        private final String dob;

        @JsonProperty("gender")
        private final String gender;

        @JsonProperty("address")
        private final Address address;

        @JsonProperty("swd")
        private final String swd;

        @JsonProperty("phone")
        private final String phone;

        @JsonProperty("swdIndicator")
        private final String swdIndicator;

        @JsonProperty("photo")
        private final Photo photo;

        @JsonProperty("religion")
        private final String religion;

        @JsonProperty("email")
        private final String email;

        @JsonProperty("name")
        private final String name;

        @JsonPOJOBuilder(withPrefix = "")
        public static class IssuedToBuilder {
        }
    }

    @Data
    @JsonDeserialize(builder = Photo.PhotoBuilder.class)
    @Builder(builderClassName = "PhotoBuilder", toBuilder = true)
    public static class Photo {
        @JsonProperty("content")
        private final String content;

        @JsonProperty("format")
        private final String format;

        @JsonPOJOBuilder(withPrefix = "")
        public static class PhotoBuilder {
        }
    }

    @Data
    @JsonDeserialize(builder = Address.AddressBuilder.class)
    @Builder(builderClassName = "AddressBuilder", toBuilder = true)
    public static class Address {
        @JsonProperty("vtc")
        private final String vtc;

        @JsonProperty("pin")
        private final String pin;

        @JsonProperty("district")
        private final String district;

        @JsonProperty("locality")
        private final String locality;

        @JsonProperty("house")
        private final String house;

        @JsonProperty("line2")
        private final String line2;

        @JsonProperty("line1")
        private final String line1;

        @JsonProperty("state")
        private final String state;

        @JsonProperty("country")
        private final String country;

        @JsonProperty("landmark")
        private final String landmark;

        @JsonProperty("type")
        private final String type;

        @JsonPOJOBuilder(withPrefix = "")
        public static class AddressBuilder {
        }
    }

    @Data
    @JsonDeserialize(builder = AdditionalData.AdditionalDataBuilder.class)
    @Builder(builderClassName = "AdditionalDataBuilder", toBuilder = true)
    public static class AdditionalData {
        @JsonProperty("txn")
        private final String txn;

        @JsonProperty("code")
        private final String code;

        @JsonProperty("ts")
        private final String ts;

        @JsonProperty("ret")
        private final String ret;

        @JsonProperty("ttl")
        private final String ttl;

        @JsonProperty("kyc")
        private final String kyc;

        @JsonProperty("categories")
        private final Categories categories;

        @JsonProperty("statusDate")
        private final String statusDate;

        @JsonProperty("vehicle")
        private final Vehicle vehicle;

        @JsonProperty("financer")
        private final String financer;

        @JsonProperty("insurance")
        private final Insurance insurance;

        @JsonProperty("normsDesc")
        private final String normsDesc;

        @JsonPOJOBuilder(withPrefix = "")
        public static class AdditionalDataBuilder {
        }
    }

    @Data
    @JsonDeserialize(builder = Insurance.InsuranceBuilder.class)
    @Builder(builderClassName = "InsuranceBuilder", toBuilder = true)
    public static class Insurance {
        @JsonProperty("validTill")
        private final String validTill;

        @JsonProperty("policyNo")
        private final String policyNo;

        @JsonProperty("companyName")
        private final String companyName;

        @JsonPOJOBuilder(withPrefix = "")
        public static class InsuranceBuilder {
        }
    }

    @Data
    @JsonDeserialize(builder = Categories.CategoriesBuilder.class)
    @Builder(builderClassName = "CategoriesBuilder", toBuilder = true)
    public static class Categories {
        @JsonProperty("abbreviation")
        private final String abbreviation;

        @JsonProperty("code")
        private final String code;

        @JsonProperty("description")
        private final String description;

        @JsonPOJOBuilder(withPrefix = "")
        public static class CategoriesBuilder {
        }
    }

    @Data
    @JsonDeserialize(builder = Vehicle.VehicleBuilder.class)
    @Builder(builderClassName = "VehicleBuilder", toBuilder = true)
    public static class Vehicle {
        @JsonProperty("cylinder")
        private final String cylinder;

        @JsonProperty("cubicCapacity")
        private final String cubicCapacity;

        @JsonProperty("standingCapacity")
        private final String standingCapacity;

        @JsonProperty("grossWeight")
        private final String grossWeight;

        @JsonProperty("fitTill")
        private final String fitTill;

        @JsonProperty("color")
        private final String color;

        @JsonProperty("make")
        private final String make;

        @JsonProperty("fuelDesc")
        private final String fuelDesc;

        @JsonProperty("chasisNo")
        private final String chasisNo;

        @JsonProperty("unladenWeight")
        private final String unladenWeight;

        @JsonProperty("engineNo")
        private final String engineNo;

        @JsonProperty("seatCapacity")
        private final String seatCapacity;

        @JsonProperty("model")
        private final String model;

        @JsonProperty("wheelbase")
        private final String wheelbase;

        @JsonProperty("sleeperCapacity")
        private final String sleeperCapacity;

        @JsonProperty("class")
        private final String vehicleClass;

        @JsonProperty("mfgDate")
        private final String mfgDate;

        @JsonPOJOBuilder(withPrefix = "")
        public static class VehicleBuilder {
        }
    }
}
