package com.freewayemi.merchant.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class CreateBrandRequest {

    @NotEmpty(message = "Please provide brand code.")
    private final String brandDisplayId;

    @JsonCreator
    public CreateBrandRequest(@JsonProperty("brandDisplayId") String brandDisplayId) {
        this.brandDisplayId = brandDisplayId;
    }
}
