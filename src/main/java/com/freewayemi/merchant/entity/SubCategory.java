package com.freewayemi.merchant.entity;

import lombok.Data;

@Data
public class SubCategory {
    private String subCategory;
    private String mcc;
    private Integer order;
    private Boolean gstWaived;
}
