package com.freewayemi.merchant.dto.webengage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebEngageResponseStatus {
    private final String status;

    @JsonCreator
    public WebEngageResponseStatus(@JsonProperty("status") String status) {
        this.status = status;
    }
}
