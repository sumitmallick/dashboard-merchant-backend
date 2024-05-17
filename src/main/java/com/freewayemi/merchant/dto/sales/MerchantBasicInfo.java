package com.freewayemi.merchant.dto.sales;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
public class MerchantBasicInfo {
    private String mobile;
    private String name;
    private String login;
    private String status;
    private Instant createdDate;
    private String storeUseId;
    private String merchantFirstName;
    private String merchantLastName;
    private Map<String, String> metadata;
    private String merchantId;
    private String checkOutVersion;
    private String category;
}
