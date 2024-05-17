package com.freewayemi.merchant.dto.webengage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebEngageResponse {
    private final WebEngageResponseStatus response;

    @JsonCreator
    public WebEngageResponse(@JsonProperty("response") WebEngageResponseStatus response) {
        this.response = response;
    }
}
