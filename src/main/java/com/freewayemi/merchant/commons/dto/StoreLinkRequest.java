package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StoreLinkRequest {

    private final String mobile;

    @JsonCreator
    public StoreLinkRequest(@JsonProperty("mobile") String mobile) {
        this.mobile = mobile;
    }

}
