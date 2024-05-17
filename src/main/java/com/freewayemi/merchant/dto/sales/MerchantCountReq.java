package com.freewayemi.merchant.dto.sales;

import lombok.Data;

import java.time.Instant;

@Data
public class MerchantCountReq {

    public String leadOwnerId;
    public String profileDate;
    public Instant startDate;
    public Instant endDate;

}
