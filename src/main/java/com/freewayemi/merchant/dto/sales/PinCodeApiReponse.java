package com.freewayemi.merchant.dto.sales;

import lombok.Data;

import java.util.List;

@Data
public class PinCodeApiReponse {
    private List<PinCodeResult> results;
}
