package com.freewayemi.merchant.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class StoreUserResponse {
    private final String id;
    private final String name;
    private final String mobile;
    private final String status;
    private final String referralId;
    private final String email;
    private final String DOB;
    private final Instant createdDate;
    private final Integer transactionCount;
    private final Float transactionTotal;
    private final String userType;
    private final String partner;
    private final String role;

}
