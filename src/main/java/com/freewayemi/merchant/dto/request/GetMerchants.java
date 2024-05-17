package com.freewayemi.merchant.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class GetMerchants {
    private List<String> ids;
}
