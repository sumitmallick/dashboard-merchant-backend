package com.freewayemi.merchant.dto.response;

import com.freewayemi.merchant.commons.dto.Address;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerifyDetailsResponse {
    private final String status;
    private final String accountName;
    private final String businessName;
    private final Address address;
}
