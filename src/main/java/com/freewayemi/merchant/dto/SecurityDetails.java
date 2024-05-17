package com.freewayemi.merchant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SecurityDetails {
    private Map<String, String> credentials;
    private List<String> authorities;
    private String merchantIdOrDisplayId;
}
