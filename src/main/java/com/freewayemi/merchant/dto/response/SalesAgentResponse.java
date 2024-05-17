package com.freewayemi.merchant.dto.response;

import com.freewayemi.merchant.dto.request.Account;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SalesAgentResponse {
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String mobile;
    private final Account account;
}
