package com.freewayemi.merchant.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Data
public class VerificationDocuments {
    private final String name;
    private final String type;

    @JsonCreator
    public VerificationDocuments(
            @JsonProperty("name") String name,
            @JsonProperty("type") String type){
        this.name = name;
        this.type = type;
    }
}
