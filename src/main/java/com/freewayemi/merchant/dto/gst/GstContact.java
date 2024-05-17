package com.freewayemi.merchant.dto.gst;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GstContact {
    private String email;
    private String mobileNumber;
    private String name;
    @JsonCreator
    public GstContact(@JsonProperty("email") String email,
                      @JsonProperty("mobileNumber") String mobileNumber,
                      @JsonProperty("name") String name) {
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.name = name;
    }
}

