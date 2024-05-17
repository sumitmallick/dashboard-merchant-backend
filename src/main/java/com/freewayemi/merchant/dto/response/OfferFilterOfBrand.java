package com.freewayemi.merchant.dto.response;

import com.freewayemi.merchant.dto.request.OfferBankRequest;
import lombok.Data;

@Data
public class OfferFilterOfBrand {
    OfferBankRequest banks;
    String[] categories;
}
