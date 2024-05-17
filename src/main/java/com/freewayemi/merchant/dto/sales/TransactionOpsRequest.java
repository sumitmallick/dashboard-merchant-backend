package com.freewayemi.merchant.dto.sales;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionOpsRequest {
    private String leadOwnerId;
    private String merchantId;
    private String fromDate;
    private String toDate;
    private String text;
    private Integer skip;
    private Integer limit;
    private Integer pageSize;
    private Integer pageNo;
    private String transactionStatus;
}

