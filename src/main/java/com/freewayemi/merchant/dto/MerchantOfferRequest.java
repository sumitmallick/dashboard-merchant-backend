package com.freewayemi.merchant.dto;

import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@ToString
public class MerchantOfferRequest {

    private String brand;
    private String productSkuCode;
    private String consumerMobile;
    @NotNull(message = "Please provide amount value")
    private String amount;
    private List<String> cardTypes;
    private List<String> bankCodes;
    private List<String> tenures;
    private String brandId;
    private String brandProductId;
    private String merchantId;
    private String maxTenure;
}
