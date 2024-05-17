package com.freewayemi.merchant.dto.webengage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class WebengageEventRequest {
    private final String userId;
    private final String eventName;
    private final String eventTime;
    private final Map<String, String> eventData ;

    @JsonCreator
    public WebengageEventRequest(@JsonProperty("userId") String userId,
                                 @JsonProperty("eventName") String eventName,
                                 @JsonProperty("eventTime") String eventTime,
                                 @JsonProperty("eventData") Map<String, String> eventData) {
        this.userId = userId;
        this.eventName = eventName;
        this.eventTime = eventTime;
        this.eventData = eventData;
    }
}
