package com.freewayemi.merchant.dto.BankAccount;

import com.freewayemi.merchant.type.Source;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BankDetailsDTO
{
    private  String paymentRefId;
    private  String ifsc;
    private String accountNumber;
    private Source source;
    private Integer  code;
    private String status;
    private String statusMessage;
    private String provider;

    private Boolean bankTxnStatus;
    private String accountName;
    private String bankResponse;



}
