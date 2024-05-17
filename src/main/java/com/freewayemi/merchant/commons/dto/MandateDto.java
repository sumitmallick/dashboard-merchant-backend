package com.freewayemi.merchant.commons.dto;

import com.freewayemi.merchant.commons.type.CardTypeEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MandateDto {
    private String consumerId;
    private String mobile;
    private String email;
    private String orderId;
    private String firstName;
    private String lastName;
    private Float amount;
    private String mandateProvider;
    private CardTypeEnum cardType;
    private String accountHolderName;
    private String accountType;
    private String accountNumber;
    private String bankName;
    private String ifscCode;
    private String firstCollectionDate;
    private Integer bankId;
    private Integer categoryId;
    private String frequency;
    private String returnUrl;
    private String prospectId;
    private String loanReferenceId;
    private String applicationNumber;
    private String lenderCode;
}
