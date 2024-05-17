package com.freewayemi.merchant.dto;

import lombok.Data;

@Data
public class DisplayEligibilityBand {
    private Boolean webCheckout;
    private Boolean dashboard;
    private Boolean consumerApp;
    private Boolean merchantApp;
    private Boolean salesApp;
    private Boolean apiIntegration;
}
