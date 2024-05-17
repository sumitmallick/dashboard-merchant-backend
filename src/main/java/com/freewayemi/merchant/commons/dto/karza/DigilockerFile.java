package com.freewayemi.merchant.commons.dto.karza;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class DigilockerFile {
    private final String uri;
    private final Boolean pdfB64;
    private final Boolean parsed;
    private final Boolean xml;

    @JsonCreator
    public DigilockerFile(@JsonProperty("uri") String uri,
                          @JsonProperty("pdfB64") Boolean pdfB64,
                          @JsonProperty("parsed") Boolean parsed,
                          @JsonProperty("xml") Boolean xml) {
        this.uri = uri;
        this.pdfB64 = pdfB64;
        this.parsed = parsed;
        this.xml = xml;
    }
}
