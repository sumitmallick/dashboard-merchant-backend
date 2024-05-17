package com.freewayemi.merchant.dto.sales;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MerchantInfoAndCountResponse {
    private long merchantCount;
    private List<MerchantInfo> merchantInfos;
}
