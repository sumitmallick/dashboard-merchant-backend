package com.freewayemi.merchant.commons.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MerchantRefundConfig {

    private final Boolean stopRefundFromDashboard;
    private final Boolean stopRefundFromApi;
    private final Boolean doRefundFromSaleOnly;

    @JsonCreator
    public MerchantRefundConfig(@JsonProperty("stopRefundFromDashboard") Boolean stopRefundFromDashboard,
                                @JsonProperty("stopRefundFromApi") Boolean stopRefundFromApi,
                                @JsonProperty("doRefundFromSaleOnly") Boolean doRefundFromSaleOnly) {
        this.stopRefundFromDashboard = stopRefundFromDashboard;
        this.stopRefundFromApi = stopRefundFromApi;
        this.doRefundFromSaleOnly = doRefundFromSaleOnly;
    }

}
