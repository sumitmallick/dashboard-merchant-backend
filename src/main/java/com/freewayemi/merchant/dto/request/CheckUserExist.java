package com.freewayemi.merchant.dto.request;

import lombok.Data;

@Data
public class CheckUserExist {
    private String mobile;
    private String email;
}
