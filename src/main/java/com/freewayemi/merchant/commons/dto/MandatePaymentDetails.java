package com.freewayemi.merchant.commons.dto;

import com.freewayemi.merchant.commons.type.CardTypeEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MandatePaymentDetails {

    private CardTypeEnum cardType;
    private String accountHolderName;
    private String accountType;
    private String accountNumber;
    private String bankName;
    private String ifscCode;
    private String firstCollectionDate;
    private String frequency;
}
