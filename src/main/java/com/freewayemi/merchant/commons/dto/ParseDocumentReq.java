package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ParseDocumentReq {
    private String key;
    private String type;

    public ParseDocumentReq(@JsonProperty("key") String key,
                            @JsonProperty("type") String type)
    {
        this.key = key;
        this.type = type;
    }
}
