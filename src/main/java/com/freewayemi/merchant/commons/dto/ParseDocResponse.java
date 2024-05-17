package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ParseDocResponse {
    private Integer code;
    private String status;
    private String name;
    private String docNumber;
    private String DOB;

    @JsonCreator
    public ParseDocResponse(@JsonProperty("code") Integer code,
                            @JsonProperty("status") String status,
                            @JsonProperty("name") String name,
                            @JsonProperty("DOB") String DOB,
                            @JsonProperty("docNumber") String docNumber) {
        this.code = code;
        this.status = status;
        this.name = name;
        this.DOB = DOB;
        this.docNumber = docNumber;
    }
}
