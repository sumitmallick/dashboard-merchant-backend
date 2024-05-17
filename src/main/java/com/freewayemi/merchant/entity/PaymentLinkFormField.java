package com.freewayemi.merchant.entity;

import lombok.Data;

import java.util.List;

@Data
public class PaymentLinkFormField {
    private String field_name;
    private String display_name;
    private String constraint;
    private Boolean mandatory;
    private List<String> options;
}
