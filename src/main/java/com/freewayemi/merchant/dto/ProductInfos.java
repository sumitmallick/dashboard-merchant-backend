package com.freewayemi.merchant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductInfos {

    public String category;
    public List<ProductNameIdInfo> productNameIdInfoList;
}
