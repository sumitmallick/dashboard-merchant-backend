package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SendNotificationRequest {

    private final List<String> channels;
    private final String type;
    private final Map<String, Object> data;
    private Map<String, String> eventProps;
    private Map<String, String> userProps;

    @JsonCreator
    public SendNotificationRequest(@JsonProperty("channels") List<String> channels, @JsonProperty("type") String type,
                                   @JsonProperty("data") Map<String, Object> data) {
        this.channels = channels;
        this.type = type;
        this.data = data;
    }

    public void setEventProps(Map<String, String> eventProps) {
        this.eventProps = eventProps;
    }

    public void setUserProps(Map<String, String> userProps) {
        this.userProps = userProps;
    }
}
