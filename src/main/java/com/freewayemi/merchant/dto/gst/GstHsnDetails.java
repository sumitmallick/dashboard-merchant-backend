package com.freewayemi.merchant.dto.gst;


import lombok.Data;

@Data
public class GstHsnDetails {
    private String descriptionOfGoods;
    private String hsnCode;
    private String subIndustry;
    private String industry;
}