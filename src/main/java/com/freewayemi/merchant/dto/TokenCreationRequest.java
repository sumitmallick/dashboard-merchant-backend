package com.freewayemi.merchant.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class TokenCreationRequest {
    private String id;
    private String name;
    private String user;
    private Instant exp;
    private String role;
    private String type;
    private String userType;
    private Set<String> permissions;
    private String merchantId;
    private List<String> merchantIds;
    private String shopName;
    private String session;
}
