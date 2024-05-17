package com.freewayemi.merchant.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class DataDashBoard {
    private final Double dcPer;
    private final Double ccPer;
    private final Map<Integer, Integer> tenures;
}
