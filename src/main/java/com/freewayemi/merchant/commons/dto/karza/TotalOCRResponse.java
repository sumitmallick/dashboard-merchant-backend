package com.freewayemi.merchant.commons.dto.karza;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@JsonDeserialize(builder = TotalOCRResponse.TotalOCRResponseBuilder.class)
@Builder(builderClassName = "TotalOCRResponseBuilder", toBuilder = true)
public class TotalOCRResponse {

    @JsonProperty("result")
    private final Result[] result;

    @JsonProperty("requestId")
    private final String requestId;

    @JsonProperty("statusCode")
    private final String statusCode;

    Instant CreatedDate;

    @JsonPOJOBuilder(withPrefix = "")
    public static class TotalOCRResponseBuilder {
    }

    @Data
    @JsonDeserialize(builder = Result.ResultBuilder.class)
    @Builder(builderClassName = "ResultBuilder", toBuilder = true)
    public static class Result {

        @JsonProperty("type")
        private final String type;

        @JsonProperty("details")
        private final Details details;

        @JsonPOJOBuilder(withPrefix = "")
        public static class ResultBuilder {
        }

        @Data
        @JsonDeserialize(builder = Details.DetailsBuilder.class)
        @Builder(builderClassName = "DetailsBuilder", toBuilder = true)
        public static class Details {


            @JsonProperty("aadhaar")
            private final Aadhaar aadhaar;

            @JsonProperty("imageUrl")
            private final ImageUrl imageUrl;

            @JsonProperty("name")
            private final Name name;

            @JsonProperty("father")
            private final Father father;

            @JsonProperty("husband")
            private final Husband husband;

            @JsonProperty("address")
            private final Address address;

            @JsonProperty("addressSplit")
            private final AddressSplit addressSplit;

            @JsonProperty("pin")
            private final Pin pin;

            @JsonProperty("phone")
            private final Phone phone;

            @JsonProperty("mother")
            private final Mother mother;

            @JsonProperty("gender")
            private final Gender gender;

            @JsonProperty("dob")
            private final DOB dob;

            @JsonProperty("yob")
            private final YOB yob;

            @JsonProperty("qr")
            private final QR qr;

            @JsonProperty("passportNum")
            private final PassportNum passportNum;

            @JsonProperty("givenName")
            private final GivenName givenName;

            @JsonProperty("surname")
            private final Surname surname;

            @JsonProperty("placeOfBirth")
            private final PlaceOfBirth placeOfBirth;

            @JsonProperty("countryCode")
            private final CountryCode countryCode;

            @JsonProperty("nationality")
            private final Nationality nationality;

            @JsonProperty("placeOfIssue")
            private final PlaceOfIssue placeOfIssue;

            @JsonProperty("doi")
            private final DOI doi;

            @JsonProperty("doe")
            private final DOE doe;

            @JsonProperty("type")
            private final Type type;

            @JsonProperty("mrz")
            private final Mrz mrz;

            @JsonProperty("spouse")
            private final Spouse spouse;

            @JsonProperty("oldPassportNum")
            private final OldPassportNum oldPassportNum;

            @JsonProperty("oldDoi")
            private final OldDoi oldDoi;

            @JsonProperty("oldPlaceOfIssue")
            private final OldPlaceOfIssue oldPlaceOfIssue;

            @JsonProperty("fileName")
            private final FileName fileName;

            @JsonProperty("voterid")
            private final VoterId voterid;

            @JsonProperty("age")
            private final Age age;

            @JsonProperty("doc")
            private final Doc doc;

            @JsonProperty("relation")
            private final Relation relation;

            @JsonProperty("date")
            private final Date date;

            @JsonProperty("dlNo")
            private final DLNo dlNo;

            @JsonProperty("dateOfIssue")
            private final DateOfIssue dateOfIssue;

            @JsonProperty("panNo")
            private final PanNo panNo;

            @JsonPOJOBuilder(withPrefix = "")
            public static class DetailsBuilder {
            }

            @Data
            @JsonDeserialize(builder = Aadhaar.AadhaarBuilder.class)
            @Builder(builderClassName = "AadhaarBuilder", toBuilder = true)
            public static class Aadhaar{
                @JsonProperty("isMasked")
                private final String isMasked;

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class AadhaarBuilder {
                }

            }

            @Data
            @JsonDeserialize(builder = ImageUrl.ImageUrlBuilder.class)
            @Builder(builderClassName = "ImageUrlBuilder", toBuilder = true)
            public static class ImageUrl{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class ImageUrlBuilder {
                }

            }

            @Data
            @JsonDeserialize(builder = Father.FatherBuilder.class)
            @Builder(builderClassName = "FatherBuilder", toBuilder = true)
            public static class Father{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class FatherBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = Name.NameBuilder.class)
            @Builder(builderClassName = "NameBuilder", toBuilder = true)
            public static class Name{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class NameBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = Husband.HusbandBuilder.class)
            @Builder(builderClassName = "HusbandBuilder", toBuilder = true)
            public static class Husband{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class HusbandBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = Address.AddressBuilder.class)
            @Builder(builderClassName = "AddressBuilder", toBuilder = true)
            public static class Address{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class AddressBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = AddressSplit.AddressSplitBuilder.class)
            @Builder(builderClassName = "AddressSplitBuilder", toBuilder = true)
            public static class AddressSplit{

                @JsonProperty("city")
                private final String city;

                @JsonProperty("district")
                private final String district;

                @JsonProperty("pin")
                private final String pin;

                @JsonProperty("locality")
                private final String locality;

                @JsonProperty("line2")
                private final String line2;

                @JsonProperty("line1")
                private final String line1;

                @JsonProperty("state")
                private final String state;

                @JsonProperty("street")
                private final String street;

