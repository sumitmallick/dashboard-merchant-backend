package com.freewayemi.merchant.commons.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AppContentCard {
    private final String type;
    private final String landing;
    private final String title;
    private final String text;
    private final String subText;
    private final String icon;
}
