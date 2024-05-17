package com.freewayemi.merchant.dto.request;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class DefaultFilterDTO {
    private Map<String, String> ccMap = new HashMap<>();
    private Map<String, String> dcMap = new HashMap<>();
    private Map<String, String> brandIdsMap = new HashMap<>();
    private Map<String, String> productCategoryMap = new HashMap<>();
    private boolean isMerchantOffline;
    private boolean isBrandSubventionModel;
    private String partner;
}
