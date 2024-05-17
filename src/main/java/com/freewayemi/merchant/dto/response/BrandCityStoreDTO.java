package com.freewayemi.merchant.dto.response;

import com.freewayemi.merchant.commons.dto.Address;
import lombok.Data;

@Data
public class BrandCityStoreDTO {
    private String shopName;
    private String mobile;
    private Address address;
    private Integer totalCount;

}
