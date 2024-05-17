package com.freewayemi.merchant.dto.sales;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse {

    private Integer code;
    private String status;
    private String message;

    @JsonCreator
    public void setBaseResponse(PaymentOpsCode paymentOpsCode) {
        this.code = paymentOpsCode.getCode();
        this.status = paymentOpsCode.getStatus();
        this.message = paymentOpsCode.getStatusMsg();
    }

}
