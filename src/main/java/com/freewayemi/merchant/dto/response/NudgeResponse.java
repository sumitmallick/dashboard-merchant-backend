package com.freewayemi.merchant.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NudgeResponse {
    private final String nudgeId;
    private final String type;
    private final String title;
    private final String text;
    private final String icon;
    private final String subText;
    private final String action;

    @JsonCreator
    public NudgeResponse(String nudgeId, String type, String title, String text, String icon, String subText, String action) {
        this.nudgeId = nudgeId;
        this.type = type;
        this.title = title;
        this.text = text;
        this.icon = icon;
        this.subText = subText;
        this.action = action;
    }
}
