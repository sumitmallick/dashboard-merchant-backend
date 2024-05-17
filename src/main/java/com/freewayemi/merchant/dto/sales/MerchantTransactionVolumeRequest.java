package com.freewayemi.merchant.dto.sales;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MerchantTransactionVolumeRequest {
    private String salesApiKey;
    private String salesAuthKey;
    private String merchantId;
    private String month;
    private String year;
    private Integer limit;
    private Integer skip;
    private String status;

}
