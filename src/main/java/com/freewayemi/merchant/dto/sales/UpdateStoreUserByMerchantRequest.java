package com.freewayemi.merchant.dto.sales;

import lombok.Data;

@Data
public class UpdateStoreUserByMerchantRequest {
    private String email;
    private String mobile;
    private String name;
}
