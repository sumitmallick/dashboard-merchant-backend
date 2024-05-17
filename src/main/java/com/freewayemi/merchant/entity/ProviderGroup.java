package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.PaymentProviderInfo;
import lombok.Data;

import java.util.List;

@Data
public class ProviderGroup {

    private String name;
    private List<PaymentProviderInfo> providers;
    private Boolean encTxnLinkEnabled;
}
