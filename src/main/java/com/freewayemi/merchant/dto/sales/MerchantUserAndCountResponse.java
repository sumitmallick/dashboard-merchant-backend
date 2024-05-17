package com.freewayemi.merchant.dto.sales;

import com.freewayemi.merchant.entity.MerchantUser;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MerchantUserAndCountResponse {
    private Long merchantCount;
    private List<MerchantUser> merchantUsers;
}
