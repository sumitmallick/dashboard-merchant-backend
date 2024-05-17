package com.freewayemi.merchant.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MerchantConfigDto {

    private String label;
    private List<String> values;
    private Integer version;
}
