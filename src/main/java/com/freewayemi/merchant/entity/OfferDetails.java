package com.freewayemi.merchant.entity;
import lombok.Data;

@Data
public class OfferDetails {
    private Float maxOfferAmount;
    private Float offerPercentage;
    private Float bankPercentShare;
    private Float bankShareAmt;
    private Float brandPercentShare;
    private Float brandShareAmt;
    private Float paymentPercentShare;
    private Float paymentShareAmt;
    private Float merchantPercentShare;
    private Float merchantShareAmt;
}
