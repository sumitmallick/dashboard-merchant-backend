package com.freewayemi.merchant.commons.dto;

import lombok.Data;

@Data
public class ConsumerAccount {
    private Boolean active;
    private String type;
    private String upi;
    private String number;
    private String ifsc;
    private String bankName;
    private String bankType;
    private String beneficiaryName;
    private Boolean isValid;
}
