package com.freewayemi.merchant.dto.request;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class BankInterestRequest {

    private String merchantId;

    // use to query for brand associated txn
    private String brandId;

    private List<InterestPerTenureRequest> interestPerTenures;

    private Boolean isActive;

    // By default applicable for all format:- dd/MM/yyyy
    private Instant validFrom;

    // By default applicable for all format:- dd/MM/yyyy
    private Instant validTo;

    private String createdBy;

    private String updatedBy;
}
