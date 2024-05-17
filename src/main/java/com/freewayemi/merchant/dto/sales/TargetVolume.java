package com.freewayemi.merchant.dto.sales;

import lombok.Data;

@Data
public class TargetVolume {
    private String txns;
    private String txnsToday;
    private String volume;
    private String volumeToday;
}
