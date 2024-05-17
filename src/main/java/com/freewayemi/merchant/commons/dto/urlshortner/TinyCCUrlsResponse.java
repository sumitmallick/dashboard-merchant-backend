package com.freewayemi.merchant.commons.dto.urlshortner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@JsonDeserialize(builder = TinyCCUrlsResponse.TinyUrlsResponseBuilder.class)
@Builder(builderClassName = "TinyUrlsResponseBuilder", toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TinyCCUrlsResponse {

    private final String hash;

    private final String domain;

    @JsonProperty("long_url")
    private final String longUrl;

    @JsonProperty("short_url")
    private final String shortUrl;

    @JsonProperty("short_url_with_protocol")
    private final String shortUrlWithProtocol;

    @JsonProperty("total_clicks")
    private final String totalClicks;

    @JsonProperty("unique_clicks")
    private final String uniqueClicks;

    @JsonProperty("note")
    private final String note;

    @JsonProperty("email_stats")
    private final String emailStats;

    @JsonProperty("expiration_date")
    private final String expirationDate;

    @JsonProperty("max_clicks")
    private final Integer maxClicks;

    private final TinyCCUrlLinks links;

    private final TinyCCUrlError error;

    @JsonPOJOBuilder(withPrefix = "")
    public static class TinyUrlsResponseBuilder {
    }

}
