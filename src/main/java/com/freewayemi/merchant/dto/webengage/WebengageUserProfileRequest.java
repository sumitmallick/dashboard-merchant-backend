package com.freewayemi.merchant.dto.webengage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class WebengageUserProfileRequest {
    private final String userId;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String gender;
    private final String birthDate;
    private final String phone;
    private final Boolean whatsappOptIn;
    private final Map<String, String> attributes ;

    @JsonCreator
    public WebengageUserProfileRequest(@JsonProperty("userId") String userId,
                                       @JsonProperty("firstName") String firstName,
                                       @JsonProperty("lastName") String lastName,
                                       @JsonProperty("email") String email,
                                       @JsonProperty("gender") String gender,
                                       @JsonProperty("birthDate") String birthDate,
                                       @JsonProperty("phone") String phone,
                                       @JsonProperty("whatsappOptIn")  Boolean whatsappOptIn, @JsonProperty("attributes") Map<String, String> attributes) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.gender = gender;
        this.birthDate = birthDate;
        this.phone = phone;
        this.whatsappOptIn = whatsappOptIn;
        this.attributes = attributes;
    }
}
