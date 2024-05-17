package com.freewayemi.merchant.commons.dto.urlshortner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;


@Data
@JsonDeserialize(builder = TinyCCUrlLinks.TinyUrlLinksBuilder.class)
@Builder(builderClassName = "TinyUrlLinksBuilder", toBuilder = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TinyCCUrlLinks {

    @JsonProperty("qr_small")
    private final String qrSmall;

    @JsonProperty("qr_big")
    private final String qrBig;

    @JsonPOJOBuilder(withPrefix = "")
    public static class TinyUrlLinksBuilder {
    }

}
