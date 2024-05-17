package com.freewayemi.merchant.dto.sales.config;

import lombok.Builder;
import lombok.Data;

@Data
public class LyraPgConfig {

    private String merchantId;
    private String terminalId;
    private String shopId;
    private String password;
    
}
