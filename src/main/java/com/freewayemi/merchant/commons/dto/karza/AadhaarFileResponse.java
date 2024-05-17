package com.freewayemi.merchant.commons.dto.karza;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@JsonDeserialize(builder = AadhaarFileResponse.AadhaarFileResponseBuilder.class)
@Builder(builderClassName = "AadhaarFileResponseBuilder", toBuilder = true)
public class AadhaarFileResponse {
    @JsonProperty("requestId")
    private final String requestId;

    @JsonProperty("statusCode")
    private final String statusCode;

    @JsonProperty("clientData")
    private final ClientData clientData;

    @JsonProperty("result")
    private final Result result;

    @JsonPOJOBuilder(withPrefix = "")
    public static class AadhaarFileResponseBuilder {
    }

    @Data
    @JsonDeserialize(builder = Result.ResultBuilder.class)
    @Builder(builderClassName = "ResultBuilder", toBuilder = true)
    public static class Result {

        @JsonProperty("message")
        private final String message;

        @JsonProperty("dataFromAadhaar")
        private final DataFromAadhaar dataFromAadhaar;

        @JsonProperty("consentValidation")
        private final ConsentValidation consentValidation;

        @JsonPOJOBuilder(withPrefix = "")
        public static class ResultBuilder {
        }

        @Data
        @JsonDeserialize(builder = DataFromAadhaar.DataFromAadhaarBuilder.class)
        @Builder(builderClassName = "DataFromAadhaarBuilder", toBuilder = true)
        public static class DataFromAadhaar {

            @JsonProperty("generatedDateTime")
            private final String generatedDateTime;

            @JsonProperty("maskedAadhaarNumber")
            private final String maskedAadhaarNumber;

            @JsonProperty("name")
            private final String name;

            @JsonProperty("dob")
            private final String dob;

            @JsonProperty("gender")
            private final String gender;

            @JsonProperty("mobileHash")
            private final String mobileHash;

            @JsonProperty("emailHash")
            private final String emailHash;

            @JsonProperty("relativeName")
            private final String relativeName;

            @JsonProperty("address")
            private final Address address;

            @JsonProperty("image")
            private final String image;

            @JsonProperty("maskedVID")
            private final String maskedVID;

            @JsonProperty("file")
            private final String file;

            @JsonPOJOBuilder(withPrefix = "")
            public static class DataFromAadhaarBuilder {
            }

            @Data
            @JsonDeserialize(builder = Address.AddressBuilder.class)
            @Builder(builderClassName = "AddressBuilder", toBuilder = true)
            public static class Address {

                @JsonProperty("splitAddress")
                private final SplitAddress splitAddress;

                @JsonProperty("combinedAddress")
                private final String combinedAddress;

                @JsonPOJOBuilder(withPrefix = "")
                public static class AddressBuilder {
                }

                @Data
                @JsonDeserialize(builder = SplitAddress.SplitAddressBuilder.class)
                @Builder(builderClassName = "SplitAddressBuilder", toBuilder = true)
                public static class SplitAddress {

                    @JsonProperty("houseNumber")
                    private final String houseNumber;

                    @JsonProperty("street")
                    private final String street;

                    @JsonProperty("landmark")
                    private final String landmark;

                    @JsonProperty("subdistrict")
                    private final String subDistrict;

                    @JsonProperty("district")
                    private final String district;

                    @JsonProperty("vtcName")
                    private final String vtcName;

                    @JsonProperty("location")
                    private final String location;

                    @JsonProperty("postOffice")
                    private final String postOffice;

                    @JsonProperty("state")
                    private final String state;

                    @JsonProperty("country")
                    private final String country;

                    @JsonProperty("pincode")
                    private final String pincode;

                    @JsonPOJOBuilder(withPrefix = "")
                    public static class SplitAddressBuilder {
                    }
                }
            }
        }

        @Data
        @JsonDeserialize(builder = ConsentValidation.ConsentValidationBuilder.class)
        @Builder(builderClassName = "ConsentValidationBuilder", toBuilder = true)
        public static class ConsentValidation {

            @JsonProperty("providedName")
            private final String providedName;

            @JsonProperty("percentageOfMatch")
            private final Float percentageOfMatch;

            @JsonProperty("status")
            private final Boolean status;

            @JsonPOJOBuilder(withPrefix = "")
            public static class ConsentValidationBuilder {
            }
        }
    }
}
