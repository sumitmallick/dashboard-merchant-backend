package com.freewayemi.merchant.dto.request;

import com.freewayemi.merchant.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantLeadsRequest {
    private String status;
    private String name;
    private String leadOwnerId;
    private Integer skip=0;
}
