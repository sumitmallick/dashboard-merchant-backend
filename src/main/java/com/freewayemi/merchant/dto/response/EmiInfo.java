package com.freewayemi.merchant.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class EmiInfo {
    private final Map<String, String> offers;
}
