package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DigilockerLinkResp {
    private String status;
    private String statusMsg;
    private String Link;
    private String requestId;

    @JsonCreator
    public DigilockerLinkResp(@JsonProperty("status") String status,
                                @JsonProperty("statusMsg") String statusMsg,
                                @JsonProperty("Link") String Link,
                                @JsonProperty("requestId") String requestId) {
        this.status = status;
        this.statusMsg = statusMsg;
        this.Link = Link;
        this.requestId = requestId;
    }
}
