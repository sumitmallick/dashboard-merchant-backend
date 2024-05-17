package com.freewayemi.merchant.commons.entity;

import lombok.Data;

@Data
public class BrandParams {
    private Boolean emiOfferValidationEnabled;

    //Generate Delivery Order flag is to control
    //whether to send DO statement to merchants or not.
    //Requirement from Servify and Samsung.
    //If "generateDeliveryOrderEnabled" is true.
    //"deliveryOrderProvider" should be there with configured factory.
    //Initially we have SERVIFY,SAMSUNG providers to generate DO statement.
    //This factory is implemented in the Paymentms project.
    private Boolean generateDeliveryOrderEnabled;
    private String deliveryOrderProvider;
}
