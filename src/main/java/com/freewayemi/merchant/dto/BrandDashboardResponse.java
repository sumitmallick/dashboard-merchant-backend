package com.freewayemi.merchant.dto;

import com.freewayemi.merchant.entity.Brand;
import com.freewayemi.merchant.entity.MerchantUser;
import lombok.Data;

import java.util.List;

@Data
public class BrandDashboardResponse {
    private String brandId;
    private String merchantId;
    private Brand brand;
    private MerchantUser merchantDetails;
    private Integer storeCount;
}
