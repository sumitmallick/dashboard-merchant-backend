package com.freewayemi.merchant.commons.dto.urlshortner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@JsonDeserialize(builder = TinyCCUrlError.TinyUrlErrorBuilder.class)
@Builder(builderClassName = "TinyUrlErrorBuilder", toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TinyCCUrlError {

    private final Integer code;
    private final String message;
    private final String details;

    @JsonPOJOBuilder(withPrefix = "")
    public static class TinyUrlErrorBuilder {
    }
}
