package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@JsonDeserialize(builder = StoreLinkResponse.StoreLinkResponseBuilder.class)
@Builder(builderClassName = "StoreLinkResponseBuilder", toBuilder = true)
public class StoreLinkResponse {

    private final String storeLink;

    @JsonPOJOBuilder(withPrefix = "")
    public static class StoreLinkResponseBuilder {
    }
}
