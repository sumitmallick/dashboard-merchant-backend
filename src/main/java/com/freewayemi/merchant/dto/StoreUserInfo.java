package com.freewayemi.merchant.dto;

import com.freewayemi.merchant.entity.AccountDetails;
import lombok.Data;

@Data
public class StoreUserInfo {
    private Boolean consent;
    private String name;
    private AccountDetails accountDetails;
    private String email;
    private String mobile;
}