                @JsonProperty("landmark")
                private final String landmark;

                @JsonProperty("careOf")
                private final String careOf;

                @JsonProperty("houseNumber")
                private final String houseNumber;

                @JsonPOJOBuilder(withPrefix = "")
                public static class AddressSplitBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = Pin.PinBuilder.class)
            @Builder(builderClassName = "PinBuilder", toBuilder = true)
            public static class Pin{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class PinBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = Phone.PhoneBuilder.class)
            @Builder(builderClassName = "PhoneBuilder", toBuilder = true)
            public static class Phone{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class PhoneBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = Mother.MotherBuilder.class)
            @Builder(builderClassName = "MotherBuilder", toBuilder = true)
            public static class Mother{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class MotherBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = Gender.GenderBuilder.class)
            @Builder(builderClassName = "GenderBuilder", toBuilder = true)
            public static class Gender{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class GenderBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = DOB.DOBBuilder.class)
            @Builder(builderClassName = "DOBBuilder", toBuilder = true)
            public static class DOB{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class DOBBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = YOB.YOBBuilder.class)
            @Builder(builderClassName = "YOBBuilder", toBuilder = true)
            public static class YOB{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class YOBBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = QR.QRBuilder.class)
            @Builder(builderClassName = "QRBuilder", toBuilder = true)
            public static class QR{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class QRBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = PassportNum.PassportNumBuilder.class)
            @Builder(builderClassName = "PassportNumBuilder", toBuilder = true)
            public static class PassportNum{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class PassportNumBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = GivenName.GivenNameBuilder.class)
            @Builder(builderClassName = "GivenNameBuilder", toBuilder = true)
            public static class GivenName{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class GivenNameBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = Surname.SurnameBuilder.class)
            @Builder(builderClassName = "SurnameBuilder", toBuilder = true)
            public static class Surname{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class SurnameBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = PlaceOfBirth.PlaceOfBirthBuilder.class)
            @Builder(builderClassName = "PlaceOfBirthBuilder", toBuilder = true)
            public static class PlaceOfBirth{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class PlaceOfBirthBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = CountryCode.CountryCodeBuilder.class)
            @Builder(builderClassName = "CountryCodeBuilder", toBuilder = true)
            public static class CountryCode{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class CountryCodeBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = Nationality.NationalityBuilder.class)
            @Builder(builderClassName = "NationalityBuilder", toBuilder = true)
            public static class Nationality{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class NationalityBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = PlaceOfIssue.PlaceOfIssueBuilder.class)
            @Builder(builderClassName = "PlaceOfIssueBuilder", toBuilder = true)
            public static class PlaceOfIssue{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class PlaceOfIssueBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = DOI.DOIBuilder.class)
            @Builder(builderClassName = "DOIBuilder", toBuilder = true)
            public static class DOI{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class DOIBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = DOE.DOEBuilder.class)
            @Builder(builderClassName = "DOEBuilder", toBuilder = true)
            public static class DOE{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class DOEBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = Type.TypeBuilder.class)
            @Builder(builderClassName = "TypeBuilder", toBuilder = true)
            public static class Type{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class TypeBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = Mrz.MrzBuilder.class)
            @Builder(builderClassName = "MrzBuilder", toBuilder = true)
            public static class Mrz{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class MrzBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = Spouse.SpouseBuilder.class)
            @Builder(builderClassName = "SpouseBuilder", toBuilder = true)
            public static class Spouse{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class SpouseBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = OldPassportNum.OldPassportNumBuilder.class)
            @Builder(builderClassName = "OldPassportNumBuilder", toBuilder = true)
            public static class OldPassportNum{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class OldPassportNumBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = OldDoi.OldDoiBuilder.class)
            @Builder(builderClassName = "OldDoiBuilder", toBuilder = true)
            public static class OldDoi{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class OldDoiBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = OldPlaceOfIssue.OldPlaceOfIssueBuilder.class)
            @Builder(builderClassName = "OldPlaceOfIssueBuilder", toBuilder = true)
            public static class OldPlaceOfIssue{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class OldPlaceOfIssueBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = FileName.FileNameBuilder.class)
            @Builder(builderClassName = "FileNameBuilder", toBuilder = true)
            public static class FileName{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class FileNameBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = VoterId.VoterIdBuilder.class)
            @Builder(builderClassName = "VoterIdBuilder", toBuilder = true)
            public static class VoterId{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class VoterIdBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = Age.AgeBuilder.class)
            @Builder(builderClassName = "AgeBuilder", toBuilder = true)
            public static class Age{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class AgeBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = Doc.DocBuilder.class)
            @Builder(builderClassName = "DocBuilder", toBuilder = true)
            public static class Doc{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class DocBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = Relation.RelationBuilder.class)
            @Builder(builderClassName = "RelationBuilder", toBuilder = true)
            public static class Relation{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class RelationBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = Date.DateBuilder.class)
            @Builder(builderClassName = "DateBuilder", toBuilder = true)
            public static class Date{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class DateBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = DLNo.DLNoBuilder.class)
            @Builder(builderClassName = "DLNoBuilder", toBuilder = true)
            public static class DLNo{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class DLNoBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = DateOfIssue.DateOfIssueBuilder.class)
            @Builder(builderClassName = "DateOfIssueBuilder", toBuilder = true)
            public static class DateOfIssue{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class DateOfIssueBuilder {
                }
            }

            @Data
            @JsonDeserialize(builder = PanNo.PanNoBuilder.class)
            @Builder(builderClassName = "PanNoBuilder", toBuilder = true)
            public static class PanNo{

                @JsonProperty("value")
                private final String value;

                @JsonPOJOBuilder(withPrefix = "")
                public static class PanNoBuilder {
                }
            }

        }
    }
}
