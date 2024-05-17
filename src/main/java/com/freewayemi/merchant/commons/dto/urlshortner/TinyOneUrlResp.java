package com.freewayemi.merchant.commons.dto.urlshortner;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TinyOneUrlResp {

    private final TinyOneUrlRespData data;

    @JsonCreator
    public TinyOneUrlResp(@JsonProperty("data") TinyOneUrlRespData data) {
        this.data = data;
    }
}
