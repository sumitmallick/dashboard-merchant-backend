package com.freewayemi.merchant.dto.sales.config;

import lombok.Builder;
import lombok.Data;

@Data
public class EasebuzzPgConfig {

    private String merchantId;
    private String terminalId;
    private String key;
    private String salt;
}
