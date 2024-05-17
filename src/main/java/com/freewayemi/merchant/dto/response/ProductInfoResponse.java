package com.freewayemi.merchant.dto.response;

import com.freewayemi.merchant.dto.ProductInfos;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ProductInfoResponse {
    
    public Integer code;
    public List<ProductInfos> productInfo;

    public String status;

    public String message;
}
