package com.freewayemi.merchant.dto;

import lombok.Data;

@Data
public class Password {
    private String password;
    private String salt;
}
