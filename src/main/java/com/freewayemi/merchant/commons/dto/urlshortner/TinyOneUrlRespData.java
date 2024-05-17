package com.freewayemi.merchant.commons.dto.urlshortner;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TinyOneUrlRespData {

    @JsonProperty("tiny_url")
    private final String tinyUrl;

    @JsonCreator
    public TinyOneUrlRespData(@JsonProperty("tiny_url") String tinyUrl) {
        this.tinyUrl = tinyUrl;
    }

}
