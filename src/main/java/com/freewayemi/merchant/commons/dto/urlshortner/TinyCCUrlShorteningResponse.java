package com.freewayemi.merchant.commons.dto.urlshortner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@JsonDeserialize(builder = TinyCCUrlShorteningResponse.TinyUrlShorteningResponseBuilder.class)
@Builder(builderClassName = "TinyUrlShorteningResponseBuilder", toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TinyCCUrlShorteningResponse {

    private final TinyCCUrlError error;
    private final String version;
    private final List<TinyCCUrlsResponse> urls;

    @JsonPOJOBuilder(withPrefix = "")
    public static class TinyUrlShorteningResponseBuilder {
    }
}
