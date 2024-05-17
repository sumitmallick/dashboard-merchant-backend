package com.freewayemi.merchant.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsentDto {
    private final String name;
    private final String type;
    private final String content;

    @JsonCreator
    public ConsentDto(@JsonProperty(value = "name") String name,
                      @JsonProperty(value = "type") String type,
                      @JsonProperty(value = "content") String content) {
        this.name = name;
        this.type = type;
        this.content = content;
    }
}
